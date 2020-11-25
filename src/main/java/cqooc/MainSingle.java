package cqooc;

import bean.Course;
import utils.Core;

import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author ziy
 * @version 1.0
 * @date 下午9:50 2020/11/13
 * @description TODO:程序入口
 * @className Main
 */
public class MainSingle {

    public void single() throws Exception {
        //填充用户数据
        Core.fillUserInfo(Main.packet);
        //获取全部课程
        Course[] courses = Core.getCourses(Main.packet);
        for (int i = 0; i < courses.length; i++) {
            System.out.println("    课程序号 "+(i+1));
            System.out.println(courses[i]);
            System.out.println();
        }

        //用户输入
        System.out.print("请输入课程序号: ");
        int orderNum = 1;
        while(Main.SC.hasNextInt()) {
            orderNum = Main.SC.nextInt();
            if(orderNum > 0 && orderNum <= courses.length) {
                break;
            }
            System.out.print("请输入课程序号: ");
        }

        //填充课程数据
        Course course = courses[orderNum - 1];
        Main.packet.setParentId(course.getParentId());
        Main.packet.setCourseId(course.getId());

        /*
        至此，Packet包中基本信息填充完毕，接下来只需要修改Packet包中的sectionId和chapterId (两个值是当前需要完成的任务的相关值)
         */
        //获取未完成的任务集合
        Map<String, String> map = Core.getTaskMap(Main.packet);
        System.out.println();
        addTask(map);
    }
    private void addTask(Map<String, String> map) throws UnsupportedEncodingException {
        //计算 进度
        int total = Core.getTotal(Main.packet);
        int tasking = map.size();
        int tasked = total - tasking;
        System.out.println("进度: "+tasked+"/"+total+"   "+"预计需要: "+(map.size()*Main.INTERVAL_TIME/60)+"分钟完成");
        System.out.println();
        Main.sleep(1);
        /*
        开始完成任务
         */
        //创建未完成任务的队列
        ArrayDeque<Map.Entry<String, String>> taskingQueue = new ArrayDeque<>(270);
        //创建失败任务队列
        ArrayList<Integer> failList = new ArrayList<>(50);
        //添加关闭线程----打印失败任务线程
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            for (Integer taskNum : failList) {
                System.out.println("任务: "+taskNum+" 执行失败");
            }
        }));
        taskingQueue.addAll(map.entrySet());
        while(!taskingQueue.isEmpty()) {
            Map.Entry<String, String> entry = taskingQueue.pop();
            String id = entry.getKey();
            String parentId = entry.getValue();
            System.out.println("正在完成: "+(++tasked)+"/"+total);
            //构建完整封包
            Main.packet.setSectionId(id).setChapterId(parentId);
            //直接完成
            if (!Core.addTask(Main.packet)) {
                System.err.println("当前任务遇到异常");
                //添加失败的任务到失败队列
                failList.add(tasked);
            }
            else{
                System.out.println("已完成: "+(tasked)+"/"+total);
            }
            System.out.println();
            if (taskingQueue.isEmpty()) {
                break;
            }
            System.out.println("-------等待-------");
            System.out.println();
            //控制每个任务的间隔
            Main.sleep(Main.INTERVAL_TIME);
        }
    }
}

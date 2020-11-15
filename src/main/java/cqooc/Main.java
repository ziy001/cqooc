package cqooc;

import bean.Course;
import bean.Packet;
import utils.Core;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * @author ziy
 * @version 1.0
 * @date 下午9:50 2020/11/13
 * @description TODO:程序入口
 * @className Main
 */
public class Main {
    private static volatile boolean flag = false;
    private static final Scanner SC;
    private static Packet packet;
    static {
        SC = new Scanner(System.in);
        SC.useDelimiter("\n");
    }
    public static void main(String[] args) throws Exception {
        System.err.println("\n在程序运行期间不要登录平台避免刷课失败\n");
        System.out.print("输入你的xsid值:");
        String xsid = SC.next();
        packet = new Packet(xsid);
        //填充用户数据
        Core.fillUserInfo(packet);
        //获取全部课程
        Course[] courses = Core.getCourses(packet);
        for (int i = 0; i < courses.length; i++) {
            System.out.println("    课程序号 "+(i+1));
            System.out.println(courses[i]);
            System.out.println();
        }
        //用户输入
        System.out.print("请输入课程序号: ");
        int orderNum = 1;
        while(SC.hasNextInt()) {
            orderNum = SC.nextInt();
            if(orderNum > 0 && orderNum <= courses.length) {
                System.out.print(courses[orderNum - 1].getTitle()+"? [Y/N]");
                String s = SC.next();
                if (s.equals("Y")) {
                    break;
                }
            }
            System.out.print("请输入课程序号: ");
        }

        //填充课程数据
        Course course = courses[orderNum - 1];
        packet.setParentId(course.getParentId());
        packet.setCourseId(course.getId());
        /*
        至此，Packet包中基本信息填充完毕，接下来只需要修改Packet包中的sectionId和chapterId (两个值是当前需要完成的任务的相关值)
         */
        //获取未完成的任务集合
        Map<String, String> map = Core.taskMap(packet);
        System.out.println();
        //计算 进度
        int total = Core.total(packet);
        int tasking = map.size();
        int tasked = total - tasking;
        System.out.println("课程进度: "+tasked+"/"+total);
        System.out.println();
        //确定任务间隔
        System.out.print("任务间隔"+"(单位:秒  最低值30): ");
        int time = 30;
        while(SC.hasNextInt()) {
            time = SC.nextInt();
            if(time >= 30) {
                break;
            }
            System.out.print("任务间隔"+"(单位:秒  最低值30): ");
        }
        System.out.println();
        //自动循环完成任务
        ArrayDeque<Map.Entry<String, String>> deque = new ArrayDeque<>();
        deque.addAll(map.entrySet());
        for (int i = 0; i < deque.size(); i++) {
            Map.Entry<String, String> entry = deque.pop();
            String id = entry.getKey();
            String parentId = entry.getValue();
            System.out.println("正在完成: "+(tasked+i+1));
            //构建完整封包
            packet.setSectionId(id).setChapterId(parentId);
            //直接完成
            if (!Core.add(packet)) {
                System.err.println("当前任务遇到异常");
            }
            else{
                System.out.println("已完成: "+(tasked+i+1));
            }
            System.out.println();
            //控制每个任务的间隔
            sleep(time);
        }

    }

    /**
     * 暂停线程
     * @param time
     */
    private static void sleep(int time) {
        //启动一个线程休眠
        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).thenApply((e) -> flag = true);
        while(!flag) {}
        flag = false;
    }

}

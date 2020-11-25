package cqooc;

import bean.Course;
import utils.Core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author ZIY
 * @version 1.0
 * @date 下午10:54 2020/11/19
 * @description TODO:循环刷
 * @className MainWhile
 */
public class MainWhile {
    /**
     * 循环主程序
     * @throws Exception
     */
    public void allWhile() throws Exception {
        //填充用户数据
        Core.fillUserInfo(Main.packet);
        //获取全部课程69D786A516F8E03B
        Course[] courses = Core.getCourses(Main.packet);
        //失败队列
        ArrayList failList = new ArrayList<String>(50);

        //循环所有课程执行任务刷取
        for (Course cours : courses) {
            System.out.println("\n"+cours);
            //填充课程数据
            Main.packet.setParentId(cours.getParentId());
            Main.packet.setCourseId(cours.getId());
            //获取未完成的任务集合
            Map<String, String> map = Core.getTaskMap(Main.packet);
            //开始add完成该课程任务
            addTask(cours, map, failList);
        }
    }
    private void addTask(Course course, Map<String, String> map, ArrayList<String> failList) {
        System.out.println("正在完成: "+course.getTitle()
                +"  预计需要: " + (map.size() * Main.INTERVAL_TIME / 60) + "分钟完成");
        //创建未完成任务的队列
        ArrayDeque<Map.Entry<String, String>> taskingQueue = new ArrayDeque<>(270);
        taskingQueue.addAll(map.entrySet());
        while(!taskingQueue.isEmpty()) {
            Map.Entry<String, String> entry = taskingQueue.pop();
            String id = entry.getKey();
            String parentId = entry.getValue();
            Main.packet.setSectionId(id)
                    .setChapterId(parentId);
            if (!Core.addTask(Main.packet)) {
                failList.add(Main.packet.getSectionId());
            }
            if (taskingQueue.isEmpty()) {
                break;
            }
            Main.sleep(Main.INTERVAL_TIME);
        }
        System.out.println();
        if (!failList.isEmpty()) {
            System.out.println("未完成任务 "+failList.size()+"个: ");
            failList.forEach((failTask) ->
                    System.out.println("    "+failTask)
            );
        }
        System.out.println("已完成: "+course.getTitle()+"\n");
    }

    private static class DelayTask implements Delayed {

        @Override
        public long getDelay(TimeUnit unit) {
            return 0;
        }

        @Override
        public int compareTo(Delayed o) {
            return 0;
        }
    }
}

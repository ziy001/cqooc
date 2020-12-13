package cqooc;

import bean.Packet;

import javax.swing.*;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * @author ZIY
 * @version 1.0
 * @date 下午11:11 2020/11/19
 * @description TODO:
 * @className Main
 */
public class Main {
    static final Scanner SC = new Scanner(System.in);
    static final int INTERVAL_TIME = 30;
    static Packet packet;
    static {
        SC.useDelimiter("\n");
    }
    public static void main(String[] args) {
        //启动一个窗口防止系统被杀死
        JFrame jFrame = new JFrame();
        jFrame.setSize(0, 0);
        System.err.println("\n在程序运行期间不要登录平台以免导致任务异常\n");
        System.out.print("输入你的xsid值:");
        String xsid = Main.SC.nextLine().replaceAll("\\r", "")
                .replaceAll("\\n", "")
                .replaceAll(" ", "");
        Main.packet = new Packet(xsid);
        try {
            System.out.println("1: 单个课程"+"\n"+"2: 全部课程: ");
            while (SC.hasNext()) {
                String s = SC.next().replaceAll("\\r", "")
                        .replaceAll("\\n", "")
                        .replaceAll(" ", "");
                int i = Integer.parseInt(s);
                //格式化
                if(i == 2) {
                    new MainWhile().allWhile();
                    return;
                }
                if(i == 1) {
                    new MainSingle().single();
                    return;
                }
                System.out.println("1: 单个课程"+"\n"+"2: 全部课程: ");
            }
        } catch (Exception e) {
            System.err.println("网络请求错误,请检查xsid值和网络是否连接!!!");
            System.err.println(e.getMessage());
            System.exit(-1);
        }

    }
    /**
     * 暂停线程
     * @param time
     */
    static void sleep(int time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

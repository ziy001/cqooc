import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author ZIY
 * @version 1.0
 * @date 下午5:08 2020/11/22
 * @description TODO:
 * @className MainTest
 */
public class MainTest {
    public static void main(String[] args) throws InterruptedException {
        DelayQueue<MyDelay> myDelays = new DelayQueue<>();
        long now = System.currentTimeMillis();
        myDelays.add(new MyDelay(now+2000, TimeUnit.SECONDS));
        myDelays.add(new MyDelay(now+10000, TimeUnit.SECONDS));
        for (int i = 0; i < 2; i++) {
            System.out.println(myDelays.take());
        }
    }
}

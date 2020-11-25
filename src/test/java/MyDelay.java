import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author ZIY
 * @version 1.0
 * @date 下午5:09 2020/11/22
 * @description TODO:
 * @className MyDelay
 */
public class MyDelay implements Delayed {
    private long time;
    private TimeUnit timeUnit;

    public MyDelay(long time, TimeUnit timeUnit) {
        this.time = time;
        this.timeUnit = timeUnit;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(time-System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (this.getDelay(TimeUnit.SECONDS) < o.getDelay(TimeUnit.SECONDS)) {
            return -1;
        }
        if (this.getDelay(TimeUnit.SECONDS) > o.getDelay(TimeUnit.SECONDS)) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "MyDelay{" +
                "time=" + time +
                ", timeUnit=" + timeUnit +
                '}';
    }
}

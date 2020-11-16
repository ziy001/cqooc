package constant;

import java.nio.charset.Charset;

/**
 * @author ziy
 * @version 1.0
 * @date 下午1:43 2020/11/16
 * @description TODO:
 * @className Constant
 */
public class Constant {
    public static final Charset CHARSET;
    static {
        String os = System.getProperty("os.name");
        if(os.toLowerCase().contains("linux")) {
            CHARSET = Charset.forName("UTF-8");
        } else {
            CHARSET = Charset.forName("GBK");
        }
    }
}

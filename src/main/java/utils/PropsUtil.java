package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author ziy
 * @version 1.0
 * @date 下午10:47 2020/11/11
 * @description TODO:属性文件访问工具类,每次启动会重新加载文件
 * @className PropsUtil
 */
public class PropsUtil {
    private static Properties props;
    static {
        props = new Properties();
        try(InputStream in = PropsUtil.class.getClassLoader()
                .getResourceAsStream("apis.properties")) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    /**
     * 获取指定键的值
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        return props.getProperty(key);
    }
}

package utils;

import bean.Course;
import bean.Packet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.*;

/**
 * @author ziy
 * @version 1.0
 * @date 下午9:56 2020/11/13
 * @description TODO:
 * @className Core
 */
public class Core {
    private static final Charset CHAR_SET = Charset.forName("utf-8");
    private static final HttpResponse.BodyHandler BODY_HANDLER = HttpResponse.BodyHandlers.ofString(CHAR_SET);
    private static final Duration OUT_TIME = Duration.ofSeconds(5);
    private static final HttpClient CLIENT;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static HttpRequest request;
    private static HttpResponse<String> response;
    static {
        CLIENT = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(OUT_TIME)
                    .build();
    }
    public static void fillUserInfo(Packet packet) throws Exception {
        //参数
        String xsid = packet.getXsid();
        //获取指定API
        String api = MessageFormat.format(PropsUtil.getProperty("userInfo"), xsid);
        //请求体
        request = createRequest(api, null,
                "referer", "http://www.cqooc.net/my/learn",
                "cookie", "Hm_lvt_deeb6849ac84929e8ae01644f8d27145=1605178664; xsid="+xsid+"; Hm_lpvt_deeb6849ac84929e8ae01644f8d27145=1605178705");
        //发送请求
        response = send(request);
        //解析Json
        boolean b = userInfoJson(response.body(), packet);
        if (!b) {
            System.err.println("当前用户无法登录，请重试");
            System.exit(0);
        }
    }
    /**
     * 获取所有在线课程的相关信息
     * @param packet 封包
     * @return 课程类数组
     * @throws Exception
     */
    public static Course[] getCourses(Packet packet) throws Exception {
        //参数
        String ownerId = packet.getOwnerId();
        String xsid = packet.getXsid();
        //获取指定API
        String api = MessageFormat.format(PropsUtil.getProperty("allCourses"), ownerId);
        //请求体
        request = createRequest(api, null,
                "Referer", "http://www.cqooc.net/my/learn",
                "Cookie", "xsid=" + xsid);

        //发送请求
        response = send(request);
        return coursesJson(response.body(), packet);
    }
    /**
     * 获取全部任务总数
     * @param packet
     * @return
     */
    public static int getTotal(Packet packet) {
        String api = MessageFormat.format(PropsUtil.getProperty("total"), packet.getCourseId());
        int total = 0;
        HttpRequest request = createRequest(api, null,
                "Referer", "http://www.cqooc.net/learn/mooc/progress?id="+packet.getCourseId(),
                "Cookie", "xsid="+packet.getXsid());
        try {
            total = MAPPER.readTree(send(request).body())
                    .findPath("meta").findPath("total")
                    .asInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }
    /**
     * 获取未完成任务的Map集合
     * @param packet
     * @return 未完成任务的Map集合对象
     * @throws Exception
     */
    public static Map<String, String> getTaskMap(Packet packet) throws Exception {
        String courseId = packet.getCourseId();
        String userName = packet.getUserName();
        String xsid = packet.getXsid();

        //构建已完成list
        ArrayList<String> list = new ArrayList<>(270);
        int start = 1;
        while (true) {
            String api = MessageFormat.format(PropsUtil.getProperty("tasked"), start, courseId, userName);
            //获取已完成任务数据Json
            request = createRequest(api, null,
                    "Referer", "http://www.cqooc.net/learn/mooc/progress?id=" + courseId,
                    "Cookie", "xsid=" + xsid);
            //获取已完成任务数据Json
            response = send(request);
            boolean b = taskedJson(response.body(), list);
            if (b) {
                start += 100;
            } else {
                break;
            }
        }
        //构建全部任务的Map集合
        Map<String, String> map = new HashMap<>(270);
        String allTaskApi = MessageFormat.format(PropsUtil.getProperty("allTask"), courseId);
        request = createRequest(allTaskApi, null,
                "Referer", "http://www.cqooc.net/learn/mooc/structure?id=" + courseId,
                "Cookie", "xsid=" + xsid);
        response = send(request);
        //将全部任务填充到Map中
        taskingJson(response.body(), map);
        //从上面Map中筛选出已完成的任务，保留未完成
        screen(map, list);
        return map;
    }
    /**
     * 提交任务完成到服务器
     * @param packet 封包，将该包转换为Json数据作为请求体
     * @return 提交状态
     */
    public static boolean addTask(Packet packet) {
        //准备
        boolean flag = false;
        try {
            String json = MAPPER.writeValueAsString(packet);
            String api = PropsUtil.getProperty("addTask");
            request = createRequest(api,
                    HttpRequest.BodyPublishers.ofString(json, CHAR_SET),
                    "requestMethod", "POST",
                    "Origin", "http://www.cqooc.net",
                    "Referer", "http://www.cqooc.net/learn/mooc/structure?id=" + packet.getCourseId(),
                    "Cookie", "xsid=" + packet.getXsid());

            response = send(request);
            //分析返回数据
            JsonNode rootNode = MAPPER.readTree(response.body());
            boolean b = rootNode.findPath("code").asInt() == 0 || Objects.equals(rootNode.findPath("msg").asText(), "已经添加记录");
            if(b) {
                flag = true;
            }
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }
    /**
     * 判断任务是否完成
     * @param packet
     * @return
     * @throws Exception
     */
    public static boolean isComplete(Packet packet) throws Exception {
        String courseId = packet.getCourseId();
        String sectionId = packet.getSectionId();
        String userName = packet.getUserName();
        String api = MessageFormat.format(PropsUtil.getProperty("status"), sectionId, userName);
        request = createRequest(api, null,
                "Referer", "http://www.cqooc.net/learn/mooc/structure?id=" + courseId,
                "Cookie", "xsid=" + packet.getXsid());
        response = send(request);
        JsonNode data = MAPPER.readTree(response.body()).findPath("data");
        if(data.size() <= 0) {
            return false;
        }
        return true;
    }

    private static boolean userInfoJson(String json, Packet packet) throws JsonProcessingException {
        if (json.contains("No")) {
            return false;
        }
        JsonNode rootNode = MAPPER.readTree(json);
        //检查请求信息判断是否成功登录
        packet.setUserName(rootNode.findPath("username").asText())
                .setOwnerId(rootNode.findPath("id").asText());
        return true;
    }
    private static Course[] coursesJson(String json, Packet packet) throws JsonProcessingException {
        JsonNode rootNode = MAPPER.readTree(json);
        //创建数组
        Course[] allCourses = new Course[rootNode.findPath("meta").findPath("total").asInt()];
        JsonNode dataNode = rootNode.findPath("data");
        //循环遍历JSON数组取出每一项数据并创建对象
        for (int i = 0; i < dataNode.size(); i++) {
            JsonNode node = dataNode.path(i);
            JsonNode cNode = node.findPath("course");
            allCourses[i] = new Course(node.findPath("courseId").asText(), node.findPath("id").asText(),
                    cNode.findPath("title").asText(), cNode.findPath("courseManager").asText(), cNode.findPath("school").asText());
        }
        //返回数组
        return allCourses;
    }
    private static boolean taskedJson(String edJson, ArrayList<String> list) throws JsonProcessingException {
        //先解析已完成任务
        JsonNode rootNode = MAPPER.readTree(edJson);
        JsonNode meta = rootNode.findPath("meta");
        JsonNode data = rootNode.findPath("data");
        for (int i = 0; i < data.size(); i++) {
            String id = data.path(i).findPath("sectionId").asText();
            list.add(id);
        }
        int i = meta.findPath("total").asInt() - meta.findPath("start").asInt() - 1 - meta.findPath("size").asInt();
        return i > 0;
    }
    private static void taskingJson(String allJson, Map<String, String> map) throws JsonProcessingException {
        JsonNode rootNode2 = MAPPER.readTree(allJson);
        JsonNode body = rootNode2.findPath("data").path(0).findPath("body");
        Iterator<Map.Entry<String, JsonNode>> entry = body.fields();
        while (entry.hasNext()) {
            Map.Entry<String, JsonNode> next = entry.next();
            JsonNode arrayNode = next.getValue();
            for (int i = 0; i < arrayNode.size(); i++) {
                String id = arrayNode.path(i).asText();
                map.put(id, next.getKey());
            }
        }
    }
    private static void screen(Map<String, String> map, ArrayList<String> list) {
        list.forEach((k) -> {
            if (map.containsKey(k)) {
                map.remove(k);
            }
        });
    }

    /**
     * 只支持修改请求方式 请求头参数
     * 传入顺序： 请求方式 请求头参数
     * 请求方式: api, Post需要的BodyHandler, requestMethod, GET | POST...
     * @param api
     * @param attributes
     * @return
     */
    private static HttpRequest createRequest(String api, HttpRequest.BodyPublisher bodyPublisher, String... attributes) {

        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(api))
                .version(HttpClient.Version.HTTP_1_1)
                .timeout(OUT_TIME)
                .headers("user-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:82.0) Gecko/20100101 Firefox/82.0",
                        "accept", "*/* ",
                        "accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2",
                        "accept-Encoding", "gzip, deflate");
        int i = 0;
        //确定并设置请求方式
        boolean b = Objects.nonNull(attributes) && attributes.length > 1 &&
                Objects.equals(attributes[0].toLowerCase(), "requestmethod") &&
                Objects.equals(attributes[1].toUpperCase(), "POST");
        if(b) {
            builder.POST(bodyPublisher);
            i = 2;
        }
        else {
            builder.GET();
        }
        //设置自定义属性
        for (;i < attributes.length; i+=2) {
            builder.setHeader(attributes[i], attributes[i+1]);
        }
        return builder.build();
    }
    /**
     * 发送网络请求
     * @param request
     * @return
     * @throws Exception
     */
    private static HttpResponse<String> send(HttpRequest request) throws Exception {
        HttpResponse<String> resp = null;
        try {
            resp = CLIENT.send(request, BODY_HANDLER);
            if(resp.statusCode() != 200) {
                throw new Exception("statusCode: "+ resp.statusCode());
            }
        } catch (Exception e) {
             throw e;
        }
        return resp;
    }

}

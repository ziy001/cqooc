package bean;

import constant.Constant;

import java.net.URLEncoder;

/**
 * @author ziy
 * @version 1.0
 * @date 上午9:23 2020/11/13
 * @description TODO:用户封装一些重要参数
 * @className Packet
 */
public class Packet {
    private String userName;
    private String ownerId;
    private String xsid;
    private String courseId;
    private String parentId;
    private String sectionId;
    private String chapterId;
    private final int action = 0;
    private final int category = 2;

    public Packet(String xsid) {
        this.xsid = xsid;
    }
    public String getUserName() {
        return URLEncoder.encode(userName, Constant.CHARSET);
    }

    public Packet setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getOwnerId() {
        return URLEncoder.encode(ownerId, Constant.CHARSET);
    }

    public Packet setOwnerId(String ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public String getXsid() {
        return URLEncoder.encode(xsid, Constant.CHARSET);
    }

    public Packet setXsid(String xsid) {
        this.xsid = xsid;
        return this;
    }

    public String getCourseId() {
        return URLEncoder.encode(courseId, Constant.CHARSET);
    }

    public Packet setCourseId(String courseId) {
        this.courseId = courseId;
        return this;
    }

    public String getParentId() {
        return URLEncoder.encode(parentId, Constant.CHARSET);
    }

    public Packet setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getSectionId() {
        return URLEncoder.encode(sectionId, Constant.CHARSET);
    }

    public Packet setSectionId(String sectionId) {
        this.sectionId = sectionId;
        return this;
    }

    public String getChapterId() {
        return URLEncoder.encode(chapterId, Constant.CHARSET);
    }

    public Packet setChapterId(String chapterId) {
        this.chapterId = chapterId;
        return this;
    }

    public int getAction() {
        return action;
    }

    public int getCategory() {
        return category;
    }
    @Override
    public String toString() {
        return "Packet{" +
                "userName='" + userName + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", xsid='" + xsid + '\'' +
                ", courseId='" + courseId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", sectionId='" + sectionId + '\'' +
                ", chapterId='" + chapterId + '\'' +
                '}';
    }
}

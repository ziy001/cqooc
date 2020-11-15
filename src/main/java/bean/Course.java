package bean;

/**
 * @author ziy
 * @version 1.0
 * @date 下午8:35 2020/11/13
 * @description TODO:
 * @className Course
 */
public class Course {
    /**
     * 课程id值
     */
    private String id;
    /**
     * 课程的parentId  (cid)
     */
    private String parentId;
    /**
     * 课程标题
     */
    private String title;
    /**
     * 课程管理者老师
     */
    private String courseManager;
    /**
     * 课程所属学校
     */
    private String school;

    public Course(String id, String parentId, String title, String courseManager, String school) {
        this.id = id;
        this.parentId = parentId;
        this.title = title;
        this.courseManager = courseManager;
        this.school = school;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourseManager() {
        return courseManager;
    }

    public void setCourseManager(String courseManager) {
        this.courseManager = courseManager;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    @Override
    public String toString() {
        return "课程id: "+id+"\n"+
                "课程名称: "+title+"\n"+
                "课程老师: "+courseManager+"\n"+
                "学校: "+school+"\n";
    }
}


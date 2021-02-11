package cn.sakuramiku.lightblog;

import cn.sakuramiku.lightblog.common.boot.TomcatSupport;

/**
 * Tomcat启动类
 *
 * @author lyy
 */
public class LightBlogApplication extends TomcatSupport {

    /**
     * 程序入口
     *
     * @param args
     */
    public static void main(String[] args) {
        LightBlogApplication application = new LightBlogApplication();
        application.run();
    }
}

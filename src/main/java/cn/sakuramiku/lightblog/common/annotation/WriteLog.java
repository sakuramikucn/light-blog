package cn.sakuramiku.lightblog.common.annotation;


import java.lang.annotation.*;

/**
 * 标记为写日志
 *
 * @author LYY
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WriteLog {

    /**
     * 具体引用
     * @return
     */
    String reference() default "";

    /**
     * 日志记录的行为
     *
     * @return
     */
    Action action() default Action.OTHER;

    String note() default "";

    boolean result() default false;

    enum Action {
        /**
         * 新增
         */
        INSERT("新增"),
        /**
         * 删除
         */
        DELETE("删除"),
        /**
         * 修改
         */
        UPDATE("修改"),
        /**
         * 查询
         */
        QUERY("查询"),
        /**
         * 定时任务
         */
        Job("定时任务"),
        /**
         * 其他
         */
        OTHER("其他");

        protected String name;

        Action(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

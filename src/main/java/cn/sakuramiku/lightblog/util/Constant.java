package cn.sakuramiku.lightblog.util;

import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.Set;

/**
 * 常量
 * @author lyy
 */
public class Constant {

    /** shiro 过滤器放行的 url */
    public static final Set<String> SHIRO_PASS_URL = new ConcurrentHashSet<>();

    /** MySQL排序-降序 */
    public static final String ORDERBY_DESC = "DESC";
    /** MySQL排序-升序 */
    public static final String ORDERBY_ASC = "ASC";

    /** 文章状态-正常 */
    public static final Byte ARTICLE_STATE_NORMAL = 1;
    /** 文章状态-待删除 */
    public static final Byte ARTICLE_STATE_DELETE = 2;

    /** 评论状态-正常 */
    public static final Byte COMMENT_STATE_NORMAL = 0;
    /** 评论状态-屏蔽 */
    public static final Byte COMMENT_STATE_HIDDEN = 1;
    /** 评论状态-删除 */
    public static final Byte COMMENT_STATE_DELETE = 2;
}

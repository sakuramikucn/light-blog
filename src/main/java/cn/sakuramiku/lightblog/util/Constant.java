package cn.sakuramiku.lightblog.util;

import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.Set;

/**
 * 常量
 * @author lyy
 */
public class Constant {

    /** 请求路径-未授权 */
    public static final String REQUEST_UNAUTHORIZED = "/error/unauthorized";
    /** 请求路径-未登录 */
    public static final String REQUEST_NOT_LOGIN = "/error/not_login";
    /** 请求路径-登录过期 */
    public static final String REQUEST_LOGIN_EXPIRED = "/error/login_expired";

    /** shiro 过滤器放行的 url */
    public static final Set<String> SHIRO_PASS_URL = new ConcurrentHashSet<>();

    /** 角色-管理员（admin） */
    public static final String ROLE_ADMIN = "admin";

    /** MySQL排序-降序 */
    public static final String ORDERBY_DESC = "DESC";
    /** MySQL排序-升序 */
    public static final String ORDERBY_ASC = "ASC";

    /** 文章状态-正常 */
    public static final Integer ARTICLE_STATE_NORMAL = 1;
    /** 文章状态-待删除 */
    public static final Integer ARTICLE_STATE_DELETE = 2;

    /** 评论状态-正常 */
    public static final Integer COMMENT_STATE_NORMAL = 0;
    /** 评论状态-屏蔽 */
    public static final Integer COMMENT_STATE_HIDDEN = 1;
    /** 评论状态-删除 */
    public static final Integer COMMENT_STATE_DELETE = 2;
}

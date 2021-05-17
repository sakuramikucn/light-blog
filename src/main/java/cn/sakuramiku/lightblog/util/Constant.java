package cn.sakuramiku.lightblog.util;

import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.Set;

/**
 * 常量
 * @author lyy
 */
public class Constant {

    /** 角色-管理员（admin） */
    public static final String ROLE_ADMIN = "admin";
    /** 角色-访客（guest） */
    public static final String ROLE_GUEST = "guest";
    /** 角色-测试（test） */
    public static final String ROLE_TEST = "test";
    /** 角色-超级管理员（root） */
    public static final String ROLE_ROOT= "root";

    /** Token可刷新名单 */
    public static final String PREFIX_REFRESH_TOKEN_REFRESH = "light_blog:token:refresh:";
    /** Token黑名单 */
    public static final String PREFIX_REFRESH_TOKEN_BAN = "light_blog:token:ban:";
    /** Token白名单 */
    public static final String PREFIX_REFRESH_TOKEN_ALLOW = "light_blog:token:allow:";

    /** 文章浏览数key前缀 */
    public static final String PREFIX_ARTICLE_VIEWS = "light_blog:views:";

    /** 请求路径-未授权 */
    public static final String REQUEST_UNAUTHORIZED = "/error/unauthorized";
    /** 请求路径-未登录 */
    public static final String REQUEST_NOT_LOGIN = "/error/not_login";
    /** 请求路径-登录过期 */
    public static final String REQUEST_LOGIN_EXPIRED = "/error/login_expired";

    /** shiro 过滤器放行的 url */
    public static final Set<String> SHIRO_PASS_URL = new ConcurrentHashSet<>();



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
    /** 文章评论 */
    public static final Integer COMMENT_TYPE_ARTICLE = 1;
    /** 留言板评论 */
    public static final Integer COMMENT_TYPE_BOARD = 2;
    /** 评论角色-游客 */
    public static final Integer COMMENT_ROLE_TYPE_GUEST = 2;
    /** 评论角色-管理员 */
    public static final Integer COMMENT_ROLE_TYPE_MANAGER = 2;
    /** 评论角色-作者 */
    public static final Integer COMMENT_ROLE_TYPE_AUTHOR = 2;

    /** 引用标识-标签 */
    public static final String REF_TAG = "Tag";
    /** 引用标识-评论 */
    public static final String REF_COMMENT = "Comment";
    /** 引用标识-角色 */
    public static final String REF_ROLE = "Role";
    /** 引用标识-权限 */
    public static final String REF_RIGHT = "Right";
    /** 引用标识-文章 */
    public static final String REF_ARTICLE = "Article";
    /** 引用标识-友链 */
    public static final String REF_FRIENDLINK = "FriendLink";
    /** 引用标识-用户 */
    public static final String REF_USER = "User";
    /** 引用标识-分类 */
    public static final String REF_CATEGORY = "Category";

    /** 用户状态-正常 */
    public static final Integer USER_STATE_NORMAL = 0;
    /** 用户状态-冻结 */
    public static final Integer USER_STATE_FREEZ = 1;
    /** 用户状态 - 已删除 */
    public static final Integer USER_STATE_DELETE = 2;
}

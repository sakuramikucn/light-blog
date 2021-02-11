package cn.sakuramiku.lightblog.common.util;

import cn.hutool.crypto.SecureUtil;

/**
 * 安全工具类封装，加密、解密...
 *
 * @author lyy
 */
public class SecurityUtil {

    /**
     * MD5加密
     *
     * @param data
     * @return
     */
    public static String md5(String data) {
        return SecureUtil.md5(data);
    }

}

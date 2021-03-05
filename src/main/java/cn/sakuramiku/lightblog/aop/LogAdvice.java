package cn.sakuramiku.lightblog.aop;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.IdUtil;
import cn.sakuramiku.lightblog.entity.Log;
import cn.sakuramiku.lightblog.service.LogService;
import cn.sakuramiku.lightblog.util.Constant;
import cn.sakuramiku.lightblog.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 写日志
 *
 * @author lyy
 */
@Component
@Aspect
public class LogAdvice {

    @Resource
    protected LogService logService;

    /**
     * 处理有 {@link WriteLog} 注解的方法
     *
     * @param writeLog
     */
    @Pointcut("@annotation(writeLog))")
    public void pointCut(WriteLog writeLog) {
    }

    @AfterReturning(value = "pointCut(writeLog)", returning = "result", argNames = "joinPoint,result,writeLog")
    public void writeLog(JoinPoint joinPoint, Object result, WriteLog writeLog) {
        Object[] args = joinPoint.getArgs();
        WriteLog.Action action = writeLog.action();
        String note = writeLog.note();
        StringBuilder content = new StringBuilder();
        Log log = new Log();
        log.setId(IdUtil.nextId());
        log.setCreateTime(LocalDateTime.now());
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String entityName = className.substring(className.lastIndexOf('.') + 1).replace("Service", "");
        log.setReference(entityName);
        String actionName = action.getName();
        if (!action.getName().equalsIgnoreCase(WriteLog.Action.OTHER.getName())) {
            if (StrUtil.equals(entityName, Constant.REF_ARTICLE)) {
                actionName += "文章";
            } else if (StrUtil.equals(entityName, Constant.REF_USER)) {
                actionName += "用户";
            } else if (StrUtil.equals(entityName, Constant.REF_TAG)) {
                actionName += "标签";
            } else if (StrUtil.equals(entityName, Constant.REF_ROLE)) {
                actionName += "角色";
            } else if (StrUtil.equals(entityName, Constant.REF_RIGHT)) {
                actionName += "权限";
            } else if (StrUtil.equals(entityName, Constant.REF_COMMENT)) {
                actionName += "评论";
            } else if (StrUtil.equals(entityName, Constant.REF_FRIENDLINK)) {
                actionName += "友链";
            } else if (StrUtil.equals(entityName, Constant.REF_CATEGORY)) {
                actionName += "分类";
            }
        }
        log.setAction(actionName);
        String shiroStr = System.getProperty("shiro.enable", "false");
        boolean shiro = Boolean.parseBoolean(shiroStr);
        if (shiro) {
            Subject subject = SecurityUtils.getSubject();
            String token = (String) subject.getPrincipal();
            Claims claims = JwtUtil.getClaims(token);
            String userName = (String) claims.get("username");
            String nickname = (String) claims.get("nickName");
            if (!StrUtil.isBlank(nickname)) {
                userName += "(" + nickname + ")";
            }
            log.setOperator(userName);
        } else {
            log.setOperator("system");
        }
        Object[] objects = Arrays.asList(args).parallelStream().map(StrUtil::toString).toArray();
        log.setWhat("参数(" + ArrayUtil.join(objects, ",") + ")");
        String method = joinPoint.getSignature().getName();
        log.setNote(joinPoint.getSignature().getName() + " {" + writeLog.note() + "}");
        logService.writeLog(log);
    }
}

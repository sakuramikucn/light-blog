package cn.sakuramiku.lightblog.common.aop;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.IdGenerator;
import cn.sakuramiku.lightblog.entity.Log;
import cn.sakuramiku.lightblog.service.LogService;
import cn.sakuramiku.lightblog.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
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
public class LogAspect {

    @Resource
    protected LogService logService;

    @AfterReturning(value = "@annotation(writeLog)", returning = "result", argNames = "joinPoint,result,writeLog")
    public void writeLog(JoinPoint joinPoint, Object result, WriteLog writeLog) {

        // 获取 类上注解LogConfig信息
        Class<?> clazz = joinPoint.getTarget().getClass();
        LogConfig logConfig = clazz.getAnnotation(LogConfig.class);
        String ref = null != logConfig ? logConfig.reference() : "";
        String name = null != logConfig ? logConfig.name() : StrUtil.isNotBlank(ref) ? ref : "";

        // 获取 方法上注解 WriteLog 信息
        WriteLog.Action action = writeLog.action();

        Log log = new Log();
        log.setId(IdGenerator.nextId());
        log.setCreateTime(LocalDateTime.now());
        log.setReference(ref);
        String actionName = action.getName();
        log.setAction(actionName + name);

        // 获取操作人
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

        String method = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        Object[] objects = Arrays.asList(args).parallelStream().map(StrUtil::toString).toArray();
        log.setWhat("执行方法：" + method + "，方法参数: {" + ArrayUtil.join(objects, ",") + "},执行结果：" + null == result ? null : result.toString());
        log.setNote(writeLog.note());
        logService.writeLog(log);
    }
}

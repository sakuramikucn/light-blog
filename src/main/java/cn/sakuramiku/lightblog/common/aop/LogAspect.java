package cn.sakuramiku.lightblog.common.aop;

import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.IdGenerator;
import cn.sakuramiku.lightblog.entity.Log;
import cn.sakuramiku.lightblog.service.LogService;
import cn.sakuramiku.lightblog.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 写日志
 *
 * @author lyy
 */
@Component
@Aspect
public class LogAspect {

    public static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

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
        long id = IdGenerator.nextId();
        log.setId(id);
        log.setCreateTime(LocalDateTime.now(ZoneId.systemDefault()));
        log.setReference(ref);
        String actionName = action.getName();
        log.setAction(actionName + name);

        // 获取操作人
        try {
            Subject subject = SecurityUtils.getSubject();
            String token = (String)subject.getPrincipal();
            if (StrUtil.isNotBlank(token)) {
                Claims claims = JwtUtil.getClaims(token);
                String userName = (String) claims.get("username");
                String nickname = (String) claims.get("nickName");
                if (!StrUtil.isBlank(nickname)) {
                    userName += "(" + nickname + ")";
                }
                log.setOperator(userName);
            }
        } catch (Exception e) {
            logger.warn("记录日志获取操作人失败");
        }
        if (StringUtils.isEmpty(log.getOperator())){
            log.setOperator("system");
        }
        String method = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        if ("login".equalsIgnoreCase(method)) {
            args[1] = "***";

        }
        try {
            log.setWhat("<b>执行方法：</b>" + method + "，<br><b>方法参数:</b> " + new ObjectMapper().writeValueAsString(args) +
                    ",<br><b>执行结果状态：</b>" + (null == result ? "异常: null" : "正常"));
        } catch (Exception e) {
            logger.warn("记录日志失败，日志ID{}", id);
        }
        log.setNote(writeLog.note());
        logService.writeLog(log);
    }
}

package cn.sakuramiku.lightblog.common.aop;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.AspectUtil;
import cn.sakuramiku.lightblog.common.util.IdGenerator;
import cn.sakuramiku.lightblog.entity.Log;
import cn.sakuramiku.lightblog.service.LogService;
import cn.sakuramiku.lightblog.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

    static ObjectMapper objectMapper = new ObjectMapper();
    static{
        // datetime 序列化、反序列化
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA));
        LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss",Locale.CHINA));
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(localDateTimeSerializer);
        javaTimeModule.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
        objectMapper.registerModule(javaTimeModule);
    }

    @AfterReturning(value = "@annotation(writeLog)", returning = "result", argNames = "joinPoint,result,writeLog")
    public void writeLog(JoinPoint joinPoint, Object result, WriteLog writeLog) {

        // 获取 类上注解LogConfig信息
        Class<?> clazz = joinPoint.getTarget().getClass();
        LogConfig logConfig = clazz.getAnnotation(LogConfig.class);
        String ref = null != logConfig ? logConfig.reference() : "";
        String category = null != logConfig ? logConfig.category() : "";
        String name = null != logConfig ? logConfig.name() : StrUtil.isNotBlank(ref) ? ref : "";

        // 获取 方法上注解 WriteLog 信息
        WriteLog.Action action = writeLog.action();
        // 获取方法上的reference的值,方法上的优先级高
        String reference = "";
        String ref2 = writeLog.reference();
        if (!StringUtils.isEmpty(ref2)){
            reference = AspectUtil.parseElValueString(ref2,joinPoint,result);
        }else {
            if (!StringUtils.isEmpty(ref)){
                reference = AspectUtil.parseElValueString(ref, joinPoint, result);
            }
        }

        Log log = new Log();
        long id = IdGenerator.nextId();
        log.setId(id);
        log.setCreateTime(LocalDateTime.now(ZoneOffset.of("+8")));
        String actionName = action.getName();
        log.setAction(actionName + name);
        if (action == WriteLog.Action.Job){
            log.setAction(actionName);
        }
        log.setCategory(category);
        log.setReference(reference);

        // 获取操作人
        try {
            Subject subject = SecurityUtils.getSubject();
            String token = (String) subject.getPrincipal();
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
        if (StringUtils.isEmpty(log.getOperator())) {
            log.setOperator("system");
        }
        String method = joinPoint.getSignature().toString();
        Object[] args = joinPoint.getArgs();
        if ("login".equalsIgnoreCase(joinPoint.getSignature().getName())) {
            args[1] = "***";

        }
        try {
            StrBuilder builder = new StrBuilder();
            builder.append("<b>执行方法：</b>").append(method).append("，<br>");
            builder.append("<b>方法参数:</b> ").append(objectMapper.writeValueAsString(args)).append("，<br>");
            if (writeLog.result() && null != result){
                builder.append("<b>执行结果：</b>");
                builder.append(objectMapper.writeValueAsString(result));
            }
            log.setWhat( builder.toString());
        } catch (Exception e) {
            logger.warn("记录日志失败，日志ID{}", id);
        }
        log.setNote(writeLog.note());
        logService.writeLog(log);
    }
}

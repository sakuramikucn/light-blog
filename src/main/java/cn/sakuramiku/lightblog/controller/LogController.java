package cn.sakuramiku.lightblog.controller;

import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.entity.Log;
import cn.sakuramiku.lightblog.service.LogService;
import cn.sakuramiku.lightblog.vo.SearchLogParam;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 日志模块方法集
 *
 * @author lyy
 */
@Api(tags = "日志模块方法集")
@CrossOrigin("*")
@RestController
@RequestMapping("/log")
public class LogController {

    @Resource
    private LogService logService;

    @RequiresAuthentication
    @ApiOperation("搜索日志")
    @PostMapping("/search")
    public Result<PageInfo<Log>> logs(@RequestBody SearchLogParam param) {
        String category = param.getCategory();
        String ref = param.getRef();
        LocalDateTime begin = param.getBegin();
        LocalDateTime end = param.getEnd();
        Integer page = param.getPage();
        Integer pageSize = param.getPageSize();
        PageInfo<Log> logs = logService.searchLog(category, ref, begin, end, page, pageSize);
        return RespResult.ok(logs);
    }
}

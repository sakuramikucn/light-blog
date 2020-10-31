package cn.sakuramiku.lightblog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页相关的请求处理
 * @author lyy
 */
@RestController
public class IndexController {

    @GetMapping("/")
    public String hello(){
        return "hello lightblog";
    }
}

package com.example.autooccupation.scheduled;

import com.example.autooccupation.bean.OccupyUser;
import com.example.autooccupation.bean.Result;
import com.example.autooccupation.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @Author iCHuang
 * @Date 2022/4/10 16:24
 */
@Component
@Slf4j
public class ScheduledTask {

    @Autowired
    private AutoService autoService;


    //  21：34开始任务  开始占座    200ms一次
    @Scheduled(cron = "${task.cron.start}")
    public void regularlyPerform() {
        autoService.startTask();
        log.info("定时抢座任务全部开始");
    }

    //  21：36开始任务 结束占座
    @Scheduled(cron = "${task.cron.end}")
    public void stopRegularlyPerform() {
        autoService.stopTask();
        log.info("抢座任务全部结束");
    }

    @Scheduled(cron = "${task.cron.cookie}")
    public void getCookie() {
        String cookie = autoService.requestCookie();
        if (cookie != null) {
            autoService.setCookie(cookie);
        }
    }

}

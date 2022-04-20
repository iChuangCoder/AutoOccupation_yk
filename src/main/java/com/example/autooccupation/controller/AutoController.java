package com.example.autooccupation.controller;

import com.example.autooccupation.bean.AutoBean;
import com.example.autooccupation.bean.Result;
import com.example.autooccupation.scheduled.ScheduledTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @Author iCHuang
 * @Date 2022/4/11 21:55
 */
@RestController()
@Slf4j
public class AutoController {

    @Autowired
    private ScheduledTask scheduledTask;


    @PostMapping("/auto")
    public Result autoTask(@RequestBody AutoBean autoBean) {
        if (checkParam(autoBean)) {
            return Result.fail("参数有误。");
        }
        if (autoBean.getEnable() == null)
            autoBean.setEnable(true);
        return Result.suss(scheduledTask.automaticSeat(autoBean));
    }

    @DeleteMapping("/auto")
    public String enable(@RequestBody AutoBean autoBean) {
        String sqid = autoBean.getSqid();
        if (sqid == null || sqid.length() != 32) return "sqid错误";
         return scheduledTask.setEnable(sqid);
    }

    @GetMapping("/auto")
    public HashMap getAll() {
        return scheduledTask.getAllTask();
    }

    @PostMapping("/addtesk")
    public Result addtesk(AutoBean autoBean) {

        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        if (hour != 21 || minute < 10) {
            return Result.fail("21:10分可以开始提交任务！");
        }
        if (autoBean.getEnable() == null)
            autoBean.setEnable(true);
        scheduledTask.addTesk(autoBean);
        log.info("side:{}添加成功", autoBean.getSqid());
        return Result.suss("添加成功。");
    }

    public boolean checkParam(AutoBean autoBean) {
        String sqid,cookie,seatid;
        if (autoBean == null ||( sqid = autoBean.getSqid()) == null || sqid.length() != 32
                || (cookie = autoBean.getCookie()) == null || !cookie.contains("Auth-Token")
                || (seatid = autoBean.getSeatid()) == null || seatid.length() != 32) {
            return false;
        }
        for (int i = 0; i < sqid.length(); i++) {
            char c = sqid.charAt(i);
            char c1 = seatid.charAt(i);
            if (!(((c >= 48 && c <=57) || (c >=65 && c <= 90)) &&
                    ((c1 >= 48 && c1 <=57) || (c1 >=97 && c1 <= 122)))) return false;
        }
        return true;
    }

}

package com.example.autooccupation.controller;

import com.example.autooccupation.bean.AutoBean;
import com.example.autooccupation.bean.Result;
import com.example.autooccupation.scheduled.ScheduledTask;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private static ArrayList<String> seatList;

    static {
        String s = "[\n" +
                "  \"91743dadd31e4d7e9533d2c7a43c7de4\",\n" +
                "  \"542d311d12434c498c7e71d358a36d6f\",\n" +
                "  \"4c8e5f44453d4890ae5a2dad3fce7dd2\",\n" +
                "  \"aca947fc804e4df3af70fd17359fe62e\",\n" +
                "  \"505b73044a5347aaba230453dbe5c7e7\",\n" +
                "  \"736b18a4084d43dd9f022f9053eff75f\",\n" +
                "  \"7d992217564041fd993631913b6eff2d\",\n" +
                "  \"4814b2b3eddb47108b7316ae23f33b0c\",\n" +
                "  \"8cc2b5fab30e46149c867cd4fe1562c3\",\n" +
                "  \"ca5fbe4d31f743c1b79a2beb16364f09\",\n" +
                "  \"8e8d9f73c7124667bed477f25c130ccf\",\n" +
                "  \"3e652401893346989ee3df96980e17d7\",\n" +
                "  \"26号没有位置。\",\n" +
                "  \"e6ef26c3c01948189b947768a6445a69\",\n" +
                "  \"dc429e303d74496097cdbfa498a56232\",\n" +
                "  \"67559ba2922349c793b20592a1f988f3\",\n" +
                "  \"291fbf44e0bc4ea09af27d8cdfde8c5d\",\n" +
                "  \"04495eafc48c4a7eac47051ef69ae911\",\n" +
                "  \"6b66109ee7f1406f88077d6313585101\",\n" +
                "  \"2b34fad8dbb14aaca0785a0885d98cbd\",\n" +
                "  \"b19f7a8f707e4f05b7c1eaf928ad410d\",\n" +
                "  \"c02e72a9da0a4614b378b17e37869677\",\n" +
                "  \"763089f9a15544659234f66b0d232715\",\n" +
                "  \"de63bbab367e429ab2b7e516eb4facef\",\n" +
                "  \"d1387086ad7c43dc85b9f2d7f1234e36\",\n" +
                "  \"89122f557cee4b3f9a71d149e3d48be2\",\n" +
                "  \"435ff2321a5d49b78277be50394d871e\",\n" +
                "  \"89ee458f5ed94d6e951f571a50860462\",\n" +
                "  \"1b6db4d5bd474e4da0f49a9fa2fd3cfd\",\n" +
                "  \"605c346be4a34365b952df94e372aec2\",\n" +
                "  \"92cc9e1d6a484b53aca497ec0cee71a5\",\n" +
                "  \"8bd67bbc8f6a434381716a3aa2e782b9\",\n" +
                "  \"2e845577524b4274a4bc1fd9ba97bab5\",\n" +
                "  \"8ac4bfd6d7734f30bbf95305b355d8ab\",\n" +
                "  \"3552b4222d07420fb1abb888733a1a5e\",\n" +
                "  \"994dbb76db2448b7b60c19ac27a0606b\"\n" +
                "]";
        ObjectMapper om = new ObjectMapper();
        try {
            seatList = om.readValue(s, ArrayList.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    @PostMapping("/auto")
    public Result autoTask(@RequestBody AutoBean autoBean) {
        if (!checkParam(autoBean)) {
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
        if (!checkTime()) {
            return Result.fail("21:10分可以开始提交任务！");
        }
        if (!checkParam(autoBean)) {
            return Result.fail("参数有误。");
        }
        if (autoBean.getEnable() == null)
            autoBean.setEnable(true);
        Result result = scheduledTask.addTesk(autoBean);
        return result;
    }

    @GetMapping("/getcookie")
    public Result getcookie() {
        if (!checkTime()) {
            return Result.fail("21:10分可以开始提交任务！");
        }
        String cookie = scheduledTask.getCookie();
        if (cookie.equals("")) return Result.fail("请手动添加cookie。");
        return Result.suss(cookie);
    }

    @GetMapping("/getseat")
    public Result getseat(@RequestParam Integer seatId) {
        if (!checkTime()) {
            return Result.fail("21:10分可以开始提交任务！");
        }
        if (seatId == null || seatId > 72 || seatId < 0 || seatId % 2 == 1)
            return Result.fail("只能选A416 2-72 双数座位");
        return Result.suss(seatList.get((seatId / 2) - 1));
    }

    public boolean checkParam(AutoBean autoBean) {
        String sqid, cookie, seatid;
        if (autoBean == null || (sqid = autoBean.getSqid()) == null || sqid.length() != 32
                || (cookie = autoBean.getCookie()) == null || !cookie.contains("Auth-Token")
                || (seatid = autoBean.getSeatid()) == null || seatid.length() != 32) {
            return false;
        }
        for (int i = 0; i < sqid.length(); i++) {
            char c = sqid.charAt(i);
            char c1 = seatid.charAt(i);
            if (!(((c >= 48 && c <= 57) || (c >= 65 && c <= 90)) &&
                    ((c1 >= 48 && c1 <= 57) || (c1 >= 97 && c1 <= 122)))) return false;
        }
        return true;
    }

    public boolean checkTime() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        if (hour == 21 && minute >= 10 && minute <= 35) return true;
        return false;
    }

}

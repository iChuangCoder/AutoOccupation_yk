package com.example.autooccupation.controller;

import com.example.autooccupation.bean.OccupyUser;
import com.example.autooccupation.bean.Result;
import com.example.autooccupation.scheduled.ScheduledTask;
import com.example.autooccupation.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.autooccupation.utils.SystemCheck.checkParam;
import static com.example.autooccupation.utils.SystemCheck.checkTime;

/**
 * @Author iCHuang
 * @Date 2022/4/11 21:55
 */
@RestController
@Slf4j
public class AutoController {


    @Autowired
    private AutoService service;


    @GetMapping("/auto")
    public Map<String, OccupyUser> getAll() {
        return service.getUserMap();
    }

    @PostMapping("/addtask")
    public Result addtask(OccupyUser occupyUser) {
        if (!checkParam(occupyUser)) {
            return Result.fail("参数有误。");
        }
        if (!service.checkSeat(occupyUser.getSeatNum())) {
            return Result.fail("座位已被占。");
        }
        service.addUser(occupyUser);
        return Result.suss("添加成功!");
    }

    @GetMapping("/admin")
    public Result admin() {
        ConcurrentHashMap<String, OccupyUser> userMap = service.getUserMap();
        Result result = new Result();
        result.setData(userMap.values());
        return result;
    }

    @PostMapping("/setSeatId")
    public Result setSeatId(@RequestBody OccupyUser occupyUser) {
        if (occupyUser == null|| occupyUser.getName() == null)
            return Result.fail("参数异常。");
        if (!service.checkSeat(occupyUser.getSeatNum())) {
            return Result.fail("座位错误，或者已被占用。");
        }
        service.setUserSeatId(occupyUser);
        return Result.suss(service.getUserMap().values(), occupyUser.getName() + "开启占座" + occupyUser.getSeatNum() + "号,操作成功。");
    }

    @PostMapping("/changeEnable")
    public Result changeEnable(@RequestBody OccupyUser occupyUser) {
        if (occupyUser.getSeatNum() == null)
            return Result.fail("请先选中座位。");
        if (occupyUser.getEnable() == null || occupyUser.getName() == null) {
            return Result.fail("请刷新重试。");
        }
        String name = occupyUser.getName();
        service.getUserMap().get(name).setEnable(occupyUser.getEnable().booleanValue());
        String msg = name + (occupyUser.getEnable() ? "开启" : "暂停") + "抢座任务";
        log.info(msg);
        return Result.suss(service.getUserMap().values(), msg);
    }

//    @GetMapping("/admin")
//    public ModelAndView admin(ModelAndView mv) {
//        mv.setViewName("admin");
//        mv.addObject("users",service.getUserMap());
//        return mv;
//    }

//    @PostMapping("/setSeatId")
//    public String setSeatId(@RequestBody OccupyUser occupyUser, ModelAndView mv) {
//        if (occupyUser.getSeatId() == null || occupyUser.getName() == null) {
//            if (!service.checkSeat(occupyUser.getSeatNum())) {
//                return Result.fail("座位错误，或者已被占用。");
//            }
//        }
//        service.setUserSeatId(occupyUser);
//        return "admin::users_table";
//        return Result.suss("操作成功");
//    }


//    @GetMapping("/getseat")
//    public Result getseat(@RequestParam Integer seatId) {
//        if (!checkTime()) {
//            return Result.fail("还不可以开始提交任务！");
//        }
//        if (seatId == null || seatId > 72 || seatId <= 0)
//            return Result.fail("只能选A416 1-72座位");
//        return Result.suss(seatList.get(seatId - 1));
//    }


}

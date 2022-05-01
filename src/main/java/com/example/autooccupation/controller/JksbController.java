package com.example.autooccupation.controller;

import com.example.autooccupation.bean.JksbBean;
import com.example.autooccupation.bean.Result;
import com.example.autooccupation.scheduled.JksbAutoTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JksbController {

    @Autowired
    private JksbAutoTask jksbAutoTask;

    @GetMapping("jksb")
    public String goJksbHtml() {
        return "jksb";
    }

    @PostMapping("autojksb")
    @ResponseBody()
    public Result autojksb(JksbBean bean) {
        if (bean.getToken().length() != 32 || bean.getUserId().length() != 32)
            return Result.fail("参数有误。");
        return jksbAutoTask.addTask(bean);
    }

    @GetMapping("jksbtask")
    @ResponseBody
    public Object getjksbtask() {
        return jksbAutoTask.getAll();
    }
}

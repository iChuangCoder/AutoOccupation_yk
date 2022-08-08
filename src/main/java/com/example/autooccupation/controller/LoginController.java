package com.example.autooccupation.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController()
public class LoginController {

    @Value("${login.admin.username}")
    private String u;
    @Value("${login.admin.password}")
    private String p;

    @RequestMapping("/login/{username}/{password}")
    public String login(@PathVariable String username,@PathVariable String password, HttpSession session) {
        if (username != null && password != null && username.equals(u) && password.equals(p)) {
            session.setAttribute("auth-token","admin");
            return "ok";
        } else return "fail";
    }

}

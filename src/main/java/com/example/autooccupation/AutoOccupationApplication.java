package com.example.autooccupation;

import com.example.autooccupation.service.AutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AutoOccupationApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AutoOccupationApplication.class, args);
        AutoService autoService = context.getBean(AutoService.class);
        String cookie = autoService.requestCookie();
        autoService.setCookie(cookie);


    }

}

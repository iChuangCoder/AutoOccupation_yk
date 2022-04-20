package com.example.autooccupation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * @Author iCHuang
 * @Date 2022/4/10 16:24
 */
@Configuration
public class HttpConfig {


    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }


}

package com.example.autooccupation;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @Author iCHuang
 * @Date 2022/4/14 22:56
 */
@SpringBootTest
public class Tes {
    @Autowired
    RestTemplate restTemplate;

    @Test
    public void t1() {
        String url = "https://ujnpl.educationgroup.cn/xsgl/stdYy/saveZw?sqid=DC4100DCAFF3BFC3E0530C96CE0A9F53";
        String cookie = "SmartUserRole=; HWWAFSESID=468757f04bd53a246b; HWWAFSESTIME=1649942232047; sid=12; surl=ujnpl; loginCode=9b523b0c92185f39a0da77a82c51b46a; serverUID=8fcb9f26eba73828d140221ce08de818; Auth-Token=d1ed27610fb4e06e039083cc7a337338";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Cookie", cookie);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        ResponseEntity responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        System.out.println(responseEntity);
    }


}

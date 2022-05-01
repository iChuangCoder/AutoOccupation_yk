package com.example.autooccupation.scheduled;

import com.example.autooccupation.bean.JksbBean;
import com.example.autooccupation.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service
@Slf4j
public class JksbAutoTask {

    @Autowired
    private RestTemplate restTemplate;
    private HashSet<JksbBean> tasks = new HashSet<>();
    public void sendReq(String userId, String token) {
        String url = "http://ujnpl.educationgroup.cn/jksb/tb/save";
        int hour = LocalDateTime.now().getHour();
        String param = "";
        if (hour >= 0 && hour <= 9) {
            param = "setid=48bbe6ec8524453d93302b35adec2004&userid=" + userId + "&id=&ticket=&zx=%E6%98%AF&zx_select=%E6%98%AF&wxwz=%E5%B1%B1%E4%B8%9C%E7%9C%81%E7%83%9F%E5%8F%B0%E5%B8%82%E8%93%AC%E8%8E%B1%E5%8C%BA%E8%93%AC%E8%8E%B1%E9%98%81%E8%A1%97%E9%81%93%E6%B5%B7%E6%BB%A8%E8%A5%BF%E8%B7%AF&tw=36.6&ks=%E5%90%A6&ks_select=%E5%90%A6&jkm=&xcm=&gtjz1=&gtjz5=";
        } else if (hour >= 10 && hour <= 14) {
            param = "setid=c9c0ef9b16fb45f6b9f0cfab0b9a03fc&userid=" + userId + "&id=&ticket=&sfzx=%E6%98%AF&sfzx_select=%E6%98%AF&wxwz=%E5%B1%B1%E4%B8%9C%E7%9C%81%E7%83%9F%E5%8F%B0%E5%B8%82%E8%93%AC%E8%8E%B1%E5%8C%BA%E8%93%AC%E8%8E%B1%E9%98%81%E8%A1%97%E9%81%93%E6%B5%B7%E6%BB%A8%E8%A5%BF%E8%B7%AF&tw=36.5&zz=%E5%90%A6&zz_select=%E5%90%A6&jkm=&xcm=&gtjz=&gtjzr2=";
        } else {
            param = "setid=704efaef6b6b41c7ad9ebf3ab1ee5bd3&userid=" + userId + "&id=&ticket=&sfzx=%E6%98%AF&sfzx_select=%E6%98%AF&wxwz=%E5%B1%B1%E4%B8%9C%E7%9C%81%E7%83%9F%E5%8F%B0%E5%B8%82%E8%93%AC%E8%8E%B1%E5%8C%BA%E8%93%AC%E8%8E%B1%E9%98%81%E8%A1%97%E9%81%93%E6%B5%B7%E6%BB%A8%E8%A5%BF%E8%B7%AF&tw=36.6&ks2=%E5%90%A6&ks2_select=%E5%90%A6&gtjz1=&gtjz3=&gtjz=&gtjz333=";
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Cookie", "Auth-Token=" + token + "; sid=12; surl=ujnpl; SmartUserRole=; HWWAFSESID=c691a736a161ed6bb8; HWWAFSESTIME=1651282609258");
        HttpEntity<String> httpEntity = new HttpEntity(param, httpHeaders);
        String s = restTemplate.postForObject(url, httpEntity, String.class);
        String[] split = s.split("\n");
        if (split != null && split.length < 61) {
            log.error("userId:{} ->> 提交失败",userId);
        }
        log.info("userId:{} ->>{}",userId,split[61]);
    }

    @Scheduled(cron = "00 30 8,11,18 * * ?")
    public void autoSend() {
        for (JksbBean task : tasks) {
            sendReq(task.getUserId(),task.getToken());
        }
    }

    public Result addTask(JksbBean bean) {
        boolean add = tasks.add(bean);
        if (add) {
            return Result.suss("添加成功。");
        } else {
            return Result.fail("不能重复添加。");
        }
    }

    public HashSet<JksbBean> getAll() {
        return tasks;
    }
}

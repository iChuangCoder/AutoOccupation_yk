package com.example.autooccupation.scheduled;

import com.example.autooccupation.bean.AutoBean;
import com.example.autooccupation.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @Author iCHuang
 * @Date 2022/4/10 16:24
 */
@Component
@Slf4j
public class ScheduledTask {

    private static final String URL_PREFIX = "https://ujnpl.educationgroup.cn/xsgl/stdYy/saveZw?sqid=";
    private HashMap<String,AutoBean> tasks = new HashMap<>();
    private ScheduledThreadPoolExecutor refreshCookiePool = new ScheduledThreadPoolExecutor(10);
    private ScheduledThreadPoolExecutor grabAPool = new ScheduledThreadPoolExecutor(10);
    private String cookie = "";
    @Autowired
    RestTemplate restTemplate;

    public synchronized String getCookie() {
        return cookie;
    }

    // 刷新cookie 20m一次
    public synchronized String automaticSeat(AutoBean autoBean) {
        if (tasks.get(autoBean.getSqid())!=null) {
            log.info("sqid:{}已经提交任务",autoBean.getSqid());
            tasks.remove(autoBean.getSqid());
            tasks.put(autoBean.getSqid(),autoBean);
            return "该用户已经提交任务";
        }
        Runnable task = getTask(autoBean);
        if (task == null) return "检查参数创建请求失败。";
        try {
            refreshCookiePool.scheduleAtFixedRate(task, 0, 20, TimeUnit.MINUTES);
            log.info("Cookie刷新任务启动：sqid:{},cookie:{},seatid{}", autoBean.getSqid(), autoBean.getCookie(), autoBean.getSeatid());
            tasks.put(autoBean.getSqid(),autoBean);
        } catch (Exception e) {
            log.error("err:",e);
        }
        return "添加任务成功。";
    }

    //  21：34开始任务  开始占座    200ms一次
//    @Scheduled(cron = "0 49,51 ** * * ?")
    @Scheduled(cron = "30 34 21 * * ?")
    public void regularlyPerform() {
        Set<String> keySet = tasks.keySet();
        for (String sqid : keySet) {
            AutoBean autoBean = tasks.get(sqid);
            if (!autoBean.getEnable()) continue;
            Runnable task = getTask(autoBean);
            grabAPool.scheduleAtFixedRate(task, 0, 200, TimeUnit.MILLISECONDS);
            log.info("占座任务启动：sqid:{},cookie:{},seatid{}", autoBean.getSqid(), autoBean.getCookie(), autoBean.getSeatid());
        }
        log.info("定时任务全部开始");
    }

    //  21：36开始任务 结束占座
//    @Scheduled(cron = "0 50,52 ** * * ?")
    @Scheduled(cron = "30 35 21 * * ?")
    public void remove() {
        grabAPool.shutdownNow();
        grabAPool = new ScheduledThreadPoolExecutor(10);
        tasks = new HashMap<>();
        cookie = "";
        log.info("定时任务全部结束");
    }


    private Runnable getTask(AutoBean autoBean) {
        if (autoBean == null) return null;
        String cookie = autoBean.getCookie();
        String seatid = autoBean.getSeatid();
        String sqid = autoBean.getSqid();
        if (sqid == null || cookie == null || seatid == null) return null;
        //请求url
        String url = URL_PREFIX + sqid;
        //请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpHeaders.set("Cookie", cookie);
        // 参数
        String param = "seatid=" + seatid;
        HttpEntity<String> httpEntity = new HttpEntity(param, httpHeaders);
        Thread task = new Thread(() -> {
            String res = restTemplate.postForObject(url, httpEntity, String.class);
            if (res == null) {
                log.error("sqid:{}请求参数异常,请求返回：{},缺少cookie参数", sqid, res);
                tasks.remove(sqid);
                throw new RuntimeException(res);
            }
            String[] split = res.split("\n");
            if (split == null || split.length < 63) {
                log.error("sqid:{}请求参数异常,请求返回：{}", sqid, res);
                tasks.remove(sqid);
                throw new RuntimeException(res);
            }
            log.info(sqid + Thread.currentThread().getName() + split[63]);
        },"线程-"+sqid);
        return task;
    }

    public String setEnable(String sqid) {
        AutoBean autoBean = tasks.get(sqid);
        if (autoBean == null) {
            log.info("sqid:{},该用户还未添加到任务",sqid);
            return "该用户还未添加到任务！";
        }
        autoBean.setEnable(!autoBean.getEnable());
        tasks.put(sqid,autoBean);
        String res = autoBean.getEnable() ? "开启自动占座任务！" : "取消自动占座任务。";
        log.info("sqid:{},{}",sqid,res);
        return res;
    }

    public HashMap getAllTask() {
        return tasks;
    }

    public synchronized Result addTesk(AutoBean autoBean) {
        if (cookie.equals("")) {
            cookie = autoBean.getCookie();
        }
        autoBean.setEnable(true);
        for (AutoBean value : tasks.values()) {
            if (value.getSeatid().equals(autoBean.getSeatid())) {
                log.info(autoBean.getSeatid() + "座位已被选中。");
                return Result.fail("座位已被选中。");
            }
        }
        tasks.put(autoBean.getSqid(),autoBean);
        log.info(autoBean  + " : 添加成功。");
        return Result.fail(autoBean.getSqid() +":添加成功。");
    }
}

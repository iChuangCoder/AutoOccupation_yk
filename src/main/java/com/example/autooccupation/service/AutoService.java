package com.example.autooccupation.service;

import com.example.autooccupation.bean.OccupyUser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Base64;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Data
public class AutoService {

    @Autowired
    private RestTemplate restTemplate;
    // A416 seatList
    private Properties seatMap = new Properties();
    // match userName
    private Properties nameMap = new Properties();
    // 验证码识别
    private Tesseract tessreact;
    // auth cookie
    private String cookie;
    // all user map
    private ConcurrentHashMap<String, OccupyUser> userMap = new ConcurrentHashMap<>();
    // task thread pool
    private ScheduledThreadPoolExecutor taskPoll;
    // pool max size
    private int poolSize;
    // get Auto-Token
    private static final String LOGIN_URL = "https://ujnpl.educationgroup.cn/sso/auth/login";
    // request verification-code url get auth-token
    private static final String CODE_IMAGE_URL = "https://ujnpl.educationgroup.cn/sso/auth/genCode";
    // 占座url
    private static final String TASK_URL = "https://ujnpl.educationgroup.cn/xsgl/stdYy/saveZw?sqid=";
    // auth username
    private String username;
    // auth password
    private String password;
    // auth max count
    private int count = 10;
    private String matchNameFilePath;
    private String seatIdFilePath;

    public AutoService(@Value("${file.seatid}") String seatIdFilePath
            , @Value("${file.matchName}") String matchNameFilePath
            , @Value("${auth.username}") String username,
                       @Value("${auth.password}") String password,
                       @Value("${task.poolSize}") int poolSize) throws IOException {
        // 读取文件
        this.seatIdFilePath = seatIdFilePath;
        ClassPathResource resource = new ClassPathResource(seatIdFilePath);
//        File file = ResourceUtils.getFile(seatIdFilePath);
//        InputStream inputStream = new FileInputStream(file);
        InputStream inputStream = resource.getInputStream();
        seatMap.load(inputStream);
        this.matchNameFilePath = matchNameFilePath;
//        file = ResourceUtils.getFile(matchNameFilePath);
//        inputStream = new FileInputStream(file);
        resource = new ClassPathResource(matchNameFilePath);
        inputStream = resource.getInputStream();
        nameMap.load(inputStream);
        for (Object s : nameMap.keySet()) {
            userMap.put((String) s, new OccupyUser((String) s, (String) nameMap.get(s)));
        }
        log.info("tessdata datapath ===>{}", System.getenv("TESSDATA_PREFIX"));
        tessreact = new Tesseract();
        //认证密码编码
        this.username = Base64.getEncoder().encodeToString(username.getBytes());
        this.password = Base64.getEncoder().encodeToString(password.getBytes());
        //线程池初始化
        this.poolSize = poolSize;
        taskPoll = new ScheduledThreadPoolExecutor(poolSize);
        inputStream.close();
//        cookie = requestCookie();
    }

    /**
     * @param code      验证码
     * @param loginCode 验证码绑定cookie信息
     */
    private String loginGetCookie(String code, String loginCode) {
        // 构建请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Cookie", loginCode);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 请求体
        StringBuilder sb = new StringBuilder();
        sb.append("loginType=account&usertel=&usertel_code=&username=")
                .append(username).append("&password=").append(password)
                .append("&code=").append(code);
        HttpEntity httpEntity = new HttpEntity(sb.toString(), httpHeaders);
        try {
            ResponseEntity entity = restTemplate.exchange(LOGIN_URL, HttpMethod.POST, httpEntity, String.class);
            log.info("获取Auth-Token失败：" +
                    "参数：{}::{}", code, loginCode);
            return null;
        } catch (Exception e) {
            if (e instanceof HttpServerErrorException) {
                HttpServerErrorException e1 = (HttpServerErrorException) e;
                String authToken = e1.getResponseHeaders().get("Set-Cookie").get(2);
                log.info("获取Auth-Token成功：{}", authToken);
                return authToken;
            } else {
                log.info("获取Auth-Token失败：" +
                        "参数：{}::{}", code, loginCode);
                return null;
            }

        }
    }

    public static void main(String[] args) throws IOException {
//        HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://ichuang.xyz:8888/a.zip").openConnection();
//        InputStream inputStream = urlConnection.getInputStream();
//        byte[] bytes = new byte[1024 * 1024];
//        int read = inputStream.read(bytes);
//        FileOutputStream outputStream = new FileOutputStream(new File("D:\\a.zip"));
//        outputStream.write(bytes,0,read);


        HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://ichuang.xyz:8888/a.zip").openConnection();
        FileInputStream inputStream = (FileInputStream) urlConnection.getInputStream();
        FileChannel channel = inputStream.getChannel();

    }


    public String requestCookie() {
        HttpURLConnection urlConnection = null;
        try {
            while (true) {
                urlConnection = (HttpURLConnection) new URL(CODE_IMAGE_URL).openConnection();
                urlConnection.setRequestMethod("GET");
                InputStream inputStream = urlConnection.getInputStream();
                BufferedImage codeImage = ImageIO.read(inputStream);
                String code = tessreact.doOCR(codeImage);
                if (code.length() != 5) continue;
                code = code.substring(0, 4);
                String loginCode = urlConnection.getHeaderFields().get("Set-Cookie").get(2);
                log.info("获取验证码成功：{}，loginCode：{}", code, loginCode);
                String authToken = loginGetCookie(code, loginCode);
                if (authToken != null) {
                    count = 10;
                    return authToken;
                } else {
                    if (count-- > 0) {
                        return requestCookie();
                    } else {
                        // TODO 发送邮件通知
                        return null;
                    }
                }
            }
        } catch (IOException | TesseractException e) {
            e.printStackTrace();
            count--;
            return count > 0 ? requestCookie() : null;
        }
    }

    public void startTask() {
        for (String name : userMap.keySet()) {
            OccupyUser occupyUser = userMap.get(name);
            if (occupyUser.getEnable() != null && occupyUser.getEnable() != false) addTask(occupyUser);
        }
    }

    public void stopTask() {
        taskPoll.shutdownNow();
        taskPoll = new ScheduledThreadPoolExecutor(poolSize);
    }

    public void addUser(OccupyUser occupyUser) {
        occupyUser.setEnable(true);
        occupyUser.setSeatId(seatMap.getProperty(occupyUser.getSeatNum()));
        userMap.put(occupyUser.getName(), occupyUser);
        try {
            if (nameMap.get(occupyUser.getName()) == null) {
                nameMap.setProperty(occupyUser.getName(), occupyUser.getSqid());
                FileOutputStream fileOutputStream = new FileOutputStream(new File(matchNameFilePath));
                nameMap.store(fileOutputStream, null);
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("添加人员成功：姓名{}，座位{}", occupyUser.getName(), occupyUser.getSeatNum());
    }


    private void addTask(OccupyUser occupyUser) {
        taskPoll.scheduleAtFixedRate(getTask(occupyUser), 0, 200, TimeUnit.MILLISECONDS);
        log.info("{}=====>>{}", occupyUser.getName(), occupyUser.getSeatNum());
    }

    private Runnable getTask(OccupyUser occupyUser) {
        if (occupyUser == null) return null;
        String cookie = getCookie();
        String seatid = occupyUser.getSeatId();
        String sqid = occupyUser.getSqid();
        if (sqid == null || cookie == null || seatid == null) return null;
        //请求url
        String url = TASK_URL + sqid;
        //请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Cookie", cookie);
        // 参数
        String param = "seatid=" + seatid;
        HttpEntity<String> httpEntity = new HttpEntity(param, httpHeaders);
        Thread task = new Thread(() -> {
            String res = restTemplate.postForObject(url, httpEntity, String.class);
            if (res == null) {
                log.error("sqid:{}请求参数异常,请求返回：{},缺少cookie参数", sqid, res);
                throw new RuntimeException(res);
            }
            String[] split = res.split("\n");
            if (split == null || split.length < 63) {
                log.error("sqid:{}请求参数异常,请求返回：{}", sqid, res);
                throw new RuntimeException(res);
            }
            log.info(sqid + Thread.currentThread().getName() + split[63]);
        }, "线程-" + sqid);
        return task;
    }

    public boolean checkSeat(String seatNum) {
        for (String s : userMap.keySet()) {
            if (userMap.get(s).getSeatNum() != null && userMap.get(s).getSeatNum().equals(seatNum)) {
                if (userMap.get(s).getEnable())
                    return false;
            }
        }
        return true;
    }

    public void setUserSeatId(OccupyUser occupyUser) {
        String seatNum = occupyUser.getSeatNum();
        if (seatNum == null) return;
        String seatId = (String) seatMap.get(seatNum);
        occupyUser.setSeatId(seatId);
        OccupyUser onUser = userMap.get(occupyUser.getName());
        onUser.setSeatNum(occupyUser.getSeatNum());
        onUser.setSeatId(occupyUser.getSeatId());
        onUser.setEnable(true);
        log.info("{}开启占座{}号。", occupyUser.getName(), occupyUser.getSeatNum());
    }
}

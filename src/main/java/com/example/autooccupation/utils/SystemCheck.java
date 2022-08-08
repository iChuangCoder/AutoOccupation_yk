package com.example.autooccupation.utils;

import com.example.autooccupation.bean.OccupyUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class SystemCheck {
    public SystemCheck(@Value("${task.submit.time}") String patten) {
        SystemCheck.patten = patten;
    }

    private static String patten;

    public static boolean checkParam(OccupyUser occupyUser) {
        if (occupyUser == null) return false;
        String sqid = occupyUser.getSqid(), seatNum = occupyUser.getSeatNum();
        if(sqid == null || seatNum == null ) return false;
        if (sqid.length() != 32) return false;
        try {
            int num = Integer.parseInt(seatNum);
            if (num < 0 || num > 72) return false;
        } catch (Exception e) {
            return false;
        }
        for (int i = 0; i < sqid.length(); i++) {
            char c = sqid.charAt(i);
            if (!(((c >= 48 && c <= 57) || (c >= 65 && c    <= 90)))) return false;
        }
        return true;
    }


    public static boolean checkTime() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        String[] split = patten.split(":");
        int h = Integer.parseInt(split[0]);
        int m = Integer.parseInt(split[1]);
        int m2 = Integer.parseInt(split[2]);
        if (hour == h && minute >= m && minute <= m2) return true;
        return false;
    }
}

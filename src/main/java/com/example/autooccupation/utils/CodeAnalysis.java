package com.example.autooccupation.utils;
import com.example.autooccupation.scheduled.ScheduledTask;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeAnalysis {
    public static boolean search(String regex,StringBuilder stringBuilder)
    {
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(stringBuilder);
        while(matcher.find())
        {
            return true;
        }
        return false;
    }
}

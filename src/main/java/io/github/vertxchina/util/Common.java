package io.github.vertxchina.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Common {
    public static String HandlerDate(String time){
        if (time.length() == 0){
            return ZonedDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        try {
            Date msgTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(time);
            SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
            String msgData = dateSdf.format(msgTime);
            String nowData = dateSdf.format(new Date());
            if (msgData.equals(nowData)){
                time = new SimpleDateFormat("HH:mm:ss").format(msgTime);
            }else{
                time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msgTime);
            }
            return time;
        } catch (ParseException e) {
            return time;
        }
    }
}

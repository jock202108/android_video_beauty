package org.lasque.twsdkvideo.video_beauty.utils;

import java.util.Date;

public class TimeUtils {

    public static String duration(long duration) {
        String time = "" ;

        long minute = duration / 60000 ;
        long seconds = duration % 60000 ;

        long second = Math.round((float)seconds/1000) ;

//        if( minute < 10 ){
//            time += "0" ;
//        }
        time += minute+":" ;

        if( second < 10 ){
            time += "0" ;
        }
        time += second ;

        return time ;
    }


}

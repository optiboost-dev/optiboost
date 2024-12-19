package org.dev.optiboost;

import java.text.DecimalFormat;

public class Utils {
    public static String calculateSize(Double size){
        if(size < 1024){
            return String.format("%.2f B", size);
        }else if(size < 1024 * 1024){
            return String.format("%.2f KB", size / 1024);
        }else if(size < 1024 * 1024 * 1024){
            return String.format("%.2f MB", size / 1024 / 1024);
        }else{
            return String.format("%.2f GB", size / 1024 / 1024 / 1024);
        }
    }

    public static String formatDouble(double value) {
        // 保留一位小数，如果没有小数则显示整数
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(value);
    }
}

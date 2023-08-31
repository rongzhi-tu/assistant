package com.bsoft.assistant.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Ycl
 * @Description:
 * @Date Created in 2022/10/24  17:44.
 */
public class IdCreateUtils {
    private static final Map<String, Integer> suffixMap = new ConcurrentHashMap<String, Integer>();
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMddHH24mmssSSS");

    public synchronized static String createId(String ds) {
        int suffix = 0;
        if (suffixMap.containsKey(ds)) {
            suffix = suffixMap.get(ds);
            if (suffix < 999999) {
                suffix++;
            } else {
                suffix = 100001;
            }
        } else {
            suffix = 100001;
        }
        suffixMap.put(ds, suffix);
        String idTemp = ds + DATE_FORMATTER.format(new Date()) + suffix;
        return idTemp;
    }
}

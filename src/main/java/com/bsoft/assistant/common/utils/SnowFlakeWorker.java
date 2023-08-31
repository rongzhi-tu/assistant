package com.bsoft.assistant.common.utils;

import cn.hutool.core.util.RandomUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Properties;

public class SnowFlakeWorker {

    // 开发时间(这里使用2019年04月01日整点)
    private final static long START_TIME = 1554048000000l;
    // 数据中心编码所占位数
    private final static long DATA_CENTER_BITS = 10l;
    // 最大数据中心编码
    private final static long MAX_DATA_CENTER_ID = 1023;
    // 序列编号占位位数
    private final static long SEQUENCE_BIT = 12l;
    // 数据中心编号向左移12位
    private final static long DATA_CENTER_SHIFT = SEQUENCE_BIT;
    // 时间戳向左移22位
    private final static long TIMESTAMP_SHIFT = DATA_CENTER_SHIFT + DATA_CENTER_BITS;
    // 最大生成序列号
    private final static long MAX_SEQUENCE = 4095;

    //数据中心ID(0-1023)
    private long dataCenter = 36L;
    //毫秒内序列(0-4095)
    private long sequence = 0L;
    //  上次生成ID的时间戳
    private long lastTimestamp = -1L;
    // 序列值前缀
    private String prefixSequence;

    private static final SnowFlakeWorker idWorker = new SnowFlakeWorker(prefixSeq(), getDataCenterId());
    ;


    public SnowFlakeWorker(String prefixSequence, long dataCenter) {
        if (dataCenter > MAX_DATA_CENTER_ID)
            throw new RuntimeException(String.format("数据中心编号[ %d ]超过最大允许值[ %d ]", dataCenter, MAX_DATA_CENTER_ID));
        if (dataCenter < 0)
            throw new RuntimeException(String.format("数据中心编号[ %d ]不允许小于0", dataCenter));
        this.dataCenter = dataCenter;
        this.prefixSequence = prefixSequence;
    }

    public static synchronized String generateId() {
        return idWorker.nextId();
    }

    /**
     * 获取下一个id
     *
     * @return
     */
    public synchronized String nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp)
            throw new RuntimeException(String.format("clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        if (timestamp == lastTimestamp) {
            sequence += 1;
            if (sequence > MAX_SEQUENCE) {
                sequence = 0;
                timestamp = tilNextMillis(timestamp);
            }
        } else {
            lastTimestamp = timestamp;
            sequence = 0;
        }
        long result = ((timestamp - START_TIME) << TIMESTAMP_SHIFT)
                | (this.dataCenter << DATA_CENTER_SHIFT)
                | sequence;
        return prefixSequence + result;
    }


    /**
     * 阻塞到下一毫秒,直到获取新的时间戳
     *
     * @param lastTimestamp 上次生成id的时间戳
     * @return
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp;
        do {
            timestamp = System.currentTimeMillis();
        } while (timestamp > lastTimestamp);
        return timestamp;
    }

    private static String prefixSeq() {
        String projectName = "BI";
        byte[] bytes = projectName.getBytes();
        int computeVal = bytes[0] + bytes[1];
        computeVal = computeVal >> 1;
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();

            return fillAddress(hostAddress, computeVal);
        } catch (UnknownHostException e) {
            Properties properties = new Properties();
            try (InputStream is = SnowFlakeWorker.class.getClassLoader().getResourceAsStream("bip.properties")) {
                properties.load(is);
            } catch (IOException ioException) {

            }
            String ipAddress = properties.getProperty("host.ipAddress");
            return fillAddress(ipAddress, computeVal);
        }

    }


    private static String fillAddress(String address, int projectVal) {
        String[] addrSplit = address.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (String val : addrSplit) {
            String newVal = val.length() == 1 ? ("00" + val) : val.length() == 2 ? ("0" + val) : val;
            sb.append(newVal);
        }
        sb.append(projectVal);
        return sb.toString();
    }

    private static long getWorkId() {
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] ints = toCodePoints(hostAddress);
            int sums = 0;
            for (int b : ints) {
                sums += b;
            }
            return (long) (sums % 1024);
        } catch (UnknownHostException e) {
            return RandomUtil.randomLong(0, 1024);
        }

    }

    private static Long getDataCenterId() {
        int[] ints = toCodePoints(SystemUtils.IS_OS_WINDOWS ? System.getenv("COMPUTERNAME") : System.getenv("HOSTNAME"));
        int sums = 0;
        for (int b : ints) {
            sums += b;
        }
        return (long) (sums % 1024);
    }


    private static int[] toCodePoints(final CharSequence cs) {
        if (cs == null)
            return null;
        if (cs.length() == 0)
            return ArrayUtils.EMPTY_INT_ARRAY;
        String s = cs.toString();
        int[] result = new int[s.codePointCount(0, s.length())];
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            result[i] = s.codePointAt(index);
            index += Character.charCount(result[i]);
        }
        return result;
    }

}

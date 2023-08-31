package com.bsoft.assistant.common.utils;

import java.net.InetAddress;
import java.util.StringTokenizer;

/**
 * Created by lujy on 2019/1/11.
 */
public class ValidIPUtil {

    /**
     * 校验Ip格式 这是模仿js校验ip格式，使用java做的判断
     *
     * @param str
     * @return
     */
    public static boolean checkIp(String str) {
        String[] ipValue = str.split("\\.");
        if (ipValue.length != 4) {
            return false;
        }
        for (int i = 0; i < ipValue.length; i++) {
//            String temp = ipValue[i];
            try {
                // java判断字串是否整数可以用此类型转换异常捕获方法，也可以用正则 var regu = /^\d+$/;
                Integer q = Integer.valueOf(ipValue[i]);
                if (q > 255) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验Ip地址是否合法
     *
     * @param addr
     * @return
     */
    public static boolean isIpValid(String addr) {
        String[] ipStr = new String[4];
        int[] ipb = new int[4];
        StringTokenizer tokenizer = new StringTokenizer(addr, ".");
        int len = tokenizer.countTokens();

        if (len != 4) {
            return false;
        }
        try {
            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                ipStr[i] = tokenizer.nextToken();
                ipb[i] = (new Integer(ipStr[i])).intValue();

                if (ipb[i] < 0 || ipb[i] > 255) {
                    return false;
                }
                i++;
            }
            if (ipb[0] > 0) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 将byte[4] 数组转换成int 型
     *
     * @param b
     * @param len
     * @return
     */
    public static int byteToInt(byte[] b, int len) {
        final int LOWBYTE = 0x000000ff;
        int i;
        if (len < 1 || len > 4) {
            len = 4;
        }
        int[] temp = new int[len];
        for (i = 0; i < len; i++) {
            temp[i] = (int) b[i];
            temp[i] &= LOWBYTE;
            temp[i] <<= ((len - 1 - i) * 8);
        }
        for (i = 1; i < len; i++) {
            temp[0] |= temp[i];
        }
        return temp[0];
    }

    /**
     * 将ip地址从String转换成Int型
     *
     * @param addr
     * @return
     */
    public static int covtAddrToInt(String addr) {
        try {
            InetAddress ip = InetAddress.getByName(addr);
            byte[] ipb = new byte[4];
            ipb = ip.getAddress();
            int tIp = byteToInt(ipb, 4);
            return tIp;
        } catch (java.net.UnknownHostException e) {
            return 0;
        }
    }

    /**
     * 将ip地址从int型转换成String
     *
     * @param ip
     * @return
     */
    public static String covtAddrToStr(int ip) {
        if (ip == 0) {
            return new String("0.0.0.0");
        }
        long newIp;
        if (ip < 0) {
            newIp = ((long) 256) * ((long) 256) * ((long) 256) * ((long) 256);
            newIp = newIp + ip;
        } else {
            newIp = (long) ip;
        }
        long yushu = newIp % 256;
        newIp = newIp / 256;
        String tmpIp = Long.toString(yushu);
        for (int i = 0; i < 3; i++) {
            yushu = newIp % 256;
            newIp = newIp / 256;
            tmpIp = Long.toString(yushu) + "." + tmpIp;

        }
        return tmpIp;
    }

    /**
     * 优化输入的Ip值，如将"01.02.03.04"转化为"1.2.3.4";
     * 注：此方法仅提供优化，不提供校验，请使用合法的Ip格式字串调用
     *
     * @param oldIpStr 将被优化的ip值
     *                 必须确保传过来的Ip值已经符合Ip格式规范
     * @return 返回转化后的Ip值
     */
    public String suitableIp(String oldIpStr) {
        String newIp = "";
        // 分离为数组
        String[] tmpStr = oldIpStr.split("\\.");
        for (int i = 0; i < tmpStr.length; i++) {
            newIp += Integer.valueOf(tmpStr[i]);

            if (i < tmpStr.length - 1) {
                newIp += ".";
            }
        }
        return newIp;
    }

}

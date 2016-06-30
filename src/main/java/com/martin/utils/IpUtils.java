package com.martin.utils;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @ClassName: IpUtils
 * @Description: ip 工具类
 * @author wjj
 * @date 2016年4月19日 下午4:34:24
 */
public class IpUtils {

    /**
     * @Description: 获取登录用户的IP地址
     * @param request
     * @return ip 地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.equals("0:0:0:0:0:0:0:1")) {
            ip = "127.0.0.1";
        }
        if (ip.split(",").length > 1) {
            ip = ip.split(",")[0];
        }
        return ip;
    }

    //根据掩码判断ip是否在某IP地址段内 clientIp=101.226.103.255 ipList=101.226.103.0/25
    public static boolean isIpInList(String clientIp, String ipList) {
        String[] ips = clientIp.split("\\.");
        int ipAddress = (Integer.parseInt(ips[0]) << 24) | (Integer.parseInt(ips[1]) << 16) | (Integer.parseInt(ips[2]) << 8) | Integer.parseInt(ips[3]);
        int type = Integer.parseInt(ipList.replaceAll(".*/", ""));
        int mask = 0xFFFFFFFF << (32 - type);
        String cIp = ipList.replaceAll("/.*", "");
        String[] cIps = cIp.split("\\.");
        int cIpAddress = (Integer.parseInt(cIps[0]) << 24) | (Integer.parseInt(cIps[1]) << 16) | (Integer.parseInt(cIps[2]) << 8) | Integer.parseInt(cIps[3]);

        return (ipAddress & mask) == (cIpAddress & mask);
    }

    public static void main(String[] args) {
        System.out.println(IpUtils.isIpInList("101.226.103.0", "101.226.103.0/24"));
    }


}

package com.martin.filter;

import com.martin.bean.ResultInfo;
import com.martin.utils.IpUtils;
import com.martin.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName: BlacklistFilter
 * @Description: IP地址黑名单、白名单过滤器
 * @author ZXY
 * @date 2016/6/21 10:53
 */
public class IPFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String whitelist;

    private String blacklist;

    public void setWhitelist(String whitelist) {
        this.whitelist = whitelist;
    }

    public void setBlacklist(String blacklist) {
        this.blacklist = blacklist;
    }

    /**
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ipAddress = IpUtils.getIpAddress(request);
        String urlPath = request.getRequestURI();

        //在黑名单
        if (ipAddress.matches(blacklist)) {
            logger.error("访问uri-{}", urlPath);
            logger.error("IP地址-{}在黑名单中", ipAddress);
            String json = JsonUtils.translateToJson(new ResultInfo(-1, "", "无访问权限"));
            response.getWriter().write(json);
            return;
        }

        if (ipAddress.matches(whitelist)) {
            //在白名单
            logger.info("白名单访问IP-{},-{}", urlPath, ipAddress);
            filterChain.doFilter(request, response);
        } else {
            logger.error("访问uri-{}", urlPath);
            logger.error("IP地址-{}不在白名单中", ipAddress);
            String json = JsonUtils.translateToJson(new ResultInfo(-1, "", "已报警，等待110来接您"));
            response.getWriter().write(json);
            return;
        }
    }
}

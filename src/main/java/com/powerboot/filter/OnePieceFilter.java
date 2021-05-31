package com.powerboot.filter;

import com.powerboot.base.BaseResponse;
import com.powerboot.common.JsonUtils;
import com.powerboot.consts.DictConsts;
import com.powerboot.consts.I18nEnum;
import com.powerboot.consts.TipConsts;
import com.powerboot.utils.RedisUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

/**
 * @description : 全局拦截器
 **/
@Order(1)
@WebFilter(filterName = "onePieceFilter", urlPatterns = "/*")
public class OnePieceFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(OnePieceFilter.class);

    public static void outPrint(HttpServletResponse response, String msg) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.write(JsonUtils.toJSONString(BaseResponse.fail(msg)));
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            logger.error("拦截器返回参数异常", e);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletRequest.setCharacterEncoding("UTF-8");
        String uri = httpServletRequest.getRequestURI();

        //        if (StringUtils.isNotEmpty(RedisUtils.getValue(getIp(httpServletRequest),String.class))){
        //            this.outPrint(httpServletResponse, "The operation is too fast, please try again!");
        //            return;
        //        }
        //
        //        RedisUtils.setValue(getIp(httpServletRequest),"1",2);

        String userIp = getIp(httpServletRequest);
        String openIpBlack = RedisUtils.getValue(DictConsts.OPEN_IP_BLACK_LIST, String.class);
        //是否开启IP黑名单
        if (StringUtils.isNotBlank(openIpBlack) && "1".equals(openIpBlack)) {
            //IP黑名单拦截
            String ipBlackJson = RedisUtils.getValue(DictConsts.IP_BLACK_LIST, String.class);
            if (StringUtils.isNotBlank(ipBlackJson)) {
                List<String> ipBlackList = Arrays.asList(ipBlackJson.split("[|]"));
                if (ipBlackList.contains(userIp)) {
                    this.outPrint(httpServletResponse, I18nEnum.IP_BLACK_LIST_TIP.getMsg());
                    return;
                }
            }
        }

        //swagger安全配置
        if ("/swagger-ui.html".equals(uri)) {
            //是否开启swagger访问
            String swaggerOpen = RedisUtils.getValue("swaggerOpen", String.class);
            if (StringUtils.isNotBlank(swaggerOpen) && "1".equals(swaggerOpen)) {
                String swaggerIPList = RedisUtils.getValue("swaggerIPList", String.class);
                if (StringUtils.isNotBlank(swaggerIPList)) {
                    List<String> ipList = Arrays.asList(swaggerIPList.split(","));
                    //是否在白名单中
                    if (!ipList.contains(userIp)) {
                        this.outPrint(httpServletResponse, I18nEnum.IP_BLACK_LIST_TIP.getMsg());
                    }
                } else {
                    this.outPrint(httpServletResponse, I18nEnum.IP_BLACK_LIST_TIP.getMsg());
                }
            } else {
                this.outPrint(httpServletResponse, I18nEnum.IP_BLACK_LIST_TIP.getMsg());
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }

    public String getIp(HttpServletRequest request) {

        try {
            String ip = request.getHeader("x-forwarded-for");
            String[] array = ip.split(",");
            ip = array[0];
            return ip;
        } catch (Exception e) {
            //logger.error("获取唯一ip异常,异常::",e);
        }
        String ip = request.getHeader("X-real-ip");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.length() > 0 && ip.trim().length() > 0) {
            ip = ip.split(",")[0];
        }
        return ip;
    }

}

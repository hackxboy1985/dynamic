package cn.zhishu.core.filter;


import cn.zhishu.core.datasource.RoutingDataSourceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * @description
 * @author: fanxl
 * @date: 2018/9/26 0026 13:36
 */
@Slf4j
public class TimeFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("[MsDynamic][TimeFilter] filter初始化:{}",filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        long start = System.currentTimeMillis();
        log.info("filter start");

        String as = request.getParameter("as");
        String ds = request.getParameter("ds");
        log.info("as: {} {}", as, ds);
        if (as == null) {
            throw new InvalidParameterException("as不能为空");
        } else {

            RoutingDataSourceContext.setDataSourceProductKey(as);
            RoutingDataSourceContext.setThreadLocalDataSourceKey(ds);
            filterChain.doFilter(request, response);
            RoutingDataSourceContext.clearThreadLocalDataSourceKey();
        }
        log.info("filter end, time=" + (System.currentTimeMillis() - start));
    }

    @Override
    public void destroy() {
        log.info("filter销毁");
    }
}

package cn.zhishu.core.aspect;

import cn.zhishu.core.common.annotation.DataSource;
import cn.zhishu.core.datasource.RoutingDataSourceContext;
import cn.zhishu.core.logger.MsLogger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @description
 * @author: fanxl
 * @date: 2018/9/26 0026 13:31
 */
@Aspect
@Component
public class RoutingAspect {

    private static final MsLogger LOG = MsLogger.getLogger(RoutingAspect.class);

    @Pointcut("@annotation(cn.zhishu.core.common.annotation.DataSource)")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        DataSource ds = method.getAnnotation(DataSource.class);

        String dataSource = ds.value();
        if (StringUtils.isEmpty(dataSource)) {
            RoutingDataSourceContext.setThreadLocalDataSourceDefaultKey();
            LOG.info("[MsDynamic][RoutingAspect]annotation set datasource is null, use datasource : default ");
        } else {
            RoutingDataSourceContext.setThreadLocalDataSourceKey(dataSource);
            LOG.info("[MsDynamic][RoutingAspect]annotation use datasource : {}", dataSource);
        }

        try {
            return point.proceed();
        } finally {
            RoutingDataSourceContext.clearThreadLocalDataSourceKey();
            /*            LOG.debug("clear datasource...");
             */
        }

    }


}

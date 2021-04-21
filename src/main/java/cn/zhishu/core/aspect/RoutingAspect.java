package cn.zhishu.core.aspect;

import cn.zhishu.core.common.annotation.DataSource;
import cn.zhishu.core.datasource.RoutingDataSourceContext;
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

    private static Logger LOG = LoggerFactory.getLogger(RoutingAspect.class);

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
//            DynamicDataSource.setDataSource(DataSourceEnum.MASTER.getName());
//            RoutingDataSourceContext.setThreadLocalDataSourceKey(RoutingDataSourceContext.SUID_DS_DEFAULT_KEY);
            LOG.debug("set datasource is null, use datasource : {}", dataSource);
        } else {
            RoutingDataSourceContext.setThreadLocalDataSourceKey(dataSource);
            LOG.debug("use datasource : {}", dataSource);
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

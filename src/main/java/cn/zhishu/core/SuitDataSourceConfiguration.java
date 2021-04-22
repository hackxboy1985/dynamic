package cn.zhishu.core;


import cn.zhishu.core.datasource.RoutingDataSource;
import cn.zhishu.core.datasource.RoutingDataSourceContext;
import cn.zhishu.core.datasource.SuitAcquireImplement;
import cn.zhishu.core.datasource.SuitAcquireInterface;
import cn.zhishu.core.filter.ProductFilter;
import cn.zhishu.core.logger.MsLogger;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnProperty(value = {"spring.datasource.type"}, havingValue = "cn.zhishu.core.datasource.RoutingDataSource", matchIfMissing = false)
public class SuitDataSourceConfiguration {
    private static final Logger log = LoggerFactory.getLogger(SuitDataSourceConfiguration.class);

    @Bean
    RoutingDataSource routingDataSource(){
        log.info("[MsDynamic][SuitDataSourceConfiguration] 创建RoutingDataSource");
        RoutingDataSource rds = new RoutingDataSource();
        return rds;
    }

    @Bean
    JdbcTemplate jdbcTemplate(){
        log.info("[MsDynamic][SuitDataSourceConfiguration] 创建jdbcTemplate");
        DruidDataSource dataSource = RoutingDataSource.getDruidDataSource(RoutingDataSourceContext.getMainKey());
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }

    @Bean
    SuitAcquireInterface suitAcquireImplement(RoutingDataSource routingDataSource, JdbcTemplate jdbcTemplate){
        log.info("[MsDynamic][SuitDataSourceConfiguration] 创建SuitAcquireInterface");
        SuitAcquireInterface suitAcquireImplement = new SuitAcquireImplement(jdbcTemplate);
        routingDataSource.setSuitAcquireInterface(suitAcquireImplement);
        return suitAcquireImplement;
    }


    @Bean
    ProductFilter timeFilter(){
        log.info("[MsDynamic][SuitDataSourceConfiguration] 创建TimeFilter");
        return new ProductFilter();
    }


    @Value("${spring.datasource.msdynamic_log_enabled:false}")
    Boolean enabled;

    @PostConstruct
    void init(){
        log.info("[MsDynamic]多账套数据源日志开关:{}",enabled);
        MsLogger.setEnabled(enabled);
    }
}

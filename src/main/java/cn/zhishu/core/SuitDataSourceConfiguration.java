package cn.zhishu.core;


import cn.zhishu.core.datasource.RoutingDataSource;
import cn.zhishu.core.datasource.RoutingDataSourceContext;
import cn.zhishu.core.datasource.SuitAcquireImplement;
import cn.zhishu.core.datasource.SuitAcquireInterface;
import cn.zhishu.core.filter.TimeFilter;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Configuration
@ConditionalOnProperty(value = {"spring.datasource.type"}, havingValue = "cn.zhishu.core.datasource.RoutingDataSource", matchIfMissing = false)
public class SuitDataSourceConfiguration {

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
    TimeFilter timeFilter(){
        log.info("[MsDynamic][SuitDataSourceConfiguration] 创建TimeFilter");
        return new TimeFilter();
    }

}

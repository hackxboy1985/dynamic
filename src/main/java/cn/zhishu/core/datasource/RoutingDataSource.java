package cn.zhishu.core.datasource;

import cn.zhishu.core.Utils;
import cn.zhishu.core.entity.SuitDataSource;
import cn.zhishu.core.logger.MsLogger;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description
 * @author: fanxl
 * @date: 2018/9/26 0026 11:27
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

    private static final MsLogger log = MsLogger.getLogger(RoutingDataSource.class);

    private JdbcTemplate jdbcTemplate;


    @Value("${spring.datasource.url}")
    private String main_url;
    @Value("${spring.datasource.username}")
    private String main_username;
    @Value("${spring.datasource.password}")
    private String main_password;

    //账套的缺省数据源名
    @Value("${spring.datasource.suit-default-ds-name:slaveDataSourceRead}")
    private String suit_default_ds_name;

    private static Map<Object, Object> dataSources = new HashMap<>();

    private SuitAcquireInterface suitAcquireInterface;


    @PostConstruct
    public void RoutingDataSourceInit() {
        log.info("[MsDynamic][RoutingDataSource]初始化主数据源");
        if (StringUtils.isEmpty(main_url) || StringUtils.isEmpty(main_username)){
            throw new RuntimeException("[MsDynamic]spring.datasource.url or spring.datasource.username is null");
        }
        createAndSaveDataSource(RoutingDataSourceContext.getMainKey());

        RoutingDataSourceContext.setSuitDsDefaultKey(suit_default_ds_name);
    }

    public RoutingDataSource() {
//        log.info("[MsDynamic][RoutingDataSource]RoutingDataSource Constructor");
    }

    public void setSuitAcquireInterface(SuitAcquireInterface suitAcquireInterface){
        this.suitAcquireInterface = suitAcquireInterface;
    }


    @Override
    protected Object determineCurrentLookupKey(){
        String currentAccountSuit = RoutingDataSourceContext.getDataSourceRoutingKey();
        if (StringUtils.isEmpty(currentAccountSuit)) {
            throw new RuntimeException("[MsDynamic]CurrentSuit["+ currentAccountSuit +"] error!!!");
        }
        log.info("[MsDynamic][RoutingDataSource] 当前操作账套:{}", currentAccountSuit);
        Utils.traceStack();
        if (!dataSources.containsKey(currentAccountSuit)){
           log.info("[MsDynamic][RoutingDataSource] {}数据源不存在, 创建对应的数据源", currentAccountSuit);
            createAndSaveDataSource(currentAccountSuit);
        } else {
            //log.info("{}数据源已存在不需要创建", currentAccountSuit);
        }
        log.info("[MsDynamic][RoutingDataSource] 切换到{}数据源", currentAccountSuit);
        return currentAccountSuit;
    }

    private synchronized void createAndSaveDataSource(String currentAccountSuit) {
        DruidDataSource dataSource = createDataSource(currentAccountSuit);
        checkDs(dataSource);
        dataSources.put(currentAccountSuit, dataSource);
        super.setTargetDataSources(dataSources);
        afterPropertiesSet();
        log.info("[MsDynamic][RoutingDataSource] {}数据源创建成功", currentAccountSuit);
    }

    private void checkDs(DruidDataSource dataSource){
        try{
            dataSource.init();
        }catch (SQLException e){
//            throw e;
            log.error(e.getMessage(),e);
            throw new RuntimeException("数据库连接异常");
        }
    }

    /**
     * 创建数据源
     * @param currentAccountSuit
     * @return
     */
    DruidDataSource createDataSource(String currentAccountSuit) {
        SuitDataSource suitDataSource;
        if (currentAccountSuit.equalsIgnoreCase(RoutingDataSourceContext.getMainKey())) {
            suitDataSource = new SuitDataSource();
            suitDataSource.setName(RoutingDataSourceContext.MAIN_KEY);
            suitDataSource.setDbindex(RoutingDataSourceContext.MAIN_KEY);
            suitDataSource.setUrl(main_url);
            suitDataSource.setUsername(main_username);
            suitDataSource.setPassword(main_password);
        } else {
            suitDataSource = getFanDataSource(currentAccountSuit);
        }
        if (suitDataSource == null) {
            throw new InvalidParameterException("账套不存在");
        }
        return createDruidDataSource(suitDataSource);
    }

    /**
     * 通过jdbc从数据库中查找数据源配置
     * @param suitname
     * @return
     */
    private SuitDataSource getFanDataSource(String suitname) {
        return suitAcquireInterface.getSuitDataSource(suitname);

//        String sql = "select name, url, username, password from fan_datasource where name = ?";
//        RowMapper<SuitDataSource> rowMapper = new BeanPropertyRowMapper<>(SuitDataSource.class);
//        return jdbcTemplate.queryForObject(sql, rowMapper, name);
    }

    /**
     * 根据配置创建DruidDataSource
     * @param suitDataSource
     * @return
     */
    public static DruidDataSource createDruidDataSource(SuitDataSource suitDataSource) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setName(suitDataSource.getName() + RoutingDataSourceContext.SUIT_SEPERATE + suitDataSource.getDbindex());
        dataSource.setUrl(suitDataSource.getUrl());
        dataSource.setUsername(suitDataSource.getUsername());
        dataSource.setPassword(suitDataSource.getPassword());

        dataSource.setInitialSize(1);
        dataSource.setMaxActive(100);
        dataSource.setMinIdle(1);
        dataSource.setMaxWait(60000);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        // 每十分钟验证一下连接
        dataSource.setTimeBetweenEvictionRunsMillis(600000);
        // 如果连接空闲超过5分钟/1小时就断开
        dataSource.setMinEvictableIdleTimeMillis(60000 * 5);//1 * 60000 * 60
        dataSource.setValidationQuery("select 1 from dual");
        dataSource.setTestWhileIdle(true);
        // 从池中取得链接时做健康检查，该做法十分保守
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);

        dataSource.setConnectionErrorRetryAttempts(1); //重试次数置为0
        dataSource.setBreakAfterAcquireFailure(true); // 这个配置可以跳出循环
//        StatFilter statFilter = new StatFilter();
//        // 运行ilde链接测试线程，剔除不可用的链接
//        dataSource.setMaxWait(-1);
        return dataSource;
    }

    /**
     * 通过账套获取DruidDataSource
     * @param currentAccountSuit
     * @return
     */
    public static DruidDataSource getDruidDataSource(String currentAccountSuit) {
        return (DruidDataSource) dataSources.get(currentAccountSuit);
    }


}

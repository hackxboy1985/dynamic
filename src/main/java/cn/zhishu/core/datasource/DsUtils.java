package cn.zhishu.core.datasource;

import com.alibaba.druid.pool.DruidAbstractDataSource;
import com.alibaba.druid.pool.DruidDataSource;

import java.sql.SQLException;

public class DsUtils {
    RoutingDataSource routingDataSource;

    public void validateMysqlUrl(String ds){
        DruidDataSource dataSource = routingDataSource.createDataSource(ds);
        try {
            DruidAbstractDataSource.PhysicalConnectionInfo connection = dataSource.createPhysicalConnection();
//            dataSource.setFailContinuous(false);
        } catch (SQLException var28) {
//            dataSource.setFailContinuous(true);
        }
    }
}

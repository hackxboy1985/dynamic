package cn.zhishu.core.datasource;

import cn.zhishu.core.entity.SuitDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import java.security.InvalidParameterException;

@Slf4j
public class SuitAcquireImplement implements SuitAcquireInterface{

//    private final static String DS_READ = "slaveDataSourceRead";
//    private final static String DS_WRITE = "slaveDataSourceWrite";


    private JdbcTemplate jdbcTemplate;

    public SuitAcquireImplement(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SuitDataSource getFanDataSource(String suitname) {
        if(StringUtils.isEmpty(suitname) || !suitname.contains(RoutingDataSourceContext.SUIT_SEPERATE))
            throw new InvalidParameterException("账套不存在-"+suitname);
        String[] split = suitname.split(RoutingDataSourceContext.SUIT_SEPERATE);
        String product = split[0];
        String dsindex = split[1];//split.length > 1 ? split[1]: DS_READ;
//        if (!DS_WRITE.equalsIgnoreCase(dsindex)){
//            log.info("[MsDynamic][SuitAcquire]Query Ds {}-{} >> {}-{}",product,dsindex,product,DS_READ);
//            dsindex = DS_READ;
//        }
        String sql = "select name, dbindex, url, username, password from suit_datasource where name= ? and dbindex = ?";
        RowMapper<SuitDataSource> rowMapper = new BeanPropertyRowMapper<>(SuitDataSource.class);
        SuitDataSource suitDataSource = null;
        try {
            suitDataSource = jdbcTemplate.queryForObject(sql, rowMapper, product, dsindex);
        }catch (EmptyResultDataAccessException e){
        }
        if (suitDataSource == null){
            log.info("[MsDynamic][SuitAcquire]Query Ds {}-{} empty! please verity the datasource info in table: suit_datasource",product,dsindex);
            throw new InvalidParameterException(product+RoutingDataSourceContext.SUIT_SEPERATE+dsindex+"-账套不存在,请检查该产品数据库配置");
        }

        return suitDataSource;
    }
}
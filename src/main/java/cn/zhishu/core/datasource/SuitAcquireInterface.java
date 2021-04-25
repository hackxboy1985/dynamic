package cn.zhishu.core.datasource;

import cn.zhishu.core.entity.SuitDataSource;

import java.util.List;

public interface SuitAcquireInterface {

    SuitDataSource getSuitDataSource(String suitname);

    List<SuitDataSource> getSuitProducts();

}

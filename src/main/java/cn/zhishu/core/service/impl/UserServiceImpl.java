package cn.zhishu.core.service.impl;

import cn.zhishu.core.entity.User;
import cn.zhishu.core.dao.UserDao;
import cn.zhishu.core.datasource.RoutingDataSourceContext;
import cn.zhishu.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description
 * @author: fanxl
 * @date: 2018/9/26 0026 11:46
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User getByUsername(String username) {
        return userDao.getByUsername(username);
    }

    @Override
    public boolean copyData2MainByUsername(String username, String as, String ds) {
        RoutingDataSourceContext.clearThreadLocalDataSourceKey();
        User user = getByUsername(username);
        RoutingDataSourceContext.setDataSourceProductKey(as);
        RoutingDataSourceContext.setThreadLocalDataSourceKey(ds);
        return userDao.createUser(user)>0;
    }
}

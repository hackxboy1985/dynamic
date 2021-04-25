package cn.zhishu.core.entity;

import lombok.Data;

import java.util.Date;

/**
 * @description
 * @author: fanxl
 * @date: 2018/9/26 0026 12:15
 */
@Data
public class SuitDataSource {

    private Long id;

    private String name;

    private String dbindex;

    private String db;

    private String url;

    private String username;

    private String password;

    private Date createDate;

    private Date updateDate;

}

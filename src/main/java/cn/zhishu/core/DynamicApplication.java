package cn.zhishu.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @description
 * @author: fanxl
 * @date: 2018/9/26 0026 11:35
 */
@Slf4j
@SpringBootApplication
public class DynamicApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicApplication.class, args);
    }
}

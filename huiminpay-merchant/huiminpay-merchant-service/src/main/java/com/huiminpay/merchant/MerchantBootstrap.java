package com.huiminpay.merchant;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @AUTHOR: yadong
 * @DATE: 2021/8/17 14:51
 * @DESC:
 */
@SpringBootApplication/*(exclude={DataSourceAutoConfiguration.class,
        DruidDataSourceAutoConfigure.class})*/
public class MerchantBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(MerchantBootstrap.class,args);
    }
}

package com.love.example;

import com.love.example.Config.FirstDataSourceConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@MapperScan("com.love.example.mapper")
@SpringBootApplication

@EnableTransactionManagement(proxyTargetClass = true)
@Import(FirstDataSourceConfig.class)
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

}

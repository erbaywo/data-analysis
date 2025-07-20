package com.zy.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//扫描包
@ComponentScan(basePackages = "com.zy")
public class DataUploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataUploadApplication.class, args);
    }

}

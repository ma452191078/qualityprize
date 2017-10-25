package com.majy.zlj.qualityprize;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
@MapperScan("com.majy.zlj.qualityprize.mapper")
public class QualityprizeApplication  extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(QualityprizeApplication.class);
    }
	public static void main(String[] args) {
		SpringApplication.run(QualityprizeApplication.class, args);
	}
}

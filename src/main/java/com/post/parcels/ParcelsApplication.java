package com.post.parcels;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableConfigurationProperties
@EnableAspectJAutoProxy
@EntityScan(basePackages = {"com.post.parcels.model.entity"})
public class ParcelsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParcelsApplication.class, args);
    }

}

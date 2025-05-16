package com.beeja.api.expense;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableMongoAuditing
public class ExpenseManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(ExpenseManagementApplication.class, args);
  }
}

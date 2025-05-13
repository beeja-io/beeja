package com.beeja.api.projectmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableMongoRepositories
@EnableMongoAuditing
public class ProjectManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProjectManagementApplication.class, args);
  }
}

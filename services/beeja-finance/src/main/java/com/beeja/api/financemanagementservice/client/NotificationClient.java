package com.beeja.api.financemanagementservice.client;

import com.beeja.api.financemanagementservice.requests.PayslipEmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "notification-service", url = "${client-urls.notificationService}")
public interface NotificationClient {

  @PostMapping("/v1/mail/send-email")
  void sendEmail(
      @RequestBody PayslipEmailRequest request,
      @RequestHeader("authorization") String authorizationHeader);
}

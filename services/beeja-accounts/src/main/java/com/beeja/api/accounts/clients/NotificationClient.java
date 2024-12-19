package com.beeja.api.accounts.clients;

import com.beeja.api.accounts.requests.NewUserEmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "notification-service", url = "${client-urls.notificationService}")
public interface NotificationClient {

  @PostMapping("/v1/mail/send-email")
  void sendEmail(
      @RequestBody NewUserEmailRequest request,
      @RequestHeader("authorization") String authorizationHeader);
}

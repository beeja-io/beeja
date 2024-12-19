package com.beeja.api.accounts.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "postalClient", url = "https://api.postalpincode.in")
public interface PostalClient {
  @GetMapping("/pincode/{pincode}")
  String getPostalResponseByPincode(@PathVariable("pincode") String pincode);
}

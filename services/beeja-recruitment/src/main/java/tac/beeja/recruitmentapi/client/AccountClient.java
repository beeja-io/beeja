package tac.beeja.recruitmentapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "account-service", url = "${client-urls.accountsService}")
public interface AccountClient {

  @GetMapping("v1/users/email/{email}")
  ResponseEntity<?> getEmployeeByEmail(@PathVariable String email);

  @GetMapping("v1/users/permissions/{permission}")
  ResponseEntity<?> getUsersByPermissionAndOrganization(@PathVariable String permission);

  @GetMapping("v1/users/permission/{employeeId}/{permission}")
  ResponseEntity<Boolean> isEmployeeHasPermission(
      @PathVariable String employeeId, @PathVariable String permission);
}

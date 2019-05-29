package com.cme.budderfly.authenticate.client;

import com.cme.budderfly.authenticate.service.dto.ContactDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@AuthorizedFeignClient(name = "sites")
public interface SitesClient {

    @GetMapping("/api/contacts/by-email/{email}")
    List<ContactDTO> getContactsByEmail(@PathVariable("email") String email);

}

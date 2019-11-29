package com.cme.budderfly.authenticate.client;

import com.cme.budderfly.authenticate.service.dto.ContactDTO;
import com.cme.budderfly.authenticate.service.dto.SiteDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@AuthorizedFeignClient(name = "sites")
public interface SitesClient {

    @GetMapping("/api/contacts/by-contain-email/{email}")
    List<ContactDTO> getContactsByContainEmail(@PathVariable("email") String email);

    @GetMapping("/api/sites/owned-by-contacts/{email}")
    List<SiteDTO> getSitesBySiteContacts(@PathVariable("email") String email);

}

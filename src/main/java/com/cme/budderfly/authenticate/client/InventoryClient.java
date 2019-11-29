package com.cme.budderfly.authenticate.client;

import com.cme.budderfly.authenticate.service.dto.TemplatesDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@AuthorizedFeignClient(name = "inventory")
public interface InventoryClient {

    @GetMapping("/api/templates/for-type/{customerType}")
    TemplatesDTO getTemplateFromCustomerTypeAndTemplateTypeAndTemplateName(@PathVariable("customerType") String customerType, @RequestParam("templateType") String templateType, @RequestParam("templateName") String templateName);

}

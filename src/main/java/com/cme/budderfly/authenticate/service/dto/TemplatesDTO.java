package com.cme.budderfly.authenticate.service.dto;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

public class TemplatesDTO implements Serializable {

    private Long id;

    @NotNull
    private String customerType;

    @NotNull
    private String templateType;

    @NotNull
    private String templateName;

    @Lob
    private String template;

    private Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TemplatesDTO templatesDTO = (TemplatesDTO) o;
        if (templatesDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), templatesDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TemplatesDTO{" +
            "id=" + getId() +
            ", customerType='" + getCustomerType() + "'" +
            ", templateType='" + getTemplateType() + "'" +
            ", templateName='" + getTemplateName() + "'" +
            ", template='" + getTemplate() + "'" +
            "}";
    }

}

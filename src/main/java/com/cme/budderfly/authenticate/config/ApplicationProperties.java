package com.cme.budderfly.authenticate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties specific to Authenticate.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@Component
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private String basePortal;

    public String getBasePortal() {
        return basePortal;
    }

    public void setBasePortal(String basePortal) {
        this.basePortal = basePortal;
    }


}

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

    private String mailZippyyumUsername;
    private String mailZippyyumPassword;
    private String mailZippyyumHost;
    private String basePortal;

    public String getMailZippyyumUsername() {
        return mailZippyyumUsername;
    }

    public void setMailZippyyumUsername(String mailZippyyumUsername) {
        this.mailZippyyumUsername = mailZippyyumUsername;
    }

    public String getMailZippyyumPassword() {
        return mailZippyyumPassword;
    }

    public void setMailZippyyumPassword(String mailZippyyumPassword) {
        this.mailZippyyumPassword = mailZippyyumPassword;
    }

    public String getMailZippyyumHost() {
        return mailZippyyumHost;
    }

    public void setMailZippyyumHost(String mailZippyyumHost) {
        this.mailZippyyumHost = mailZippyyumHost;
    }

    public String getBasePortal() {
        return basePortal;
    }

    public void setBasePortal(String basePortal) {
        this.basePortal = basePortal;
    }

}

package com.cme.budderfly.authenticate.service;

import com.cme.budderfly.authenticate.client.InventoryClient;
import com.cme.budderfly.authenticate.config.ApplicationProperties;
import com.cme.budderfly.authenticate.config.EmailProperties;
import com.cme.budderfly.authenticate.domain.User;

import com.cme.budderfly.authenticate.domain.enumeration.CustomerType;
import com.cme.budderfly.authenticate.security.AuthoritiesConstants;
import com.cme.budderfly.authenticate.service.dto.TemplatesDTO;
import com.cme.budderfly.authenticate.service.mapper.UserMapper;
import io.github.jhipster.config.JHipsterProperties;

import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.annotation.Async;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.util.Locale;

/**
 * Service for sending emails.
 * <p>
 * We use the @Async annotation to send emails asynchronously.
 */
@Service
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";

    private static final String TOKEN = "token";

    private static final String BASE_URL = "baseUrl";

    private static final String EMAIL = "email";

    private final JHipsterProperties jHipsterProperties;

    private final EmailProperties emailProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    private final ApplicationProperties applicationProperties;

    private final UserSiteService userSiteService;

    private final InventoryClient inventoryClient;

    private final UserMapper userMapper;

    public MailService(JHipsterProperties jHipsterProperties, JavaMailSender javaMailSender, InventoryClient inventoryClient, UserMapper userMapper, EmailProperties emailProperties,
            MessageSource messageSource, SpringTemplateEngine templateEngine, ApplicationProperties applicationProperties, UserSiteService userSiteService) {

        this.jHipsterProperties = jHipsterProperties;
        this.emailProperties = emailProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
        this.applicationProperties = applicationProperties;
        this.userSiteService = userSiteService;
        this.inventoryClient = inventoryClient;
        this.userMapper = userMapper;
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml, String from) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.warn("Email could not be sent to user '{}'", to, e);
            } else {
                log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
            }
        }
    }

    @Async
    @Transactional(readOnly = true)
    public void sendEmailFromTemplate(User user, String templateName, String titleKey) throws Exception {
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        String customerType = CustomerType.BUDDERFLY;

        Boolean onlyPortal = Optional.ofNullable(user.getAuthorities())
            .map(authentication -> authentication.stream()
            .allMatch(grantedAuthority -> grantedAuthority.getName().equals(AuthoritiesConstants.PORTAL))).orElse(false);

        if (onlyPortal) { // we need to set what link to use
            templateName += "Portal";
            List<String> sites = userSiteService.getShopsOwnedByUser(user.getLogin());
            if (sites != null && !sites.isEmpty()) {
                if (sites.get(0).contains("SUBW")) {
                    customerType = CustomerType.ZIPPYYUM;
                }
            } else {
                customerType = user.getDefaultPartner();
            }
        }

        TemplatesDTO contentTemplate = inventoryClient.getTemplateFromCustomerTypeAndTemplateTypeAndTemplateName(customerType, "EMAIL_TEMPLATE", templateName);
        TemplatesDTO titleTemplate = inventoryClient.getTemplateFromCustomerTypeAndTemplateTypeAndTemplateName(customerType, "EMAIL_TEMPLATE", titleKey);
        TemplatesDTO fromTemplate = inventoryClient.getTemplateFromCustomerTypeAndTemplateTypeAndTemplateName(customerType, "EMAIL_TEMPLATE", "fromEmail");

        if (contentTemplate == null || titleTemplate == null || fromTemplate == null) {
            throw new Exception("Template does not exist in the DB");
        }

        String subject = titleTemplate.getTemplate();
        String content = contentTemplate.getTemplate().replaceAll("\\{0\\}", user.getLogin());
        content = content.replaceAll("\\{1\\}", user.getResetKey()); // would use MessageFormat but the html has curly braces which is making it fail

        try {
            setEmailProperties(customerType);
            sendEmail(user.getEmail(), subject, content, false, true, fromTemplate.getTemplate());
        } catch (MessagingException  | MailException e) {
            throw e;
        }
    }

    private void setEmailProperties(String customerType) {
        JavaMailSenderImpl javaMailsender = ((JavaMailSenderImpl)javaMailSender);
        if (customerType.equals(CustomerType.BUDDERFLY)) {
            javaMailsender.setUsername(emailProperties.getUsername());
            javaMailsender.setPassword(emailProperties.getPassword());
            javaMailsender.setHost(emailProperties.getHost());
        } else if (customerType.equals(CustomerType.ZIPPYYUM)) {
            javaMailsender.setUsername(applicationProperties.getMailZippyyumUsername());
            javaMailsender.setPassword(applicationProperties.getMailZippyyumPassword());
            javaMailsender.setHost(applicationProperties.getMailZippyyumHost());
        }
    }

    @Async
    public void sendActivationEmail(User user) throws Exception {
        log.debug("Sending activation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "activationEmail", "emailActivationTitle");
    }

    @Async
    public void sendCreationEmail(User user) throws Exception {
        log.debug("Sending creation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "creationEmail", "emailActivationTitle");
    }

    @Async
    public void sendPasswordResetMail(User user) throws Exception {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "passwordResetEmail", "emailResetTitle");
    }

    @Async
    public void sendPortalRegisterationMail(String email, String domain) throws Exception {
        log.debug("Sending registration email to " + email);

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date date = c.getTime();

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("email", email);

        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String jws = Jwts.builder().setSubject(email).addClaims(payload).setHeaderParam("email", email).setExpiration(date).signWith(key).compact();

        Locale locale = Locale.forLanguageTag("en");
        Context context = new Context(locale);
        String customerType = CustomerType.BUDDERFLY;

        if (domain.contains("cadderpillar") || domain.contains("zippyyum")) {
            customerType = CustomerType.ZIPPYYUM;
        }

        TemplatesDTO contentTemplate = inventoryClient.getTemplateFromCustomerTypeAndTemplateTypeAndTemplateName(customerType, "EMAIL_TEMPLATE", "creationEmailPortal");
        TemplatesDTO titleTemplate = inventoryClient.getTemplateFromCustomerTypeAndTemplateTypeAndTemplateName(customerType, "EMAIL_TEMPLATE", "emailActivationTitle");
        TemplatesDTO fromTemplate = inventoryClient.getTemplateFromCustomerTypeAndTemplateTypeAndTemplateName(customerType, "EMAIL_TEMPLATE", "fromEmail");

        if (contentTemplate == null || titleTemplate == null || fromTemplate == null) {
            throw new Exception("Template does not exist in the DB");
        }

        String subject = titleTemplate.getTemplate();

        String content = contentTemplate.getTemplate().replaceAll("\\{0\\}", email);
        content = content.replaceAll("\\{1\\}", jws); // would use MessageFormat but the html has curly braces which is making it fail

        setEmailProperties(customerType);
        sendEmail(email, subject, content, false, true, fromTemplate.getTemplate());
    }
}

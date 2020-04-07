package com.cme.budderfly.authenticate.web.rest;

import com.cme.budderfly.authenticate.client.SitesClient;
import com.cme.budderfly.authenticate.service.dto.SiteDTO;
import com.codahale.metrics.annotation.Timed;

import com.cme.budderfly.authenticate.domain.User;
import com.cme.budderfly.authenticate.repository.UserRepository;
import com.cme.budderfly.authenticate.security.SecurityUtils;
import com.cme.budderfly.authenticate.service.MailService;
import com.cme.budderfly.authenticate.service.UserService;
import com.cme.budderfly.authenticate.service.dto.PasswordChangeDTO;
import com.cme.budderfly.authenticate.service.dto.UserDTO;
import com.cme.budderfly.authenticate.web.rest.errors.*;
import com.cme.budderfly.authenticate.web.rest.vm.KeyAndPasswordVM;
import com.cme.budderfly.authenticate.web.rest.vm.ManagedUserVM;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    private final SitesClient sitesClient;

    public AccountResource(UserRepository userRepository, UserService userService, MailService mailService, SitesClient sitesClient) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.sitesClient = sitesClient;
    }

    /**
     * POST  /register : register the user.
     *
     * @param managedUserVM the managed user View Model
     * @throws InvalidPasswordException 400 (Bad Request) if the password is incorrect
     * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already used
     * @throws LoginAlreadyUsedException 400 (Bad Request) if the login is already used
     */
    @PostMapping("/register")
    @Timed
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) throws Exception {
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
        mailService.sendActivationEmail(user);
    }

    @PostMapping("/register-portal-user")
    @Timed
    @ResponseStatus(HttpStatus.CREATED)
    public void registerPortalAccount(@Valid @RequestBody ManagedUserVM managedUserVM, HttpServletRequest request) {
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        String domain = request.getHeader("referer").toLowerCase();
        userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase()).ifPresent(u -> {throw new LoginAlreadyUsedException();});
        userRepository.findOneByEmailIgnoreCase(managedUserVM.getEmail()).ifPresent(u -> {throw new EmailAlreadyUsedException();});
        User user = userService.registerPortalUser(managedUserVM, managedUserVM.getPassword(), domain);
    }

    /**
     * Registers a user of role Portal- the logic for portal registration is different than a normal user which is why we have this separate endpoint.
     * @param email
     * @param request - to get the domain since we use different email templates based on it
     * @throws Exception - when the template does not exist in the DB
     */
    @PostMapping("/send-register-for-portal")
    @Timed
    public ResponseEntity<String> sendRegisterForPortal(@RequestParam String email, HttpServletRequest request) throws Exception {
        Boolean isValid = true;
        String message = email + " cannot be registered";
        String domain = request.getHeader("referer").toLowerCase();

        Optional<User> user = userRepository.findOneByEmailIgnoreCase(email);
        if (user.isPresent()) {
            throw new EmailAlreadyUsedException();
        }

        try {
            List<SiteDTO> sites = sitesClient.getSitesBySiteContacts(email);
            if (sites == null || sites.isEmpty()) {
                isValid = false;
                message = "Email is not associated with any site";
            }
        } catch ( HystrixRuntimeException e ) {
            isValid = false;
            message = e.getCause().getMessage();
            log.debug(message);
        } catch ( Exception ex ) {
            isValid = false;
            message = ex.getMessage();
            log.debug(message);
        }

        if (isValid){
            mailService.sendPortalRegisterationMail(email, domain);
            return ResponseEntity.ok("success");
        }

        return ResponseEntity.badRequest().body(message);
    }

    /**
     * GET  /activate : activate the registered user.
     *
     * @param key the activation key
     * @throws RuntimeException 500 (Internal Server Error) if the user couldn't be activated
     */
    @GetMapping("/activate")
    @Timed
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new InternalServerErrorException("No user was found for this activation key");
        }
    }

    /**
     * GET  /authenticate : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request
     * @return the login if the user is authenticated
     */
    @GetMapping("/authenticate")
    @Timed
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET  /account : get the current user.
     *
     * @return the current user
     * @throws RuntimeException 500 (Internal Server Error) if the user couldn't be returned
     */
    @GetMapping("/account")
    @Timed
    public UserDTO getAccount() {
        return userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new InternalServerErrorException("User could not be found"));
    }

    /**
     * POST  /account : update the current user information.
     *
     * @param userDTO the current user information
     * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already used
     * @throws RuntimeException 500 (Internal Server Error) if the user login wasn't found
     */
    @PostMapping("/account")
    @Timed
    public void saveAccount(@Valid @RequestBody UserDTO userDTO) {
        final String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new InternalServerErrorException("User could not be found");
        }
        userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
            userDTO.getLangKey(), userDTO.getImageUrl());
    }

    /**
     * POST  /account/change-password : changes the current user's password
     *
     * @param passwordChangeDto current and new password
     * @throws InvalidPasswordException 400 (Bad Request) if the new password is incorrect
     */
    @PostMapping(path = "/account/change-password")
    @Timed
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (!checkPasswordLength(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * POST   /account/reset-password/init : Send an email to reset the password of the user
     *
     * @param mail the mail of the user
     * @throws EmailNotFoundException 400 (Bad Request) if the email address is not registered
     */
    @PostMapping(path = "/account/reset-password/init")
    @Timed
    public void requestPasswordReset(@RequestBody String mail) throws Exception {
       mailService.sendPasswordResetMail(
           userService.requestPasswordReset(mail).orElseThrow(EmailNotFoundException::new));
    }

    /**
     * POST   /account/reset-password/finish : Finish to reset the password of the user
     *
     * @param keyAndPassword the generated key and the new password
     * @throws InvalidPasswordException 400 (Bad Request) if the password is incorrect
     * @throws RuntimeException 500 (Internal Server Error) if the password could not be reset
     */
    @PostMapping(path = "/account/reset-password/finish")
    @Timed
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user =
            userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new InternalServerErrorException("No user was found for this reset key");
        }
    }

    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
            password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
            password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }
}

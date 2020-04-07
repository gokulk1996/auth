package com.cme.budderfly.authenticate.service;

import com.cme.budderfly.authenticate.domain.User;
import com.cme.budderfly.authenticate.domain.enumeration.KafkaDataKeys;
import com.cme.budderfly.authenticate.repository.UserRepository;
import com.cme.budderfly.authenticate.repository.UserSiteRepository;
import com.cme.budderfly.authenticate.service.dto.UserSiteDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class KafkaConsumer {

    private final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    private final UserRepository userRepository;
    private final UserSiteRepository userSiteRepository;
    private final UserSiteService userSiteService;

    private final ObjectMapper objectMapper;

    public KafkaConsumer(UserRepository userRepository, UserSiteRepository userSiteRepository, UserSiteService userSiteService) {
        this.userRepository = userRepository;
        this.userSiteRepository = userSiteRepository;
        this.userSiteService = userSiteService;
        this.objectMapper = new ObjectMapper();
    }

    /*
    Listens for kafka topic OWNER_SYNC
    Called from injobs
    Data is sent as a json string of a HashMap
    */
    @KafkaListener(topics = "OWNER_SYNC")
    public void updateStoreOwners(String data) {
        log.debug("starting KAFKA updateStoreOwners with " + data);

        HashMap<String, Object> map = new HashMap<>();
        List<String> emails = new ArrayList<>();
        String budderflyId = "";

        try {
            map = objectMapper.readValue(data, HashMap.class);
        } catch (IOException e) {
            log.error("Could not convert kafka message to hash map. " + e);
            return;
        }

        if (map.containsKey(KafkaDataKeys.EMAIL.toString())) {
            emails = (List<String>)map.get(KafkaDataKeys.EMAIL.toString());
        }

        if (map.containsKey(KafkaDataKeys.BUDDERFLY_ID.toString())) {
            budderflyId = (String)map.get(KafkaDataKeys.BUDDERFLY_ID.toString());
        }

        for (String email: emails) {
            Optional<User> user = userRepository.findOneByEmailIgnoreCase(email);
            if (!user.isPresent()) {
                log.debug(email + " exists in sites but not authenticate, user did not register yet");
                continue;
            }

            List<String> shops = userSiteRepository.getShopsBasedOnUser(user.get().getLogin());
            if (!shops.contains(budderflyId)) { // we need to add the shop
                UserSiteDTO userSiteDTO = new UserSiteDTO();
                userSiteDTO.setBudderflyId(budderflyId);
                userSiteDTO.setUserId(user.get().getId());
                userSiteService.save(userSiteDTO);
            }
        }
    }

}

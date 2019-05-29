package com.cme.budderfly.authenticate.service.impl;

import com.cme.budderfly.authenticate.service.UserSiteService;
import com.cme.budderfly.authenticate.domain.UserSite;
import com.cme.budderfly.authenticate.repository.UserSiteRepository;
import com.cme.budderfly.authenticate.repository.search.UserSiteSearchRepository;
import com.cme.budderfly.authenticate.service.dto.UserSiteDTO;
import com.cme.budderfly.authenticate.service.mapper.UserSiteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing UserSite.
 */
@Service
@Transactional
public class UserSiteServiceImpl implements UserSiteService {

    private final Logger log = LoggerFactory.getLogger(UserSiteServiceImpl.class);

    private final UserSiteRepository userSiteRepository;

    private final UserSiteMapper userSiteMapper;

    private final UserSiteSearchRepository userSiteSearchRepository;

    public UserSiteServiceImpl(UserSiteRepository userSiteRepository, UserSiteMapper userSiteMapper, UserSiteSearchRepository userSiteSearchRepository) {
        this.userSiteRepository = userSiteRepository;
        this.userSiteMapper = userSiteMapper;
        this.userSiteSearchRepository = userSiteSearchRepository;
    }

    /**
     * Save a userSite.
     *
     * @param userSiteDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public UserSiteDTO save(UserSiteDTO userSiteDTO) {
        log.debug("Request to save UserSite : {}", userSiteDTO);
        UserSite userSite = userSiteMapper.toEntity(userSiteDTO);
        userSite = userSiteRepository.save(userSite);
        UserSiteDTO result = userSiteMapper.toDto(userSite);
        userSiteSearchRepository.save(userSite);
        return result;
    }

    /**
     * Get all the userSites.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserSiteDTO> findAll(Pageable pageable) {
        log.debug("Request to get all UserSites");
        return userSiteRepository.findAll(pageable)
            .map(userSiteMapper::toDto);
    }


    /**
     * Get one userSite by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public UserSiteDTO findOne(Long id) {
        log.debug("Request to get UserSite : {}", id);
        UserSite userSite = userSiteRepository.findOne(id);
        return userSiteMapper.toDto(userSite);
    }

    @Override
    public List<String> getShopsOwnedByUser(String login) {
        log.debug("Request to get shops by user " + login);
        List<String> userSites = userSiteRepository.getShopsBasedOnUser(login);

        return userSites;
    }

    /**
     * Delete the userSite by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserSite : {}", id);
        userSiteRepository.delete(id);
        userSiteSearchRepository.delete(id);
    }

    /**
     * Search for the userSite corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserSiteDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of UserSites for query {}", query);
        return userSiteSearchRepository.search(queryStringQuery(query), pageable)
            .map(userSiteMapper::toDto);
    }
}

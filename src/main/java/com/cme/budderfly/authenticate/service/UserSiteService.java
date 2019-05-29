package com.cme.budderfly.authenticate.service;

import com.cme.budderfly.authenticate.service.dto.UserSiteDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing UserSite.
 */
public interface UserSiteService {

    /**
     * Save a userSite.
     *
     * @param userSiteDTO the entity to save
     * @return the persisted entity
     */
    UserSiteDTO save(UserSiteDTO userSiteDTO);

    /**
     * Get all the userSites.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<UserSiteDTO> findAll(Pageable pageable);


    /**
     * Get the "id" userSite.
     *
     * @param id the id of the entity
     * @return the entity
     */
    UserSiteDTO findOne(Long id);

    List<String> getShopsOwnedByUser(String login);

    /**
     * Delete the "id" userSite.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the userSite corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<UserSiteDTO> search(String query, Pageable pageable);
}

package com.cme.budderfly.authenticate.web.rest;
import com.cme.budderfly.authenticate.service.UserSiteService;
import com.cme.budderfly.authenticate.web.rest.errors.BadRequestAlertException;
import com.cme.budderfly.authenticate.web.rest.util.HeaderUtil;
import com.cme.budderfly.authenticate.web.rest.util.PaginationUtil;
import com.cme.budderfly.authenticate.service.dto.UserSiteDTO;
import com.cme.budderfly.authenticate.service.dto.UserSiteCriteria;
import com.cme.budderfly.authenticate.service.UserSiteQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing UserSite.
 */
@RestController
@RequestMapping("/api")
public class UserSiteResource {

    private final Logger log = LoggerFactory.getLogger(UserSiteResource.class);

    private static final String ENTITY_NAME = "userSite";

    private final UserSiteService userSiteService;

    private final UserSiteQueryService userSiteQueryService;

    public UserSiteResource(UserSiteService userSiteService, UserSiteQueryService userSiteQueryService) {
        this.userSiteService = userSiteService;
        this.userSiteQueryService = userSiteQueryService;
    }

    /**
     * POST  /user-sites : Create a new userSite.
     *
     * @param userSiteDTO the userSiteDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new userSiteDTO, or with status 400 (Bad Request) if the userSite has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/user-sites")
    public ResponseEntity<UserSiteDTO> createUserSite(@RequestBody UserSiteDTO userSiteDTO) throws URISyntaxException {
        log.debug("REST request to save UserSite : {}", userSiteDTO);
        if (userSiteDTO.getId() != null) {
            throw new BadRequestAlertException("A new userSite cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserSiteDTO result = userSiteService.save(userSiteDTO);
        return ResponseEntity.created(new URI("/api/user-sites/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /user-sites : Updates an existing userSite.
     *
     * @param userSiteDTO the userSiteDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated userSiteDTO,
     * or with status 400 (Bad Request) if the userSiteDTO is not valid,
     * or with status 500 (Internal Server Error) if the userSiteDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/user-sites")
    public ResponseEntity<UserSiteDTO> updateUserSite(@RequestBody UserSiteDTO userSiteDTO) throws URISyntaxException {
        log.debug("REST request to update UserSite : {}", userSiteDTO);
        if (userSiteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UserSiteDTO result = userSiteService.save(userSiteDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, userSiteDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /user-sites : get all the userSites.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of userSites in body
     */
    @GetMapping("/user-sites")
    public ResponseEntity<List<UserSiteDTO>> getAllUserSites(UserSiteCriteria criteria, Pageable pageable) {
        log.debug("REST request to get UserSites by criteria: {}", criteria);
        Page<UserSiteDTO> page = userSiteQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user-sites");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /user-sites/count : count all the userSites.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/user-sites/count")
    public ResponseEntity<Long> countUserSites(UserSiteCriteria criteria) {
        log.debug("REST request to count UserSites by criteria: {}", criteria);
        return ResponseEntity.ok().body(userSiteQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /user-sites/:id : get the "id" userSite.
     *
     * @param id the id of the userSiteDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userSiteDTO, or with status 404 (Not Found)
     */
    @GetMapping("/user-sites/{id}")
    public ResponseEntity<UserSiteDTO> getUserSite(@PathVariable Long id) {
        log.debug("REST request to get UserSite : {}", id);
        UserSiteDTO userSiteDTO = userSiteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(userSiteDTO));
    }

    @GetMapping("/user-sites-shops/{login}")
    public ResponseEntity<List<String>> getShopsOwnedByUser(@PathVariable String login) {
        log.debug("REST request to get shops by user ", login);

        List<String> sites = userSiteService.getShopsOwnedByUser(login);

        return ResponseEntity.ok(sites);
    }

    /**
     * DELETE  /user-sites/:id : delete the "id" userSite.
     *
     * @param id the id of the userSiteDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/user-sites/{id}")
    public ResponseEntity<Void> deleteUserSite(@PathVariable Long id) {
        log.debug("REST request to delete UserSite : {}", id);
        userSiteService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    @DeleteMapping("/user-sites/by-login-id/{id}")
    public ResponseEntity<Void> deleteUserSiteFromBudderflyId(@PathVariable Long id, @RequestParam String budderflyId) {
        log.debug("REST request to delete UserSite from budderflyId " + id + " for " + budderflyId);
        userSiteService.deleteFromUserIdAndBudderflyId(id, budderflyId);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, budderflyId)).build();
    }

    /**
     * SEARCH  /_search/user-sites?query=:query : search for the userSite corresponding
     * to the query.
     *
     * @param query the query of the userSite search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/user-sites")
    public ResponseEntity<List<UserSiteDTO>> searchUserSites(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of UserSites for query {}", query);
        Page<UserSiteDTO> page = userSiteService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/user-sites");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}

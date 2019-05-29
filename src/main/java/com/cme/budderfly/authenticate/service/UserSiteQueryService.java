package com.cme.budderfly.authenticate.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.cme.budderfly.authenticate.domain.UserSite;
import com.cme.budderfly.authenticate.domain.*; // for static metamodels
import com.cme.budderfly.authenticate.repository.UserSiteRepository;
import com.cme.budderfly.authenticate.repository.search.UserSiteSearchRepository;
import com.cme.budderfly.authenticate.service.dto.UserSiteCriteria;
import com.cme.budderfly.authenticate.service.dto.UserSiteDTO;
import com.cme.budderfly.authenticate.service.mapper.UserSiteMapper;

/**
 * Service for executing complex queries for UserSite entities in the database.
 * The main input is a {@link UserSiteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserSiteDTO} or a {@link Page} of {@link UserSiteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserSiteQueryService extends QueryService<UserSite> {

    private final Logger log = LoggerFactory.getLogger(UserSiteQueryService.class);

    private final UserSiteRepository userSiteRepository;

    private final UserSiteMapper userSiteMapper;

    private final UserSiteSearchRepository userSiteSearchRepository;

    public UserSiteQueryService(UserSiteRepository userSiteRepository, UserSiteMapper userSiteMapper, UserSiteSearchRepository userSiteSearchRepository) {
        this.userSiteRepository = userSiteRepository;
        this.userSiteMapper = userSiteMapper;
        this.userSiteSearchRepository = userSiteSearchRepository;
    }

    /**
     * Return a {@link List} of {@link UserSiteDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserSiteDTO> findByCriteria(UserSiteCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UserSite> specification = createSpecification(criteria);
        return userSiteMapper.toDto(userSiteRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link UserSiteDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserSiteDTO> findByCriteria(UserSiteCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserSite> specification = createSpecification(criteria);
        return userSiteRepository.findAll(specification, page)
            .map(userSiteMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserSiteCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UserSite> specification = createSpecification(criteria);
        return userSiteRepository.count(specification);
    }

    /**
     * Function to convert UserSiteCriteria to a {@link Specification}
     */
    private Specification<UserSite> createSpecification(UserSiteCriteria criteria) {
        Specification<UserSite> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = ((Specifications<UserSite>) specification).and(buildSpecification(criteria.getId(), UserSite_.id));
            }
            if (criteria.getUserId() != null) {
                specification = ((Specifications<UserSite>) specification).and(buildRangeSpecification(criteria.getUserId(), UserSite_.userId));
            }
            if (criteria.getBudderflyId() != null) {
                specification = ((Specifications<UserSite>) specification).and(buildStringSpecification(criteria.getBudderflyId(), UserSite_.budderflyId));
            }
        }
        return specification;
    }
}

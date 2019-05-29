package com.cme.budderfly.authenticate.web.rest;

import com.cme.budderfly.authenticate.AuthenticateApp;

import com.cme.budderfly.authenticate.config.SecurityBeanOverrideConfiguration;

import com.cme.budderfly.authenticate.domain.UserSite;
import com.cme.budderfly.authenticate.domain.JhiUser;
import com.cme.budderfly.authenticate.repository.UserSiteRepository;
import com.cme.budderfly.authenticate.repository.search.UserSiteSearchRepository;
import com.cme.budderfly.authenticate.service.UserSiteService;
import com.cme.budderfly.authenticate.service.dto.UserSiteDTO;
import com.cme.budderfly.authenticate.service.mapper.UserSiteMapper;
import com.cme.budderfly.authenticate.web.rest.errors.ExceptionTranslator;
import com.cme.budderfly.authenticate.service.dto.UserSiteCriteria;
import com.cme.budderfly.authenticate.service.UserSiteQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;


import static com.cme.budderfly.authenticate.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserSiteResource REST controller.
 *
 * @see UserSiteResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthenticateApp.class)
public class UserSiteResourceIntTest {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String DEFAULT_BUDDERFLY_ID = "AAAAAAAAAA";
    private static final String UPDATED_BUDDERFLY_ID = "BBBBBBBBBB";

    @Autowired
    private UserSiteRepository userSiteRepository;

    @Autowired
    private UserSiteMapper userSiteMapper;

    @Autowired
    private UserSiteService userSiteService;

    /**
     * This repository is mocked in the com.cme.budderfly.authenticate.repository.search test package.
     *
     * @see com.cme.budderfly.authenticate.repository.search.UserSiteSearchRepositoryMockConfiguration
     */
    @Autowired
    private UserSiteSearchRepository mockUserSiteSearchRepository;

    @Autowired
    private UserSiteQueryService userSiteQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restUserSiteMockMvc;

    private UserSite userSite;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UserSiteResource userSiteResource = new UserSiteResource(userSiteService, userSiteQueryService);
        this.restUserSiteMockMvc = MockMvcBuilders.standaloneSetup(userSiteResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserSite createEntity(EntityManager em) {
        UserSite userSite = new UserSite()
            .userId(DEFAULT_USER_ID)
            .budderflyId(DEFAULT_BUDDERFLY_ID);
        return userSite;
    }

    @Before
    public void initTest() {
        userSite = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserSite() throws Exception {
        int databaseSizeBeforeCreate = userSiteRepository.findAll().size();

        // Create the UserSite
        UserSiteDTO userSiteDTO = userSiteMapper.toDto(userSite);
        restUserSiteMockMvc.perform(post("/api/user-sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userSiteDTO)))
            .andExpect(status().isCreated());

        // Validate the UserSite in the database
        List<UserSite> userSiteList = userSiteRepository.findAll();
        assertThat(userSiteList).hasSize(databaseSizeBeforeCreate + 1);
        UserSite testUserSite = userSiteList.get(userSiteList.size() - 1);
        assertThat(testUserSite.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testUserSite.getBudderflyId()).isEqualTo(DEFAULT_BUDDERFLY_ID);

        // Validate the UserSite in Elasticsearch
        verify(mockUserSiteSearchRepository, times(1)).save(testUserSite);
    }

    @Test
    @Transactional
    public void createUserSiteWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userSiteRepository.findAll().size();

        // Create the UserSite with an existing ID
        userSite.setId(1L);
        UserSiteDTO userSiteDTO = userSiteMapper.toDto(userSite);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserSiteMockMvc.perform(post("/api/user-sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userSiteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserSite in the database
        List<UserSite> userSiteList = userSiteRepository.findAll();
        assertThat(userSiteList).hasSize(databaseSizeBeforeCreate);

        // Validate the UserSite in Elasticsearch
        verify(mockUserSiteSearchRepository, times(0)).save(userSite);
    }

    @Test
    @Transactional
    public void getAllUserSites() throws Exception {
        // Initialize the database
        userSiteRepository.saveAndFlush(userSite);

        // Get all the userSiteList
        restUserSiteMockMvc.perform(get("/api/user-sites?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userSite.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].budderflyId").value(hasItem(DEFAULT_BUDDERFLY_ID.toString())));
    }
    
    @Test
    @Transactional
    public void getUserSite() throws Exception {
        // Initialize the database
        userSiteRepository.saveAndFlush(userSite);

        // Get the userSite
        restUserSiteMockMvc.perform(get("/api/user-sites/{id}", userSite.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userSite.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.budderflyId").value(DEFAULT_BUDDERFLY_ID.toString()));
    }

    @Test
    @Transactional
    public void getAllUserSitesByUserIdIsEqualToSomething() throws Exception {
        // Initialize the database
        userSiteRepository.saveAndFlush(userSite);

        // Get all the userSiteList where userId equals to DEFAULT_USER_ID
        defaultUserSiteShouldBeFound("userId.equals=" + DEFAULT_USER_ID);

        // Get all the userSiteList where userId equals to UPDATED_USER_ID
        defaultUserSiteShouldNotBeFound("userId.equals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllUserSitesByUserIdIsInShouldWork() throws Exception {
        // Initialize the database
        userSiteRepository.saveAndFlush(userSite);

        // Get all the userSiteList where userId in DEFAULT_USER_ID or UPDATED_USER_ID
        defaultUserSiteShouldBeFound("userId.in=" + DEFAULT_USER_ID + "," + UPDATED_USER_ID);

        // Get all the userSiteList where userId equals to UPDATED_USER_ID
        defaultUserSiteShouldNotBeFound("userId.in=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllUserSitesByUserIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        userSiteRepository.saveAndFlush(userSite);

        // Get all the userSiteList where userId is not null
        defaultUserSiteShouldBeFound("userId.specified=true");

        // Get all the userSiteList where userId is null
        defaultUserSiteShouldNotBeFound("userId.specified=false");
    }

    @Test
    @Transactional
    public void getAllUserSitesByUserIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userSiteRepository.saveAndFlush(userSite);

        // Get all the userSiteList where userId greater than or equals to DEFAULT_USER_ID
        defaultUserSiteShouldBeFound("userId.greaterOrEqualThan=" + DEFAULT_USER_ID);

        // Get all the userSiteList where userId greater than or equals to UPDATED_USER_ID
        defaultUserSiteShouldNotBeFound("userId.greaterOrEqualThan=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllUserSitesByUserIdIsLessThanSomething() throws Exception {
        // Initialize the database
        userSiteRepository.saveAndFlush(userSite);

        // Get all the userSiteList where userId less than or equals to DEFAULT_USER_ID
        defaultUserSiteShouldNotBeFound("userId.lessThan=" + DEFAULT_USER_ID);

        // Get all the userSiteList where userId less than or equals to UPDATED_USER_ID
        defaultUserSiteShouldBeFound("userId.lessThan=" + UPDATED_USER_ID);
    }


    @Test
    @Transactional
    public void getAllUserSitesByBudderflyIdIsEqualToSomething() throws Exception {
        // Initialize the database
        userSiteRepository.saveAndFlush(userSite);

        // Get all the userSiteList where budderflyId equals to DEFAULT_BUDDERFLY_ID
        defaultUserSiteShouldBeFound("budderflyId.equals=" + DEFAULT_BUDDERFLY_ID);

        // Get all the userSiteList where budderflyId equals to UPDATED_BUDDERFLY_ID
        defaultUserSiteShouldNotBeFound("budderflyId.equals=" + UPDATED_BUDDERFLY_ID);
    }

    @Test
    @Transactional
    public void getAllUserSitesByBudderflyIdIsInShouldWork() throws Exception {
        // Initialize the database
        userSiteRepository.saveAndFlush(userSite);

        // Get all the userSiteList where budderflyId in DEFAULT_BUDDERFLY_ID or UPDATED_BUDDERFLY_ID
        defaultUserSiteShouldBeFound("budderflyId.in=" + DEFAULT_BUDDERFLY_ID + "," + UPDATED_BUDDERFLY_ID);

        // Get all the userSiteList where budderflyId equals to UPDATED_BUDDERFLY_ID
        defaultUserSiteShouldNotBeFound("budderflyId.in=" + UPDATED_BUDDERFLY_ID);
    }

    @Test
    @Transactional
    public void getAllUserSitesByBudderflyIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        userSiteRepository.saveAndFlush(userSite);

        // Get all the userSiteList where budderflyId is not null
        defaultUserSiteShouldBeFound("budderflyId.specified=true");

        // Get all the userSiteList where budderflyId is null
        defaultUserSiteShouldNotBeFound("budderflyId.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultUserSiteShouldBeFound(String filter) throws Exception {
        restUserSiteMockMvc.perform(get("/api/user-sites?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userSite.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].budderflyId").value(hasItem(DEFAULT_BUDDERFLY_ID)));

        // Check, that the count call also returns 1
        restUserSiteMockMvc.perform(get("/api/user-sites/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultUserSiteShouldNotBeFound(String filter) throws Exception {
        restUserSiteMockMvc.perform(get("/api/user-sites?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserSiteMockMvc.perform(get("/api/user-sites/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingUserSite() throws Exception {
        // Get the userSite
        restUserSiteMockMvc.perform(get("/api/user-sites/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateNonExistingUserSite() throws Exception {
        int databaseSizeBeforeUpdate = userSiteRepository.findAll().size();

        // Create the UserSite
        UserSiteDTO userSiteDTO = userSiteMapper.toDto(userSite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserSiteMockMvc.perform(put("/api/user-sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userSiteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserSite in the database
        List<UserSite> userSiteList = userSiteRepository.findAll();
        assertThat(userSiteList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserSite in Elasticsearch
        verify(mockUserSiteSearchRepository, times(0)).save(userSite);
    }

    @Test
    @Transactional
    public void deleteUserSite() throws Exception {
        // Initialize the database
        userSiteRepository.saveAndFlush(userSite);

        int databaseSizeBeforeDelete = userSiteRepository.findAll().size();

        // Delete the userSite
        restUserSiteMockMvc.perform(delete("/api/user-sites/{id}", userSite.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<UserSite> userSiteList = userSiteRepository.findAll();
        assertThat(userSiteList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the UserSite in Elasticsearch
        verify(mockUserSiteSearchRepository, times(1)).delete(userSite.getId());
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserSite.class);
        UserSite userSite1 = new UserSite();
        userSite1.setId(1L);
        UserSite userSite2 = new UserSite();
        userSite2.setId(userSite1.getId());
        assertThat(userSite1).isEqualTo(userSite2);
        userSite2.setId(2L);
        assertThat(userSite1).isNotEqualTo(userSite2);
        userSite1.setId(null);
        assertThat(userSite1).isNotEqualTo(userSite2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserSiteDTO.class);
        UserSiteDTO userSiteDTO1 = new UserSiteDTO();
        userSiteDTO1.setId(1L);
        UserSiteDTO userSiteDTO2 = new UserSiteDTO();
        assertThat(userSiteDTO1).isNotEqualTo(userSiteDTO2);
        userSiteDTO2.setId(userSiteDTO1.getId());
        assertThat(userSiteDTO1).isEqualTo(userSiteDTO2);
        userSiteDTO2.setId(2L);
        assertThat(userSiteDTO1).isNotEqualTo(userSiteDTO2);
        userSiteDTO1.setId(null);
        assertThat(userSiteDTO1).isNotEqualTo(userSiteDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(userSiteMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(userSiteMapper.fromId(null)).isNull();
    }
}

package com.cme.budderfly.authenticate.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of UserSiteSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class UserSiteSearchRepositoryMockConfiguration {

    @MockBean
    private UserSiteSearchRepository mockUserSiteSearchRepository;

}

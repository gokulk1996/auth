package com.cme.budderfly.authenticate.repository.search;

import com.cme.budderfly.authenticate.domain.UserSite;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the UserSite entity.
 */
public interface UserSiteSearchRepository extends ElasticsearchRepository<UserSite, Long> {
}

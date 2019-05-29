package com.cme.budderfly.authenticate.repository;

import com.cme.budderfly.authenticate.domain.UserSite;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the UserSite entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserSiteRepository extends JpaRepository<UserSite, Long>, JpaSpecificationExecutor<UserSite> {

    @Query("SELECT m.budderflyId FROM UserSite m, JhiUser u WHERE u.login = ?1 AND m.userId = u.id")
    List<String> getShopsBasedOnUser(String login);

}

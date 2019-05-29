package com.cme.budderfly.authenticate.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A UserSite.
 */
@Entity
@Table(name = "user_site")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "usersite")
public class UserSite implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "budderfly_id")
    private String budderflyId;

    @OneToMany(mappedBy = "userSite")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<JhiUser> jhiUsers = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public UserSite userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBudderflyId() {
        return budderflyId;
    }

    public UserSite budderflyId(String budderflyId) {
        this.budderflyId = budderflyId;
        return this;
    }

    public void setBudderflyId(String budderflyId) {
        this.budderflyId = budderflyId;
    }

    public Set<JhiUser> getJhiUsers() {
        return jhiUsers;
    }

    public UserSite jhiUsers(Set<JhiUser> jhiUsers) {
        this.jhiUsers = jhiUsers;
        return this;
    }

    public void setJhiUsers(Set<JhiUser> jhiUsers) {
        this.jhiUsers = jhiUsers;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserSite userSite = (UserSite) o;
        if (userSite.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userSite.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserSite{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", budderflyId='" + getBudderflyId() + "'" +
            "}";
    }
}

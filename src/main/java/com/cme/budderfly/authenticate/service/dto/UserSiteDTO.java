package com.cme.budderfly.authenticate.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the UserSite entity.
 */
public class UserSiteDTO implements Serializable {

    private Long id;

    private Long userId;

    private String budderflyId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBudderflyId() {
        return budderflyId;
    }

    public void setBudderflyId(String budderflyId) {
        this.budderflyId = budderflyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserSiteDTO userSiteDTO = (UserSiteDTO) o;
        if (userSiteDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userSiteDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserSiteDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", budderflyId='" + getBudderflyId() + "'" +
            "}";
    }
}

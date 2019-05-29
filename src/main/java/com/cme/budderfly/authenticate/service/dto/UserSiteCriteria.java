package com.cme.budderfly.authenticate.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the UserSite entity. This class is used in UserSiteResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /user-sites?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class UserSiteCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter userId;

    private StringFilter budderflyId;

    private LongFilter jhiUserId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public StringFilter getBudderflyId() {
        return budderflyId;
    }

    public void setBudderflyId(StringFilter budderflyId) {
        this.budderflyId = budderflyId;
    }

    public LongFilter getJhiUserId() {
        return jhiUserId;
    }

    public void setJhiUserId(LongFilter jhiUserId) {
        this.jhiUserId = jhiUserId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UserSiteCriteria that = (UserSiteCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(budderflyId, that.budderflyId) &&
            Objects.equals(jhiUserId, that.jhiUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        userId,
        budderflyId,
        jhiUserId
        );
    }

    @Override
    public String toString() {
        return "UserSiteCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (budderflyId != null ? "budderflyId=" + budderflyId + ", " : "") +
                (jhiUserId != null ? "jhiUserId=" + jhiUserId + ", " : "") +
            "}";
    }

}

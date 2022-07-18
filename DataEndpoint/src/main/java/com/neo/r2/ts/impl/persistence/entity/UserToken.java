package com.neo.r2.ts.impl.persistence.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.neo.util.common.api.json.Views;
import com.neo.util.common.impl.RandomString;
import com.neo.util.framework.api.persistence.entity.DataBaseEntity;
import com.neo.util.framework.api.security.RolePrincipal;
import com.neo.util.framework.persistence.impl.AuditableDataBaseEntity;

import javax.persistence.*;
import javax.security.auth.Subject;
import java.util.*;

@Entity
@Table(name = UserToken.TABLE_NAME, indexes = {
        @Index(name = "key", columnList = UserToken.C_KEY, unique = true)})
public class UserToken extends AuditableDataBaseEntity implements DataBaseEntity, RolePrincipal {

    public static final String TABLE_NAME = "user_token";

    public static final String C_KEY = "key";
    public static final String C_OWNER = "owner";
    public static final String C_DESCRIPTION = "description";
    public static final String C_DISABLED = "disabled";

    public static final String T_ROLE = TABLE_NAME + "_role";
    public static final String C_ROLE = "role";

    @Id
    @Column(name = DataBaseEntity.C_ID, columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonView(Views.Internal.class)
    private Long id;

    @Column(name = C_KEY, nullable = false, unique = true, updatable = false)
        @JsonView(Views.Owner.class)
    private String key = new RandomString().nextString();

    @Column(name = C_OWNER, nullable = false, unique = true, updatable = false)
        @JsonView(Views.Owner.class)
    private UUID owner = UUID.randomUUID();

    @Column(name = C_DESCRIPTION, nullable = false)
        @JsonView(Views.Owner.class)
    private String description;

    @Column(name = C_DISABLED)
        @JsonView(Views.Owner.class)
    private boolean disabled = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = T_ROLE, joinColumns = @JoinColumn(name = DataBaseEntity.C_ID))
    @Column(name = C_ROLE)
        @JsonView(Views.Owner.class)
    private List<String> roles = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Set<String> getRoles() {
        return new HashSet<>(roles);
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public Object getPrimaryKey() {
        return getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserToken that = (UserToken) o;
        return that.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String getName() {
        return getOwner().toString();
    }

    @Override
    public boolean implies(Subject subject) {
        return RolePrincipal.super.implies(subject);
    }
}

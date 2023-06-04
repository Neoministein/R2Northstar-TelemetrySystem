package com.neo.r2.ts.persistence.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.neo.util.common.api.json.Views;
import com.neo.util.common.impl.RandomString;
import com.neo.util.framework.api.persistence.entity.PersistenceEntity;
import com.neo.util.framework.api.security.RolePrincipal;

import com.neo.util.framework.database.impl.AuditableDataBaseEntity;
import jakarta.persistence.*;
import javax.security.auth.Subject;
import java.util.*;

@Entity
@Table(name = ApplicationUser.TABLE_NAME, indexes = {
        @Index(name = ApplicationUser.C_API_KEY, columnList = ApplicationUser.C_API_KEY, unique = true),
        @Index(name = ApplicationUser.C_UID, columnList = ApplicationUser.C_UID, unique = true)})
public class ApplicationUser extends AuditableDataBaseEntity implements PersistenceEntity, RolePrincipal {

    public static final String TABLE_NAME = "APPLICATION_USER";

    public static final String C_API_KEY = "API_KEY";
    public static final String C_UID = "UID";
    public static final String C_DISPLAY_NAME = "DISPLAY_NAME";
    public static final String C_DESCRIPTION = "DESCRIPTION";
    public static final String C_DISABLED = "DISABLED";

    public static final String T_ROLE = TABLE_NAME + "_ROLE";
    public static final String C_ROLE = "ROLE";

    @Id
    @Column(name = PersistenceEntity.C_ID, columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonView(Views.Internal.class)
    private Long id;

    @Column(name = C_UID, nullable = false, unique = true, updatable = false)
    @JsonView(Views.Owner.class)
    private UUID uid = UUID.randomUUID();

    @Column(name = C_API_KEY, nullable = false, unique = true, updatable = false)
        @JsonView(Views.Owner.class)
    private String apiKey = new RandomString().nextString();

    @Column(name = C_DISPLAY_NAME, nullable = false)
    @JsonView(Views.Owner.class)
    private String displayName;

    @Column(name = C_DESCRIPTION, nullable = false)
    @JsonView(Views.Owner.class)
    private String description;

    @Column(name = C_DISABLED)
        @JsonView(Views.Owner.class)
    private boolean disabled = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = T_ROLE, joinColumns = @JoinColumn(name = PersistenceEntity.C_ID))
    @Column(name = C_ROLE)
        @JsonView(Views.Owner.class)
    private List<String> roles = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String key) {
        this.apiKey = key;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID owner) {
        this.uid = owner;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public List<String> getUserRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public Object getPrimaryKey() {
        return getId();
    }

    @Override
    public Set<String> getRoles() {
        return new HashSet<>(roles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ApplicationUser that = (ApplicationUser) o;
        return that.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String getName() {
        return getUid().toString();
    }

    @Override
    public boolean implies(Subject subject) {
        return RolePrincipal.super.implies(subject);
    }
}

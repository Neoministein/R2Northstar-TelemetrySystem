package com.neo.tf2.ms.impl.security;

import javax.enterprise.context.RequestScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestScoped
public class RequestContext {

    private UUID user;

    private List<String> roles = List.of();

    public Optional<UUID> getUser() {
        return Optional.ofNullable(user);
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}

package com.neo.r2.ts.impl;

import com.neo.util.helidon.rest.connection.RequestDetailsImpl;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Specializes;

@RequestScoped
@Specializes
public class R2RequestDetails extends RequestDetailsImpl {

    //TODO NEEDS FIXING IN NEO UTIL
    @Override
    public String getRequestId() {
        try {
            return super.getRequestId();
        } catch (IllegalStateException ex) {
            return "";
        }
    }
}

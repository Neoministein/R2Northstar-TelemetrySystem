package com.neo.r2.ts.impl.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.common.impl.StringUtils;
import com.neo.common.impl.json.JsonUtil;
import com.neo.javax.api.connection.RequestDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.util.List;

@Provider
public class AccessFilter implements ContainerResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessFilter.class);
    private static final Logger ACCESS_LOGGER = LoggerFactory.getLogger("Access");

    @Inject
    RequestDetails requestDetails;

    @Override
    public void filter(ContainerRequestContext containerRequest,
            ContainerResponseContext containerResponse) {
        try {
            if (containerResponse.getEntity() instanceof String) {
                JsonNode response = JsonUtil.fromJson((String) containerResponse.getEntity());
                String errorCode = StringUtils.EMPTY;
                if (response.has("error")) {
                    errorCode = response.get("error").get("code").asText();
                }
                List<String> agents = containerRequest.getHeaders().get("User-Agent");
                long timeTaken = System.currentTimeMillis() - requestDetails.getRequestReceiveDate().getTime();
                ACCESS_LOGGER.info("{}|{}|{}|{}|{}|{}",
                        requestDetails.getRemoteAddress(),
                        requestDetails.getRequestContext(),
                        response.get("status").asInt(),
                        errorCode,
                        timeTaken,
                        agents);
            }



        } catch (Exception ex) {
            LOGGER.error("Unable to create access log entry [{}]", ex.getMessage());
        }
    }
}

package com.neo.r2.ts.impl.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.persistence.searchable.RequestSearchable;
import com.neo.util.common.impl.exception.CommonRuntimeException;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.connection.RequestDetails;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.impl.connection.HttpRequestDetails;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.time.Instant;
import java.util.Optional;

@Provider
@ApplicationScoped
public class RequestRecorder implements ContainerResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestRecorder.class);

    public static final String ENABLED_CONFIG = "r2gs.request-recorder.enabled";

    protected final boolean enabled;

    @Inject
    protected jakarta.inject.Provider<RequestDetails> requestDetailsProvider;

    @Inject
    protected SearchProvider searchProvider;

    @Inject
    public RequestRecorder(ConfigService configService) {
        enabled = configService.get(ENABLED_CONFIG).asBoolean().orElse(true);
    }

    @Override
    public void filter(ContainerRequestContext containerRequest,
            ContainerResponseContext containerResponse) {
        if (enabled && containerResponse.getStatus() != 404 && !containerResponse.getStatusInfo().getReasonPhrase().equals("Not Found")) {
            try {
                RequestDetails requestDetails = requestDetailsProvider.get();
                HttpRequestDetails httpRequestDetails = (HttpRequestDetails) requestDetails;

                RequestSearchable requestSegment = new RequestSearchable(
                        Instant.now(),
                        requestDetails.getRequestId(),
                        httpRequestDetails.getUser().map(Principal::getName).orElse(""),
                        httpRequestDetails.getRemoteAddress(),
                        requestDetails.getRequestContext().toString(),
                        Integer.toString(containerResponse.getStatus()),
                        getErrorCodeIfPresent(containerResponse),
                        System.currentTimeMillis() - requestDetails.getRequestStartDate().toEpochMilli(),
                        Optional.ofNullable(containerRequest.getHeaders().get("User-Agent")).map(Object::toString).orElse(""));
                if (searchProvider.enabled()) {
                    searchResolver(requestSegment);
                }
                LOGGER.trace("Status [{}] Context: [{}] Took: [{}]", requestSegment.getStatus(), requestSegment.getContext(), requestSegment.getProcessTime());
            } catch (Exception ex) {
                LOGGER.warn("Unable to parse request segments [{}]", ex.getMessage());
            }
        }
    }

    protected String getErrorCodeIfPresent(ContainerResponseContext containerResponse) {
        if (containerResponse.getStatus() == 200 || !containerResponse.hasEntity()) {
            return "";
        }
        try {
            JsonNode responseBody = JsonUtil.fromJson((String) containerResponse.getEntity());
            if (responseBody.has("code")) {
                return responseBody.get("code").asText();
            }
        } catch (CommonRuntimeException ignored) {

        } catch (Exception ex) {
            LOGGER.warn("Unable to parse response body [{}]", ex.getMessage());
        }
        return "";
    }

    protected void searchResolver(RequestSearchable requestSegments) {
        try {
            searchProvider.index(requestSegments);
        } catch (Exception ex) {
            LOGGER.warn("Unable to persist RequestSearchable [{}]", ex.getMessage());
        }
    }
}

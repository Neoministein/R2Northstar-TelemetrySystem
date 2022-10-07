package com.neo.r2.ts.impl.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.impl.persistence.searchable.RequestLog;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.connection.RequestDetails;
import com.neo.util.framework.api.persistence.search.SearchRepository;
import com.neo.util.framework.impl.connection.HttpRequestDetails;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.util.Date;

@Provider
@ApplicationScoped
public class RequestRecorder implements ContainerResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestRecorder.class);
    private static final Logger ACCESS_LOGGER = LoggerFactory.getLogger("Request");

    @Inject
    jakarta.inject.Provider<RequestDetails> requestDetailsProvider;

    @Inject
    SearchRepository searchRepository;

    @Override
    public void filter(ContainerRequestContext containerRequest,
            ContainerResponseContext containerResponse) {
        if (containerResponse.getStatus() != 404 && !containerResponse.getStatusInfo().getReasonPhrase().equals("Not Found")) {
            try {
                RequestDetails requestDetails = requestDetailsProvider.get();
                HttpRequestDetails httpRequestDetails = (HttpRequestDetails) requestDetails;

                JsonNode responseBody = JsonUtil.fromJson((String) containerResponse.getEntity());
                RequestLog.RequestSegments requestSegment = new RequestLog.RequestSegments(
                        new Date(),
                        requestDetails.getRequestId(),
                        httpRequestDetails.getUser().isPresent() ? httpRequestDetails.getUser().get().getName() : "",
                        httpRequestDetails.getRemoteAddress(),
                        requestDetails.getRequestContext().toString(),
                        responseBody.get("status").asText(),
                        responseBody.has("error") ? responseBody.get("error").get("code").asText() : "",
                        System.currentTimeMillis() - requestDetails.getRequestStartDate().getTime(),
                        containerRequest.getHeaders().get("User-Agent") != null ? containerRequest.getHeaders().get("User-Agent").toString() : "");
                if (searchRepository.enabled()) {
                    searchResolver(requestSegment);
                } else {
                    logResolver(requestSegment);
                }
            } catch (Exception ex) {
                LOGGER.warn("Unable to parse request segments [{}]", ex.getMessage());
            }
        }
    }

    protected void searchResolver(RequestLog.RequestSegments requestSegments) {
        try {
            searchRepository.index(new RequestLog(requestSegments));
        } catch (Exception ex) {
            LOGGER.warn("Unable to persist access log searchable [{}]", ex.getMessage());
        }
    }

    protected void logResolver(RequestLog.RequestSegments requestSegments) {
        try {
            ACCESS_LOGGER.trace("{}|{}|{}|{}|{}|{}|{}",
                    requestSegments.owner(),
                    requestSegments.remoteAddress(),
                    requestSegments.context(),
                    requestSegments.status(),
                    requestSegments.error(),
                    requestSegments.processTime(),
                    requestSegments.agent());
        } catch (Exception ex) {
            LOGGER.warn("Unable to create access log entry [{}]", ex.getMessage());
        }
    }
}

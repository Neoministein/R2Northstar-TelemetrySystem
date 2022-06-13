package com.neo.r2.ts.impl.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.common.impl.json.JsonUtil;
import com.neo.javax.api.connection.RequestDetails;
import com.neo.javax.api.persitence.search.SearchRepository;
import com.neo.r2.ts.impl.persistence.searchable.RequestLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class RequestRecorder implements ContainerResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestRecorder.class);
    private static final Logger ACCESS_LOGGER = LoggerFactory.getLogger("Request");

    @Inject
    RequestDetails requestDetails;

    @Inject
    SearchRepository searchRepository;

    @Override
    public void filter(ContainerRequestContext containerRequest,
            ContainerResponseContext containerResponse) {
        if (containerResponse.getStatus() != 404 && !containerResponse.getStatusInfo().getReasonPhrase().equals("Not Found")) {
            try {
                JsonNode responseBody = JsonUtil.fromJson((String) containerResponse.getEntity());
                RequestLog.RequestSegments requestSegment = new RequestLog.RequestSegments();
                requestSegment.setRequestId(requestSegment.getRequestId());
                requestSegment.setOwner(requestDetails.getUUId().isPresent() ? requestDetails.getUUId().toString() : "");
                requestSegment.setRemoteAddress(requestSegment.getRemoteAddress());
                requestSegment.setContext(requestDetails.getRequestContext().toString());
                requestSegment.setStatus(responseBody.get("status").asText());
                requestSegment.setError(responseBody.has("error") ? responseBody.get("error").get("code").asText() : "");
                requestSegment.setProcessTime(System.currentTimeMillis() - requestDetails.getRequestReceiveDate().getTime());
                requestSegment.setAgent(containerRequest.getHeaders().get("User-Agent").toString());
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
                    requestSegments.getOwner(),
                    requestSegments.getRemoteAddress(),
                    requestSegments.getContext(),
                    requestSegments.getStatus(),
                    requestSegments.getError(),
                    requestSegments.getProcessTime(),
                    requestSegments.getAgent());
        } catch (Exception ex) {
            LOGGER.warn("Unable to create access log entry [{}]", ex.getMessage());
        }
    }
}

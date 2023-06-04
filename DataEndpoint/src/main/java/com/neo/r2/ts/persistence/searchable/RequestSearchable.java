package com.neo.r2.ts.persistence.searchable;

import com.neo.util.framework.api.persistence.search.AbstractSearchable;
import com.neo.util.framework.api.persistence.search.IndexPeriod;
import com.neo.util.framework.api.persistence.search.Searchable;
import com.neo.util.framework.api.persistence.search.SearchableIndex;

import java.time.Instant;

@SearchableIndex(indexName = RequestSearchable.INDEX_NAME, indexPeriod = IndexPeriod.DAILY)
public class RequestSearchable extends AbstractSearchable implements Searchable {

    public static final String INDEX_NAME = "log-request";

    protected Instant timestamp;
    protected String requestId;
    protected String owner;
    protected String remoteAddress;
    protected String context;
    protected String status;
    protected String error;
    protected long processTime;
    protected String agent;

    public RequestSearchable(Instant timestamp, String requestId, String owner, String remoteAddress, String context, String status, String error, long processTime, String agent) {
        this.timestamp = timestamp;
        this.requestId = requestId;
        this.owner = owner;
        this.remoteAddress = remoteAddress;
        this.context = context;
        this.status = status;
        this.error = error;
        this.processTime = processTime;
        this.agent = agent;
    }

    protected RequestSearchable() {}

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getProcessTime() {
        return processTime;
    }

    public void setProcessTime(long processTime) {
        this.processTime = processTime;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }
}

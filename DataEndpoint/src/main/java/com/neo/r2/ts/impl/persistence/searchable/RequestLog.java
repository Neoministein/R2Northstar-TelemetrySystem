package com.neo.r2.ts.impl.persistence.searchable;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.persistence.search.GenericSearchable;
import com.neo.util.framework.api.persistence.search.IndexPeriod;
import com.neo.util.framework.api.persistence.search.Searchable;

import java.io.Serializable;
import java.util.Date;

public class RequestLog extends GenericSearchable implements Searchable {

    protected RequestSegments segment;

    public RequestLog(RequestSegments requestSegments) {
        this.segment = requestSegments;
    }

    @Override
    public ObjectNode getJsonNode() {
        return JsonUtil.fromPojo(this);
    }

    @Override
    public String getClassName() {
        return RequestLog.class.getSimpleName();
    }

    @Override
    public IndexPeriod getIndexPeriod() {
        return IndexPeriod.DAILY;
    }

    @Override
    public String getIndexName() {
        return "request-log";
    }

    public static class RequestSegments implements Serializable {
        protected Date timestamp = new Date();
        protected String requestId;
        protected String owner;
        protected String remoteAddress;
        protected String context;
        protected String status;
        protected String error;
        protected long processTime;
        protected String agent;

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
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
}

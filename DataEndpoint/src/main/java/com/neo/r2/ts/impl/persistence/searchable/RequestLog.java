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

    public record RequestSegments(
            Date timestamp,
            String requestId,
            String owner,
            String remoteAddress,
            String context,
            String status,
            String error,
            long processTime,
            String agent
    ) implements Serializable {}
}

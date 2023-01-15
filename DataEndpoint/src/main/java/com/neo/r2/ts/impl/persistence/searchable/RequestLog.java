package com.neo.r2.ts.impl.persistence.searchable;

import com.neo.util.framework.api.persistence.search.AbstractSearchable;
import com.neo.util.framework.api.persistence.search.IndexPeriod;
import com.neo.util.framework.api.persistence.search.Searchable;
import jakarta.enterprise.context.Dependent;

import java.io.Serializable;
import java.util.Date;

@Dependent
public class RequestLog extends AbstractSearchable implements Searchable {

    protected RequestSegments segment;

    public RequestLog(RequestSegments requestSegments) {
        this.segment = requestSegments;
    }

    protected RequestLog() {}
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

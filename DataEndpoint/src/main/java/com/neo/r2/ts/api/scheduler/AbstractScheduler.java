package com.neo.r2.ts.api.scheduler;

import com.neo.util.framework.api.connection.RequestContext;
import com.neo.util.framework.api.connection.RequestDetails;
import com.neo.util.framework.impl.RequestContextExecutor;
import com.neo.util.framework.impl.connection.SchedulerRequestDetails;
import jakarta.inject.Inject;
import org.slf4j.Logger;

import java.util.UUID;

public abstract class AbstractScheduler {

    protected final RequestContext requestContext = new RequestContext.Scheduler(this.getClass().getSimpleName());

    @Inject
    protected RequestContextExecutor requestContextExecutor;

    protected abstract void scheduledAction();

    protected abstract Logger getLogger();

    protected void runSchedule() {
        RequestDetails requestDetails = new SchedulerRequestDetails(UUID.randomUUID().toString(), requestContext);
        try {
            requestContextExecutor.execute(requestDetails,this::scheduledAction);
        } catch (Exception ex) {
            getLogger().error("Unexpected error occurred while processing a scheduled action [{}], action won't be retried.", ex.getMessage());
        }
    }
}

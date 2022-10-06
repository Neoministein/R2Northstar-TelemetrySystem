package com.neo.r2.ts.api.scheduler;

import com.neo.util.framework.api.connection.RequestContext;
import com.neo.util.framework.impl.connection.RequestDetailsProducer;
import com.neo.util.framework.impl.connection.SchedulerRequestDetails;
import jakarta.enterprise.context.control.RequestContextController;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.slf4j.Logger;

import java.util.UUID;

public abstract class AbstractScheduler {

    protected abstract void scheduledAction();

    protected abstract Logger getLogger();

    protected abstract String getName();

    @Inject
    protected RequestDetailsProducer requestDetailsProducer;

    @Inject
    protected Provider<RequestContextController> requestContextControllerFactory;

    protected void runSchedule() {
        RequestContextController requestContextController = requestContextControllerFactory.get();
        requestContextController.activate();
        try {
            requestDetailsProducer.setRequestDetails(new SchedulerRequestDetails(UUID.randomUUID().toString(), new RequestContext.Scheduler(getName())));
            scheduledAction();
        } catch (Exception ex) {
            getLogger().error("Unexpected error occurred while processing a scheduled action [{}], action won't be retried.", ex.getMessage());
        } finally {
            requestContextController.deactivate();
        }
    }
}

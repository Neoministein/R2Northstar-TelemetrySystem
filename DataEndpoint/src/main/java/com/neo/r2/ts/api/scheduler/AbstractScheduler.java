package com.neo.r2.ts.api.scheduler;

import com.neo.util.framework.api.component.ApplicationComponent;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.request.RequestContext;
import com.neo.util.framework.api.request.RequestDetails;
import com.neo.util.framework.impl.request.RequestContextExecutor;
import com.neo.util.framework.impl.request.SchedulerRequestDetails;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.slf4j.Logger;

import java.util.UUID;

public abstract class AbstractScheduler implements ApplicationComponent {

    protected final RequestContext requestContext = new RequestContext.Scheduler(this.getClass().getSimpleName());

    protected boolean enabled;

    @Inject
    protected RequestContextExecutor requestContextExecutor;

    @Inject
    protected ConfigService configService;

    protected abstract void scheduledAction();

    protected abstract Logger getLogger();

    @Override
    public boolean enabled() {
        return enabled;
    }

    @PostConstruct
    public void reload() {
        this.enabled = configService.get("scheduler." + this.getClass().getSimpleName()).asBoolean().orElse(true);
    }

    protected void runSchedule() {
        if (enabled) {
            RequestDetails requestDetails = new SchedulerRequestDetails(UUID.randomUUID().toString(), requestContext);
            try {
                requestContextExecutor.execute(requestDetails, this::scheduledAction);
            } catch (Exception ex) {
                getLogger().error("Unexpected error occurred while processing a scheduled action [{}], action won't be retried.", ex.getMessage());
            }
        }
    }
}

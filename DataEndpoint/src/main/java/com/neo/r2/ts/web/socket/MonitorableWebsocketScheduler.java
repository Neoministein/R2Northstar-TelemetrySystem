package com.neo.r2.ts.web.socket;

import com.neo.r2.ts.api.scheduler.AbstractScheduler;
import com.neo.r2.ts.api.socket.MonitorableWebsocket;
import com.neo.r2.ts.persistence.searchable.metric.SocketLogSearchable;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import io.helidon.microprofile.scheduling.FixedRate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class MonitorableWebsocketScheduler extends AbstractScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorableWebsocketScheduler.class);

    @Inject
    protected Instance<MonitorableWebsocket> monitorableWebsocketList;

    @Inject
    protected SearchProvider searchProvider;

    @Override
    protected void scheduledAction() {
        LOGGER.info("Schedule called");
        try {
            for (MonitorableWebsocket monitorableWebsocket: monitorableWebsocketList) {
                for (SocketLogSearchable socketDataSearchable: monitorableWebsocket.getSocketData()) {
                    searchProvider.index(socketDataSearchable);
                }
                monitorableWebsocket.clearSocketData();
            }
        } catch (Exception ex) {
            LOGGER.error("An unexpected exception occurred during the schedule", ex);
        }
    }

    @FixedRate(initialDelay = 1, value = 1, timeUnit = TimeUnit.MINUTES)
    public void monitorSchedule() {
        super.runSchedule();
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}

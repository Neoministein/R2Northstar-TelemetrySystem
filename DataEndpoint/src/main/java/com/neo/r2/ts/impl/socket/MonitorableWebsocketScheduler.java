package com.neo.r2.ts.impl.socket;

import com.neo.r2.ts.api.socket.MonitorableWebsocket;
import com.neo.r2.ts.api.socket.SocketLogSearchable;
import com.neo.util.framework.api.persistence.search.SearchRepository;
import io.helidon.microprofile.scheduling.FixedRate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class MonitorableWebsocketScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorableWebsocketScheduler.class);

    @Inject
    protected Instance<MonitorableWebsocket> monitorableWebsocketList;

    @Inject
    protected SearchRepository searchRepository;

    @FixedRate(initialDelay = 1, value = 1, timeUnit = TimeUnit.MINUTES)
    public void monitorSchedule() {
        LOGGER.info("Schedule called");
        try {
            for (MonitorableWebsocket monitorableWebsocket: monitorableWebsocketList) {
                for (SocketLogSearchable socketDataSearchable: monitorableWebsocket.getSocketData()) {
                    searchRepository.index(socketDataSearchable);
                }
                monitorableWebsocket.clearSocketData();
            }
        } catch (Exception ex) {
           LOGGER.error("An unexpected exception occurred during the schedule", ex);
        }
    }
}

package com.neo.r2.ts.api.socket;

import com.neo.r2.ts.persistence.searchable.metric.SocketLogSearchable;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class AbstractMonitorableWebsocket implements MonitorableWebsocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMonitorableWebsocket.class);

    protected Map<Session, SocketLogSearchable> socketData = new ConcurrentHashMap<>();

    public Collection<SocketLogSearchable> getSocketData() {
        return socketData.values();
    }

    public void clearSocketData() {
        socketData.clear();
    }

    public void updateIncomingSocketLog(Session session, String message) {
        modifySearchableData(session, val -> val.addToIncoming(message.length()));
    }

    @SuppressWarnings("java:S2445") //Based on oracle guide
    protected void broadcast(final Session session, final String message) {
        modifySearchableData(session, val -> val.addToOutgoing(message.length()));

        synchronized (session) {
            try {
               session.getBasicRemote().sendObject(message);
            } catch (IOException | EncodeException ex) {
                LOGGER.warn("Unable to broadcast message [{}] to session [{}]", ex.getMessage(), session.getId());
            } catch (IllegalArgumentException ex) {
                LOGGER.warn("Provided message for session [{}] is null", session.getId());
            }
        }
    }

    protected void modifySearchableData(final Session session, Consumer<SocketLogSearchable> edit) {
        edit.accept(socketData.computeIfAbsent(session, SocketLogSearchable::new));
    }
}

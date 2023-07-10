package com.neo.r2.ts.api.socket;

import com.neo.r2.ts.persistence.searchable.metric.SocketLogSearchable;

import java.util.Collection;

public interface MonitorableWebsocket {

    Collection<SocketLogSearchable> getSocketData();

    void clearSocketData();
}

package com.neo.r2.ts.api.socket;

import java.util.Collection;

public interface MonitorableWebsocket {

    Collection<SocketLogSearchable> getSocketData();

    void clearSocketData();
}

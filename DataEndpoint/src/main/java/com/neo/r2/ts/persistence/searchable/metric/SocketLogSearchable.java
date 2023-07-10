package com.neo.r2.ts.persistence.searchable.metric;

import com.neo.util.framework.api.persistence.search.AbstractSearchable;
import com.neo.util.framework.api.persistence.search.IndexPeriod;
import com.neo.util.framework.api.persistence.search.SearchableIndex;
import jakarta.websocket.Session;

import java.time.Instant;

@SearchableIndex(indexName = "socket-log", indexPeriod = IndexPeriod.MONTHLY)
public class SocketLogSearchable extends AbstractSearchable {

    private Instant timestamp;
    private String socketId;
    private String protocolVersion;
    private String negotiatedSubProtocol;
    private String requestUri;

    private long incoming = 0;
    private long outgoing = 0;


    public SocketLogSearchable(Session session) {
        this.timestamp = Instant.now();
        this.socketId = session.getId();
        this.protocolVersion = session.getProtocolVersion();
        this.negotiatedSubProtocol = session.getNegotiatedSubprotocol();
        this.requestUri = session.getRequestURI().toString();
    }

    //Required for Jackson
    protected SocketLogSearchable(){}

    public void addToIncoming(long toAdd) {
        incoming += toAdd;
    }

    public void addToOutgoing(long toAdd) {
        outgoing += toAdd;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getSocketId() {
        return socketId;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getNegotiatedSubProtocol() {
        return negotiatedSubProtocol;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public long getIncoming() {
        return incoming;
    }

    public long getOutgoing() {
        return outgoing;
    }
}

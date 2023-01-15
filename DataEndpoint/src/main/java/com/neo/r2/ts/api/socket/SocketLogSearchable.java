package com.neo.r2.ts.api.socket;

import com.neo.util.framework.api.persistence.search.AbstractSearchable;
import com.neo.util.framework.api.persistence.search.IndexPeriod;
import jakarta.websocket.Session;

import java.util.Date;

public class SocketLogSearchable extends AbstractSearchable {

    private Date timestamp;
    private String socketId;
    private String protocolVersion;
    private String negotiatedSubProtocol;
    private String requestUri;

    private long incoming = 0;
    private long outgoing = 0;


    public SocketLogSearchable(Session session) {
        this.timestamp = new Date();
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

    @Override
    public IndexPeriod getIndexPeriod() {
        return IndexPeriod.MONTHLY;
    }

    @Override
    public String getIndexName() {
        return "socket-log";
    }

    public Date getTimestamp() {
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

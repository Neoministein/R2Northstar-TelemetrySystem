package com.neo.r2.ts.impl.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.rest.api.RestAction;
import com.neo.util.framework.rest.impl.AbstractRestEndpoint;
import com.neo.util.framework.rest.impl.DefaultResponse;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path(GameClientResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class GameClientResource extends AbstractRestEndpoint {

    public static final String RESOURCE_LOCATION = "api/client";

    @GET
    public Response get() {
        RestAction restAction = () -> {
            ObjectNode node =  JsonUtil.emptyObjectNode();
            node.put("newestVersion","1.0");
            node.put("requiredVersion","1.0");
            return DefaultResponse.success(requestDetails.getRequestContext(), node);
        };

        return super.restCall(restAction);
    }
}

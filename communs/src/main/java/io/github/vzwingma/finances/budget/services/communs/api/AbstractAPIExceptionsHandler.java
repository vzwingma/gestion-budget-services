package io.github.vzwingma.finances.budget.services.communs.api;

import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Handler for exceptions pour les API REST
 */
public abstract  class AbstractAPIExceptionsHandler implements ExceptionMapper<AbstractBusinessException> {


    @Override
    public Response toResponse(AbstractBusinessException e) {
        if(e instanceof BadParametersException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getLibelle()).build();
        }
        else if(e instanceof DataNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getLibelle()).build();
        }
        else if(e instanceof BudgetNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getLibelle()).build();
        }
        else if(e instanceof CompteClosedException) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getLibelle()).build();
        }
        else if(e instanceof UserNotAuthorizedException) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getLibelle()).build();
        }
        else if(e instanceof UserAccessForbiddenException) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getLibelle()).build();
        }
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(e.getLibelle()).build();
    }
}

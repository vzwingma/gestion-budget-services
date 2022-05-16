package io.github.vzwingma.finances.budget.services.comptes.api;

import io.github.vzwingma.finances.budget.services.communs.api.AbstractAPIExceptionsHandler;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.AbstractBusinessException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Handler for exceptions pour les API REST
 */
@Provider
public class APIExceptionsHandler extends AbstractAPIExceptionsHandler {

    @Override
    public Response toResponse(AbstractBusinessException e) {
        return super.toResponse(e);
    }

}

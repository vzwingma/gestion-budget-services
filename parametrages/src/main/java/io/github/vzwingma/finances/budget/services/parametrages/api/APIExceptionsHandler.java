package io.github.vzwingma.finances.budget.services.parametrages.api;

import io.github.vzwingma.finances.budget.services.communs.api.AbstractAPIExceptionsHandler;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.AbstractBusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Handler for exceptions pour les API REST
 */
@Provider
public class APIExceptionsHandler extends AbstractAPIExceptionsHandler {


    private final Logger LOG = LoggerFactory.getLogger(APIExceptionsHandler.class);
    @Override
    public Response toResponse(AbstractBusinessException e) {
        return super.toResponse(e);
    }

}

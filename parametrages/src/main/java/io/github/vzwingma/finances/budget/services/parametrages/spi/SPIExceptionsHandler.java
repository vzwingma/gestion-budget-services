package io.github.vzwingma.finances.budget.services.parametrages.spi;

import com.mongodb.MongoClientException;
import io.github.vzwingma.finances.budget.services.communs.spi.AbstractBDDExceptionsHandler;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Handler for exceptions pour les SPI
 */
@Provider
public class SPIExceptionsHandler extends AbstractBDDExceptionsHandler {

    @Override
    public Response toResponse(MongoClientException e) {
        return super.toResponse(e);
    }
}

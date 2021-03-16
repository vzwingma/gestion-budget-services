package com.terrier.finances.gestion.communs.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.terrier.finances.gestion.communs.api.model.Info;
import com.terrier.finances.gestion.test.TestClientAPI;
import com.terrier.finances.gestion.test.server.AbstractTestsServerAPI;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={TestClientAPI.class})
public class TestHTTPReactiveClientError extends AbstractTestsServerAPI {


    @Autowired
    private TestClientAPI clientAPI;

    @Test
    public void testClientResponseAndBody() throws JsonProcessingException {
        Info infoTest = new Info();
        infoTest.setApp(infoTest.new App());
        infoTest.getApp().setName("TEST");
        infoTest.getApp().setVersion("1.0");

        Mono<Info> response = clientAPI.callAPIandReturnResponse(HttpMethod.GET, "/", null, null, null, Info.class);
        assertNotNull(response);
        try {
            response.block();
            fail("Ce block devrait faire une exception");
        }
        catch (Exception e){
            assertEquals(WebClientRequestException.class, e.getClass());
        }
    }



    @Test
    public void testClientResponseStatus() throws JsonProcessingException {

        getMockWebServer().enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));


        HttpStatus response = clientAPI.callAPIandReturnStatus(HttpMethod.GET, "/", null, null, null);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response);
    }

    @Override
    public int getServerPort() {
        return 8099;
    }
}

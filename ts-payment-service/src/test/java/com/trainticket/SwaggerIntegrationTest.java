package com.trainticket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainticket.controller.PaymentController;
import com.trainticket.entity.Payment;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SwaggerIntegrationTest {
    private static final String API_DOCS_PATH = "/v2/api-docs";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void swaggerUiShouldBeAccessible() {
        // given
        String url = "http://127.0.0.1:" + port + "/swagger-ui.html";

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // then
        HttpStatus status = response.getStatusCode();
        boolean accessible = status.is2xxSuccessful() || status.is3xxRedirection();

        assertTrue("Expected HTTP 2xx/3xx from " + url + " but got " + status, accessible);

        // Optional – verify that the body (or redirect target) looks like Swagger UI
        if (status.is2xxSuccessful()) {
            assertTrue(
                    "Response body does not contain the expected Swagger keyword",
                    response.getBody() != null &&
                            response.getBody().toLowerCase().contains("swagger"));
        }
    }

    @Test
    public void mediaTypesAreSpecified() throws Exception {

        // 1. Download the Swagger/OpenAPI JSON
        JsonNode root = retrieveGeneratedDoc();
        JsonNode paths = root.path("paths");
        assertFalse("No 'paths' node found in Swagger JSON", paths.isMissingNode());

        assertMediaType(paths,
                "/api/v1/paymentservice/welcome",
                "get",
                "text/plain");

        assertMediaType(paths,
                "/api/v1/paymentservice/payment",
                "post",
                "application/json");

        assertMediaType(paths,
                "/api/v1/paymentservice/payment/money",
                "post",
                "application/json");
    }

    private JsonNode retrieveGeneratedDoc() throws JsonProcessingException {
        ResponseEntity<String> response =
                restTemplate.getForEntity("http://127.0.0.1:" + port + API_DOCS_PATH, String.class);

        assertTrue("Swagger JSON should be returned successfully",
                response.getStatusCode().is2xxSuccessful());

        // 2. Parse JSON
        JsonNode root = mapper.readTree(response.getBody());
        return root;
    }

    /**
     * Verifies that the given operation declares the expected media type in its
     * 'produces' array.
     */
    private void assertMediaType(JsonNode paths,
                                 String endpoint,
                                 String httpMethod,
                                 String expectedMediaType) {

        JsonNode producesNode = paths
                .path(endpoint)
                .path(httpMethod)
                .path("produces");

        assertFalse("Missing 'produces' section for " + httpMethod.toUpperCase() + " " + endpoint,
                producesNode.isMissingNode() || !producesNode.isArray());

        boolean found = false;
        for (JsonNode node : producesNode) {
            if (expectedMediaType.equals(node.asText())) {
                found = true;
                break;
            }
        }

        assertTrue("Media type '" + expectedMediaType + "' not specified for "
                        + httpMethod.toUpperCase() + " " + endpoint,
                found);
    }

    @Test
    public void methodsAreAnnotatedApiOperationAnnotation() throws Exception {
        checkAnnotatedWithOpenSpecAnnotations(PaymentController.class.getDeclaredMethod("home"));
        checkAnnotatedWithOpenSpecAnnotations(PaymentController.class.getDeclaredMethod("pay", Payment.class, org.springframework.http.HttpHeaders.class));
        checkAnnotatedWithOpenSpecAnnotations(PaymentController.class.getDeclaredMethod("addMoney", Payment.class, org.springframework.http.HttpHeaders.class));
        checkAnnotatedWithOpenSpecAnnotations(PaymentController.class.getDeclaredMethod("query", org.springframework.http.HttpHeaders.class));
    }

    private static void checkAnnotatedWithOpenSpecAnnotations(Method m) throws NoSuchMethodException {

        assertTrue(m.isAnnotationPresent(ApiOperation.class));
        assertTrue(m.isAnnotationPresent(ApiResponses.class));
    }

    @Test
    public void everyPaymentServiceOperationHasDescription() throws JsonProcessingException {
        JsonNode root = retrieveGeneratedDoc();
        JsonNode paths = root.path("paths");

        // Iterate over all documented paths
        Iterator<String> pathIt = paths.fieldNames();
        while (pathIt.hasNext()) {
            String path = pathIt.next();

            // Only look at Payment-service endpoints
            if (!path.startsWith("/api/v1/paymentservice")) {
                continue;
            }

            JsonNode operations = paths.get(path);
            Iterator<String> methodIt = operations.fieldNames();
            while (methodIt.hasNext()) {
                String httpMethod = methodIt.next();
                JsonNode operation = operations.get(httpMethod);

                JsonNode descriptionNode = operation.get("description");
                assertThat(descriptionNode)
                        .withFailMessage("Missing description for %s %s",
                                httpMethod.toUpperCase(), path)
                        .isNotNull();

                assertThat(descriptionNode.asText())
                        .withFailMessage("Blank description for %s %s",
                                httpMethod.toUpperCase(), path)
                        .isNotBlank();
            }
        }
    }
}

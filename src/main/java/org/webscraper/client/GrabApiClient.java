package org.webscraper.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webscraper.model.Payload;

import java.io.IOException;

/**
 * Client class for making API requests to Grab's search endpoint
 */
public class GrabApiClient extends Client {
    private static final Logger logger = LoggerFactory.getLogger(GrabApiClient.class);
    private static final String GRAB_API_SEARCH_URL = "https://portal.grab.com/foodweb/v2/search";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_INTERVAL_MS = 1000;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a GrabApiClient with a specific HTTP client and an object mapper for JSON processing.
     *
     * @param httpClient the OkHttpClient instance to use for requests
     * @param objectMapper the ObjectMapper instance to use for JSON serialization
     */
    public GrabApiClient(OkHttpClient httpClient, ObjectMapper objectMapper) {
        super(httpClient);
        this.objectMapper = objectMapper;
    }

    @Override
    public Response makeRequest(String requestBody) {
        // Implement if necessary, or remove if not used.
        return null;
    }

    @Override
    public Response makeRequest(Payload payload) throws IOException, InterruptedException {
        if (payload == null) {
            logger.error("Payload cannot be null");
            throw new IllegalArgumentException("Payload cannot be null");
        }

        String requestBody = objectMapper.writeValueAsString(payload);
        Request request = new Request.Builder()
                .url(GRAB_API_SEARCH_URL)
                .post(RequestBody.create(requestBody, MediaType.parse("application/json; charset=utf-8")))
                .build();

        return executeRequestWithRetry(request);
    }

    private Response executeRequestWithRetry(Request request) throws IOException, InterruptedException {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                logger.info("Sending request to URL: {}", request.url());
                Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response;
                }
                logger.warn("Request failed with status code: {}", response.code());
                response.close();
                Thread.sleep(RETRY_INTERVAL_MS);
                retryCount++;
            } catch (IOException | InterruptedException e) {
                logger.error("Request failed: {}", e.getMessage(), e);
                retryCount++;
                Thread.sleep(RETRY_INTERVAL_MS);
            }
        }
        throw new IOException("Max retries exceeded for URL: " + request.url());
    }
}

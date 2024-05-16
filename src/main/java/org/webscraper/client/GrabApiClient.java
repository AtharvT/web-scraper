package org.webscraper.client;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrabApiClient extends Client {
    private static final Logger logger = LoggerFactory.getLogger(GrabApiClient.class);
    private static final String GRAB_API_SEARCH_URL = "https://portal.grab.com/foodweb/v2/search";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_INTERVAL_MS = 1000;

    public GrabApiClient(OkHttpClient httpClient) {
        super(httpClient);
    }

    @Override
    public Response makeRequest(String requestBody) throws IOException, InterruptedException {

        if (requestBody == null || requestBody.isEmpty()) {
            logger.error("Request body cannot be null or empty");
            throw new IllegalArgumentException("Request body cannot be null or empty");
        }

        // Create the request
        Request request = new Request.Builder()
                .url(GRAB_API_SEARCH_URL)
                .post(RequestBody.create(requestBody, MediaType.parse("application/json; charset=utf-8")))
                .build();

        // Send the request with retry logic
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                logger.info("Sending request to URL: {}", GRAB_API_SEARCH_URL);
                Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response;
                } else {
                    logger.warn("Request to URL {} failed with status code {}", GRAB_API_SEARCH_URL, response.code());
                    response.close();
                    retryCount++;
                    Thread.sleep(RETRY_INTERVAL_MS);
                }
            } catch (IOException e) {
                logger.error("Failed to execute request to URL {}: {}", GRAB_API_SEARCH_URL, e.getMessage(), e);
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } else {
                    throw e;
                }
            } catch (InterruptedException e) {
                logger.error("Retry interval interrupted: {}", e.getMessage(), e);
                Thread.currentThread().interrupt();
                throw new IOException("Retry interval interrupted", e);
            }
        }

        throw new IOException("Max retries exceeded for URL: " + GRAB_API_SEARCH_URL);
    }
}

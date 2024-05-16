package org.webscraper.client;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.webscraper.model.Payload;

import java.io.IOException;

public abstract class Client {
    protected final OkHttpClient httpClient;

    public Client(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public abstract Response makeRequest(String requestBody) throws IOException, InterruptedException;
    public abstract Response makeRequest(Payload payload) throws IOException, InterruptedException;
}


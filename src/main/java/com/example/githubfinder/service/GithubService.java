package com.example.githubfinder.service;

import com.example.githubfinder.model.GithubRepository;
import com.example.githubfinder.model.GithubUserProfile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

@Service
public class GithubService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GithubService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Fetches GitHub user profile data by username.
     */
    public GithubUserProfile getUserProfile(String username) throws IOException, InterruptedException {
        String url = "https://api.github.com/users/" + username;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Java-Spring-Boot-Github-Finder")
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), GithubUserProfile.class);
        } else if (response.statusCode() == 404) {
            throw new RuntimeException("USER_NOT_FOUND");
        } else {
            throw new RuntimeException("API_ERROR_" + response.statusCode());
        }
    }

    /**
     * Fetches top 4 recent public repositories of a user.
     */
    public List<GithubRepository> getUserRepositories(String username) {
        String url = "https://api.github.com/users/" + username + "/repos?sort=updated&per_page=4";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Java-Spring-Boot-Github-Finder")
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), new TypeReference<List<GithubRepository>>() {});
            }
        } catch (Exception e) {
            System.err.println("Error fetching user repos: " + e.getMessage());
        }
        return Collections.emptyList();
    }
}

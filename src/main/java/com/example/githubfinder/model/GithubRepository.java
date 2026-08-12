package com.example.githubfinder.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GithubRepository {

    private String name;
    
    @JsonProperty("html_url")
    private String htmlUrl;
    
    private String description;
    private String language;
    
    @JsonProperty("stargazers_count")
    private int stargazersCount;
    
    @JsonProperty("forks_count")
    private int forksCount;

    // Default Constructor
    public GithubRepository() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getHtmlUrl() { return htmlUrl; }
    public void setHtmlUrl(String htmlUrl) { this.htmlUrl = htmlUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public int getStargazersCount() { return stargazersCount; }
    public void setStargazersCount(int stargazersCount) { this.stargazersCount = stargazersCount; }

    public int getForksCount() { return forksCount; }
    public void setForksCount(int forksCount) { this.forksCount = forksCount; }
}

package kz.shakenov.javaoptai.metadata.downloader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubSearchResponse {
    public int total_count;
    public List<GitHubRepository> items;

    public int getTotalCount() {
        return total_count;
    }

    public List<GitHubRepository> getItems() {
        return this.items;
    }
}

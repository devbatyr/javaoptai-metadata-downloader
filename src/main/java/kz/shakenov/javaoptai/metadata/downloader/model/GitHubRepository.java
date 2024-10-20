package kz.shakenov.javaoptai.metadata.downloader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubRepository {
    public String html_url;
    public int id;

    public String getHtml_url() {
        return html_url;
    }

    public int getId() {
        return id;
    }
}

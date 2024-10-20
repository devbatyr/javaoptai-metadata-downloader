package kz.shakenov.javaoptai.metadata.downloader.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import kz.shakenov.javaoptai.metadata.downloader.model.GitHubSearchResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import jakarta.inject.Inject;

@ApplicationScoped
public class GitHubService {

    @Inject
    @RestClient
    GitHubRESTService gitHubRESTService;
    @Inject
    RepositoryManager repositoryManager;

    private final ObjectMapper objectMapper;

    private static final int PER_PAGE = 100;
    private static final int MAX_GITHUB_PAGE = 1000/PER_PAGE;   // search limit in GitHub is 1000 entities

    public GitHubService() {
        this.objectMapper = new ObjectMapper();
    }

    public void fetchRepositories(String query, String token) {
        int pageIndex = 1;
        GitHubSearchResponse response;

        do {
            response = searchRepositories(pageIndex, query, token);
            if (response != null && !response.getItems().isEmpty()) {
                repositoryManager.saveRepositories(response.getItems());
            }

            pageIndex++;
        } while (pageIndex <= MAX_GITHUB_PAGE || (response != null && !response.getItems().isEmpty()));
    }

    private GitHubSearchResponse searchRepositories(int pageIndex, String query, String token) {
        try {
            String response = gitHubRESTService.searchRepositories(query, PER_PAGE, pageIndex, String.format("token %s", token));

            return objectMapper.readValue(response, GitHubSearchResponse.class);
        } catch (Exception e) {
            System.err.println("Error while requesting GitHub API: " + e.getMessage());
            return null;
        }
    }
}

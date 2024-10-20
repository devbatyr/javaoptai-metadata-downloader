package kz.shakenov.javaoptai.metadata.downloader.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import kz.shakenov.javaoptai.metadata.downloader.model.GitHubRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

@Singleton
public class RepositoryManager {

    private final HashMap<Integer, GitHubRepository> existingRepositories;
    private final ObjectMapper objectMapper;

    private static final String FILE_PATH;

    static {
        try {
            String jarPath = RepositoryManager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            FILE_PATH = Paths.get(jarPath).getParent().resolve("github-repositories-list.json").toString();
        } catch (Exception e) {
            throw new RuntimeException("Could not determine the path to the JAR file", e);
        }
    }

    @Inject
    private RepositoryManager(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.existingRepositories = loadExistingRepositories();
    }

    public HashMap<Integer, GitHubRepository> getExistingRepositories() {
        return existingRepositories;
    }

    private HashMap<Integer, GitHubRepository> loadExistingRepositories() {
        try {
            if (Files.exists(Paths.get(FILE_PATH))) {
                List<GitHubRepository> repoList = objectMapper.readValue(new File(FILE_PATH), new TypeReference<>() {
                });
                HashMap<Integer, GitHubRepository> repoMap = new HashMap<>();
                for (GitHubRepository repo : repoList) {
                    repoMap.put(repo.getId(), repo);
                }
                return repoMap;
            } else {
                return new HashMap<>();
            }
        } catch (IOException e) {
            System.err.println("Error loading existing repositories: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public void saveRepositories(List<GitHubRepository> repositoryList) {
        try {
            int newRepoIndex = 0;
            for (GitHubRepository newRepo : repositoryList) {
                // check if repository is already in the file
                if (!existingRepositories.containsKey(newRepo.getId())) {
                    existingRepositories.put(newRepo.getId(), newRepo);
                    newRepoIndex++;
                }
            }

            objectMapper.writeValue(new File(FILE_PATH), existingRepositories.values());
            System.out.println(newRepoIndex + " new repositories saved successfully in: " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("Error when saving repositories to a file: " + e.getMessage());
        }
    }
}

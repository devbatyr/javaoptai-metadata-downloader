package kz.shakenov.javaoptai.metadata.downloader.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kz.shakenov.javaoptai.metadata.downloader.model.GitHubRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class RepositoryService {

    @Inject
    RepositoryManager repositoryManager;

    private static final String DIRECTORY_PATH;
    private static final int STATUS_FAILED = 0;
    private static final int STATUS_CLONED = 1;
    private static final int STATUS_SKIPPED = 2;

    static {
        try {
            String jarPath = RepositoryManager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            DIRECTORY_PATH = Paths.get(jarPath).getParent().resolve("repositories").toString();
        } catch (Exception e) {
            throw new RuntimeException("Could not determine the path to the JAR file", e);
        }
    }

    public void cloneAllRepositories(int threads, int timeout) {
        File cloneDir = new File(DIRECTORY_PATH);

        if (!cloneDir.exists() && !cloneDir.mkdirs()) {
            System.err.println("Failed to create directory: " + cloneDir.getAbsolutePath());
        }

        AtomicInteger failedCount = new AtomicInteger(0);
        AtomicInteger clonedCount = new AtomicInteger(0);
        AtomicInteger skippedCount = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        for (GitHubRepository repo : repositoryManager.getExistingRepositories().values()) {
            executorService.submit(() -> {
                int status = cloneRepository(String.valueOf(repo.getId()), repo.getHtml_url());
                switch (status) {
                    case STATUS_FAILED -> failedCount.incrementAndGet();
                    case STATUS_CLONED -> clonedCount.incrementAndGet();
                    case STATUS_SKIPPED -> skippedCount.incrementAndGet();
                }

                System.out.printf("\r%d repositories cloned successfully | %d repositories not cloned | %d repositories skipped",
                        clonedCount.get(), failedCount.get(), skippedCount.get());
            });
        }
        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(timeout, TimeUnit.SECONDS)) {
                System.err.println("\nFailed to complete task within the specified time");
            }
        } catch (InterruptedException e) {
            System.err.println("\nWaiting for threads to complete was interrupted: " + e.getMessage());
        }

        System.out.println();
    }

    private int cloneRepository(String repoName, String repoUrl) {
        File repoDir = new File(DIRECTORY_PATH + File.separator + repoName);

        if (!repoDir.exists()) {
            try (Git ignored = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(repoDir)
                    .setDepth(1)    // clone only last commit
                    .call()) {
                Thread.sleep(100);
                return STATUS_CLONED;
            } catch (GitAPIException | InterruptedException e) {
                return STATUS_FAILED;
            }
        } else {
            return STATUS_SKIPPED;  // repository already exists
        }
    }

}

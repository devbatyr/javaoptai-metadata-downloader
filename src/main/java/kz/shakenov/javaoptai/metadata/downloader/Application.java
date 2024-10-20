package kz.shakenov.javaoptai.metadata.downloader;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

import jakarta.inject.Inject;
import kz.shakenov.javaoptai.metadata.downloader.service.GitHubService;
import kz.shakenov.javaoptai.metadata.downloader.service.RepositoryService;

import java.util.Scanner;

@QuarkusMain
public class Application implements QuarkusApplication {

    @Inject
    GitHubService gitHubService;
    @Inject
    RepositoryService repositoryService;

    @Override
    public int run(String... args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Do you want to update the list of Java repositories for project metadata? (Y/n)");
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.equals("y")) {
            System.out.println("Enter your GitHub token:");
            String token = scanner.nextLine();
            System.out.println("Enter search query:");
            String query = scanner.nextLine();

            gitHubService.fetchRepositories(query, token);
        } else {
            System.out.println("The list of repositories has not been updated");
        }

        System.out.println("Do you want to update the repositories? (Y/n)");
        input = scanner.nextLine().trim().toLowerCase();
        if (input.equals("y")) {
            System.out.println("Enter the number of thread pools to download the repositories:");
            int threads = scanner.nextInt();
            System.out.println("Enter the maximum amount of time to download 1 repository (in seconds):");
            int timeout = scanner.nextInt();

            repositoryService.cloneAllRepositories(threads, timeout);
        } else {
            System.out.println("The repositories were not cloned");
        }

        return 0;
    }

    public static void main(String... args) {
        Quarkus.run(Application.class, args);
    }
}

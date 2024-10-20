package kz.shakenov.javaoptai.metadata.downloader.service;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@RegisterRestClient(configKey="github-api")
@Path("/search/repositories")
public interface GitHubRESTService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    String searchRepositories(@QueryParam("q") String query,
                              @QueryParam("per_page") int perPage,
                              @QueryParam("page") int page,
                              @HeaderParam("Authorization") String authHeader);

}

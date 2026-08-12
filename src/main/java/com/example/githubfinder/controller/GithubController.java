package com.example.githubfinder.controller;

import com.example.githubfinder.model.GithubRepository;
import com.example.githubfinder.model.GithubUserProfile;
import com.example.githubfinder.service.GithubService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/github")
@CrossOrigin(origins = "*")
public class GithubController {

    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getGithubUser(@PathVariable String username) {
        try {
            GithubUserProfile profile = githubService.getUserProfile(username);
            List<GithubRepository> repos = githubService.getUserRepositories(username);

            Map<String, Object> result = new HashMap<>();
            result.put("profile", profile);
            result.put("repositories", repos);

            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            if ("USER_NOT_FOUND".equals(e.getMessage())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                error.put("message", "The username '" + username + "' does not exist on GitHub.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            Map<String, String> error = new HashMap<>();
            error.put("error", "API Error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Server Error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

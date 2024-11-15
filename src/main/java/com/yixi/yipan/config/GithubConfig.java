package com.yixi.yipan.config;

import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class GithubConfig {

    @Value("${github.token}")
    private String token;

    @Bean
    public GitHub gitHub() throws IOException {
        return new GitHubBuilder()
                .withOAuthToken(token)
                .build();
    }
}

package com.deliveryworkbench.integration.mock;

import com.deliveryworkbench.integration.GitIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MockGitIntegrationService implements GitIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(MockGitIntegrationService.class);

    @Override
    public String createBranch(String repository, String branchName, String baseBranch) {
        log.info("Mocking Git System: Created branch {} in repo {} from {}", branchName, repository, baseBranch);
        return "https://mock-git.local/" + repository + "/tree/" + branchName;
    }

    @Override
    public String createPullRequest(String repository, String sourceBranch, String targetBranch, String title) {
        String prId = "PR-" + UUID.randomUUID().toString().substring(0, 4);
        log.info("Mocking Git System: Created PR {} in repo {} from {} to {} with title '{}'", 
                prId, repository, sourceBranch, targetBranch, title);
        return "https://mock-git.local/" + repository + "/pull/" + prId;
    }
}

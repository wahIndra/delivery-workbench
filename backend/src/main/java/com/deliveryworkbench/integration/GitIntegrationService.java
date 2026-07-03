package com.deliveryworkbench.integration;

public interface GitIntegrationService {

    /**
     * Creates a branch in the version control system.
     * @param repository the repository name
     * @param branchName the new branch name
     * @param baseBranch the base branch to branch off from
     * @return the branch URL or ID
     */
    String createBranch(String repository, String branchName, String baseBranch);

    /**
     * Creates a pull request in the version control system.
     * @param repository the repository name
     * @param sourceBranch the source branch
     * @param targetBranch the target branch
     * @param title the PR title
     * @return the PR URL or ID
     */
    String createPullRequest(String repository, String sourceBranch, String targetBranch, String title);
}

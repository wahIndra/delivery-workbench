package com.deliveryworkbench.integration;

public interface CICDIntegrationService {

    /**
     * Triggers a pipeline in the CI/CD system.
     * @param repository the repository name
     * @param branch the branch to run the pipeline on
     * @param environment the target environment (e.g. dev, uat, prod)
     * @return the pipeline run ID
     */
    String triggerPipeline(String repository, String branch, String environment);

    /**
     * Gets the current status of the pipeline.
     * @param pipelineId the pipeline ID
     * @return the status of the pipeline
     */
    String getPipelineStatus(String pipelineId);
}

package Utilities;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import java.io.File;
import java.net.URI;

public class JiraUtility {

    private final String jiraUrl;
    private final String username;
    private final String apiToken;
    private final String projectKey;
    private JiraRestClient restClient;

    public JiraUtility(String jiraUrl, String username, String apiToken, String projectKey) {
        this.jiraUrl = jiraUrl;
        this.username = username;
        this.apiToken = apiToken;
        this.projectKey = projectKey;
        this.restClient = getJiraRestClient();
    }

    private JiraRestClient getJiraRestClient() {
        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create(this.jiraUrl), this.username, this.apiToken);
    }

    /**
     * Creates a new bug in Jira.
     * @param issueSummary The summary/title of the bug.
     * @param issueDescription The detailed description of the bug.
     * @return The key of the newly created issue (e.g., "PROJ-123").
     */
    public String createJiraIssue(String issueSummary, String issueDescription) {
        try {
            IssueInputBuilder issueBuilder = new IssueInputBuilder(projectKey, 10005L, issueSummary); // Use your project key and Issue Type ID for "Bug"
            issueBuilder.setDescription(issueDescription);
            IssueInput newIssue = issueBuilder.build();

            String issueKey = restClient.getIssueClient().createIssue(newIssue).claim().getKey();
            LogsUtils.info("Successfully created Jira issue: " + issueKey);
            return issueKey;
        } catch (Exception e) {
            LogsUtils.error("Error creating Jira issue: " + e.getMessage());
            return null;
        }
    }

    /**
     * Attaches a file (like a screenshot) to an existing Jira issue.
     * @param issueKey The key of the issue to attach the file to.
     * @param fileToAttach The File object of the screenshot.
     */
    public void addAttachmentToIssue(String issueKey, File fileToAttach) {
        try {
            URI issueUri = URI.create(this.jiraUrl + "/rest/api/2/issue/" + issueKey);
            restClient.getIssueClient().addAttachments(issueUri, fileToAttach).claim();
            LogsUtils.info("Successfully attached screenshot to issue: " + issueKey);
        } catch (Exception e) {
            LogsUtils.error("Error attaching file to Jira issue: " + e.getMessage());
        }
    }
}
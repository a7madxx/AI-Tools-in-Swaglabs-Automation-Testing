package Utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class JiraUtility {

    private final String jiraUrl;
    private final String authHeader;
    private final String projectKey;
    private final String issueTypeId;
    private final OkHttpClient client;

    public JiraUtility(String jiraUrl, String username, String apiToken, String projectKey, String issueTypeId) {
        this.jiraUrl = jiraUrl;
        this.projectKey = projectKey;
        this.issueTypeId = issueTypeId;
        this.client = new OkHttpClient();
        // Create the Base64 authentication header
        String auth = username + ":" + apiToken;
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }

    public String createJiraIssue(String summary, String description) throws IOException {
        String url = jiraUrl + "/rest/api/3/issue";

        // Manually create the JSON payload
        String jsonPayload = "{"
                + "\"fields\": {"
                + "\"project\": {\"key\": \"" + projectKey + "\"},"
                + "\"summary\": \"" + escapeJson(summary) + "\","
                + "\"description\": {\"type\": \"doc\", \"version\": 1, \"content\": [{\"type\": \"paragraph\", \"content\": [{\"type\": \"text\", \"text\": \"" + escapeJson(description) + "\"}]}]},"
                + "\"issuetype\": {\"id\": \"" + issueTypeId + "\"}"
                + "}"
                + "}";

        RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", authHeader)
                .header("Accept", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                LogsUtils.error("Error creating Jira issue: " + response.code() + " - " + response.body().string());
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            String issueKey = jsonObject.get("key").getAsString();
            LogsUtils.info("Successfully created Jira issue: " + issueKey);
            return issueKey;
        }
    }

    public void addAttachmentToIssue(String issueKey, File fileToAttach) throws IOException {
        String url = jiraUrl + "/rest/api/3/issue/" + issueKey + "/attachments";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileToAttach.getName(),
                        RequestBody.create(fileToAttach, MediaType.parse("application/octet-stream")))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", authHeader)
                .header("X-Atlassian-Token", "no-check")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                LogsUtils.error("Error attaching file to Jira issue: " + response.code() + " - " + response.body().string());
                throw new IOException("Unexpected code " + response);
            }
            LogsUtils.info("Successfully attached screenshot to issue: " + issueKey);
        }
    }

    // Helper method to make strings safe for JSON
    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
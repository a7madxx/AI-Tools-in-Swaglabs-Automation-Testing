package Utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AiUtility {

    private static final OkHttpClient client = new OkHttpClient();

    public static String[] generateBugReport(String apiKey, String testName, Throwable throwable) throws IOException {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;        // Convert the stack trace to a string
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        // This is the NEW, USER-FOCUSED prompt
        String promptText = String.format(
                "You are a professional Quality Assurance Analyst writing a bug report for a non-technical audience (like a project manager). " +
                        "Your tone should be clear, simple, and focused on user experience. Do not use programming jargon like 'AssertionError' or 'stack trace' in the main report. " +
                        "Based on the following automated test failure, create a user-friendly bug report. " +
                        "Format the output strictly with 'SUMMARY:' on one line, and 'DESCRIPTION:' on the next.\n\n" +

                        "## Instructions for the 'Description' section:\n" +
                        "1.  **Steps to Reproduce:** Describe the actions a user would take on the website. Infer these from the test case name. For example, if the test is 'inValidLogInTC', the steps are 'Go to login page', 'Enter wrong username', etc.\n" +
                        "2.  **Expected Result:** Describe what a user should have seen happen on the website. This should be a full sentence, not just 'true' or 'false'.\n" +
                        "3.  **Actual Result:** Describe what the user actually saw, or what went wrong from their perspective. For example, 'The user was incorrectly logged in and taken to the inventory page.'\n" +
                        "4.  **Technical Note for Developers:** Create a separate, final section with this exact title. In this section *only*, you can include a brief technical note about the error message for the developers.\n\n" +

                        "--- AUTOMATED TEST FAILURE DETAILS ---\n" +
                        "Test Case Name: %s\n" +
                        "Error Message: %s\n" +
                        "Stack Trace:\n%s",
                testName,
                throwable.getMessage(),
                stackTrace
        );

        // Manually create the JSON payload
        String jsonPayload = "{\"contents\":[{\"parts\":[{\"text\": \"" + promptText + "\"}]}]}";

        RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                LogsUtils.error("Error communicating with Google AI service: " + response.code() + " - " + response.body().string());
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();

            // Parse the response to get the generated text
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            String fullResponse = jsonObject.getAsJsonArray("candidates").get(0).getAsJsonObject()
                    .getAsJsonObject("content").getAsJsonArray("parts").get(0).getAsJsonObject()
                    .get("text").getAsString();

            LogsUtils.info("AI response received successfully.");

            // Parse the response to get summary and description
            String summary = fullResponse.substring(fullResponse.indexOf("SUMMARY:") + 8, fullResponse.indexOf("DESCRIPTION:")).trim();
            String description = fullResponse.substring(fullResponse.indexOf("DESCRIPTION:") + 12).trim();

            return new String[]{summary, description};

        } catch (Exception e) {
            LogsUtils.error("Error during AI bug report generation: " + e.getMessage());
            // Fallback in case of any failure
            String summary = "[AI FAILED] - " + testName;
            String description = "AI generation failed. Raw data below:\n\nError: " + throwable.getMessage() + "\n\nStack Trace:\n" + stackTrace;
            return new String[]{summary, description};
        }
    }

    // Helper method to make strings safe for JSON
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
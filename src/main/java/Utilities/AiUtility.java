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
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;
        // Convert the stack trace to a string for the prompt
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        // ===================================================================================
        // START: THE NEW HIGH-QUALITY PROMPT
        // ===================================================================================
        String promptText = String.format(
                "You are a Senior QA Analyst at a top software company. Your task is to convert a technical failure log from an automated test into a high-quality, human-readable bug report suitable for Jira. The report must be clear enough for a non-technical project manager to understand.\n\n" +
                        "--- RULES ---\n" +
                        "1.  DO NOT use programming jargon like 'stack trace', 'exception', or 'assertion' in the user-facing sections (Steps, Expected, Actual).\n" +
                        "2.  The 'Summary' must be a concise, user-focused title.\n" +
                        "3.  The 'Steps to Reproduce' must be from the user's perspective on the website.\n" +
                        "4.  The 'Expected Result' and 'Actual Result' must describe the user experience.\n" +
                        "5.  Create a separate 'Technical Note for Developers' section at the very end to include the specific error message.\n\n" +

                        "--- GOOD OUTPUT EXAMPLE ---\n" +
                        "This is the format and quality you must follow.\n\n" +
                        "INPUT:\n" +
                        "Test Case Name: inValidLogInTC\n" +
                        "Error Message: expected [false] but found [true]\n" +
                        "Stack Trace: org.testng.Assert.fail(Assert.java:111)\n\n" +

                        "GENERATED OUTPUT:\n" +
                        "SUMMARY: User can log in with incorrect credentials\n" +
                        "DESCRIPTION:\n" +
                        "**Steps to Reproduce:**\n" +
                        "1. Navigate to the login page.\n" +
                        "2. Enter an invalid username (e.g., 'USERNAME').\n" +
                        "3. Enter an invalid password (e.g., 'PASSWORD').\n" +
                        "4. Click the 'Login' button.\n\n" +

                        "**Expected Result:**\n" +
                        "The user should see an error message about invalid credentials and should remain on the login page.\n\n" +

                        "**Actual Result:**\n" +
                        "The user was incorrectly logged in and redirected to the main inventory page.\n\n" +

                        "**Technical Note for Developers:**\n" +
                        "The test failed due to an assertion error: expected [false] but found [true].\n" +
                        "--- END OF EXAMPLE ---\n\n" +

                        "--- ACTUAL TEST FAILURE ---\n" +
                        "Now, using the rules and example above, process the following real failure data:\n\n" +
                        "Test Case Name: %s\n" +
                        "Error Message: %s\n" +
                        "Stack Trace:\n%s\n\n" +
                        "GENERATE THE BUG REPORT BELOW:\n",
                testName,
                escapeJson(throwable.getMessage()),
                escapeJson(stackTrace)
        );
        // ===================================================================================
        // END: THE NEW HIGH-QUALITY PROMPT
        // ===================================================================================

        String jsonPayload = "{\"contents\":[{\"parts\":[{\"text\": \"" + promptText + "\"}]}], " +
                "\"generationConfig\": {\"temperature\": 0.2, \"maxOutputTokens\": 800}}";

        RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(url).post(body).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                LogsUtils.error("Error communicating with Google AI service: " + response.code() + " - " + response.body().string());
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();

            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            String fullResponse = jsonObject.getAsJsonArray("candidates").get(0).getAsJsonObject()
                    .getAsJsonObject("content").getAsJsonArray("parts").get(0).getAsJsonObject()
                    .get("text").getAsString();

            LogsUtils.info("AI response received successfully.");

            String summary = fullResponse.substring(fullResponse.indexOf("SUMMARY:") + 8, fullResponse.indexOf("DESCRIPTION:")).trim();
            String description = fullResponse.substring(fullResponse.indexOf("DESCRIPTION:") + 12).trim();

            return new String[]{summary, description};

        } catch (Exception e) {
            LogsUtils.error("Error during AI bug report generation: " + e.getMessage());
            String summary = "[AI FAILED] - " + testName;
            String description = "AI generation failed. Raw data below:\n\nError: " + throwable.getMessage() + "\n\nStack Trace:\n" + stackTrace;
            return new String[]{summary, description};
        }
    }

    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b")
                .replace("\f", "\\f").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
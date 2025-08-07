package Utilities;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.Arrays;

public class AiUtility {

    /**
     * Generates a bug report using the OpenAI (ChatGPT) API.
     * @param apiKey Your OpenAI API key.
     * @param testName The name of the failed test case.
     * @param throwable The exception that caused the failure.
     * @return A String array where [0] is the summary and [1] is the description.
     */
    public static String[] generateBugReport(String apiKey, String testName, Throwable throwable) throws IOException {

        // Use a timeout to prevent waiting forever for a response
        OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(30));

        // Convert stack trace to a string
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        // The system message sets the context for the AI
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(),
                "You are an expert QA Automation Engineer creating a Jira ticket. Based on the following test failure details, " +
                        "create a professional bug report with a 'Summary' and 'Description'. " +
                        "The description should include inferred 'Steps to Reproduce', the 'Expected Result', " +
                        "the 'Actual Result' (based on the error), and a brief 'Technical Analysis' of the stack trace. " +
                        "Format the output strictly with 'SUMMARY:' on one line, and 'DESCRIPTION:' on the next, followed by the content."
        );

        // The user message contains the specific details of the failure
        final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(),
                String.format(
                        "--- FAILURE DETAILS ---\n" +
                                "Test Case: %s\n" +
                                "Error Message: %s\n" +
                                "Stack Trace:\n%s",
                        testName,
                        throwable.getMessage(),
                        stackTrace
                )
        );

        try {
            // Build the request for the AI model
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo") // A fast and capable model
                    .messages(Arrays.asList(systemMessage, userMessage))
                    .maxTokens(500) // Limit the response size
                    .n(1)
                    .build();

            // Get the response from the AI
            String fullResponse = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage().getContent();
            LogsUtils.info("AI response received successfully.");

            // Parse the response to get summary and description
            String summary = fullResponse.substring(fullResponse.indexOf("SUMMARY:") + 8, fullResponse.indexOf("DESCRIPTION:")).trim();
            String description = fullResponse.substring(fullResponse.indexOf("DESCRIPTION:") + 12).trim();

            return new String[]{summary, description};

        } catch (Exception e) {
            LogsUtils.error("Error communicating with OpenAI service: " + e.getMessage());
            // In case of AI failure, create a ticket with raw data
            String summary = "[AI FAILED] - " + testName;
            String description = "AI generation failed. Raw data below:\n\nError: " + throwable.getMessage() + "\n\nStack Trace:\n" + stackTrace;
            return new String[]{summary, description};
        } finally {
            service.shutdownExecutor(); // Clean up the connection
        }
    }
}
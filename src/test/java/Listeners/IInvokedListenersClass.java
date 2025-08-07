package Listeners;

import Utilities.AiUtility;
import Utilities.JiraUtility;
import Utilities.LogsUtils;
import Utilities.Utility;
import io.qameta.allure.Allure;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static DriverFactory.DriverFactory.getDriver;
import static Utilities.DataUtils.getPropertyData;

public class IInvokedListenersClass implements IInvokedMethodListener {

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        // No changes needed here
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        // --- Start of existing logic to attach logs ---
        File logFile = Utility.getLatestFile(LogsUtils.logsPath);
        if (logFile != null) {
            try {
                Allure.addAttachment("logs.log", Files.readString(Path.of(logFile.getPath())));
            } catch (IOException e) {
                LogsUtils.error("Could not attach log file to Allure report: " + e.getMessage());
            }
        }
        // --- End of existing logic ---


        // === START OF JIRA INTEGRATION ON FAILURE ===
        // This block only runs if the test has failed.
        if (testResult.getStatus() == ITestResult.FAILURE) {
            LogsUtils.info("Test case '" + testResult.getName() + "' failed. Initiating AI Bug Reporting process...");

            // Step 1: Take a screenshot (your existing logic) and find the file.
            Utility.takingScreenShot(getDriver(), testResult.getName());
            File latestScreenshot = Utility.getLatestFile("Test-outputs/ScreenShots/");

            // A try-catch block is essential to prevent the reporting from crashing the whole test run.
            try {
                // Step 2: Initialize the Jira utility with credentials from the properties file.
                JiraUtility jiraUtility = new JiraUtility(
                        getPropertyData("environment", "JIRA_URL"),
                        getPropertyData("environment", "JIRA_USERNAME"),
                        getPropertyData("environment", "JIRA_API_TOKEN"),
                        getPropertyData("environment", "JIRA_PROJECT_KEY")
                );

                // Step 3: Generate the bug report using the AI utility and your OpenAI key.
                LogsUtils.info("Generating bug report content with OpenAI...");
                String[] bugReportContent = AiUtility.generateBugReport(
                        getPropertyData("environment", "OPENAI_API_KEY"),
                        testResult.getName(),
                        testResult.getThrowable() // This gives the exception details to the AI
                );
                String summary = bugReportContent[0];
                String description = bugReportContent[1];

                // Step 4: Create the bug in Jira with the AI-generated content.
                String issueKey = jiraUtility.createJiraIssue(summary, description);

                // Step 5: If the issue was created successfully, attach the screenshot.
                if (issueKey != null && latestScreenshot != null) {
                    jiraUtility.addAttachmentToIssue(issueKey, latestScreenshot);
                } else {
                    if (issueKey == null) {
                        LogsUtils.error("Jira issue creation returned null. Cannot attach screenshot.");
                    }
                    if (latestScreenshot == null) {
                        LogsUtils.error("Could not find a screenshot file to attach to Jira issue.");
                    }
                }

            } catch (Exception e) {
                // Catch any unexpected exceptions during the reporting process.
                LogsUtils.error("A critical error occurred during the AI-Jira reporting process: " + e.getMessage());
                e.printStackTrace(); // Print the full stack trace for debugging.
            }
        }
        // === END OF JIRA INTEGRATION ===
    }
}
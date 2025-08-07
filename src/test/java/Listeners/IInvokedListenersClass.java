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
        File logFile = Utility.getLatestFile(LogsUtils.logsPath);
        if (logFile != null) {
            try {
                Allure.addAttachment("logs.log", Files.readString(Path.of(logFile.getPath())));
            } catch (IOException e) {
                LogsUtils.error("Could not attach log file to Allure report: " + e.getMessage());
            }
        }

        if (testResult.getStatus() == ITestResult.FAILURE) {
            LogsUtils.info("Test case '" + testResult.getName() + "' failed. Initiating AI Bug Reporting process...");
            Utility.takingScreenShot(getDriver(), testResult.getName());
            File latestScreenshot = Utility.getLatestFile("Test-outputs/ScreenShots/");

            try {
                // IMPORTANT: The Bug ID "10005" must be passed as a String now
                JiraUtility jiraUtility = new JiraUtility(
                        getPropertyData("environment", "JIRA_URL"),
                        getPropertyData("environment", "JIRA_USERNAME"),
                        getPropertyData("environment", "JIRA_API_TOKEN"),
                        getPropertyData("environment", "JIRA_PROJECT_KEY"),
                        "10005" // The ID for a "Bug" issue type
                );

                String[] bugReportContent = AiUtility.generateBugReport(
                        getPropertyData("environment", "GEMINI_FREE_API_KEY"),
                        testResult.getName(),
                        testResult.getThrowable()
                );
                String summary = bugReportContent[0];
                String description = bugReportContent[1];

                String issueKey = jiraUtility.createJiraIssue(summary, description);

                if (issueKey != null && latestScreenshot != null) {
                    jiraUtility.addAttachmentToIssue(issueKey, latestScreenshot);
                }

            } catch (Exception e) {
                LogsUtils.error("A critical error occurred during the AI-Jira reporting process: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
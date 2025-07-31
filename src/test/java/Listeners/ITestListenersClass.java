package Listeners;

import Utilities.LogsUtils;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ITestListenersClass implements ITestListener {
    public void onTestStart(ITestResult result) {
        LogsUtils.info("test case " + result.getName() + " Started");
    }

    public void onTestSuccess(ITestResult result) {
        LogsUtils.info("test case " + result.getName() + " Succeed");

    }
    public void onTestSkipped(ITestResult result) {
        LogsUtils.info("test case " + result.getName() + " Skipped");

    }
}

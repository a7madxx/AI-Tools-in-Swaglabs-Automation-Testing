package Listeners;

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


public class IInvokedListenersClass implements IInvokedMethodListener {
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {

    }

    public void afterInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        // Utility.takingFullscreenShot(getDriver(), new P02_landingPage(getDriver()).getNumberOfProductsOnCartIcon());
        File logFile = Utility.getLatestFile(LogsUtils.logsPath);
        try {
            assert logFile != null;
            Allure.addAttachment("logs.log", Files.readString(Path.of(logFile.getPath())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (testResult.getStatus() == ITestResult.FAILURE) {
            LogsUtils.info("test case " + testResult.getName() + " failed");
            Utility.takingScreenShot(getDriver(), testResult.getName()); // testResult.getName() --> get the name of test result and name the screenshot with
            //Utility.takingFullscreenShot(getDriver(), new P02_landingPage(getDriver()).getNumberOfProductsOnCartIcon());
        }
    }
}

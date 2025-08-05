package Tests;

import Listeners.IInvokedListenersClass;
import Listeners.ITestListenersClass;
import Pages.P01_LoginPage;
import Utilities.LogsUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;

import static DriverFactory.DriverFactory.*;
import static Utilities.DataUtils.getJsonData;
import static Utilities.DataUtils.getPropertyData;
import static Utilities.Utility.implicitWait;

@Listeners({IInvokedListenersClass.class, ITestListenersClass.class})
public class TC01_LoginTest {
    private final String USERNAME = getJsonData("validLogin", "usernameValue");
    private final String LOCKEDOUT_USER = getJsonData("specialUsers", "lockedOutUser");
    private final String PERFORMANCE_GLITCH_USER = getJsonData("specialUsers", "performanceGlitchUser");
    private final String PASSWORD = getJsonData("validLogin", "passWordValue");

    @BeforeMethod
    public void setup() throws IOException {
        setUpDriver(getPropertyData("environment", "Browser"));
        LogsUtils.info("starting Edge browser");
        getDriver().get(getPropertyData("environment", "LoginPage_URL"));
        LogsUtils.info("get the URL and loading the page");
        implicitWait(getDriver(), 10);
    }

    @Test
    public void validLogInTC() throws IOException {
        new P01_LoginPage(getDriver())
                .sendUserName(USERNAME)
                .sendPassword(PASSWORD)
                .logInButton();
        Assert.assertTrue(new P01_LoginPage(getDriver()).assertLogin(getPropertyData("environment", "HomePage_URL")));
    }
    @Test
    public void inValidLogInTC() throws IOException {
        new P01_LoginPage(getDriver())
                .sendUserName("USERNAME")
                .sendPassword("PASSWORD")
                .logInButton();
        Assert.assertTrue(new P01_LoginPage(getDriver()).assertLogin(getPropertyData("environment", "HomePage_URL")));
    }

    @Test
    public void lockedOutUserLoginTC() {
        new P01_LoginPage(getDriver())
        .sendUserName(LOCKEDOUT_USER)
                .sendPassword(PASSWORD) // Reuses the valid password
                .logInButton();

        String expectedErrorMessage = "Epic sadface: Sorry, this user has been locked out.";
        Assert.assertTrue(new  P01_LoginPage(getDriver()).getErrorMessage().contains(expectedErrorMessage), "The error message for a locked-out user is incorrect.");
    }

    @Test
    public void performanceGlitchUserLoginTC() throws IOException {

        new P01_LoginPage(getDriver()).
                sendUserName(PERFORMANCE_GLITCH_USER)
                .sendPassword(PASSWORD)
                .logInButton();

        Assert.assertTrue(new P01_LoginPage(getDriver()).assertLogin(getPropertyData("environment", "HomePage_URL")),
                "The performance_glitch_user should have been able to log in successfully.");
    }
    @AfterMethod
    public void quit() {
        quitDriver();
    }
}

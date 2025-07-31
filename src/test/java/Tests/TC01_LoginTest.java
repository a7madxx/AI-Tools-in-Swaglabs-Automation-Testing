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

    @AfterMethod
    public void quit() {
        quitDriver();
    }
}

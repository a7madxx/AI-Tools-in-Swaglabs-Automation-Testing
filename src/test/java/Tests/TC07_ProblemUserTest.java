package Tests;

import Listeners.IInvokedListenersClass;
import Listeners.ITestListenersClass;
import Pages.P01_LoginPage;
import Pages.P02_landingPage;
import Utilities.Utility;
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
public class TC07_ProblemUserTest {

    // Get the username from your new JSON file
    private final String PROBLEM_USER = getJsonData("specialUsers", "problemUser");
    private final String PASSWORD = getJsonData("validLogin", "passWordValue");

    @BeforeMethod
    public void setup() throws IOException {
        setUpDriver(getPropertyData("environment", "Browser"));
        getDriver().get(getPropertyData("environment", "LoginPage_URL"));
        implicitWait(getDriver(), 10);
    }


    @Test
    public void problemUserImageVerificationTC() {
        new P01_LoginPage(getDriver())
                .sendUserName(PROBLEM_USER)
                .sendPassword(PASSWORD)
                .logInButton();

        P02_landingPage landingPage = new P02_landingPage(getDriver());
        long uniqueImageCount = landingPage.getUniqueProductImageCount();

        Utility.generalWait(getDriver());
        Assert.assertEquals(uniqueImageCount, 6L,
                "For problem_user, there should be exactly 2 unique product image sources.");
    }

    @AfterMethod
    public void quit() {
        quitDriver();
    }
}
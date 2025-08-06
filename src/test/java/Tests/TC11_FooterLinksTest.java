package Tests;

import Listeners.IInvokedListenersClass;
import Listeners.ITestListenersClass;
import Pages.P01_LoginPage;
import Pages.P02_landingPage;
import Utilities.Utility;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert; // <-- Import SoftAssert

import java.io.IOException;

import static DriverFactory.DriverFactory.*;
import static Utilities.DataUtils.getJsonData;
import static Utilities.DataUtils.getPropertyData;
import static Utilities.Utility.implicitWait;

@Listeners({IInvokedListenersClass.class, ITestListenersClass.class})
public class TC11_FooterLinksTest {

    private final String USERNAME = getJsonData("validLogin", "usernameValue");
    private final String PASSWORD = getJsonData("validLogin", "passWordValue");

    @BeforeMethod
    public void setup() throws IOException {
        setUpDriver(getPropertyData("environment", "Browser"));
        getDriver().get(getPropertyData("environment", "LoginPage_URL"));
        implicitWait(getDriver(), 10);
        new P01_LoginPage(getDriver())
                .sendUserName(USERNAME)
                .sendPassword(PASSWORD)
                .logInButton();
    }

    @Test
    public void footerSocialMediaLinksNavigateCorrectlyTC() {
        SoftAssert softAssert = new SoftAssert();
        P02_landingPage landingPage = new P02_landingPage(getDriver());

        landingPage.clickTwitterLink();
        String twitterUrl = Utility.getUrlFromNewTabAndClose(getDriver());
        softAssert.assertEquals(twitterUrl, "https://x.com/saucelabs",
                "FAIL: The Twitter link did not navigate to the correct URL.");

        landingPage.clickFacebookLink();
        String facebookUrl = Utility.getUrlFromNewTabAndClose(getDriver());
        softAssert.assertEquals(facebookUrl, "https://www.facebook.com/saucelabs",
                "FAIL: The Facebook link did not navigate to the correct URL.");

        landingPage.clickLinkedInLink();
        String linkedInUrl = Utility.getUrlFromNewTabAndClose(getDriver());
        softAssert.assertEquals(linkedInUrl, "https://www.linkedin.com/company/sauce-labs/",
                "FAIL: The LinkedIn link did not navigate to the correct URL.");

        softAssert.assertAll();
    }

    @AfterMethod
    public void quit() {
        quitDriver();
    }
}
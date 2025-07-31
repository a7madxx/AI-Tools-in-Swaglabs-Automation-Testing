package Tests;

import Listeners.IInvokedListenersClass;
import Listeners.ITestListenersClass;
import Pages.P01_LoginPage;
import Pages.P02_landingPage;
import Utilities.LogsUtils;
import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.Set;

import static DriverFactory.DriverFactory.*;
import static Utilities.DataUtils.getJsonData;
import static Utilities.DataUtils.getPropertyData;
import static Utilities.Utility.*;

@Listeners({IInvokedListenersClass.class, ITestListenersClass.class})

public class TC02_LandingTest {
    private final String USERNAME = getJsonData("validLogin", "usernameValue");
    private final String PASSWORD = getJsonData("validLogin", "passWordValue");
    private Set<Cookie> cookies;

    @BeforeClass
    public void login() throws IOException {
        setUpDriver(getPropertyData("environment", "Browser"));
        LogsUtils.info("starting Edge browser");
        getDriver().get(getPropertyData("environment", "LoginPage_URL"));
        LogsUtils.info("get the URL and loading the page");
        implicitWait(getDriver(), 10);
        new P01_LoginPage(getDriver())
                .sendUserName(USERNAME)
                .sendPassword(PASSWORD)
                .logInButton();
        cookies = getAllCookies(getDriver());
        quitDriver();
    }

    @BeforeMethod
    public void setup() throws IOException {
        setUpDriver(getPropertyData("environment", "Browser"));
        LogsUtils.info("starting Edge browser");
        getDriver().get(getPropertyData("environment", "LoginPage_URL"));
        LogsUtils.info("get the URL and loading the page");
        restoreSession(getDriver(), cookies);
        getDriver().get(getPropertyData("environment", "HomePage_URL"));
        getDriver().navigate().refresh();
    }

    @Test
    public void checkingNumberOfSelectedProductsTC() {
        new P02_landingPage(getDriver()).addProductsToCart();
        Assert.assertTrue(new P02_landingPage(getDriver()).compareNumberOfSelectedProductWithCart());
    }

    @Test
    public void addingRandomNumberToProductsCartTC() {
        new P02_landingPage(getDriver()).addRandomProduct(3, 6);
        Assert.assertTrue(new P02_landingPage(getDriver()).compareNumberOfSelectedProductWithCart());
    }

    @Test
    public void clickOnCartIcon() throws IOException {
        new P02_landingPage(getDriver()).clickOnCartIcon();
        Assert.assertTrue(verifyURL(getDriver(), getPropertyData("environment", "CartIcon_URL")));
    }

    @AfterMethod
    public void quit() {
        quitDriver();
    }

    @AfterClass
    public void deleteSession() {
        cookies.clear();
    }
}

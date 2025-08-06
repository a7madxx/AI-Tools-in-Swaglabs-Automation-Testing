package Tests;

import Listeners.IInvokedListenersClass;
import Listeners.ITestListenersClass;
import Pages.P01_LoginPage;
import Pages.P02_landingPage;
import Pages.P07_ProductDetailsPage; // <-- New Page Object
import Utilities.LogsUtils;
import Utilities.Utility;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;

import static DriverFactory.DriverFactory.*;
import static Utilities.DataUtils.getJsonData;
import static Utilities.DataUtils.getPropertyData;
import static Utilities.Utility.implicitWait;

@Listeners({IInvokedListenersClass.class, ITestListenersClass.class})
public class TC10_VisualUserTest {

    private final String VISUAL_USER = getJsonData("specialUsers", "visualUser");
    private final String PASSWORD = getJsonData("validLogin", "passWordValue");
    private final String PRODUCT_TO_TEST = "Sauce Labs Fleece Jacket";

    @BeforeMethod
    public void setup() throws IOException {
        setUpDriver(getPropertyData("environment", "Browser"));
        getDriver().get(getPropertyData("environment", "LoginPage_URL"));
        implicitWait(getDriver(), 10);
    }

    // *** REPLACE the old test method with this one ***

    @Test
    public void visualUserPriceMismatchTC() {
        // Step 1: Log in as visual_user
        String priceOnMainPage = new P01_LoginPage(getDriver())
                .sendUserName(VISUAL_USER)
                .sendPassword(PASSWORD)
                .logInButton()
                .getProductPriceByName(PRODUCT_TO_TEST);

        String priceOnDetailsPage = new P02_landingPage(getDriver())
                .clickProductTitle(PRODUCT_TO_TEST)
                .getProductPrice();
        LogsUtils.info("Price on main page: " + priceOnMainPage);
        LogsUtils.info("Price on details page: " + priceOnDetailsPage);
        Assert.assertEquals(priceOnMainPage, priceOnDetailsPage,
                "For visual_user, the product price should be different between the main page and the details page.");
    }

    @AfterMethod
    public void quit() {
        quitDriver();
    }
}
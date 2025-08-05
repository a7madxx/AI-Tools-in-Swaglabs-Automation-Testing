package Tests;

import Listeners.IInvokedListenersClass;
import Listeners.ITestListenersClass;
import Pages.P01_LoginPage;
import Pages.P02_landingPage;
import Pages.P03_CartPage;
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
public class TC03_CartTest {
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
    public void ComparingPricesCartTC() throws IOException {
        String totalPrice = new P01_LoginPage(getDriver())
                .sendUserName(USERNAME)
                .sendPassword(PASSWORD)
                .logInButton().addRandomProduct(3, 6)
                .getTotalPriceOfSelectedProducts();
        new P02_landingPage(getDriver()).clickOnCartIcon();
        Assert.assertTrue(new P03_CartPage(getDriver()).comparingTotalPrices(totalPrice));
    }

    @Test
    public void removeItemFromCartTC() {
        new P01_LoginPage(getDriver())
                .sendUserName(USERNAME)
                .sendPassword(PASSWORD)
                .logInButton()
                .addRandomProduct(2, 6); // Add 2 random items

        P03_CartPage cartPage = new P02_landingPage(getDriver()).clickOnCartIcon();
        Assert.assertEquals(cartPage.getNumberOfItemsInCart(), 2, "Cart should have 2 items initially.");

        cartPage.removeFirstItem();

        // Assert that one item remains
        Assert.assertEquals(cartPage.getNumberOfItemsInCart(), 1, "Cart should have 1 item after removal.");
    }
    @Test
    public void cartStatePersistsAfterLogoutTC() {
        // Step 1: Log in and add one item to the cart
        P02_landingPage landingPage = new P01_LoginPage(getDriver())
                .sendUserName(USERNAME)
                .sendPassword(PASSWORD)
                .logInButton()
                .addRandomProduct(1, 6);
        Assert.assertEquals(landingPage.getNumberOfProductsOnCardIcon(), "1", "Cart should have one item before logout.");

        landingPage.logout();

        P02_landingPage newLandingPage = new P01_LoginPage(getDriver())
                .sendUserName(USERNAME)
                .sendPassword(PASSWORD)
                .logInButton();
        //Assert that the cart STILL has one item
        Assert.assertEquals(newLandingPage.getNumberOfProductsOnCardIcon(), "1", "Cart state should persist after logging back in.");
    }

    @AfterMethod
    public void quit() {
        quitDriver();
    }

}

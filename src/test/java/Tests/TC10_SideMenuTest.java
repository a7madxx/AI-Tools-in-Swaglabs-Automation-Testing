package Tests;

import Listeners.IInvokedListenersClass;
import Listeners.ITestListenersClass;
import Pages.P01_LoginPage;
import Pages.P02_landingPage;
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
public class TC10_SideMenuTest {

    private final String USERNAME = getJsonData("validLogin", "usernameValue");
    private final String PASSWORD = getJsonData("validLogin", "passWordValue");

    @BeforeMethod
    public void setup() throws IOException {
        // This setup runs before each test method in this class
        setUpDriver(getPropertyData("environment", "Browser"));
        getDriver().get(getPropertyData("environment", "LoginPage_URL"));
        implicitWait(getDriver(), 10);

        // Log in once for each test
        new P01_LoginPage(getDriver())
                .sendUserName(USERNAME)
                .sendPassword(PASSWORD)
                .logInButton();
    }


    @Test
    public void resetAppStateClearsCartIconTC() {
        P02_landingPage landingPage = new P02_landingPage(getDriver());

        landingPage.addRandomProduct(1, 6);

        Assert.assertEquals(landingPage.getNumberOfProductsOnCardIcon(), "1",
                "PRE-CHECK FAILED: Cart should have one item before reset.");

        landingPage.resetAppState();

        //  Assert that the cart icon is now gone (returns "0")
        Assert.assertEquals(landingPage.getNumberOfProductsOnCardIcon(), "0",
                "Cart badge should be cleared after resetting app state.");
    }

    @Test
    public void resetAppStateRevertsButtonTC() {
        P02_landingPage landingPage = new P02_landingPage(getDriver());

        int randomIndex = landingPage.addSingleRandomProductAndReturnIndex();

        Assert.assertEquals(landingPage.getButtonTextAtIndex(randomIndex), "Remove",
                "PRE-CHECK FAILED: Button at index " + randomIndex + " should say REMOVE before reset.");

        landingPage.resetAppState();

        //  Verify the button has reverted to "ADD TO CART".
        String buttonText = landingPage.getButtonTextAtIndex(randomIndex);
        Assert.assertEquals(buttonText, "Add to cart",
                "Button at index " + randomIndex + " did not revert to 'ADD TO CART'.");
    }

    @AfterMethod
    public void quit() {
        quitDriver();
    }
}
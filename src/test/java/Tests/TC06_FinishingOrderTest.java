package Tests;

import Listeners.IInvokedListenersClass;
import Listeners.ITestListenersClass;
import Pages.*;
import com.github.javafaker.Faker;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;

import static DriverFactory.DriverFactory.*;
import static Utilities.DataUtils.getJsonData;
import static Utilities.DataUtils.getPropertyData;
import static Utilities.LogsUtils.info;
import static Utilities.Utility.*;

@Listeners({IInvokedListenersClass.class, ITestListenersClass.class})
public class TC06_FinishingOrderTest {
    private final String USERNAME = getJsonData("validLogin", "usernameValue");
    private final String PASSWORD = getJsonData("validLogin", "passWordValue");
    private final String FIRSTNAME = getJsonData("information", "firstName") + "_" + getTimeStamp();
    private final String LASTNAME = getJsonData("information", "lastName") + "_" + getTimeStamp();
    private final String ZIPCODE = new Faker().number().digits(5);

    @BeforeMethod
    public void setup() throws IOException {
        setUpDriver(getPropertyData("environment", "Browser"));
        info("starting Edge browser");
        getDriver().get(getPropertyData("environment", "LoginPage_URL"));
        info("get the URL and loading the page");
        implicitWait(getDriver(), 10);
    }

    @Test
    public void FinishingPageTC() throws IOException {
        //TODO: Login with valid credentials
        new P01_LoginPage(getDriver())
                .sendUserName(USERNAME)
                .sendPassword(PASSWORD)
                .logInButton();

        //TODO: Add random products to cart and open cart icon
        new P02_landingPage(getDriver()).addRandomProduct(3, 6)
                .clickOnCartIcon();

        //TODO: Proceed to checkout from cart page
        new P03_CartPage(getDriver()).clickOnCheckButton();

        //TODO: Fill checkout information and continue
        new P04_CheckOutPage(getDriver()).fillInformationForm(FIRSTNAME, LASTNAME, ZIPCODE)
                .clickOnContinueButton();

        //TODO: Finish the order on overview page
        new P05_OverViewPage(getDriver()).clickOnFinish();

        info("first name " + FIRSTNAME + " last name " + LASTNAME + " ZipCod " + ZIPCODE);

        Assert.assertTrue(new P06_FinishingOrderPage(getDriver()).visibilityOfFinishingMessage());
    }
    @Test
    public void backHomeAfterFinishingOrderTC() throws IOException {
        // Complete the entire order flow
        new P01_LoginPage(getDriver())
                .sendUserName(USERNAME)
                .sendPassword(PASSWORD)
                .logInButton()
                .addRandomProduct(1, 6)
                .clickOnCartIcon()
                .clickOnCheckButton()
                .fillInformationForm(FIRSTNAME, LASTNAME, ZIPCODE)
                .clickOnContinueButton()
                .clickOnFinish();

        // From the confirmation page, click "Back Home"
        P06_FinishingOrderPage finishingPage = new P06_FinishingOrderPage(getDriver());
        finishingPage.clickBackHome();

        // Assert the URL is the main inventory page
        Assert.assertTrue(verifyURL(getDriver(), getPropertyData("environment", "HomePage_URL")));
    }
    // Add this new test method inside your existing TC03_CartTest.java class

    @Test
    public void cannotCompleteOrderWithEmptyCartTC() throws IOException {
        // Step 1: Log in and go directly to the cart page without adding items
        new P01_LoginPage(getDriver())
                .sendUserName(USERNAME)
                .sendPassword(PASSWORD)
                .logInButton()
                .clickOnCartIcon()
                .clickOnCheckButton()
                .fillInformationForm(FIRSTNAME, LASTNAME, ZIPCODE)
                .clickOnContinueButton()
                .clickOnFinish();

        Assert.assertFalse(new P06_FinishingOrderPage(getDriver()).visibilityOfFinishingMessage());
    }

    @AfterMethod
    public void quit() {
        quitDriver();
    }
}

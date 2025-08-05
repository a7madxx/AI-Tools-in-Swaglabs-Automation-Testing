package Tests;

import Listeners.IInvokedListenersClass;
import Listeners.ITestListenersClass;
import Pages.P01_LoginPage;
import Pages.P04_CheckOutPage;
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
public class TC04_CheckOutTest {
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
    public void fillCheckOutInformationTC() throws IOException {
        //TODO : Login & add product & open cart icon & checkout & fill information 
        new P01_LoginPage(getDriver())
                .sendUserName(USERNAME)
                .sendPassword(PASSWORD)
                .logInButton()
                .addRandomProduct(3, 6)
                .clickOnCartIcon()
                .clickOnCheckButton()
                .fillInformationForm(FIRSTNAME, LASTNAME, ZIPCODE)
                .clickOnContinueButton();

        info("first name " + FIRSTNAME + " last name " + LASTNAME + " ZipCod " + ZIPCODE);

        Assert.assertTrue(verifyURL(getDriver(), getPropertyData("environment", "Checkout_Overview_URL")));
    }
    @Test
    public void checkoutInformationValidationTC() {
        // Navigate to the checkout page
        new P01_LoginPage(getDriver())
                .sendUserName(USERNAME)
                .sendPassword(PASSWORD)
                .logInButton()
                .addRandomProduct(1, 6)
                .clickOnCartIcon()
                .clickOnCheckButton();

        P04_CheckOutPage checkOutPage = new P04_CheckOutPage(getDriver());

        // Test for missing first name
        checkOutPage.clickOnContinueButton();
        Assert.assertTrue(checkOutPage.getErrorMessage().contains("First Name is required"));

        // Test for missing last name
        checkOutPage.fillInformationForm("Test", "", "12345");
        checkOutPage.clickOnContinueButton();
        Assert.assertTrue(checkOutPage.getErrorMessage().contains("Last Name is required"));
    }

    @AfterMethod
    public void quit() {
        quitDriver();
    }
}

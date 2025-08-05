package Tests;

import Listeners.IInvokedListenersClass;
import Listeners.ITestListenersClass;
import Pages.P01_LoginPage;
import Pages.P05_OverViewPage;
import Utilities.Utility;
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
import static Utilities.Utility.getTimeStamp;
import static Utilities.Utility.implicitWait;

@Listeners({IInvokedListenersClass.class, ITestListenersClass.class})
public class TC05_OverViewTest {
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
    public void checkOutOverviewTC() throws IOException {
        //TODO : Login & add product & open cart icon & checkout & fill information
        new P01_LoginPage(getDriver())
                .sendUserName(USERNAME)
                .sendPassword(PASSWORD)
                .logInButton()
                .addRandomProduct(3, 6)
                .clickOnCartIcon()
                .clickOnCheckButton()
                .fillInformationForm(FIRSTNAME, LASTNAME, ZIPCODE)
                .clickOnContinueButton()
        ;

        info("first name " + FIRSTNAME + " last name " + LASTNAME + " ZipCod " + ZIPCODE);

        Assert.assertTrue(new P05_OverViewPage(getDriver()).comparingPrices());
    }

    @Test
    public void cancelOrderFromOverviewTC() throws IOException {
        // Navigate to the overview page
        new P01_LoginPage(getDriver())
                .sendUserName(USERNAME)
                .sendPassword(PASSWORD)
                .logInButton()
                .addRandomProduct(1, 6)
                .clickOnCartIcon()
                .clickOnCheckButton()
                .fillInformationForm(FIRSTNAME, LASTNAME, ZIPCODE)
                .clickOnContinueButton();

        // Cancel from the overview page
        P05_OverViewPage overViewPage = new P05_OverViewPage(getDriver());
        overViewPage.clickOnCancel();

        // Assert the user is returned to the inventory/home page
        Assert.assertTrue(Utility.verifyURL(getDriver(), getPropertyData("environment", "HomePage_URL")));
    }
    @AfterMethod
    public void quit() {
        quitDriver();
    }
}

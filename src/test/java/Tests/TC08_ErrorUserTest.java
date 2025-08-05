package Tests;

import Listeners.IInvokedListenersClass;
import Listeners.ITestListenersClass;
import Pages.P01_LoginPage;
import Pages.P02_landingPage;
import Utilities.Utility; // Import your Utility class for the random number generator
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
public class TC08_ErrorUserTest {

    private final String ERROR_USER = getJsonData("specialUsers", "errorUser");
    private final String PASSWORD = getJsonData("validLogin", "passWordValue");

    @BeforeMethod
    public void setup() throws IOException {
        setUpDriver(getPropertyData("environment", "Browser"));
        getDriver().get(getPropertyData("environment", "LoginPage_URL"));
        implicitWait(getDriver(), 10);
    }

    @Test
    public void errorUser_addItem_updatesButtonToRemovetTC() {
        int randomIndex = Utility.generateRandom(6);

        String buttonText = new P01_LoginPage(getDriver())
                .sendUserName(ERROR_USER)
                .sendPassword(PASSWORD)
                .logInButton()
                .clickButtonAtIndex(randomIndex)
                .getButtonTextAtIndex(randomIndex);

        Assert.assertEquals(buttonText, "REMOVE",
                "Button text should change to REMOVE after adding item at index " + randomIndex);
    }

    @Test
    public void errorUser_removeItem_updatesButtonToAddTC() {
        int randomIndex = Utility.generateRandom(6);

        String buttonText =new P01_LoginPage(getDriver())
                .sendUserName(ERROR_USER)
                .sendPassword(PASSWORD)
                .logInButton()
                .clickButtonAtIndex(randomIndex)
                .clickButtonAtIndex(randomIndex)
                .getButtonTextAtIndex(randomIndex);

        Assert.assertEquals(buttonText, "Add To Cart",
                "Button text should revert to ADD TO CART after removing item at index " + randomIndex);
    }

    @AfterMethod
    public void quit() {
        quitDriver();
    }
}
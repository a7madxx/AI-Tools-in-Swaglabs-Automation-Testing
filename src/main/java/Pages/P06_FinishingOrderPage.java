package Pages;

import Utilities.LogsUtils;
import Utilities.Utility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static Utilities.Utility.findWebElement;
import static Utilities.Utility.getText;

public class P06_FinishingOrderPage {
    private final WebDriver driver;
    private final By thanksMessage = By.className("complete-header");

    private final By backHomeButton = By.id("back-to-products");


    public P06_FinishingOrderPage(WebDriver driver) {
        this.driver = driver;
    }

    public Boolean visibilityOfFinishingMessage() {
        LogsUtils.info("Thanks Message : " + getText(driver, thanksMessage));
        return findWebElement(driver, thanksMessage).isDisplayed();
    }

    public P02_landingPage clickBackHome() {
        Utility.clickOnElement(driver, backHomeButton);
        return new P02_landingPage(driver);
    }
}

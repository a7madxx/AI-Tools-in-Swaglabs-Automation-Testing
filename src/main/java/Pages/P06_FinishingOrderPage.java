package Pages;

import Utilities.LogsUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static Utilities.Utility.findWebElement;
import static Utilities.Utility.getText;

public class P06_FinishingOrderPage {
    private final WebDriver driver;
    private final By thanksMessage = By.className("complete-header");

    public P06_FinishingOrderPage(WebDriver driver) {
        this.driver = driver;
    }

    public Boolean visibilityOfFinishingMessage() {
        LogsUtils.info("Thanks Message : " + getText(driver, thanksMessage));
        return findWebElement(driver, thanksMessage).isDisplayed();
    }
}

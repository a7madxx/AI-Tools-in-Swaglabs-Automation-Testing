package Pages;

import Utilities.Utility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static Utilities.Utility.clickOnElement;
import static Utilities.Utility.sendData;

public class P04_CheckOutPage {

    private final WebDriver driver;
    private final By firstName = By.id("first-name");
    private final By lastName = By.id("last-name");
    private final By zipCod = By.id("postal-code");
    private final By continueButton = By.id("continue");

    private final By errorMessage = By.xpath("//h3[@data-test='error']");

    public P04_CheckOutPage(WebDriver driver) {
        this.driver = driver;
    }

    public P04_CheckOutPage fillInformationForm(String fName, String lName, String zip) {
        sendData(driver, firstName, fName);
        sendData(driver, lastName, lName);
        sendData(driver, zipCod, zip);
        return this;
    }

    public P05_OverViewPage clickOnContinueButton() {
        clickOnElement(driver, continueButton);
        return new P05_OverViewPage(driver);
    }
    public String getErrorMessage() {
        return Utility.getText(driver, errorMessage);
    }

}

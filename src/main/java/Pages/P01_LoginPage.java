package Pages;

import Utilities.Utility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class P01_LoginPage {
    private final WebDriver driver;
    private final By username = By.id("user-name");
    private final By password = By.id("password");
    private final By loginButton = By.id("login-button");


    public P01_LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public P01_LoginPage sendUserName(String usernameValue) {
        Utility.sendData(driver, username, usernameValue);
        return this;
    }

    public P01_LoginPage sendPassword(String passWordValue) {
        Utility.sendData(driver, password, passWordValue);
        return this;
    }

    public P02_landingPage logInButton() {
        Utility.clickOnElement(driver, loginButton);
        return new P02_landingPage(driver);
    }

    public boolean assertLogin(String expectedValue) {
        return driver.getCurrentUrl().equals(expectedValue);
    }

}

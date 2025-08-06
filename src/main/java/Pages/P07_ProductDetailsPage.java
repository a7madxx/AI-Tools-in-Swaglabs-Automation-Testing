package Pages;

import Utilities.Utility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class P07_ProductDetailsPage {
    private final WebDriver driver;
    private final By productPrice = By.className("inventory_details_price");

    public P07_ProductDetailsPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getProductPrice() {
        return Utility.getText(driver, productPrice);
    }
}
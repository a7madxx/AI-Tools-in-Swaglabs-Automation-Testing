package Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static Utilities.LogsUtils.error;
import static Utilities.LogsUtils.info;
import static Utilities.Utility.clickOnElement;
import static Utilities.Utility.getText;

public class P03_CartPage {
    private final WebDriver driver;
    private final By pricesOfSelectedProductsLocator = By.xpath("//button[.='Remove']//preceding-sibling::div[@class='inventory_item_price']");// بيجيب التاج اللى قبله و معاه فى نفس المستوى
    private final By checkOutButtonLocator = By.id("checkout");
    private float totalPrice = 0;

    private final By cartItems = By.className("cart_item");


    public P03_CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getTotalPriceInCart() {
        try {
            List<WebElement> pricesOfSelectedProduct = driver.findElements(pricesOfSelectedProductsLocator);
            for (int i = 1; i <= pricesOfSelectedProduct.size(); i++) {
                By element = By.xpath("(//button[.='Remove']//preceding-sibling::div[@class='inventory_item_price'])[" + i + "]");
                String fullText = getText(driver, element); // get the price text of all elements products and sort in LIST 12.30$ , 25.14$
                totalPrice += Float.parseFloat(fullText.replace("$", "")); //to remove $ so we can sum the prices & convert fulltext from String to float number
            }
            info("the total price of the products in Cart " + totalPrice);
            return String.valueOf(totalPrice);
        } catch (Exception e) {
            error(e.getMessage());
            return "0";
        }
    }

    public Boolean comparingTotalPrices(String price) {
        return getTotalPriceInCart().equals(price);
    }

    public P04_CheckOutPage clickOnCheckButton() {
        clickOnElement(driver, checkOutButtonLocator);
        return new P04_CheckOutPage(driver);
    }

    public P03_CartPage removeFirstItem() {
        // A simple implementation to remove the first item found
        By firstRemoveButton = By.xpath("(//button[.='Remove'])[1]");
        clickOnElement(driver, firstRemoveButton);
        return this;
    }
    public int getNumberOfItemsInCart() {
        return driver.findElements(cartItems).size();
    }
}

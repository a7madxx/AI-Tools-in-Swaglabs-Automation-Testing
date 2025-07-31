package Pages;

import Utilities.LogsUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Set;

import static Utilities.LogsUtils.error;
import static Utilities.LogsUtils.info;
import static Utilities.Utility.*;

public class P02_landingPage {
    private static List<WebElement> allProducts;
    private static List<WebElement> selectedProducts;
    private final WebDriver driver;
    private final By numberOfProductsOnCartIcon = By.className("shopping_cart_badge");
    private final By addToCartButtonAllProducts = By.xpath("//button[(@class)]");
    private final By numberOfSelectedProduct = By.xpath("//button[.='Remove']");
    private final By CartIcon = By.className("shopping_cart_link");
    private final By pricesOfSelectedProductsLocator = By.xpath("//button[.='Remove']//preceding-sibling::div[@class='inventory_item_price']");// بيجيب التاج اللى قبله و معاه فى نفس المستوى
    private float totalPrice = 0;

    public P02_landingPage(WebDriver driver) {
        this.driver = driver;
    }

    public By getNumberOfProductsOnCartIcon() {
        return numberOfProductsOnCartIcon;
    }

    public P02_landingPage addProductsToCart() {
        allProducts = driver.findElements(addToCartButtonAllProducts);
        info("number of selected products " + allProducts.size());
        for (int i = 1; i <= allProducts.size(); i++) {
            By addToCartButtonAllProducts = By.xpath("(//button[(@class)])[" + i + "]");
            clickOnElement(driver, addToCartButtonAllProducts);
        }
        return this;
    }

    public P02_landingPage addRandomProduct(int numberOfProductNeeded, int totalNumberOfProducts) {

        Set<Integer> randomNumbers = generateUniqueNumbers(numberOfProductNeeded, totalNumberOfProducts);
        int i = 1;
        for (int random : randomNumbers) {
            info("the random number : " + random);
            By addToCartButtonAllProducts = By.xpath("(//button[(@class)])[" + random + "]");
            clickOnElement(driver, addToCartButtonAllProducts);

            //print the random product number and it's price >>>
            By element = By.xpath("(//button[.='Remove']//preceding-sibling::div[@class='inventory_item_price'])[" + i + "]");
            String fullText = getText(driver, element);
            info("the price of product is " + fullText);
            i++;
        }
        return this;
    }

    public String getNumberOfProductsOnCardIcon() {
        try {
            info("number of products in Cart icon " + getText(driver, numberOfProductsOnCartIcon));
            return getText(driver, numberOfProductsOnCartIcon);

        } catch (Exception e) {
            LogsUtils.error(e.getMessage());
        }
        return "0";
    }

    public String getNumberOfSelectedProducts() {
        try {
            selectedProducts = driver.findElements(numberOfSelectedProduct);
            info("number of selected Products " + selectedProducts.size());
            return String.valueOf(selectedProducts.size()); //String.valueOf() casting integer value to string
        } catch (Exception e) {
            LogsUtils.error(e.getMessage());
        }
        return "0";
    }

    public String getTotalPriceOfSelectedProducts() {
        try {
            List<WebElement> pricesOfSelectedProduct = driver.findElements(pricesOfSelectedProductsLocator);
            for (int i = 1; i <= pricesOfSelectedProduct.size(); i++) {
                By element = By.xpath("(//button[.='Remove']//preceding-sibling::div[@class='inventory_item_price'])[" + i + "]");
                String fullText = getText(driver, element); // get the price text of all elements products and sort in LIST 12.30$ , 25.14$
                info("the price of product is " + fullText);
                totalPrice += Float.parseFloat(fullText.replace("$", "")); //to remove $ so we can sum the prices & convert fulltext from String to float number
            }
            info("the total price of the products in page " + totalPrice);
            return String.valueOf(totalPrice);
        } catch (Exception e) {
            error(e.getMessage());
            return "0";
        }
    }

    public Boolean compareNumberOfSelectedProductWithCart() {
        return getNumberOfSelectedProducts().equals(getNumberOfProductsOnCardIcon());

    }

    //switch to CART ICON
    public P03_CartPage clickOnCartIcon() {
        clickOnElement(driver, CartIcon);
        return new P03_CartPage(driver);
    }


}

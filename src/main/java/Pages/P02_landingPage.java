package Pages;

import Utilities.LogsUtils;
import Utilities.Utility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

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

    private final By productSortContainer = By.className("product_sort_container");
    private final By inventoryItemPrices = By.className("inventory_item_price");
    private final By inventoryItemNames = By.className("inventory_item_name");
    private final By inventoryItemImg = By.className("inventory_item_img");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.id("logout_sidebar_link");
    private final By menuCloseButton = By.id("react-burger-cross-btn");
    private final By resetAppStateLink = By.id("reset_sidebar_link");



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

    public P02_landingPage sortProductsBy(String option) {
        Utility.dropDownSelect(driver, productSortContainer, option);
        return this;
    }

    public boolean isPriceSortedLowToHigh() {
        List<WebElement> priceElements = driver.findElements(inventoryItemPrices);
        float lastPrice = 0.0f;
        for (WebElement priceElement : priceElements) {
            float currentPrice = Float.parseFloat(priceElement.getText().replace("$", ""));
            if (currentPrice < lastPrice) {
                return false;
            }
            lastPrice = currentPrice;
        }
        return true;
    }
    // Add this new method to your P02_landingPage class

    /**
     * Checks if all product images on the page have the same source URL.
     * This is the specific bug associated with the 'problem_user'.
     * @return true if all image sources are identical, false otherwise.
     */
    // *** DELETE the old areAllProductImagesTheSame() method ***

    // *** ADD this new, clearer method ***

    /**
     * Waits for the inventory to load, finds all product images,
     * and returns the count of unique image source URLs.
     * @return A long representing the number of unique images found.
     */
    public long getUniqueProductImageCount() {
        // Wait until at least one product is visible before checking.
        By inventoryItemLocator = By.className("inventory_item");
        Utility.generalWait(driver).until(ExpectedConditions.visibilityOfElementLocated(inventoryItemLocator));

        // Find all the image elements.
        List<WebElement> imageElements = driver.findElements(By.className("inventory_item_img"));

        LogsUtils.info("Found " + imageElements.size() + " total product images.");

        if (imageElements.isEmpty()) {
            return 0; // Return 0 if no images are found.
        }

        // Use a Java Stream to find and count the unique 'src' attributes.
        long uniqueImageCount = imageElements.stream()
                .map(element -> element.getAttribute("src"))
                .distinct()
                .count();

        LogsUtils.info("Found " + uniqueImageCount + " unique product image URLs.");

        return uniqueImageCount;
    }
    public P02_landingPage clickButtonAtIndex(int index) {
        String buttonXPath = String.format("(//button[(@class)])[%d]", index);
        By productButtonLocator = By.xpath(buttonXPath);
        clickOnElement(driver, productButtonLocator);
        return this;
    }
    public String getButtonTextAtIndex(int index) {
        String buttonXPath = String.format("(//button[(@class)])[%d]", index);
        By productButtonLocator = By.xpath(buttonXPath);
        return getText(driver, productButtonLocator);
    }
    public P01_LoginPage logout() {
        clickOnElement(driver, menuButton);
        // It's important to wait for the logout link to be clickable
        Utility.generalWait(driver).until(ExpectedConditions.elementToBeClickable(logoutLink));
        clickOnElement(driver, logoutLink);
        return new P01_LoginPage(driver);
    }
    public int addSingleRandomProductAndReturnIndex() {
        int randomIndex = Utility.generateRandom(6);
        LogsUtils.info("Adding a single random product at index: " + randomIndex);

        By productButtonLocator = By.xpath(String.format("(//button[(@class)])[%d]", randomIndex));
        clickOnElement(driver, productButtonLocator);
        return randomIndex;
    }

    public P02_landingPage resetAppState() {
        clickOnElement(driver, menuButton);
        Utility.generalWait(driver).until(ExpectedConditions.elementToBeClickable(resetAppStateLink));
        clickOnElement(driver, resetAppStateLink);
        // It's good practice to close the menu after
        clickOnElement(driver, menuCloseButton);
        return this;
    }

}

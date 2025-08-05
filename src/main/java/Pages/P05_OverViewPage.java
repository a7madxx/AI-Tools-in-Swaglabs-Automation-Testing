package Pages;

import Utilities.LogsUtils;
import Utilities.Utility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class P05_OverViewPage {

    private final WebDriver driver;
    private final By subTotal = By.className("summary_subtotal_label");
    private final By tax = By.className("summary_tax_label");
    private final By total = By.className("summary_total_label");
    private final By finishButton = By.xpath("//button[contains(@class,'cart_button')]");
    private final By finishButtonId = By.id("finish");

    private final By cancelButton = By.id("cancel");

    public P05_OverViewPage(WebDriver driver) {
        this.driver = driver;
    }

    public Float getSubTitle() {
        LogsUtils.info(" Item total : " + Utility.getText(driver, subTotal).replace("Item total: $", " "));
        return Float.parseFloat(Utility.getText(driver, subTotal).replace("Item total: $", " "));
    }

    public Float getTax() {
        LogsUtils.info("Tax : " + Utility.getText(driver, tax).replace("Tax: $", " "));
        return Float.parseFloat(Utility.getText(driver, tax).replace("Tax: $", " "));
    }

    public Float getTotal() {
        LogsUtils.info(" Total with Tax : " + Utility.getText(driver, total).replace("Total: $", " "));
        return Float.parseFloat(Utility.getText(driver, total).replace("Total: $", " "));
    }

    public String calculateTotalPrices() {
        LogsUtils.info(" calculated Total amount : " + (getSubTitle() + getTax()));
        //String.valueOf(getSubTitle() + getTax());
        return String.format("%.2f", getSubTitle() + getTax());
    }


    public Boolean comparingPrices() {
        return calculateTotalPrices().equals(String.valueOf(getTotal()));
    }

    public P06_FinishingOrderPage clickOnFinish() {
        Utility.clickOnElement(driver, finishButton);
        return new P06_FinishingOrderPage(driver);
    }

    public P02_landingPage clickOnCancel() {
        Utility.clickOnElement(driver, cancelButton);
        return new P02_landingPage(driver);
    }
}

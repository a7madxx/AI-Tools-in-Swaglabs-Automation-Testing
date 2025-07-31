package Utilities;

import com.assertthat.selenium_shutterbug.core.Capture;
import com.assertthat.selenium_shutterbug.core.Shutterbug;
import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

public class Utility {
    private static final String ScreenShotsPath = "Test-outputs/ScreenShots/";
    //TODO: allure : allure serve - allure Generate - allure open

    /**
     * Java Documentation
     * chick on element with explicit wait
     *
     * @param driver
     * @param locator
     */
    //TODO : General Actions with Enhancement >>>>>>
    //TODO : chick on element with explicit wait
    public static void clickOnElement(WebDriver driver, By locator) {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(locator));
        driver.findElement(locator).click();
    }

    //TODO : element get Text with explicit wait
    public static String getText(WebDriver driver, By locator) {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
        return driver.findElement(locator).getText();
    }

    //TODO : send keys data with explicit wait
    public static void sendData(WebDriver driver, By locator, String text) {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
        driver.findElement(locator).sendKeys(text);
    }

    //TODO : select from Drop Down
    public static void dropDownSelect(WebDriver driver, By locator, String option) { //select visible value from drop down list
        new Select(findWebElement(driver, locator)).selectByVisibleText(option);

    }

    //TODO : Wait Methods >>>>>
    public static void implicitWait(WebDriver driver, int seconds) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
    }

    public static WebDriverWait generalWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(5)); // call + .until(action)
    }

    //TODO : Scroll down
    public static void scroll(WebDriver driver) {
        new Actions(driver).scrollByAmount(0, 200).perform();
    }

    //TODO : Scroll down to specific element
    public static void scrollingToElement(WebDriver driver, By locator) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", findWebElement(driver, locator));
    }

    //TODO : Return Web Element with By locator (By to Web Element)
    public static WebElement findWebElement(WebDriver driver, By locator) {
        return driver.findElement(locator);
    }

    //TODO : Time and Date format
    public static String getTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd-h-mm-ssa").format(new Date());
    }

    //TODO : talking a screenShots
    public static void takingScreenShot(WebDriver driver, String imageName) {

        try { // try & catch used to prevent crashing the script if taking the screenshots field
            //capture Screenshot
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            //save Screenshots to file
            File target = new File(ScreenShotsPath + "-" + getTimeStamp() + imageName + ".png");
            FileUtils.copyFile(src, target);
            //Add screenshot to allure report
            Allure.addAttachment(imageName, Files.newInputStream(Path.of(target.getPath())));
        } catch (Exception e) {
            LogsUtils.error(e.getMessage()); // or : e.printStackTrace();
        }

    }

    /**
     * Taking a fullscreenShot
     *
     * @param driver
     * @param locator
     */
    //TODO : Taking a screenshot for fullscreen
    public static void takingFullscreenShot(WebDriver driver, By locator) {
        try {
            Shutterbug.shootPage(driver, Capture.FULL_SCROLL)
                    .highlight(findWebElement(driver, locator))
                    .save(ScreenShotsPath);
        } catch (Exception e) {
            LogsUtils.error(e.getMessage());
        }
    }

    //TODO : taking a screenShots for specific element
    public static void takingScreenShotForElement(WebDriver driver, By locator, String imageName) throws IOException {

        File src = (driver.findElement(locator)).getScreenshotAs(OutputType.FILE);
        File target = new File(ScreenShotsPath + imageName + ".png");
        FileUtils.copyFile(src, target);
        Allure.addAttachment(imageName, Files.newInputStream(Path.of(target.getPath())));

    }

    //TODO : generate Random numbers
    public static int generateRandom(int upperBound) { // 5 >> (0,1,2,3,4)
        return new Random().nextInt(upperBound) + 1; //allow redundancy >> 1 , 2 ,1 , 2 ,1
    }

    //TODO : generate unique Random numbers (set list unique elements)
    public static Set<Integer> generateUniqueNumbers(int numberOfProductNeeded, int totalNumberOfProducts) {
        Set<Integer> generatedNumbers = new HashSet<>();
        while (generatedNumbers.size() < numberOfProductNeeded) {  // 3 numbers of 25 total
            int randomNumber = generateRandom(totalNumberOfProducts); // generate random value
            generatedNumbers.add(randomNumber); // add value to SET >> ignore if duplicated
        }
        return generatedNumbers;
    }

    public static Boolean verifyURL(WebDriver driver, String expectedUrl) {
        try {
            generalWait(driver).until(ExpectedConditions.urlToBe(expectedUrl));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //TODO: Get the latest (most recently modified) file from a folder
    public static File getLatestFile(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        assert files != null;
        if (files.length == 0)
            return null;
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files[0];
    }

    //TODO: Retrieve all cookies from the current WebDriver session
    public static Set<Cookie> getAllCookies(WebDriver driver) {
        return driver.manage().getCookies();
    }

    //TODO: Set cookies in the current WebDriver session
    public static void restoreSession(WebDriver driver, Set<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            driver.manage().addCookie(cookie);
        }
    }

    //TODO: Upload a file using the input element (type="file")
    public static void uploadingFile(WebDriver driver, By locator, String path) {
        driver.findElement(locator).sendKeys(path);  //tagName --> input & type --> file
    }
}

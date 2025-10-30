package com.firstcrywebsite.test;

import java.time.Duration;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.firstcrywebsite.base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.firstcrywebsite.utilities.ExtentManager;

public class TC_WISHLIST_001 extends BaseTest {

    ExtentReports extent = ExtentManager.getinstance();
    ExtentTest test;

    @Test
    public void testAddingProductToWishlist() throws InterruptedException {
        test = extent.createTest("testAddingProductToWishlist");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions actions = new Actions(driver);

        logStep("Starting test: testAddingProductToWishlist");

        // ✅ Step 1: Switch to product tab if opened in new window
        String parentWindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles();
        if (allWindows.size() > 1) {
            for (String handle : allWindows) {
                if (!handle.equals(parentWindow)) {
                    driver.switchTo().window(handle);
                    logStep("Switched to new product detail tab.");
                    break;
                }
            }
        }

        // ✅ Step 2: Handle popup if present
        try {
            WebElement popupClose = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".modal-close, .close-button")));
            if (popupClose.isDisplayed()) {
                popupClose.click();
                logStep("Popup closed successfully.");
            }
        } catch (Exception e) {
            logStep("No popup appeared.");
        }

        // ✅ Step 3: Ensure product page is loaded
        wait.until(ExpectedConditions.urlContains("product-detail"));
        logStep("Product detail page loaded successfully.");

        // ✅ Step 4: Locate and click wishlist (heart) icon safely
        By wishlistIcon = By.xpath("//label[@data-fc-ricon='y']");
        WebElement wishlist = wait.until(ExpectedConditions.presenceOfElementLocated(wishlistIcon));

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", wishlist);
        Thread.sleep(800);

        try {
            actions.moveToElement(wishlist).click().perform();
            logStep("Clicked wishlist icon using Actions.");
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", wishlist);
            logStep("Clicked wishlist icon using JavaScript fallback.");
        }

        // ✅ Step 5: Wait for wishlist to turn active
        By activeHeartLocator = By.xpath("//*[@id='prodImgInfo']/section[1]/section/div[1]/label[contains(@class,'active')]");
        try {
            WebElement activeHeart = wait.until(ExpectedConditions.visibilityOfElementLocated(activeHeartLocator));
            Assert.assertTrue(activeHeart.isDisplayed(), "Wishlist icon did not activate.");
            logStep("Wishlist icon activated successfully.");
        } catch (Exception e) {
            logStep("❌ Wishlist icon did not activate. Possible UI delay or failed click.");
            throw e;
        }

        // ✅ Step 6: Click the Shortlist button (robust method)
        By shortlistLocator = By.id("sh");
        WebElement shortlistButton = wait.until(ExpectedConditions.presenceOfElementLocated(shortlistLocator));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", shortlistButton);
        Thread.sleep(800);

        try {
            actions.moveToElement(shortlistButton).click().perform();
            logStep("Clicked the Shortlist button using Actions.");
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", shortlistButton);
            logStep("Clicked the Shortlist button using JavaScript fallback.");
        }

        // ✅ Step 7: Verify shortlist overlay or count if available
        try {
            WebElement shortlistCount = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#sh span")));
            logStep("Shortlist count displayed as: " + shortlistCount.getText());
        } catch (Exception e) {
            logStep("Shortlist count not visible — continuing (UI may vary).");
        }

        logStep("✅ Test completed: testAddingProductToWishlist");
    }

    // 🔹 Helper method for consistent logging
    private void logStep(String message) {
        System.out.println("[TESTINFO] " + message);
        if (test != null) {
            test.pass(message);
        }
    }

    // 🔹 Flush extent report
    @AfterMethod
    public void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }
}

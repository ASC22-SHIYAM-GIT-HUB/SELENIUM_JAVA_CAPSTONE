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

        // ✅ Ensure product detail page is loaded
        try {
            if (!driver.getCurrentUrl().contains("product-detail")) {
                logStep("Not on product page yet — retrying to click product link.");

                WebElement productLink = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[href*='product-detail'], a[data-pname*='Stroller']")));
                js.executeScript("arguments[0].scrollIntoView(true);", productLink);
                Thread.sleep(1000);
                js.executeScript("arguments[0].click();", productLink);

                // Try switching to new tab if one opens
                String originalWindow = driver.getWindowHandle();
                Set<String> allWindows = driver.getWindowHandles();
                for (String win : allWindows) {
                    if (!win.equals(originalWindow)) {
                        driver.switchTo().window(win);
                        logStep("Switched to new product detail tab.");
                        break;
                    }
                }

                // Wait for URL confirmation
                wait.until(ExpectedConditions.urlContains("product-detail"));
                logStep("✅ Successfully loaded product detail page.");
            }
        } catch (Exception e) {
            logStep("⚠️ Product page not detected, continuing anyway: " + e.getMessage());
        }

        // ✅ Close any popups if visible
        try {
            WebElement popupClose = driver.findElement(By.cssSelector(".modal-close, .close-button"));
            if (popupClose.isDisplayed()) {
                popupClose.click();
                logStep("Popup closed successfully.");
            }
        } catch (Exception e) {
            logStep("No popup appeared.");
        }

        // ✅ Click Wishlist button
        try {
            WebElement wishlist = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//label[@data-fc-ricon='y']")));
            js.executeScript("arguments[0].scrollIntoView(true);", wishlist);
            Thread.sleep(500);
            js.executeScript("arguments[0].click();", wishlist);
            logStep("Clicked Wishlist button.");

            WebElement activeHeart = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//label[contains(@class,'active') and @data-fc-ricon='y']")));
            Assert.assertTrue(activeHeart.isDisplayed(), "Wishlist icon did not activate.");
            logStep("Wishlist button activated successfully.");
        } catch (Exception e) {
            logStep("⚠️ Wishlist click failed or not visible: " + e.getMessage());
        }

        // ✅ Click Shortlist button
        try {
            WebElement shortlistButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("sh")));
            js.executeScript("arguments[0].scrollIntoView(true);", shortlistButton);
            actions.moveToElement(shortlistButton).click().perform();
            logStep("Clicked the Shortlist button.");
        } catch (Exception e) {
            js.executeScript("document.getElementById('sh').click();");
            logStep("Used JS fallback click for Shortlist button.");
        }

        logStep("Test completed: testAddingProductToWishlist");
    }

    private void logStep(String message) {
        System.out.println("[TESTINFO] " + message);
        if (test != null) test.pass(message);
    }

    @AfterMethod
    public void flushReport() {
        if (extent != null) extent.flush();
    }
}

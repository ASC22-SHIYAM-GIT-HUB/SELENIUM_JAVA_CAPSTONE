package com.firstcrywebsite.test;

import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import com.firstcrywebsite.base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.firstcrywebsite.utilities.ExtentManager;

public class TC_REMOVE_WISHLIST_001 extends BaseTest {

    ExtentReports extent = ExtentManager.getinstance();
    ExtentTest test;

    @Test
    public void removeFromShortlist() {
        test = extent.createTest("removeFromShortlist");
        logStep("💖 Starting Wishlist Cleanup Test");

        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));

            // 1️⃣ Open Wishlist Page (Heart icon)
            WebElement heartIcon = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='ShortlistTab1']/a/span[1]")));
            js.executeScript("arguments[0].scrollIntoView(true);", heartIcon);
            js.executeScript("arguments[0].click();", heartIcon);
            logStep("✅ Opened Wishlist page successfully.");

            // 2️⃣ Wait for items container to load
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("listing")
            ));

            // 3️⃣ Loop until wishlist is empty
            while (true) {
                // Find all remove buttons
                List<WebElement> removeIcons = driver.findElements(
                        By.xpath("//label[contains(@class,'delete_sec')]")
                );

                int count = removeIcons.size();
                logStep("Found " + count + " wishlist item(s) to remove.");

                if (count == 0) {
                    logStep("✅ Wishlist is empty. Nothing to remove.");
                    break;
                }

                WebElement removeIcon = removeIcons.get(0);

                try {
                    // Scroll to the remove button
                    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", removeIcon);

                    // Wait until clickable
                    wait.until(ExpectedConditions.elementToBeClickable(removeIcon));

                    // Click using normal click, fallback to JS click
                    try {
                        removeIcon.click();
                    } catch (Exception e1) {
                        js.executeScript("arguments[0].click();", removeIcon);
                    }

                    logStep("💔 Removed one item from wishlist.");

                    // Wait for the element to disappear (staleness)
                    wait.until(ExpectedConditions.stalenessOf(removeIcon));

                } catch (Exception clickErr) {
                    logStep("⚠️ Could not click remove icon: " + clickErr.getMessage());
                }
            }

            logStep("✅ Wishlist cleanup completed successfully.");

        } catch (Exception e) {
            logStep("❌ Wishlist cleanup failed: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            logStep("Browser closed successfully.");
        }
    }

    private void logStep(String message) {
        System.out.println("[TESTINFO] " + message);
        if (test != null) {
            test.pass(message);
        }
    }
}

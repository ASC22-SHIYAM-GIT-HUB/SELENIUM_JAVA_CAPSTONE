package com.firstcrywebsite.test;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import com.firstcrywebsite.base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.firstcrywebsite.utilities.ExtentManager;

public class TC_REMOVE_CART_001 extends BaseTest {

    ExtentReports extent = ExtentManager.getinstance(); // singleton ExtentReports
    ExtentTest test; // one test per method

    @Test
    public void removeFromCart() {
        // Create ExtentTest for this method
        test = extent.createTest("removeFromCart");
        logStep("🛒 Starting Cart Cleanup Test (Already on Cart Page)");

        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // ✅ Loop until cart is empty
            while (true) {
                // Find all 'REMOVE' buttons — the parent divs are clickable
                List<WebElement> removeButtons = driver.findElements(
                        By.xpath("//div[contains(@class,'shortcomm') and .//span[normalize-space(text())='REMOVE']]")
                );

                int count = removeButtons.size();
                logStep("Found " + count + " REMOVE buttons.");

                if (count == 0) {
                    logStep("✅ Cart is empty. Nothing to remove.");
                    break;
                }

                // Click the first remove button
                WebElement removeBtn = removeButtons.get(0);

                try {
                    js.executeScript("arguments[0].scrollIntoView(true);", removeBtn);
                    js.executeScript("arguments[0].click();", removeBtn);
                    logStep("🗑️ Removed one item from cart.");

                    // Wait a bit for the DOM/cart to refresh
                    Thread.sleep(2000);

                } catch (Exception clickErr) {
                    logStep("⚠️ Could not click remove button: " + clickErr.getMessage());
                }
            }

            logStep("✅ Cart cleanup completed successfully.");

        } catch (Exception e) {
            logStep("❌ Cart cleanup failed: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            // Optional: log test end like spoofwislist
            logStep("Deleted successfully.");
        }
    }

    // Helper method to log steps (same as spoofwislist)
    private void logStep(String message) {
        System.out.println("[TESTINFO] " + message);
        if (test != null) {
            test.pass(message);
        }
    }
}

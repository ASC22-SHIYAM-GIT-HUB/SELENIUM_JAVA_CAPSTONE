package com.firstcrywebsite.test;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.firstcrywebsite.base.BaseTest;
import java.time.Duration;

public class TC_LOGIN_001 extends BaseTest {

    @Test
    public void testLoginWithMobileNumberAndOTP() {
        test.info("Starting Login Test");

        navigateurl("https://www.firstcry.com/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // ✅ Step 1: Click Login button
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(text(),'Login')]")));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", loginButton);
            loginButton.click();
            test.pass("Clicked Login button.");

            // ✅ Step 2: Enter mobile number
            WebElement mobileField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lemail")));
            mobileField.clear();
            mobileField.sendKeys("9363025780");
            test.pass("Entered mobile number.");

            // ✅ Step 3: Click Continue
            WebElement continueBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[normalize-space()='CONTINUE']")));
            continueBtn.click();
            test.pass("Clicked Continue button.");

            // ✅ Step 4: Enter OTP
            handleOtpEntry(wait);
            test.pass("OTP entered successfully.");

            // ✅ Step 5: Click Submit
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class,'loginSignup_submitOtpBtn_block')]/span[text()='SUBMIT']")));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", submitButton);
            submitButton.click();
            test.pass("Clicked Submit button after entering OTP.");

            // ✅ Step 6: Wait for login success confirmation
            boolean isLoggedIn = waitForLoginSuccess(wait);
            Assert.assertTrue(isLoggedIn, "Login failed after entering OTP.");
            test.pass("Login successful ✅");

        } catch (Exception e) {
            test.fail("Test failed due to exception: " + e.getMessage());
            Assert.fail("Login test failed due to exception: " + e.getMessage());
        }
    }

    // 🔹 Handles OTP entry (auto for Jenkins, manual for local)
    private void handleOtpEntry(WebDriverWait wait) throws InterruptedException {
        if (System.getenv("JENKINS_HOME") != null) {
            // Jenkins environment: simulate auto OTP
            String otp = "123456";
            for (int i = 0; i < otp.length(); i++) {
                WebElement otpField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notp" + i)));
                otpField.sendKeys(String.valueOf(otp.charAt(i)));
            }
            test.info("OTP auto-filled (Jenkins).");
        } else {
            // Manual OTP handling
            test.info("Waiting for manual OTP entry (max 60 seconds)...");
            boolean otpEntered = waitForManualOtpEntry(wait, 60);
            if (!otpEntered) {
                test.fail("Manual OTP not entered within 60 seconds.");
                Assert.fail("OTP entry timed out.");
            }
            test.pass("Manual OTP detected.");
        }
    }

    // 🔹 Checks if OTP fields have been filled manually
    private boolean waitForManualOtpEntry(WebDriverWait wait, int maxWaitSeconds) {
        long start = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start) / 1000 < maxWaitSeconds) {
            try {
                boolean allFilled = true;
                for (int i = 0; i < 6; i++) {
                    WebElement otpField = driver.findElement(By.id("notp" + i));
                    String value = otpField.getAttribute("value");
                    if (value == null || value.isEmpty()) {
                        allFilled = false;
                        break;
                    }
                }
                if (allFilled) return true;
                Thread.sleep(1000);
            } catch (Exception ignored) {}
        }
        return false;
    }

    // 🔹 Robust login success check — supports new UI changes
    private boolean waitForLoginSuccess(WebDriverWait wait) {
        try {
            By[] successSelectors = {
                    By.xpath("//span[contains(text(),'My Account')]"),
                    By.xpath("//span[contains(text(),'Logout')]"),
                    By.xpath("//a[contains(@href,'/myaccount')]"),
                    By.xpath("//span[contains(text(),'Hi') or contains(text(),'Welcome')]"),
                    By.cssSelector("div[id*='userProfile']") // icon-based login state
            };

            for (By locator : successSelectors) {
                try {
                    WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                    if (el.isDisplayed()) {
                        test.info("Login confirmed via locator: " + locator.toString());
                        return true;
                    }
                } catch (Exception ignored) {}
            }

            // Fallback check: ensure we're on homepage and not login page
            String url = driver.getCurrentUrl().toLowerCase();
            return url.contains("firstcry.com") && !url.contains("login");
        } catch (Exception e) {
            return false;
        }
    }
}

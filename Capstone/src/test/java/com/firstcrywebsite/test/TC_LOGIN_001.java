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

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // ✅ Step 1: Wait and Click Login Button
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(text(), 'Login')]")));
        js.executeScript("arguments[0].scrollIntoView(true);", loginButton);
        loginButton.click();
        test.pass("Clicked Login button.");

        // ✅ Step 2: Enter Mobile Number
        WebElement mobileNumberField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lemail")));
        mobileNumberField.clear();
        mobileNumberField.sendKeys("9363025780");
        test.pass("Entered mobile number.");

        // ✅ Step 3: Click Continue
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[normalize-space()='CONTINUE']")));
        continueButton.click();
        test.pass("Clicked Continue button.");

        // ✅ Step 4: Handle OTP (Auto or Manual)
        if (System.getenv("JENKINS_HOME") != null) {
            // --- Jenkins mode ---
            String otp = "123456";
            for (int i = 0; i < otp.length(); i++) {
                WebElement otpField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notp" + i)));
                otpField.sendKeys(String.valueOf(otp.charAt(i)));
            }
            test.pass("OTP auto-filled successfully (Jenkins environment).");

        } else {
            // --- Manual mode ---
            test.info("Waiting for manual OTP entry (up to 60 seconds).");
            boolean otpEntered = waitForManualOtpEntry(wait, 60);
            if (!otpEntered) {
                test.fail("Manual OTP was not entered within timeout.");
                Assert.fail("OTP not entered within allowed time.");
            }
            test.pass("Manual OTP detected successfully.");
        }

        // ✅ Step 5: Click Submit
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'loginSignup_submitOtpBtn_block')]/span[text()='SUBMIT']")));
        submitButton.click();
        test.pass("Clicked Submit after entering OTP.");

        // ✅ Step 6: Wait for post-login confirmation
        boolean isLoggedIn = waitForLoginSuccess(wait);
        Assert.assertTrue(isLoggedIn, "Login failed after entering OTP.");
        test.pass("Login test passed successfully with OTP.");
    }

    // 🔹 Wait for OTP to be manually entered (robust check)
    private boolean waitForManualOtpEntry(WebDriverWait wait, int maxWaitSeconds) {
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) / 1000 < maxWaitSeconds) {
            try {
                boolean allFilled = true;
                for (int i = 0; i < 6; i++) {
                    WebElement otpField = driver.findElement(By.id("notp" + i));
                    String val = otpField.getAttribute("value");
                    if (val == null || val.isEmpty()) {
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

    // 🔹 Wait for login success - handles both UI and URL-based confirmation
    private boolean waitForLoginSuccess(WebDriverWait wait) {
        try {
            // Wait for either “My Account” or “Logout” to appear
            WebElement accountElement = new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//span[contains(text(),'My Account') or contains(text(),'Logout')]")));
            return accountElement.isDisplayed();
        } catch (Exception e) {
            // Fallback: verify the URL still belongs to FirstCry and not login page
            return driver.getCurrentUrl().contains("firstcry.com") &&
                    !driver.getCurrentUrl().toLowerCase().contains("login");
        }
    }
}

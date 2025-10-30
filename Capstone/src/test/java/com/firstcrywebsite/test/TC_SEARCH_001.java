package com.firstcrywebsite.test;

import java.time.Duration;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.firstcrywebsite.base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.firstcrywebsite.utilities.ExtentManager;

public class TC_SEARCH_001 extends BaseTest {

    ExtentReports extent = ExtentManager.getinstance(); // Singleton ExtentReports
    ExtentTest test; // One test per method

    @Test
    public void searchAndNavigateToProduct() {
        // Only one ExtentTest per test method
        test = extent.createTest("searchAndNavigateToProduct");

        logStep("Starting test: searchAndNavigateToProduct");

        // Navigate to FirstCry homepage
         navigateurl("https://www.firstcry.com/");
        logStep("Navigated to FirstCry homepage");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions actions = new Actions(driver);

        // Step 1: Search for the product
        logStep("Locating search box...");
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("search_box")));
        searchBox.sendKeys("Babyhug Cosy Cosmo Stroller");

        logStep("Entering product name and clicking search button...");
        WebElement searchBtn = driver.findElement(By.cssSelector(".search-button"));
        js.executeScript("arguments[0].click();", searchBtn);

        // Step 2: Click on the product link
        logStep("Waiting for product link to be clickable...");
        WebElement product = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@title,'Babyhug Cosy Cosmo Stroller')]")
        ));
        logStep("Scrolling to product link...");
        js.executeScript("arguments[0].scrollIntoView(true);", product);
        logStep("Clicking on product link...");
        js.executeScript("arguments[0].click();", product);

        // Step 3: Handle new tab if opened
        String originalTab = driver.getWindowHandle();
        Set<String> allTabs = driver.getWindowHandles();
        for (String tab : allTabs) {
            if (!tab.equals(originalTab)) {
                driver.switchTo().window(tab);
                logStep("Switched to new tab: " + tab);
                break;
            }
        }

        // Step 4: Wait for the product page to load
        logStep("Waiting for product page to load...");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("prodImgInfo")));
        logStep("✅ Navigated to product page: " + driver.getCurrentUrl());

        logStep("Test completed: searchAndNavigateToProduct");
    }

    // Helper method to log steps both in console and ExtentReports
    private void logStep(String message) {
        System.out.println("[TESTINFO] " + message);
        if (test != null) {
            test.pass(message);
        }
    }

    // Flush ExtentReports after each test
    @AfterMethod
    public void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }
}

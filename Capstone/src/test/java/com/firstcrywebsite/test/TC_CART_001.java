package com.firstcrywebsite.test;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.firstcrywebsite.base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.firstcrywebsite.utilities.ExtentManager;

public class TC_CART_001 extends BaseTest {

    ExtentReports extent = ExtentManager.getinstance(); // singleton ExtentReports
    ExtentTest test; // one test per method

    @Test
    public void testAddingProductToAddtocart() throws InterruptedException {
        test = extent.createTest("testAddingProductToAddtocart");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Step 8: Verify Shortlist count incremented
        WebElement shortlistCount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#sh span")));
        wait.until(ExpectedConditions.textToBePresentInElement(shortlistCount, "(1)"));
        Assert.assertEquals(shortlistCount.getText(), "(1)", "Shortlist count did not increment!");
        logStep("Shortlist button clicked and count verified.");

        WebElement checkbox = driver.findElement(By.cssSelector("input[data-id='700000']"));
        checkbox.click();
        logStep("Selected the product checkbox.");

        WebElement addToCartBtn = driver.findElement(By.xpath("//div[text()='Add to cart']"));
        addToCartBtn.click();
        logStep("Clicked 'Add to cart' button.");

        // Wait until the cart count updates
        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement cartCount = wait1.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#cart_TotalCount .prodQuant")
        ));

        // Wait for the text to become "1"
        wait1.until(ExpectedConditions.textToBePresentInElement(cartCount, "1"));

        // Now assert
        Assert.assertEquals(cartCount.getText(), "1", "Cart count did not update!");
        logStep("Cart count updated successfully.");

        // Click the cart icon to go to the cart page
        WebElement cartIcon = driver.findElement(By.id("cart_TotalCount"));
        cartIcon.click();
        logStep("Navigated to the cart page successfully.");

        logStep("Test completed: testAddingProductToAddtocart");
    }

    // Helper method to log steps
    private void logStep(String message) {
        System.out.println("[TESTINFO] " + message);
        if (test != null) {
            test.pass(message);
        }
    }

    // Flush report after test
    @AfterMethod
    public void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }
}

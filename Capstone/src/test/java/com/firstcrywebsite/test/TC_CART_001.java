package com.firstcrywebsite.test;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

    ExtentReports extent = ExtentManager.getinstance();
    ExtentTest test;

    @Test
    public void testAddingProductToAddtocart() throws InterruptedException {
        test = extent.createTest("testAddingProductToAddtocart");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // ✅ Step 1: Verify shortlist count updated
        WebElement shortlistCount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#sh span")));
        wait.until(ExpectedConditions.textToBePresentInElement(shortlistCount, "(1)"));
        Assert.assertEquals(shortlistCount.getText(), "(1)", "Shortlist count did not increment!");
        logStep("Shortlist count verified as (1).");

        // ✅ Step 2: Select the product checkbox safely
        By checkboxLocator = By.cssSelector("input[data-id='700000']");
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(checkboxLocator));

        try {
            // Scroll and try normal click
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", checkbox);
            wait.until(ExpectedConditions.elementToBeClickable(checkbox));
            checkbox.click();
            logStep("Clicked the product checkbox directly.");
        } catch (Exception e) {
            logStep("Standard click failed; attempting JS click. Error: " + e.getMessage());
            js.executeScript("arguments[0].click();", checkbox);
            logStep("Clicked the product checkbox using JavaScript.");
        }

        // ✅ Step 3: Click “Add to Cart” button
        By addToCartLocator = By.xpath("//div[normalize-space()='Add to cart']");
        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(addToCartLocator));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", addToCartBtn);

        try {
            addToCartBtn.click();
            logStep("Clicked 'Add to cart' button normally.");
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", addToCartBtn);
            logStep("Clicked 'Add to cart' button using JavaScript.");
        }

        // ✅ Step 4: Verify Cart count update
        WebElement cartCount = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#cart_TotalCount .prodQuant")
        ));
        wait.until(ExpectedConditions.textToBePresentInElement(cartCount, "1"));
        Assert.assertEquals(cartCount.getText().trim(), "1", "Cart count did not update!");
        logStep("Cart count verified successfully as 1.");

        // ✅ Step 5: Navigate to Cart Page
        WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(By.id("cart_TotalCount")));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", cartIcon);
        cartIcon.click();
        logStep("Navigated to cart page successfully.");

        logStep("✅ Test completed: testAddingProductToAddtocart");
    }

    // 🔹 Helper logging method
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

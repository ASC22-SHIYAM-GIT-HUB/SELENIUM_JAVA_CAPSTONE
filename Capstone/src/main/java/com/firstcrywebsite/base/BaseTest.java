package com.firstcrywebsite.base;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.firstcrywebsite.utilities.ExtentManager;
import com.firstcrywebsite.utilities.Screenshots;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {
    
    protected static WebDriver driver;
    protected static ExtentReports extent;
    protected static ExtentTest test;

    @BeforeSuite
    public void setupSuite() {
        // Initialize ExtentReports once for the suite
        extent = ExtentManager.getinstance();

        // Setup browser once for all tests
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        System.out.println("🚀 Browser launched once for the entire suite");
    }

    @BeforeMethod
    public void setupTest(Method method) {
        // Create a test in ExtentReports for each @Test
        test = extent.createTest(method.getName());
    }

    @AfterMethod
    public void tearDownMethod(ITestResult result) throws IOException {
        // Capture screenshot on failure
        if (result.getStatus() == ITestResult.FAILURE) {
            String screenshotPath = Screenshots.Capture(driver, result.getName());
            test.fail("Test Failed", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("Test Passed");
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.skip("Test Skipped");
        }
    }

    @AfterSuite
    public void tearDownSuite() {
        // Close browser after suite
        if (driver != null) {
            driver.quit();
            System.out.println("🧹 Browser closed after entire suite completed");
        }

        // Flush ExtentReports
        if (extent != null) {
            extent.flush();
            System.out.println("📄 Extent report flushed");
        }
    }

    // Utility method to navigate to a URL
    public void navigateurl(String url) {
        driver.get(url);
    }
}

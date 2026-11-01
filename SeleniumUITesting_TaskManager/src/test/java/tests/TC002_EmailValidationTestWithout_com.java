
package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

public class TC002_EmailValidationTestWithout_com extends BaseTest {

    @Test
    public void testEmailWithoutCom() {

        // Open registration page
        driver.get("http://localhost:8000/register.html");

        // Enter registration details
        driver.findElement(By.id("name")).sendKeys("Harshi");

        WebElement emailField = driver.findElement(By.id("email"));
        emailField.sendKeys("harshi@gmail");

        driver.findElement(By.id("password")).sendKeys("Test123");

        // Check browser validation
        JavascriptExecutor js = (JavascriptExecutor) driver;

        boolean emailValid = (boolean) js.executeScript(
                "return arguments[0].checkValidity();",
                emailField
        );

        String validationMessage = (String) js.executeScript(
                "return arguments[0].validationMessage;",
                emailField
        );

        System.out.println("Email: " + emailField.getAttribute("value"));
        System.out.println("Email Valid: " + emailValid);
        System.out.println("Browser Validation Message: " + validationMessage);

        // Submit the form
        driver.findElement(By.id("register-form"))
                .findElement(By.cssSelector("button[type='submit']"))
                .click();

        // Wait briefly for application response
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Test interrupted");
        }

        String pageText = driver.findElement(By.tagName("body")).getText();

        System.out.println("Application Response: " + pageText);
        System.out.println("Current URL: " + driver.getCurrentUrl());

        // Verify application rejects the invalid email
        boolean rejected = !driver.getCurrentUrl().contains("dashboard")
                && (pageText.toLowerCase().contains("error")
                || pageText.toLowerCase().contains("invalid")
                || pageText.toLowerCase().contains("email"));

        assertTrue(rejected,
                "Application should reject an email without .com.");

        System.out.println("TC-002 PASSED!");
    }
}

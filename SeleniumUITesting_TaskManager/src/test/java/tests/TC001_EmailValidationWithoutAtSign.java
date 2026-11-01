
package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

public class TC001_EmailValidationWithoutAtSign extends BaseTest {

    @Test
    public void testEmailWithoutAtSymbol() {

        driver.get("http://localhost:8000/register.html");

        // Enter registration details
        driver.findElement(By.id("name")).sendKeys("Harshi");

        WebElement emailField = driver.findElement(By.id("email"));
        emailField.sendKeys("harshigmail.com");

        driver.findElement(By.id("password")).sendKeys("Test123");

        // Check browser email validation
        JavascriptExecutor js = (JavascriptExecutor) driver;

        boolean emailValid = (boolean) js.executeScript(
                "return arguments[0].checkValidity();",
                emailField
        );

        String validationMessage = (String) js.executeScript(
                "return arguments[0].validationMessage;",
                emailField
        );

        // Check form validity
        WebElement form = driver.findElement(By.id("register-form"));

        boolean formValid = (boolean) js.executeScript(
                "return arguments[0].checkValidity();",
                form
        );

        System.out.println("Email Valid: " + emailValid);
        System.out.println("Form Valid: " + formValid);
        System.out.println("Validation Message: " + validationMessage);

        // Assertions
        assertFalse(emailValid,
                "Invalid email should be rejected.");

        assertFalse(formValid,
                "Registration form should be invalid.");

        System.out.println("TC-001 PASSED!");
    }
}

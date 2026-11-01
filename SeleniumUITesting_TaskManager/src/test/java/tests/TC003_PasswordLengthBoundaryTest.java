package tests;

import base.BaseTest;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TC003_PasswordLengthBoundaryTest extends BaseTest {

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "TC003-01 Before Minimum, Test1, false",
            "TC003-02 Minimum Boundary, Test12, true",
            "TC003-03 After Minimum, Test123, true",
            "TC003-04 Before Maximum, Test1234567, true",
            "TC003-05 Maximum Boundary, Test12345678, true",
            "TC003-06 After Maximum, Test123456789, false"
    })

    public void testPasswordLengthBoundary(
            String testCase,
            String password,
            boolean expectedSuccess) {

        // Open registration page
        driver.get("http://localhost:8000/register.html");

        // Generate a unique email for each test
        String email = "harshi"
                + UUID.randomUUID().toString().substring(0, 8)
                + "@gmail.com";

        // Enter registration details
        driver.findElement(By.id("name")).sendKeys("Harshi");

        driver.findElement(By.id("email")).sendKeys(email);

        driver.findElement(By.id("password")).sendKeys(password);

        // Print test details
        System.out.println("\n--------------------------------");
        System.out.println("Test Case: " + testCase);
        System.out.println("Password: " + password);
        System.out.println("Password Length: " + password.length());
        System.out.println("Email: " + email);
        System.out.println("Expected Success: " + expectedSuccess);

        // Submit registration form
        driver.findElement(By.cssSelector(
                "#register-form button[type='submit']"
        )).click();

        // Wait for application response
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );

        WebElement toast = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#toast-container .toast")
                )
        );

        String response = toast.getText();

        System.out.println("Application Response: " + response);

        // Verify expected result
        if (expectedSuccess) {

            assertTrue(
                    response.contains("Account created!"),
                    "Expected registration success, but received: " + response
            );

            System.out.println(testCase + " PASSED!");

        } else {

            assertTrue(
                    response.contains("Error"),
                    "Expected password rejection, but received: " + response
            );

            System.out.println(testCase + " PASSED!");
        }

        System.out.println("--------------------------------");
    }
}
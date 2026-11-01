
package tests;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.*;

public class TC009_TaskCreationCancelTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private final String baseUrl = "http://localhost:8000";
    private final String taskName = "Cancelled Selenium Task";

    @BeforeEach
    public void setUp() {

        driver = new ChromeDriver();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(20)
        );

        driver.manage().window().maximize();

        registerAndOpenDashboard();
    }

    // Register a new user and open the dashboard
    private void registerAndOpenDashboard() {

        driver.get(baseUrl + "/register.html");

        String uniqueEmail =
                "harshi" + System.currentTimeMillis() + "@gmail.com";

        driver.findElement(By.id("name"))
                .sendKeys("Harshi Selenium");

        driver.findElement(By.id("email"))
                .sendKeys(uniqueEmail);

        driver.findElement(By.id("password"))
                .sendKeys("Harshi123");

        driver.findElement(
                By.cssSelector("button[type='submit']")
        ).click();

        // Verify dashboard is loaded
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("list-pending")
        ));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("list-inprogress")
        ));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("list-completed")
        ));

        System.out.println("Registration successful!");
        System.out.println("Dashboard opened successfully!");
    }

    @Test
    public void verifyTaskCreationCancellation() {

        System.out.println("\nStarting TC-009...");

        // Step 1: Open Add Task modal
        WebElement addTaskButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                ".header-actions button.btn-primary"
                        )
                )
        );

        addTaskButton.click();

        // Step 2: Verify modal is displayed
        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("task-modal")
                )
        );

        assertTrue(
                modal.isDisplayed(),
                "Task creation modal should open."
        );

        System.out.println("Add Task modal opened.");

        // Step 3: Enter task name
        driver.findElement(By.id("title"))
                .sendKeys(taskName);

        // Step 4: Enter deadline
        WebElement deadline =
                driver.findElement(By.id("deadline"));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '2027-12-31T12:00';",
                deadline
        );

        // Step 5: Select priority
        Select priority = new Select(
                driver.findElement(By.id("priority"))
        );

        priority.selectByVisibleText("Medium");

        // Step 6: Select status
        Select status = new Select(
                driver.findElement(By.id("status"))
        );

        status.selectByVisibleText("Pending");

        System.out.println("Task details entered.");

        // Step 7: Click Cancel
        WebElement cancelButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath(
                                "//button[normalize-space()='Cancel']"
                        )
                )
        );

        cancelButton.click();

        System.out.println("Cancel button clicked.");

        // Step 8: Verify modal is closed
        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.id("task-modal")
                )
        );

        assertFalse(
                driver.findElement(By.id("task-modal")).isDisplayed(),
                "Task modal should be closed."
        );

        System.out.println("Task creation modal closed.");

        // Step 9: Verify task does not exist
        By taskLocator = By.xpath(
                "//*[contains(@class,'task-card-title') and " +
                        "normalize-space()='" + taskName + "']"
        );

        assertTrue(
                driver.findElements(taskLocator).isEmpty(),
                "Cancelled task should not appear on dashboard."
        );

        System.out.println("Cancelled task not found on dashboard.");

        // Step 10: Refresh the dashboard
        driver.navigate().refresh();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("list-pending")
        ));

        // Step 11: Verify task was not saved
        assertTrue(
                driver.findElements(taskLocator).isEmpty(),
                "Cancelled task should not be saved after refresh."
        );

        System.out.println("--------------------------------");
        System.out.println("TC-009 PASSED!");
        System.out.println("Task creation was cancelled successfully.");
        System.out.println("Task was not saved.");
        System.out.println("--------------------------------");
    }

    @AfterEach
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}

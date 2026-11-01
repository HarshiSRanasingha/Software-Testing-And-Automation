
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

public class TC008_TaskStatusDisplayTest {

    WebDriver driver;
    WebDriverWait wait;

    String baseUrl = "http://localhost:8000";

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

        // Wait for dashboard columns
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

    // Create a task with a specified status
    private void createTask(String taskName, String status) {

        // Click Add Task button
        WebElement addTaskButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                ".header-actions button.btn-primary"
                        )
                )
        );

        addTaskButton.click();

        // Wait for task modal
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("task-modal")
        ));

        // Enter task name
        driver.findElement(By.id("title"))
                .sendKeys(taskName);

        // Enter deadline
        WebElement deadline =
                driver.findElement(By.id("deadline"));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '2027-12-31T12:00';",
                deadline
        );

        // Select priority
        Select priority = new Select(
                driver.findElement(By.id("priority"))
        );

        priority.selectByVisibleText("Medium");

        // Select status
        Select taskStatus = new Select(
                driver.findElement(By.id("status"))
        );

        taskStatus.selectByVisibleText(status);

        // Save task
        driver.findElement(By.id("submit-btn")).click();

        // Wait until modal closes
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.id("task-modal")
        ));

        System.out.println(
                "Created task: " + taskName +
                        " | Status: " + status
        );
    }

    // Verify that a task exists in the correct column
    private void verifyTaskInColumn(
            String taskName,
            String columnId
    ) {

        By taskLocator = By.xpath(
                "//*[@id='" + columnId + "']" +
                        "//*[contains(@class,'task-card-title') and " +
                        "normalize-space()='" + taskName + "']"
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        taskLocator
                )
        );

        assertTrue(
                driver.findElement(taskLocator).isDisplayed(),
                taskName + " was not displayed in " + columnId
        );

        System.out.println(
                "Verified: " + taskName +
                        " is displayed in " + columnId
        );
    }

    @Test
    public void verifyTasksDisplayedInCorrectStatusColumns() {

        System.out.println("\nStarting TC-008...");

        // Create three tasks with different statuses
        createTask("Pending Test Task", "Pending");

        createTask("Progress Test Task", "In Progress");

        createTask("Completed Test Task", "Completed");

        // Verify Pending column
        verifyTaskInColumn(
                "Pending Test Task",
                "list-pending"
        );

        // Verify In Progress column
        verifyTaskInColumn(
                "Progress Test Task",
                "list-inprogress"
        );

        // Verify Completed column
        verifyTaskInColumn(
                "Completed Test Task",
                "list-completed"
        );

        System.out.println("\n--------------------------------");
        System.out.println("TC-008 PASSED!");
        System.out.println(
                "All tasks appeared in their correct status columns."
        );
        System.out.println("--------------------------------");
    }

    @AfterEach
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}

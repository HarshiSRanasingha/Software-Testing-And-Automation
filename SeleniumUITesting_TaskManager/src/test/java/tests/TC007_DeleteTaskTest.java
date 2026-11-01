

package tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class TC007_DeleteTaskTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String BASE_URL = "http://localhost:8000";

    private static final String NAME = "Harshi";
    private static final String EMAIL =
            "harshi" + System.currentTimeMillis() + "@gmail.com";
    private static final String PASSWORD = "Test123";

    private static final String TASK_NAME = "Selenium task";

    @BeforeAll
    public static void setUp() {

        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.manage().window().maximize();

        System.out.println("Chrome browser opened successfully!");

        registerAccount();
    }

    private static void registerAccount() {

        driver.get(BASE_URL + "/register.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("name")));

        driver.findElement(By.id("name")).sendKeys(NAME);
        driver.findElement(By.id("email")).sendKeys(EMAIL);
        driver.findElement(By.id("password")).sendKeys(PASSWORD);

        driver.findElement(
                By.cssSelector("#register-form button[type='submit']")
        ).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".header-actions button.btn-primary")));

        System.out.println("Registration successful!");
        System.out.println("Dashboard opened!");
    }

    private static void createTask() {

        // Open Add Task modal
        driver.findElement(
                By.cssSelector(".header-actions button.btn-primary")
        ).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("task-modal")));

        // Enter task title
        driver.findElement(By.id("title")).sendKeys(TASK_NAME);

        // Set deadline
        WebElement deadline = driver.findElement(By.id("deadline"));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '2027-12-31T12:00';" +
                        "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));" +
                        "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                deadline
        );

        // Select priority
        new Select(driver.findElement(By.id("priority")))
                .selectByVisibleText("Medium");

        // Select status
        new Select(driver.findElement(By.id("status")))
                .selectByVisibleText("Pending");

        // Submit task
        driver.findElement(By.id("submit-btn")).click();

        // Wait until modal closes
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.id("task-modal")));

        // Verify task creation
        By taskLocator = By.xpath(
                "//*[contains(@class,'task-card-title') and normalize-space()='" +
                        TASK_NAME + "']"
        );

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                taskLocator));

        System.out.println("Task created successfully!");
    }

    @Test
    public void testDeleteTask() {

        // Step 1: Create a task
        createTask();

        // Step 2: Locate Delete button belonging to the task
        By deleteButton = By.xpath(
                "//*[contains(@class,'task-card-title') and normalize-space()='" +
                        TASK_NAME +
                        "']/ancestor::div[.//button[contains(@class,'delete-task')]][1]" +
                        "//button[contains(@class,'delete-task')]"
        );

        WebElement delete = wait.until(
                ExpectedConditions.elementToBeClickable(deleteButton)
        );

        System.out.println("Delete button found!");

        // Step 3: Handle JavaScript confirmation dialog
        driver.findElement(deleteButton).click();

        wait.until(ExpectedConditions.alertIsPresent());

        String alertMessage = driver.switchTo().alert().getText();

        System.out.println("Confirmation message: " + alertMessage);

        // Verify confirmation dialog
        assertTrue(
                alertMessage.toLowerCase().contains("delete"),
                "Delete confirmation message was not displayed!"
        );

        // Step 4: Confirm deletion
        driver.switchTo().alert().accept();

        System.out.println("Delete confirmation accepted!");

        // Step 5: Verify task disappears from dashboard
        By taskLocator = By.xpath(
                "//*[contains(@class,'task-card-title') and normalize-space()='" +
                        TASK_NAME + "']"
        );

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                taskLocator));

        assertTrue(
                driver.findElements(taskLocator).isEmpty(),
                "Task is still displayed after deletion!"
        );

        System.out.println("Task removed from dashboard!");

        // Step 6: Refresh dashboard
        driver.navigate().refresh();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".header-actions button.btn-primary")));

        // Verify deleted task does not return after refresh
        wait.until(ExpectedConditions.numberOfElementsToBe(
                taskLocator, 0));

        assertTrue(
                driver.findElements(taskLocator).isEmpty(),
                "Deleted task reappeared after refreshing!"
        );

        System.out.println("--------------------------------");
        System.out.println("TC-007 PASSED!");
        System.out.println("Task deleted successfully.");
        System.out.println("Deleted task did not reappear after refresh.");
        System.out.println("--------------------------------");
    }

    @AfterAll
    public static void tearDown() {

        if (driver != null) {
            driver.quit();
            System.out.println("Chrome browser closed!");
        }
    }
}

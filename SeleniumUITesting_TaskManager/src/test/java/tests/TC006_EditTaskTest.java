
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

public class TC006_EditTaskTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String BASE_URL = "http://localhost:8000";

    private static final String NAME = "Harshi";
    private static final String EMAIL =
            "harshi" + System.currentTimeMillis() + "@gmail.com";
    private static final String PASSWORD = "Test123";

    private static final String ORIGINAL_TASK = "Selenium task";
    private static final String UPDATED_TASK = "Edited Selenium task";

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
        driver.findElement(By.id("title")).sendKeys(ORIGINAL_TASK);

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

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.id("task-modal")));

        // Verify task creation
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(
                        "//*[contains(@class,'task-card-title') and normalize-space()='" +
                                ORIGINAL_TASK + "']"
                )));

        System.out.println("Task created successfully!");
    }

    @Test
    public void testEditTask() {

        createTask();

        // Find the Edit button belonging to the original task
        By editButton = By.xpath(
                "//*[contains(@class,'task-card-title') and normalize-space()='" +
                        ORIGINAL_TASK +
                        "']/ancestor::div[.//button[contains(@class,'edit-task')]][1]" +
                        "//button[contains(@class,'edit-task')]"
        );

        WebElement edit = wait.until(
                ExpectedConditions.elementToBeClickable(editButton)
        );

        edit.click();

        System.out.println("Edit button clicked!");

        // Wait for edit modal
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("task-modal")));

        // Verify edit modal
        String modalHeading = driver.findElement(
                By.id("form-title")).getText();

        assertEquals("Edit Task", modalHeading,
                "Edit modal did not open correctly!");

        // Update task title
        WebElement titleField = driver.findElement(By.id("title"));

        titleField.clear();
        titleField.sendKeys(UPDATED_TASK);

        // Update priority
        new Select(driver.findElement(By.id("priority")))
                .selectByVisibleText("High");

        // Verify updated priority before saving
        String selectedPriority = new Select(
                driver.findElement(By.id("priority"))
        ).getFirstSelectedOption().getText();

        assertEquals("High", selectedPriority,
                "Priority was not updated!");

        // Click Save Changes
        driver.findElement(By.id("submit-btn")).click();

        // Wait until modal closes
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.id("task-modal")));

        // Verify updated task appears
        By updatedTaskLocator = By.xpath(
                "//*[contains(@class,'task-card-title') and normalize-space()='" +
                        UPDATED_TASK + "']"
        );

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                updatedTaskLocator));

        // Verify original task title is no longer displayed
        assertTrue(
                driver.findElements(By.xpath(
                        "//*[contains(@class,'task-card-title') and normalize-space()='" +
                                ORIGINAL_TASK + "']"
                )).isEmpty(),
                "Original task title is still displayed!"
        );

        System.out.println("Task title updated successfully!");
        System.out.println("Priority updated to High!");

        // Refresh dashboard to verify persistence
        driver.navigate().refresh();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".header-actions button.btn-primary")));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                updatedTaskLocator));

        assertTrue(
                driver.findElement(updatedTaskLocator).isDisplayed(),
                "Updated task did not persist after refreshing!"
        );

        System.out.println("--------------------------------");
        System.out.println("TC-006 PASSED!");
        System.out.println("Task details updated successfully.");
        System.out.println("Changes persisted after page refresh.");
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

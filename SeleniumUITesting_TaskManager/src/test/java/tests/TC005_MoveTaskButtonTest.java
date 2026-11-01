
package tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class TC005_MoveTaskButtonTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String BASE_URL = "http://localhost:8000";

    private static final String EMAIL =
            "harshi" + System.currentTimeMillis() + "@gmail.com";

    private static final String PASSWORD = "Test123";
    private static final String TASK_NAME = "Selenium task";

    @BeforeAll
    static void setUp() {

        driver = new ChromeDriver();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );

        driver.manage().window().maximize();

        System.out.println("Chrome browser opened successfully!");
    }

    @Test
    void testMoveTaskFromPendingToInProgress() {

        // STEP 1: Register a new account
        driver.get(BASE_URL + "/register.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("name")
        )).sendKeys("Harshi");

        driver.findElement(By.id("email")).sendKeys(EMAIL);

        driver.findElement(By.id("password")).sendKeys(PASSWORD);

        driver.findElement(
                By.cssSelector("#register-form button[type='submit']")
        ).click();

        // STEP 2: Wait for dashboard
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".header-actions button.btn-primary")
        ));

        System.out.println("Registration successful!");
        System.out.println("Dashboard opened!");

        // STEP 3: Open Add Task modal
        driver.findElement(
                By.cssSelector(".header-actions button.btn-primary")
        ).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("task-modal")
        ));

        // STEP 4: Enter task details
        driver.findElement(By.id("title")).sendKeys(TASK_NAME);

        WebElement deadline = driver.findElement(By.id("deadline"));

        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript(
                        "arguments[0].value = '2027-12-31T12:00';" +
                                "arguments[0].dispatchEvent(new Event('input'));" +
                                "arguments[0].dispatchEvent(new Event('change'));",
                        deadline
                );

        driver.findElement(By.id("priority")).sendKeys("Medium");

        driver.findElement(By.id("status")).sendKeys("Pending");

        // STEP 5: Submit task
        driver.findElement(By.id("submit-btn")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.id("task-modal")
        ));

        // STEP 6: Find task in Pending column
        By pendingTask = By.xpath(
                "//div[@id='list-pending']" +
                        "//div[contains(@class,'task-card')]" +
                        "[.//div[contains(@class,'task-card-title') and " +
                        "normalize-space()='Selenium task']]"
        );

        WebElement taskCard = wait.until(
                ExpectedConditions.visibilityOfElementLocated(pendingTask)
        );

        assertTrue(taskCard.isDisplayed(),
                "Task should appear in Pending column");

        System.out.println("Task created successfully!");

        // STEP 7: Find Move button
        WebElement moveButton = taskCard.findElement(
                By.cssSelector(".move-task")
        );

        assertEquals(
                "In Progress",
                moveButton.getAttribute("data-status"),
                "Move button should target In Progress"
        );

        System.out.println("Move button found!");

        // STEP 8: Click Move button
        moveButton.click();

        // STEP 9: Verify task appears in In Progress
        By inProgressTask = By.xpath(
                "//div[@id='list-inprogress']" +
                        "//div[contains(@class,'task-card')]" +
                        "[.//div[contains(@class,'task-card-title') and " +
                        "normalize-space()='Selenium task']]"
        );

        WebElement movedTask = wait.until(
                ExpectedConditions.visibilityOfElementLocated(inProgressTask)
        );

        assertTrue(
                movedTask.isDisplayed(),
                "Task should appear in In Progress column"
        );

        // STEP 10: Verify Move button now targets Completed
        WebElement nextMoveButton = movedTask.findElement(
                By.cssSelector(".move-task")
        );

        assertEquals(
                "Completed",
                nextMoveButton.getAttribute("data-status")
        );

        System.out.println("--------------------------------");
        System.out.println("TC-005 PASSED!");
        System.out.println("Task moved from Pending to In Progress.");
        System.out.println("--------------------------------");
    }

    @AfterAll
    static void tearDown() {

        if (driver != null) {
            driver.quit();
            System.out.println("Chrome browser closed!");
        }
    }
}

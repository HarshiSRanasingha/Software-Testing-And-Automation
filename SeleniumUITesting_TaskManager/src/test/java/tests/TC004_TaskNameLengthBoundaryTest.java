
package tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TC004_TaskNameLengthBoundaryTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String BASE_URL =
            "http://localhost:8000";

    private static final String NAME = "Harshi";

    // Generate a new email address for every execution
    private static final String EMAIL =
            "harshi" + System.currentTimeMillis() + "@gmail.com";

    private static final String PASSWORD = "Test123";

    private static final Map<String, Boolean> results =
            new LinkedHashMap<>();

    // ==========================================
    // SETUP
    // ==========================================

    @BeforeAll
    public static void setUp() {

        driver = new ChromeDriver();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(20)
        );

        driver.manage().window().maximize();

        System.out.println("======================================");
        System.out.println("       TC-004 TEST EXECUTION");
        System.out.println("======================================");

        System.out.println("Chrome browser opened successfully!");

        System.out.println("Test Email: " + EMAIL);

        registerAccount();
    }

    // ==========================================
    // REGISTER ACCOUNT
    // ==========================================

    private static void registerAccount() {

        driver.get(BASE_URL + "/register.html");

        System.out.println("\nRegistration page opened.");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("register-form")
                )
        );

        driver.findElement(By.id("name"))
                .sendKeys(NAME);

        driver.findElement(By.id("email"))
                .sendKeys(EMAIL);

        driver.findElement(By.id("password"))
                .sendKeys(PASSWORD);

        System.out.println("Registration details entered.");

        driver.findElement(By.id("register-form"))
                .submit();

        try {

            wait.until(d -> {

                boolean dashboardFound =
                        !d.findElements(
                                By.cssSelector(
                                        ".header-actions button.btn-primary"
                                )
                        ).isEmpty();

                String pageText =
                        d.findElement(By.tagName("body"))
                                .getText()
                                .toLowerCase();

                boolean errorFound =
                        pageText.contains("error")
                                || pageText.contains("already exists")
                                || pageText.contains("already registered");

                return dashboardFound || errorFound;
            });

        } catch (Exception e) {

            System.out.println(
                    "Registration response timed out."
            );
        }

        System.out.println("\n========== REGISTRATION RESULT ==========");

        System.out.println("Current URL: " + driver.getCurrentUrl());

        System.out.println(
                "Page Content:\n"
                        + driver.findElement(By.tagName("body")).getText()
        );

        boolean dashboardFound =
                !driver.findElements(
                        By.cssSelector(
                                ".header-actions button.btn-primary"
                        )
                ).isEmpty();

        if (dashboardFound) {

            System.out.println("\nRegistration successful!");

            System.out.println("Dashboard opened!");

        } else {

            throw new IllegalStateException(
                    "Registration failed. Check the registration response."
            );
        }
    }

    // ==========================================
    // GENERATE TASK NAME
    // ==========================================

    private static String generateTaskName(int length) {

        StringBuilder taskName = new StringBuilder();

        while (taskName.length() < length) {

            taskName.append(
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
            );
        }

        return taskName.substring(0, length);
    }

    // ==========================================
    // CREATE TASK
    // ==========================================

    private static boolean createTask(String taskName) {

        try {

            // Open Add Task modal

            WebElement addTaskButton =
                    wait.until(
                            ExpectedConditions.elementToBeClickable(
                                    By.cssSelector(
                                            ".header-actions button.btn-primary"
                                    )
                            )
                    );

            addTaskButton.click();

            // Wait for modal

            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.id("task-modal")
                    )
            );

            // Enter task name

            WebElement titleField =
                    driver.findElement(By.id("title"));

            titleField.clear();

            titleField.sendKeys(taskName);

            // Enter deadline

            WebElement deadlineField =
                    driver.findElement(By.id("deadline"));

            JavascriptExecutor js =
                    (JavascriptExecutor) driver;

            js.executeScript(
                    "arguments[0].value = '2027-12-31T12:00';"
                            + "arguments[0].dispatchEvent("
                            + "new Event('input', {bubbles:true}));"
                            + "arguments[0].dispatchEvent("
                            + "new Event('change', {bubbles:true}));",
                    deadlineField
            );

            // Select priority

            driver.findElement(By.id("priority"))
                    .sendKeys("Medium");

            // Select status

            driver.findElement(By.id("status"))
                    .sendKeys("Pending");

            // Check HTML form validity

            Boolean formValid =
                    (Boolean) js.executeScript(
                            "return document.getElementById('task-form').checkValidity();"
                    );

            if (!formValid) {

                System.out.println(
                        "Task form validation failed."
                );

                driver.findElement(By.id("task-modal"))
                        .isDisplayed();

                driver.findElement(By.xpath(
                        "//button[@type='button' and contains(.,'Cancel')]"
                )).click();

                return false;
            }

            // Submit task

            driver.findElement(By.id("submit-btn"))
                    .click();

            // Wait until modal closes

            wait.until(
                    ExpectedConditions.invisibilityOfElementLocated(
                            By.id("task-modal")
                    )
            );

            // Verify task appears on dashboard

            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath(
                                    "//*[normalize-space(text())='"
                                            + taskName
                                            + "']"
                            )
                    )
            );

            System.out.println(
                    "Task created successfully. Length: "
                            + taskName.length()
            );

            return true;

        } catch (Exception e) {

            System.out.println(
                    "Task creation failed. Length: "
                            + taskName.length()
            );

            System.out.println(
                    "Reason: " + e.getMessage()
            );

            // Close modal if it is still open

            try {

                if (!driver.findElements(
                        By.id("task-modal")
                ).isEmpty()) {

                    driver.findElement(
                            By.xpath(
                                    "//button[@type='button' and contains(.,'Cancel')]"
                            )
                    ).click();
                }

            } catch (Exception ignored) {
                // Continue with next test
            }

            return false;
        }
    }

    // ==========================================
    // EXECUTE BOUNDARY TEST
    // ==========================================

    private static void executeBoundaryTest(
            String testId,
            String description,
            int length,
            boolean expectedResult
    ) {

        System.out.println("\n--------------------------------------");

        System.out.println(testId + " : " + description);

        System.out.println("Task name length: " + length);

        String taskName = generateTaskName(length);

        boolean actualResult = createTask(taskName);

        boolean passed = actualResult == expectedResult;

        results.put(testId, passed);

        System.out.println(
                "Expected: "
                        + (expectedResult ? "Accepted" : "Rejected")
        );

        System.out.println(
                "Actual: "
                        + (actualResult ? "Accepted" : "Rejected")
        );

        System.out.println(
                "Test Result: " + (passed ? "PASS" : "FAIL")
        );
    }

    // ==========================================
    // TC004-01 : BELOW MINIMUM
    // ==========================================

    @Test
    public void TC004_01_BelowMinimum() {

        executeBoundaryTest(
                "TC004-01",
                "Task name below minimum length",
                2,
                false
        );
    }

    // ==========================================
    // TC004-02 : MINIMUM BOUNDARY
    // ==========================================

    @Test
    public void TC004_02_MinimumBoundary() {

        executeBoundaryTest(
                "TC004-02",
                "Task name at minimum length",
                3,
                true
        );
    }

    // ==========================================
    // TC004-03 : ABOVE MINIMUM
    // ==========================================

    @Test
    public void TC004_03_AboveMinimum() {

        executeBoundaryTest(
                "TC004-03",
                "Task name just above minimum length",
                4,
                true
        );
    }

    // ==========================================
    // TC004-04 : BELOW MAXIMUM
    // ==========================================

    @Test
    public void TC004_04_BelowMaximum() {

        executeBoundaryTest(
                "TC004-04",
                "Task name just below maximum length",
                49,
                true
        );
    }

    // ==========================================
    // TC004-05 : MAXIMUM BOUNDARY
    // ==========================================

    @Test
    public void TC004_05_MaximumBoundary() {

        executeBoundaryTest(
                "TC004-05",
                "Task name at maximum length",
                50,
                true
        );
    }

    // ==========================================
    // TC004-06 : ABOVE MAXIMUM
    // ==========================================

    @Test
    public void TC004_06_AboveMaximum() {

        executeBoundaryTest(
                "TC004-06",
                "Task name above maximum length",
                51,
                false
        );
    }

    // ==========================================
    // FINAL REPORT
    // ==========================================

    @AfterAll
    public static void tearDown() {

        System.out.println("\n");

        System.out.println("======================================");
        System.out.println("       TC-004 FINAL TEST REPORT");
        System.out.println("======================================");

        int passed = 0;
        int failed = 0;

        for (Map.Entry<String, Boolean> result :
                results.entrySet()) {

            System.out.println(
                    result.getKey()
                            + " : "
                            + (result.getValue() ? "PASS" : "FAIL")
            );

            if (result.getValue()) {
                passed++;
            } else {
                failed++;
            }
        }

        System.out.println("--------------------------------------");

        System.out.println("Total Tests: " + results.size());

        System.out.println("Passed: " + passed);

        System.out.println("Failed: " + failed);

        System.out.println("======================================");

        if (driver != null) {

            driver.quit();

            System.out.println("Chrome browser closed!");
        }
    }
}

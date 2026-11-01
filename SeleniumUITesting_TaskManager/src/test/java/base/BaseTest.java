
package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

public class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    public void setUp() {

        ChromeOptions options = new ChromeOptions();

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();

        System.out.println("Chrome browser opened successfully!");
    }

    @AfterEach
    public void tearDown() {

        if (driver != null) {
            driver.quit();
            System.out.println("Chrome browser closed!");
        }
    }
}

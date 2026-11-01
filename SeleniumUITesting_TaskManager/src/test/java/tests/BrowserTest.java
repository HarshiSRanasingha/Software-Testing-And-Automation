
package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;

public class BrowserTest extends BaseTest {

    @Test
    public void openGoogle() {

        driver.get("https://www.google.com");

        System.out.println("Website title: " + driver.getTitle());
    }
}


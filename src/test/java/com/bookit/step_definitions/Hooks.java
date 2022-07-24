package com.bookit.step_definitions;

import io.cucumber.java.Scenario;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import com.bookit.utilities.DBUtils;
import com.bookit.utilities.Driver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.util.concurrent.TimeUnit;

public class Hooks {

    @Before("@db")
    public void dbHook() {

        DBUtils.createConnection();
    }

    @After("@db")
    public void afterDbHook() {

        DBUtils.destroy();

    }

    @Before("@ui")
    public void setUp() {
        // we put a logic that should apply to every scenario
        Driver.getDriver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

    }

    @After
    public void tearDownScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) Driver.getDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", scenario.getName());
        }

        // BrowserUtils.sleep(4);
        Driver.closeDriver();
        //System.out.println("--------Closing browser using cucumber @After----------");
    }
}








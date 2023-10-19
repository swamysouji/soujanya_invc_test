package selenium_test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class Testcase {

	WebDriver driver;
	ExtentReports report;
	ExtentTest test;
	Properties prop;
	
	@BeforeTest
	public void launch_browser() throws IOException {

		// This can be used for Windows local run
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\chromedriver.exe");
		
		// For github actions to run on Ubantu
		//System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/chromedriver");

		//ChromeOptions options = new ChromeOptions();
		//driver = new ChromeDriver(options);
		
		// a. Launch Browser	
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("http://the-internet.herokuapp.com");
		
		// b. setup Reporting
		report = new ExtentReports();
		String report_file_path = System.getProperty("user.dir") + "\\reports";
		ExtentSparkReporter spark_reporter = new ExtentSparkReporter(report_file_path);
		report.attachReporter(spark_reporter);
		spark_reporter.config().setReportName("Test report");
		test = report.createTest("test case");
		
		// c. Locator related setup
		String prop_file_path = System.getProperty("user.dir") + "\\locators.properties";
		System.out.println(prop_file_path);
		FileInputStream fis = new FileInputStream(prop_file_path);
		prop = new Properties();
		prop.load(fis);
	}

	@Test(description = "add n elements and verify if n elements got added")
	public void add_remove_elements_test() {

		// n can be passed into the function
		int n = 9;

		// 1. click on Add/Remove Link
		WebElement add_rem = driver
				.findElement(By.xpath(prop.getProperty("add_rem_link_locator")));
		add_rem.click();
		test.log(Status.INFO, "click on Add/Remove link");

		// 2. add n number of elements
		test.log(Status.INFO, n + " elements being added");
		for (int i = 0; i < n; i++) {
			WebElement add_del_button = driver
					.findElement(By.xpath(prop.getProperty("add_del_button_locator")));
			add_del_button.click();
		}

		// 3. Find the number of added elements
		List<WebElement> element_list = driver
				.findElements(By.xpath(prop.getProperty("element_list")));

		int display_count = element_list.size();
		test.log(Status.INFO, display_count + " elements got added");

		// 4. Verify the number of added elements
		if (display_count == n)
			test.log(Status.PASS, "Add/Verify Test succeeded");
		else
			test.log(Status.FAIL, "Add/Verify Test failed");
	}

	@AfterTest
	public void teardown() {
		report.flush();
		driver.quit();
	}
}

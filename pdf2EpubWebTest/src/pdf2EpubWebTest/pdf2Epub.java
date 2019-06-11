package pdf2EpubWebTest;

import org.testng.annotations.Test;

import pdf2EpubWebTest.zPdf2EpubUtil;
import pdf2EpubWebTest.zObjMap;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class pdf2Epub {

	public WebDriver driver;
	zObjMap objMap = new zObjMap();
	Properties testVars = new Properties();
	String mainSite, driverPath, testPdfPath;
	Integer impWait, expWait;
	
	String testFilePath = "resources/test_files.txt";
	LinkedList<String> testFileList = new LinkedList<String>();
	
	// Webdriver setup
	// This will check the "browser" parameter in the testng.xml file and create the correct webdriver.
	// This will also prep the Properties object with most test variables
	//   and get the list of test books.
	@BeforeClass(alwaysRun = true)
	@Parameters({"browser"})
	public void setUp(String browser) throws Exception{
		zPdf2EpubUtil.propLoader(testVars);
		mainSite = testVars.getProperty("pdf2epubWeb");
		driverPath = testVars.getProperty("driverPath");
		testPdfPath = testVars.getProperty("testPdfPath");
		impWait = Integer.parseInt(testVars.getProperty("impWait"));
		expWait = Integer.parseInt(testVars.getProperty("expWait"));
		
		driver = zPdf2EpubUtil.setDriver(driver, browser, driverPath, impWait);
		
		zPdf2EpubUtil.fetchTestDirectoryPdfs(testFileList, testPdfPath);
		Collections.sort(testFileList);
//		fetchTestList(testFileList, testFilePath);
	}
	
	// Main test
	@Test (groups = { "baseline"})
	public void pdf2EpubMainTest() throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		
		Iterator<String> it = testFileList.iterator();
		
		while (it.hasNext()) {
			String testBook = testPdfPath + it.next();
			driver.get(mainSite);
			wait.until(ExpectedConditions.visibilityOfElementLocated(objMap.getLoc("pdf2epub.panel-widget")));
			System.out.println("Wait complete");
			
			System.out.println("Processing book " + testBook);
			
			driver.findElement(objMap.getLoc("pdf2epub.disclaimer")).click();
			driver.findElement(objMap.getLoc("pdf2epub.upload_file")).sendKeys(testBook);
			driver.findElement(objMap.getLoc("pdf2epub.submit_btn")).click();
			
			wait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("pdf2epub.download_link")));
			driver.findElement(objMap.getLoc("pdf2epub.download_link")).click();
		}
	}
	
	
	
	// Webdriver shutdown
	// This will run after the test class, and will exit the webdriver
	@AfterClass(alwaysRun = true)
	public void quitDriver() throws InterruptedException{
		zPdf2EpubUtil.exitDriver(driver);
	}
	
//	public void 
	
	public void fetchTestList (LinkedList<String> inc, String testFilePath) throws FileNotFoundException{
		BufferedReader reader = new BufferedReader (new FileReader (new File(testFilePath)));
		String line;
		try{
			while (( line = reader.readLine()) != null) {
				testFileList.add(line);
			}
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        reader.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}
}

package pdf2EpubWebTest;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

public class zPdf2EpubUtil {
	static String propPath = "resources/config.properties";

	// exitDriver
	// exits the driver
	// Arguments:	WebDriver driver (the webdriver to exit)
	public static void exitDriver(WebDriver driver) throws InterruptedException{
		System.out.println("Exiting WebDriver instance");
		Thread.sleep(2000);
		driver.quit();
	}

	// propLoader
	// loads config.properties file
	// Arguments:	Properties incProp
	public static void propLoader (Properties incProp) {
		System.out.println("Popuating Properties object");
		InputStream input = null;

		try {
			input = new FileInputStream(propPath);
			incProp.load(input);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (input != null) {
				try{
					input.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// Fetch list of files from test file directory. Filter for PDFs smaller than 10MB 
	public static void fetchTestDirectoryPdfs(LinkedList<String> testFileList, String testPdfPath) {
		File folder = new File(testPdfPath);
		String testFile;
		FileFilter txtFileFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
    			if(file.getName().toLowerCase().endsWith(".pdf")) {
    				if(file.length() < 10*1024*1024){
    					return true;
    				}
    				else {
    					return false;
    				}
    			}
    			else {
    				return false;
    			}
			}
		};
		File[] files = folder.listFiles(txtFileFilter);
		for (File file : files) {
			testFile = file.getName();
			System.out.println(testFile);
			testFileList.add(testFile);
		}
    }
	
	// setDriver
	// sets up the WebDriver
	// Arguments: WebDriver driver
	//			  String browser 	(set in testng.xml, e.g. parameter name="browser" value="chrome")
	//			  String driverPath (set in config.properties, pulled in with propLoader)
	//			  Integer impWait	(implicit wait time, set in config.properties, also pulled in with propLoader
	// Returns:	  WebDriver driver
	public static WebDriver setDriver (WebDriver driver, String browser, String driverPath, Integer impWait) throws Exception {
		//chrome driver setup
    	if (browser.equalsIgnoreCase("chrome")) {
        	System.setProperty("webdriver.chrome.driver", driverPath+"chromedriver");
        	ChromeOptions options = new ChromeOptions();
        	options.addArguments("--start-fullscreen");
    		driver = new ChromeDriver(options);
    	}
    	//firefox driver setup
    	else if (browser.equalsIgnoreCase("firefox")) {
    		System.setProperty("webdriver.gecko.driver", driverPath+"geckodriver");
    		driver = new FirefoxDriver();
    		driver.manage().window().maximize();
    	}
    	//driver setup fails
    	else{
    		throw new Exception("error: browser incorrect");
    	}
    	// Set implicit wait time
    	driver.manage().timeouts().implicitlyWait(impWait, TimeUnit.SECONDS);
    	return driver;
	}
	
}

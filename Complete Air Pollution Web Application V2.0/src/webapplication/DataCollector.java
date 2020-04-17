package webapplication;/* code to collect data on air pollution from
 * https://www.epa.gov/outdoor-air-quality-data/download-daily-data
 * for the United States of America
 *
 * Code uses JSoup, jar can be downloaded at: https://jsoup.org/download
 *
 * Download your specific chromedriver here: https://chromedriver.chromium.org/downloads
 */

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataCollector {

    private WebDriver driver;

    public void collectPollutionData() throws IOException, InterruptedException {
        //Document doc = Jsoup.connect("http://en.wikipedia.org/").get();
        //Elements newsHeadlines = doc.select("#mp-itn b a");
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        createWebDriver();

        driver.get("https://www.epa.gov/outdoor-air-quality-data/download-daily-data");
        List<String> pollOptionList = getAllOptions("poll", driver);
        // options start from 1, skip 1 since it is not valid
        for(int pollIndex=5; pollIndex<=pollOptionList.size(); pollIndex++){
            driver.findElement(By.xpath("/html/body/section/div[2]/div[1]/div/div/form/ol/li[1]/select/option["+pollIndex+"]")).click();
            List<String> yearOptionList = getAllOptions("year", driver);

            for(int yearIndex=2; yearIndex<=yearOptionList.size(); yearIndex++){
                driver.findElement(By.xpath("/html/body/section/div[2]/div[1]/div/div/form/ol/li[2]/select/option["+yearIndex+"]")).click();
                List<String> areaOptionList = getAllOptions("state", driver);

                for(int areaIndex=2; areaIndex<=areaOptionList.size(); areaIndex++){
                    driver.findElement(By.xpath("/html/body/section/div[2]/div[1]/div/div/form/ol/li[3]/select[1]/option["+areaIndex+"]")).click();
                    // clicks the download button to download the dataset
                    Thread.sleep(700);
                    driver.findElement(By.xpath("/html/body/section/div[2]/div[1]/div/div/div[2]/input")).click();
                    Thread.sleep(300);
                    try {
                        WebDriverWait waitClick = new WebDriverWait(driver,10);
                        waitClick.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/section/div[2]/div[1]/div/div/div[3]/p/a")));
                        driver.findElement(By.xpath("/html/body/section/div[2]/div[1]/div/div/div[3]/p/a")).click();
                    }catch (Exception e){
                        System.out.println("No Data at: "+pollIndex+", "+yearIndex+", "+ areaIndex);
                    }
                }
            }
        }
        System.out.println("Downloads Complete");
        driver.close();
    }

    /**
     * Gets all of the <code><option/></code> innerHTML attributes
     *
     */
    private List<String> getAllOptions(String bySearch, WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver,5);
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(By.id(bySearch), By.tagName("option")));

        List<String> options = new ArrayList<String>();
        for (WebElement option : new Select(driver.findElement(By.id(bySearch))).getOptions()) {
            String txt = option.getText();
            if (option.getAttribute("value") != "") options.add(option.getText());
        }
        return options;
    }

    // creates a web driver that is able to download to a specific location
    private void createWebDriver(){
        final String dir = System.getProperty("user.dir");
        String downloadFilepath = (dir + "\\datasets");
        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", downloadFilepath);

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromePrefs);
        options.addArguments("start-maximized");
        options.addArguments("--ignore-certificate-errors");

        driver = new ChromeDriver(options);
    }
}

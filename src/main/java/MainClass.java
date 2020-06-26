import net.lightbody.bmp.*;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.core.*;

 

public class MainClass {
    public static void main(String [ ] args){
    	BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.start(0);
        
        /* crea un selenium proxy prendendo come argomento un browser mob proxy  */
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
        seleniumProxy.setHttpProxy("localhost:" + proxy.getPort());
        seleniumProxy.setSslProxy("localhost:" + proxy.getPort());
        
        
        String[] hashtags = null;
        try {
			hashtags = Instagram.readHashtags();
		} catch (IOException e1) {
			System.out.println("Errore nel reperimento degli hashtags.");
			e1.printStackTrace();
		} 
             
        /* setto il driver */
        System.setProperty("webdriver.gecko.driver", "./geckodriver");
        
        /* setto le sue opzioni */
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setCapability(CapabilityType.PROXY, seleniumProxy);        
        firefoxOptions.setCapability("marionette", true);
        firefoxOptions.setCapability(CapabilityType.PROXY, seleniumProxy);
        firefoxOptions.setCapability("acceptInsecureCerts", true);
        firefoxOptions.setCapability("acceptSslCerts", true);
      
        /* creo un FirefoxDriver */
        WebDriver driver = new FirefoxDriver(firefoxOptions);
      
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();

        /* setto il tipo HAR (HttpArchive) come il tipo del file che verrà utilizzato per tenere i dati delle richieste HTTP */
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);

        Instagram.setDriver(driver);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        
        try {     	
        	Instagram.login("mrtns_95","martina95");
        	
        	System.out.println("Retrieving data from hot hashtags...");	
        	
        	/* scarico i dati per 30 minuti per ogni hashtag */
        	for(int i=0; i<hashtags.length; i++) {
        		Instagram.searchHashtag(hashtags[i]);
         		
        		Instagram.downloadData(hashtags[i]);		      		
        	}
        } catch(Exception e) {
        	e.printStackTrace();
        }
        
        /* chiusura */
        proxy.stop();
        driver.close();
    }
    
}

    



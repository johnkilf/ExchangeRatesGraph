
import java.util.concurrent.TimeUnit;

import akka.util.Duration;
import play.*;
import play.Application;
import play.libs.Akka;

/**
 * @author john
 *
 */
public class Global extends GlobalSettings {
	
  @Override
	public void onStart(Application app) {
  	Logger.info("Application has started");
    
    Akka.system().scheduler().schedule(
    	  Duration.create(6, TimeUnit.HOURS),
    	  Duration.create(6, TimeUnit.HOURS),
    	  new Runnable() {
			    public void run() {
			    	Logger.info("Running rates update task");
			      controllers.Application.updateRates();
			    }
			  }
    	);
		super.onStart(app);
	}

	@Override
	public void onStop(Application app) {
		 Logger.info("Application shutdown...");
		super.onStop(app);
	}
}
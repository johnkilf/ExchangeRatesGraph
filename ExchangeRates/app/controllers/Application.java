package controllers;

import static play.libs.Json.toJson;

import java.util.ArrayList;

import models.Rate;
import models.Rates;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.avaje.ebean.Ebean;

import play.*;
import play.libs.Json;
import play.libs.WS;
import play.libs.XPath;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {
	
	private static Rates rates = Rates.getInstance();
	
	//strings used to retreive and parse xml
	private static final String ECB_URL = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml";
	private static final String CONTAINING_NODE = "//Cube";
	private static final String TIME_ATTRIBUTE = "time";
	private static final String CURRENCY_ATTRIBUTE = "currency";
	private static final String RATE_ATTRIBUTE = "rate";
	private static final String USD_CURRENCY_NAME = "USD";
	//error message to display to users in case of failure
	private static final String USER_ERROR_MESSAGE = "Unable to retreive exchange rate data";
	
  
  public static Result index() {
		return redirect(routes.Application.showGraph());
  }
  
  /**
   * Shows the graph of euro-usd conversion values for the past 90 days.
   * @return the page to display to the user
   */
	public static Result showGraph(){
		if(rates.getRates() == null){
    	Logger.info("Cannot find existing rates data, attempting to update");
    	return(updateRates());
    }else{
    	return ok(views.html.graph.render(rates));
    }
	}
  
	/**
   * Retrieves the latest rates from the ECB and stores them to the database
   * @return the graph using the updated values, or an error message in cases of failure
   */
	public static Result updateRates(){
		Promise<WS.Response> homePage = WS.url(ECB_URL).get();

		return async(
			homePage.map(
					new Function<WS.Response, Result>() {
						public Result apply(WS.Response response) {
							ArrayList<Rate> rateList = new ArrayList<Rate>();
								//test response from ecb
								Document dom = response.asXml();
								if(dom == null) {
									Logger.error("Bad result returned from ECB");
									return badRequest(USER_ERROR_MESSAGE);
								}else{
									//parse response from ecb and add each rate to the rateList
									//XML structure is:
									//Cube
									//	Cube date="2013-01-01"
									//		Cube currency="USD" rate="1.1"
									try{
										//get containing cube
										Node cube = XPath.selectNode(CONTAINING_NODE, dom);
										NodeList dateNodes = cube.getChildNodes();
										//get date nodes
										Node dateNode = dateNodes.item(0);
										while(dateNode != null){
											NamedNodeMap dateAttribs = dateNode.getAttributes();
											String date = dateAttribs.getNamedItem(TIME_ATTRIBUTE).getTextContent();
											
											//get currency/rate nodes for that date
											NodeList rateNodes = dateNode.getChildNodes();
											Node rateNode = rateNodes.item(0);
											while(rateNode != null){
												NamedNodeMap rateAttribs = rateNode.getAttributes();
												if(rateAttribs.getNamedItem(CURRENCY_ATTRIBUTE).getTextContent().equals(USD_CURRENCY_NAME)){
													String rate = rateAttribs.getNamedItem(RATE_ATTRIBUTE).getTextContent();
													rateList.add(new Rate(date, rate));
													break;
												}
												rateNode = rateNode.getNextSibling();
												while(rateNode!= null && rateNode.getNodeType()== Node.TEXT_NODE){
													rateNode = rateNode.getNextSibling();
												}
											}
											
											dateNode = dateNode.getNextSibling();
											while(dateNode!= null && dateNode.getNodeType()== Node.TEXT_NODE){
												dateNode = dateNode.getNextSibling();
											}
										}
									}catch(NullPointerException e){
										Logger.error(e.getMessage(), e);
										return badRequest(USER_ERROR_MESSAGE);
									}
									rates.updateRates(Json.stringify(toJson(rateList)));
									return ok(views.html.graph.render(rates));
								}
						}
				}
			)
		);
	}
}
package models;

import java.util.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.codehaus.jackson.JsonNode;


import com.avaje.ebean.Ebean;

import play.Logger;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * @author john
 * Singleton class to handle storing rate information in database
 */
@Entity
public class Rates extends Model {
    
	@Id
  public Long id;
  
  @Required
  @Column(length=4000) 
  public String rates;
  
  private static final long DB_ID = 1;
  private static Rates instance = null;
  
  protected Rates() {
  }
  
  /**
   * Retrieves rates instance from memory, from the database, or creates a new one.
   * @return the singleton instance of rates class
   */
  public static Rates getInstance() {
     if(instance == null) {
        instance = Ebean.find(Rates.class, DB_ID);
        if(instance == null){
        	instance = new Rates();
        	instance.id = DB_ID;
        }
     }
     return instance;
  }


  public String getRates() {
  	return this.rates;
  }
  
  private void setRates(String rates){
  	this.rates = rates;
  }
  
  public void updateRates(String ratesData) {
  	instance.setRates(ratesData);
    instance.save();
  }
    
}
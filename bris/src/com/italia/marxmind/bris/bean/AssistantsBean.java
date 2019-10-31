package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.italia.marxmind.bris.application.ApplicationFixes;
import com.italia.marxmind.bris.application.ApplicationVersionController;
import com.italia.marxmind.bris.controller.BCard;
import com.italia.marxmind.bris.controller.Barangay;
import com.italia.marxmind.bris.controller.Clearance;
import com.italia.marxmind.bris.controller.Customer;
import com.italia.marxmind.bris.controller.Features;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.UserDtls;
import com.italia.marxmind.bris.enm.CivilStatus;
import com.italia.marxmind.bris.enm.ClearanceType;
import com.italia.marxmind.bris.enm.Feature;
import com.italia.marxmind.bris.enm.Purpose;
import com.italia.marxmind.bris.enm.UserAccess;
import com.italia.marxmind.bris.security.Copyright;
import com.italia.marxmind.bris.security.License;
import com.italia.marxmind.bris.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 07/11/2017
 * @version 1.0
 *
 */
@ManagedBean(name="assistBean", eager=true)
@ViewScoped
public class AssistantsBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6567894631L;
	private String welcomeMsg;
	private int countingEmptyHit;
	private int countingWrongWords;
	
	private String syntax;
	
	public String handleCommand(String command, String[] notes) {
		if(countingEmptyHit>=7) return "The system is locked. Please logout in order to use this assistant again.";
		Login in = Login.getUserLogin();
		String answer="";
		String name = "";
    	UserDtls user = in.getUserDtls();
    	name = user.getFirstname();
    	boolean allow = false;
    	
    	if(UserAccess.DEVELOPER.getId() == in.getAccessLevel().getLevel()){
    		allow = true;
    	}else{
    		allow = Features.isEnabled(Feature.ASSISTANT);
    	}
    	
    	if(allow){
    	
    	String[] empty = {
    			"you've type empty",
    			"it seems that you hit the enter key",
    			"please give me a question",
    			"sorry! I can't process your question if you'll not provide me words."
    			};	
    		
    	if(command==null || command.isEmpty()){
    		
    		if(countingEmptyHit==0 || countingEmptyHit==1){
    			answer = name + ", " + empty[(int)(Math.random() * empty.length)];
    		}else if(countingEmptyHit==2){
    			answer = name + ", this is your second hit of empty.";
    		}else if(countingEmptyHit==3){
    			answer = name + ", this is your third time hitting the empty.";
    		}else if(countingEmptyHit==4){
    			answer = name + ", you are giving me a head ache.";
    		}else if(countingEmptyHit==5){
    			answer = name + ", Im giving you my last warning for hitting an empty.";
    		}else if(countingEmptyHit==6){
    			answer = name + ", Im locking now the system.";
    		}	
    		countingEmptyHit++; 
    		return "Mr. Bris: " + answer;
    		
    	}
    	
    	countingEmptyHit = 0; //reset counting for empty
    	
    	switch(command){
    		case "help":
    			answer = name + ", Here are the words I can only understand. "
            			+ helpFunction();
    			countingWrongWords=0;
    			break;
    		case "count":
    			answer = count(name, notes);
    			countingWrongWords=0;
    			break;
    		case "info":
    			answer = info(name, notes);
    			countingWrongWords=0;
    			break;
    		case "great" :
            	String[] ans = {
            			"Look like you have a good day " + name,
            			"Being great is awesome " + name + ", wish you a great day ahead.",
            			"What a positive day " + name,
            			"Enjoy your day " + name,
            			"Glad to know " + name,
            			"Wish you all the best today",
            			"Wow! keep going " + name,
            			"That is a good attitude",
            			"Stay high " + name,
            			"Have a cool aura " + name
            			}; 
            	answer = ans[(int) (Math.random() * ans.length)];
            	countingWrongWords=0;
            	break;
            case "good" :
            	
            	String[] good = {
            			"Look like you have a good day " + name,
            			"Being good is awesome " + name + ", wish you a great day ahead.",
            			"What a positive day " + name,
            			"Enjoy your day " + name,
            			"Glad to know " + name,
            			"Wish you all the best today",
            			"Wow! keep going " + name,
            			"That is a good attitude",
            			"Stay high " + name,
            			"Have a cool aura " + name
            			}; 
            	answer = good[(int) (Math.random() * good.length)];
            	countingWrongWords=0;
            	break;	
            case "tired" :
            	
            	String[] tired = {
            			"Sorry for being tired " + name,
            			"Looks like you need a rest " + name,
            			"Something like you have done a lot today " + name,
            			"Did you know that eating a healthy food can alleviate your tiredness",
            			"Did you know that taking a break from work can create positive energy? Try it " + name + " sometime"
            			}; 
            	answer = tired[(int) (Math.random() * tired.length)];
            	countingWrongWords=0;
            	break;
            case "busy" :
            	
            	String[] busy = {
            			"Sorry for being busy " + name,
            			"Looks like you need a rest " + name,
            			"Something like you have done a lot today " + name,
            			"Did you know that taking a break from work can create positive energy? Try it " + name + " sometime"
            			}; 
            	answer = busy[(int) (Math.random() * busy.length)];
            	countingWrongWords=0;
            	break;	
            case "date" :
            	answer = name + ", Today is " + new Date().toString();
            	countingWrongWords=0;
            	break;
            	default:{
            		if(countingWrongWords==0){
            			answer = name + ", Please specify your question. Try to use these words " + helpFunction();
            			countingWrongWords++;
            		}else if(countingWrongWords==1){
            			answer = name + ", Can you please check these words " + helpFunction();
            			countingWrongWords++;
            		}else if(countingWrongWords==2){
            			answer = name + ", If you keep continue giving me words which are not in my library. Ill stop giving you a response.";
            			countingWrongWords++;
            		}else if(countingWrongWords==3){
            			answer = name + ", I'm about to locking the system.";
            			countingWrongWords++;
            		}else if(countingWrongWords==4){
            			answer = name + ", I'm locking now the system.";
            			countingEmptyHit=7;
            			countingWrongWords=0;
            		}	
            		
            	}
    	}
    	
    	}else{
    		answer = name + ", You are not allowed to use this feature. Please contact the developer to enable this feature.";
    	}
    	
    	return "Mr. Bris: " + answer;
	}
	
	private String info(String name, String[] notes){
		String answer = "";
		int size =  notes.length;
		if(notes!=null && size>0){
			
			String phrases = "";
			int x=1;
			for(String note : notes){
				if(x==size){
					phrases += note;
				}else{
					phrases += note + " ";
				}
				x++;
			}
			
			System.out.println("info: " + phrases);
			
			switch(phrases){
				
			case "clearance":
				answer = name + ", We can create a clearance based on request by a person. To do that, go to clearance processing page.";
				break;
			case "id":
				answer = name + ", We can create an ID on-demand. To do that, go to ID Creation page.";
				break;	
			case "barangay":
				answer = loadBarangay(name);
				break;
			default:{
				//Registered person
	        	List<Customer> cus = null;
	        	
	        	if(phrases!=null && !phrases.isEmpty()){
		        	String sqlAdd=" AND ( cus.fullname like '%"+ phrases.replace("--", "") +
		        			"%' OR cus.cusfirstname like '%"+ phrases.replace("--", "") +
		        			"%' OR cus.cusmiddlename like '%"+ phrases.replace("--", "") +
		        			"%' OR cus.cuslastname like '%"+  phrases.replace("--", "") +"%')";
		        	cus = Customer.retrieve(sqlAdd, new String[0]);
	        	}
	        	if(cus.size()>0){
	        		
	        		answer = personInfo(cus,name,phrases);
	        		
	        	}else{
	        	
	        		answer = typeNotFound(name, phrases);
	        	
	        	}
			}
			}
			
		}else{
			answer = name + ", info is use to display information example info Mr. Bris";
		}
		
		return answer;
	}
	
	private String count(String name, String[] notes){
		String answer = "";
		
		if(notes!=null){
			
			if(notes.length>0){
				
				String phrases = "";
				for(String n : notes){
					phrases +=n.toLowerCase();
				}
				
				String age="0";
		    	
		    	
		    	if(phrases.contains("age>")){
		    		age = phrases.split(">")[1];
		    		phrases = "age>";
		    	}else if(phrases.contains("age<")){
		    		age = phrases.split("<")[1];
		    		phrases = "age<";
		    	}
				
				System.out.println("Check count phrases : " + phrases);
				
				switch(phrases){
				
				case "registeredperson":
					answer = registeredPerson(name, "all");
					break;
				case "registeredpersontoday":
					answer = registeredPerson(name, "today");
					break;	
				case "registeredpersonyesterday":
					answer = registeredPerson(name, "yesterday");
					break;	
				case "clearance":
					answer = clearance(name, "all");
					break;
				case "clearancetoday":
					answer = clearance(name, "today");
					break;
				case "clearanceyesterday":
					answer = clearance(name, "yesterday");
					break;
				case "id":
					answer = idTransacations(name, "all");
					break;
				case "idtoday":
					answer = idTransacations(name, "today");
					break;
				case "idyesterday":
					answer = idTransacations(name, "yesterday");
					break;
				case "male":
					answer = loadMale(name);
					break;
				case "female":
					answer = loadFemale(name);
					break;	
				case "single":
					answer = loadSingle(name);
					break;
				case "married":
					answer = loadMarried(name);
					break;
				case "divorced":
					answer = loadDivorced(name);
					break;
				case "widowed":
					answer = loadWidowed(name);
					break;
				case "age>":
					answer = loadAgeGreater(name, age);
					break;
				case "age<":
					answer = loadAgeLess(name, age);
					break;	
					default:{
						answer = name + ", Please correct the spelling. ";
					}
				}
				
			}else{
				answer = name + ", count is use to display number of registerded person example count registered person. ";
			}
			
		}else{
			answer = name + ", count is use to display number of registerded person example count registered person. ";
		}
		
		return answer;
	}
	
	private String loadBarangay(String name){
		String answer = "";
		answer = name +  ", Here are the list of Barangay: ";
    	List<Barangay> bgy = Barangay.retrieve(" AND bgy.bgisactive=1", new String[0]);
    	int cnt = bgy.size();
    	int cx=1;
    	for(Barangay bg : bgy){
    		
    		if(cx<cnt){
    			answer += bg.getName() +", ";
    		}else{
    			answer += bg.getName() +".";
    		}
    		
    		cx++;
    	}
		return answer;
	}
	
	private String loadAgeLess(String name, String age){
		String answer = "";
		
		String sqlAdd=" AND cus.cusage<? AND cus.cusisactive=1";
    	String[] params = new String[1];
    	params[0] = ""+age;
    	List<Customer> cusagebelow = Customer.retrieve(sqlAdd, params);
    	
    	if(cusagebelow.size()>0){
    		int cntAge = cusagebelow.size();
    		if(cntAge>1){
    			answer = name + ", Total of " +cntAge + " were registered for age below " + age;
    		}else{
    			answer = name + ", Total of " +cntAge + " was registered for age below " + age;
    		}
    		answer +=". To know more about age counting, please go to Person Profiling page.";
    	}else{
    		answer =name + ", No person is registered.";
    	}
		
		return answer;
	}
	
	private String loadAgeGreater(String name, String age){
		String answer = "";
		String sqlAdd=" AND cus.cusage>? AND cus.cusisactive=1";
    	String[] params = new String[1];
    	params[0] = ""+age;
    	List<Customer> cusage = Customer.retrieve(sqlAdd, params);
    	
    	if(cusage.size()>0){
    		int cntAge = cusage.size();
    		if(cntAge>1){
    			answer = name + ", Total of " +cntAge + " were registered for age above " + age;
    		}else{
    			answer = name + ", Total of " +cntAge + " was registered for age above " + age;
    		}
    		answer +=". To know more about age counting, please go to Person Profiling page.";
    	}else{
    		answer =name + ", No person is registered.";
    	}
		return answer;
	}
	
	private String loadWidowed(String name){
		String answer = "";
		String sqlAdd=" AND cus.civilstatus=? AND cus.cusisactive=1";
    	String[] params = new String[1];
    	params[0] = CivilStatus.WIDOWED.getId()+"";
    	List<Customer> widowed = Customer.retrieve(sqlAdd, params);
    	
    	if(widowed.size()>0){
    		int cntwidowed = widowed.size();
    		if(cntwidowed>1){
    			answer = name + ", " + cntwidowed + " widowed were registered.";
    		}else{
    			answer = name + ", " + cntwidowed + " widowed was registered.";
    		}
    		answer +=" To know more about widowed counting, please go to Person Profiling page.";
    	}else{
    		answer = name + ", No widowed person is registered.";
    	}
		return answer;
	}
	
	private String loadDivorced(String name){
		String answer = "";
		String sqlAdd=" AND cus.civilstatus=? AND cus.cusisactive=1";
    	String[] params = new String[1];
    	params[0] = CivilStatus.DIVORCED.getId()+"";
    	List<Customer> divorced = Customer.retrieve(sqlAdd, params);
    	
    	if(divorced.size()>0){
    		int cntdivorced = divorced.size();
    		if(cntdivorced>1){
    			answer = name + ", " + cntdivorced + " divorced were registered.";
    		}else{
    			answer = name + ", " + cntdivorced + " divorced was registered.";
    		}
    		answer +=" To know more about divorced counting, please go to Person Profiling page.";
    	}else{
    		answer =name + ", No divorced person is registered.";
    	}
		return answer;
	}
	
	private String loadMarried(String name){
		String answer = "";
		String sqlAdd=" AND cus.civilstatus=? AND cus.cusisactive=1";
    	String[] params = new String[1];
    	params[0] = CivilStatus.MARRIED.getId()+"";
    	List<Customer> married = Customer.retrieve(sqlAdd, params);
    	
    	if(married.size()>0){
    		int cntmarried = married.size();
    		if(cntmarried>1){
    			answer = name + ", " + cntmarried + " marrieds were registered.";
    		}else{
    			answer = name + ", " +cntmarried + " married was registered.";
    		}
    		answer +=" To know more about married counting, please go to Person Profiling page.";
    	}else{
    		answer =name + ", No married person is registered.";
    	}
		return answer;
	}
	
	private String loadSingle(String name){
		String answer = "";
		String sqlAdd=" AND cus.civilstatus=? AND cus.cusisactive=1";
    	String[] params = new String[1];
    	params[0] = CivilStatus.SINGLE.getId()+"";
    	List<Customer> singles = Customer.retrieve(sqlAdd, params);
    	
    	if(singles.size()>0){
    		int cntsingles = singles.size();
    		if(cntsingles>1){
    			answer = name + ", " + cntsingles + " singles were registered.";
    		}else{
    			answer = name + ", " + cntsingles + " single was registered.";
    		}
    		answer +=" To know more about single counting, please go to Person Profiling page.";
    	}else{
    		answer = name + ", No single person is registered.";
    	}
    	return answer;
	}
	
	private String loadMale(String name){
		String answer="";
		String sqlAdd=" AND cus.cusgender=? AND cus.cusisactive=1";
    	String[] params = new String[1];
    	params[0] = "1";
    	List<Customer> cusx = Customer.retrieve(sqlAdd, params);
    	
    	if(cusx.size()>0){
    		int cntMale = cusx.size();
    		if(cntMale>1){
    			answer = name + ", "+ cntMale + " males were registered.";
    		}else{
    			answer = name + ", "+cntMale + " male was registered.";
    		}
    		answer +=". To know more about male counting, please go to Person Profiling page.";
    	}else{
    		answer = name + ", No male person is registered.";
    	}
    	return answer;
	}
	
	private String loadFemale(String name){
		String answer="";
		String sqlAdd=" AND cus.cusgender=? AND cus.cusisactive=1";
    	String[] params = new String[1];
    	params[0] = "2";
    	List<Customer> cusz = Customer.retrieve(sqlAdd, params);
    	
    	if(cusz.size()>0){
    		int cntMale = cusz.size();
    		if(cntMale>1){
    			answer = name + ", " +cntMale + " females were registered.";
    		}else{
    			answer = name + ", " +cntMale + " female was registered.";
    		}
    		answer +=". To know more about female counting, please go to Person Profiling page.";
    	}else{
    		answer =name + ", No female person is registered.";
    	}
    	return answer;
	}
	
	private String idTransacations(String name, String value){
		String answer="";
		String sql = " AND crd.isactivebid ";
		String[] params = new String[0];
		List<BCard> cards = null;
		Customer customer = null;
		BCard card = null;
		String call="";
		switch(value){
		
		case "all":
			cards = BCard.retrieve(sql, params);
			if(cards.size()==0){
				answer = name + ", We have not yet generate a Barangay ID.";
			}else if(cards.size()==1){
				answer = name + ", There was one Barangay ID requested";
				customer = cards.get(0).getTaxpayer();
				card = cards.get(0);
				if("1".equalsIgnoreCase(customer.getGender())){
					answer += " and this was requested by Mr. " +customer.getFullname() +" issued on " + card.getDateTrans() + " and will expired on " + card.getValidDateTo()+"."; 
				}else if("2".equalsIgnoreCase(customer.getGender())){
					if(CivilStatus.MARRIED.getId()==customer.getCivilStatus()){
						call =  "Mrs. ";
					}else{
						call =  "Miss. ";
					}
					answer += " and this was requested by "+ call +". " +customer.getFullname() +" issued on " + card.getDateTrans() + " and will expired on " + card.getValidDateTo()+".";
				}
			}else if(cards.size()>1){
				answer = name + ", There were "+ cards.size() +" Barangay ID requested.";
			}
			break;
		case "today":
			sql += " AND crd.datetrans=?";
			params = new String[1];
			params[0] = DateUtils.getCurrentDateYYYYMMDD();
			cards = BCard.retrieve(sql, params);
			if(cards.size()==0){
				answer = name + ", We have not yet generate a Barangay ID today.";
			}else if(cards.size()==1){
				answer = name + ", There was one Barangay ID requested today";
				customer = cards.get(0).getTaxpayer();
				card = cards.get(0);
				if("1".equalsIgnoreCase(customer.getGender())){
					answer += " and this was requested by Mr. " +customer.getFullname() +" issued on " + card.getDateTrans() + " and will expired on " + card.getValidDateTo() +"."; 
				}else if("2".equalsIgnoreCase(customer.getGender())){
					if(CivilStatus.MARRIED.getId()==customer.getCivilStatus()){
						call =  "Mrs. ";
					}else{
						call =  "Miss. ";
					}
					answer += " and this was requested by "+ call +". " +customer.getFullname() +" issued on " + card.getDateTrans() + " and will expired on " + card.getValidDateTo()+".";
				}
			}else if(cards.size()>1){
				answer = name + ", There were "+ cards.size() +" Barangay ID requested today.";
			}
			break;	
		case "yesterday":
			sql += " AND crd.datetrans=?";
			params = new String[1];
			String[] date = DateUtils.getCurrentDateYYYYMMDD().split("-");
			
			int yesterday = Integer.valueOf(date[2]);
			yesterday -=1;
			if(yesterday==0){//display previous month
				int month = Integer.valueOf(date[1]);
				month -=1;
				if(month==0){//previous year
					int year = Integer.valueOf(date[0]);
					year -=1;
					params[0] = year + "-12-31"; //needs improvement if previous date is not 31
				}else{
					if(month<10){
						params[0] = date[0] + "-0" + month + "-31"; //needs improvement if previous date is not 31
					}else{
						params[0] = date[0] + "-" + month + "-31"; //needs improvement if previous date is not 31
					}
				}
			}else{
				if(yesterday<10){
					params[0] = date[0] + "-" + date[1] + "-0"+yesterday;
				}else{
					params[0] = date[0] + "-" + date[1] + "-"+yesterday;
				}
			}
			
			
			cards = BCard.retrieve(sql, params);
			if(cards.size()==0){
				answer = name + ", We have not generate a Barangay ID yesterday.";
			}else if(cards.size()==1){
				answer = name + ", There was one Barangay ID requested yesterday";
				customer = cards.get(0).getTaxpayer();
				card = cards.get(0);
				if("1".equalsIgnoreCase(customer.getGender())){
					answer += " and this was requested by Mr. " +customer.getFullname() +" issued on " + card.getDateTrans() + " and will expired on " + card.getValidDateTo()+"."; 
				}else if("2".equalsIgnoreCase(customer.getGender())){
					if(CivilStatus.MARRIED.getId()==customer.getCivilStatus()){
						call =  "Mrs. ";
					}else{
						call =  "Miss. ";
					}
					answer += " and this was requested by "+ call +". " +customer.getFullname() +" issued on " + card.getDateTrans() + " and will expired on " + card.getValidDateTo()+".";
				}
			}else if(cards.size()>1){
				answer = name + ", There were "+ cards.size() +" Barangay ID requested yesterday.";
			}
			break;		
			
		
		}
		
		return answer;
	}
	
	private String clearance(String name, String value){
		String answer="";
		String sql = " AND clz.isactiveclearance ";
		String[] params = new String[0];
		List<Clearance> clearance = null;
		Customer customer = null;
		Clearance clr = null;
		String call="";
		switch(value){
		
		case "all":
			clearance = Clearance.retrieve(sql, params);
			if(clearance.size()==0){
				answer = name + ", We have not yet registered a clearance.";
			}else if(clearance.size()==1){
				answer = name + ", There was one clearance registered";
				customer = clearance.get(0).getTaxPayer();
				clr = clearance.get(0);
				if("1".equalsIgnoreCase(customer.getGender())){
					answer += " and this was requested by Mr. " +customer.getFullname() +". The purpose of his clearance was for " + Purpose.typeName(clr.getPurposeType()) +
							" and this was signed by Hon. " + clr.getEmployee().getFirstName() + " " + clr.getEmployee().getLastName() +", issued on " + clr.getIssuedDate() +".";
				}else if("2".equalsIgnoreCase(customer.getGender())){
					if(CivilStatus.MARRIED.getId()==customer.getCivilStatus()){
						call =  "Mrs. ";
					}else{
						call =  "Miss. ";
					}
					answer += " and this was requested by "+ call +customer.getFullname() +". The purpose of her clearance was for " + Purpose.typeName(clr.getPurposeType()) +
							" and this was signed by Hon. " + clr.getEmployee().getFirstName() + " " + clr.getEmployee().getLastName() +", issued on " + clr.getIssuedDate() +".";
				}
			}else if(clearance.size()>1){
				answer = name + ", There were "+ clearance.size() +" clearance registered.";
			}
			break;
		case "today":
			sql += " AND clz.clearissueddate=?";
			params = new String[1];
			params[0] = DateUtils.getCurrentDateYYYYMMDD();
			clearance = Clearance.retrieve(sql, params);
			if(clearance.size()==0){
				answer = name + ", We have not yet registered a clearance today.";
			}else if(clearance.size()==1){
				answer = name + ", There was one clearance registered today";
				customer = clearance.get(0).getTaxPayer();
				clr = clearance.get(0);
				if("1".equalsIgnoreCase(customer.getGender())){
					answer += " and this was requested by Mr. " +customer.getFullname() +". The purpose of his clearance was for " + Purpose.typeName(clr.getPurposeType()) +
							" and this was signed by Hon. " + clr.getEmployee().getFirstName() + " " + clr.getEmployee().getLastName() +", issued on " + clr.getIssuedDate() +".";
				}else if("2".equalsIgnoreCase(customer.getGender())){
					System.out.println("customer.getCivilStatus(): "+customer.getCivilStatus());
					if(CivilStatus.MARRIED.getId()==customer.getCivilStatus()){
						call =  "Mrs. ";
					}else{
						call =  "Miss. ";
					}
					answer += " and this was requested by "+ call +customer.getFullname() +". The purpose of her clearance was for " + Purpose.typeName(clr.getPurposeType()) +
							" and this was signed by Hon. " + clr.getEmployee().getFirstName() + " " + clr.getEmployee().getLastName() +", issued on " + clr.getIssuedDate() +".";
				}
			}else if(clearance.size()>1){
				answer = name + ", There were "+ clearance.size() +" clearance registered today";
			}
			break;	
		case "yesterday":
			sql += " AND clz.clearissueddate=?";
			params = new String[1];
			String[] date = DateUtils.getCurrentDateYYYYMMDD().split("-");
			
			int yesterday = Integer.valueOf(date[2]);
			yesterday -=1;
			if(yesterday==0){//display previous month
				int month = Integer.valueOf(date[1]);
				month -=1;
				if(month==0){//previous year
					int year = Integer.valueOf(date[0]);
					year -=1;
					params[0] = year + "-12-31"; //needs improvement if previous date is not 31
				}else{
					if(month<10){
						params[0] = date[0] + "-0" + month + "-31"; //needs improvement if previous date is not 31
					}else{
						params[0] = date[0] + "-" + month + "-31"; //needs improvement if previous date is not 31
					}
				}
			}else{
				if(yesterday<10){
					params[0] = date[0] + "-" + date[1] + "-0"+yesterday;
				}else{
					params[0] = date[0] + "-" + date[1] + "-"+yesterday;
				}
			}
			
			
			clearance = Clearance.retrieve(sql, params);
			if(clearance.size()==0){
				answer = name + ", We have not registered a clearance yesterday.";
			}else if(clearance.size()==1){
				answer = name + ", There was one clearance registered yesterday";
				customer = clearance.get(0).getTaxPayer();
				clr = clearance.get(0);
				if("1".equalsIgnoreCase(customer.getGender())){
					answer += " and this was requested by Mr. " +customer.getFullname() +". The purpose of his clearance was for " + Purpose.typeName(clr.getPurposeType()) +
							" and this was signed by Hon. " + clr.getEmployee().getFirstName() + " " + clr.getEmployee().getLastName() +", issued on " + clr.getIssuedDate() +".";
				}else if("2".equalsIgnoreCase(customer.getGender())){
					if(CivilStatus.MARRIED.getId()==customer.getCivilStatus()){
						call =  "Mrs. ";
					}else{
						call =  "Miss. ";
					}
					answer += " and this was requested by "+ call +customer.getFullname() +". The purpose of her clearance was for " + Purpose.typeName(clr.getPurposeType()) +
							" and this was signed by Hon. " + clr.getEmployee().getFirstName() + " " + clr.getEmployee().getLastName() +", issued on " + clr.getIssuedDate() +".";
				}
			}else if(clearance.size()>1){
				answer = name + ", There were "+ clearance.size() +" clearance registered yesterday.";
			}
			break;		
			
		
		}
		
		return answer;
	}
	
	private String registeredPerson(String name, String value){
		String answer="";
		String sql = " AND cus.cusisactive ";
		String[] params = new String[0];
		List<Customer> customers = null;
		switch(value){
		
		case "all":
			customers = Customer.retrieve(sql, params);
			if(customers.size()==0){
				answer = name + ", We have not yet registered a person.";
			}else if(customers.size()==1){
				answer = name + ", There was one person registered";
				if("1".equalsIgnoreCase(customers.get(0).getGender())){
					answer += " and his name is " +customers.get(0).getFullname() +".";
				}else if("2".equalsIgnoreCase(customers.get(0).getGender())){
					answer += " and her name is " +customers.get(0).getFullname() +".";
				}
			}else if(customers.size()>1){
				answer = name + ", There were "+ customers.size() +" persons registered.";
			}
			break;
		case "today":
			sql += " AND cus.cusdateregistered=?";
			params = new String[1];
			params[0] = DateUtils.getCurrentDateYYYYMMDD();
			customers = Customer.retrieve(sql, params);
			if(customers.size()==0){
				answer = name + ", We have not yet registered a person today.";
			}else if(customers.size()==1){
				answer = name + ", There was one person registered today";
				if("1".equalsIgnoreCase(customers.get(0).getGender())){
					answer += " and his name is " +customers.get(0).getFullname() +".";
				}else if("2".equalsIgnoreCase(customers.get(0).getGender())){
					answer += " and her name is " +customers.get(0).getFullname() +".";
				}
			}else if(customers.size()>1){
				answer = name + ", There were "+ customers.size() +" persons registered today";
			}
			break;	
		case "yesterday":
			sql += " AND cus.cusdateregistered=?";
			params = new String[1];
			String[] date = DateUtils.getCurrentDateYYYYMMDD().split("-");
			
			int yesterday = Integer.valueOf(date[2]);
			yesterday -=1;
			if(yesterday==0){//display previous month
				int month = Integer.valueOf(date[1]);
				month -=1;
				if(month==0){//previous year
					int year = Integer.valueOf(date[0]);
					year -=1;
					params[0] = year + "-12-31"; //needs improvement if previous date is not 31
				}else{
					if(month<10){
						params[0] = date[0] + "-0" + month + "-31"; //needs improvement if previous date is not 31
					}else{
						params[0] = date[0] + "-" + month + "-31"; //needs improvement if previous date is not 31
					}
				}
			}else{
				if(yesterday<10){
					params[0] = date[0] + "-" + date[1] + "-0"+yesterday;
				}else{
					params[0] = date[0] + "-" + date[1] + "-"+yesterday;
				}
			}
			
			
			customers = Customer.retrieve(sql, params);
			if(customers.size()==0){
				answer = name + ", We have not registered a person yesterday.";
			}else if(customers.size()==1){
				answer = name + ", There was one person registered yesterday";
				if("1".equalsIgnoreCase(customers.get(0).getGender())){
					answer += " and his name is " +customers.get(0).getFullname() +".";
				}else if("2".equalsIgnoreCase(customers.get(0).getGender())){
					answer += " and her name is " +customers.get(0).getFullname() +".";
				}
			}else if(customers.size()>1){
				answer = name + ", There were "+ customers.size() +" persons registered yesterday.";
			}
			break;		
			
		
		}
		
		return answer;
	}
	
	public String helpFunction(){
		String help = "";
		
		help = "count[rigestered person, rigestered person today, rigestered person yesterday,clearance, clearance today, clearance yesterday, id, id today, id yesterday, female, male, single, married, divorced, widowed, age>your number, age<your number], info[clearance, id, info name of registered person]";
		
		return help;
	}
	
	private String typeNotFound(String name, String command){
		String answer = "";
		
		String[] remarks = {
				"hope you understand.", 
				"sorry!", 
				"can you please select on my library. Library: " + helpFunction(), 
				name + ", did you know that you can type 'help' to know the words you can use to communicate with me. " + name + ", These are the words you can use: " + helpFunction(),
				name + ", pardon me for my insufficient."
			};
		answer = name + ", you have type the word " + command + " which is not yet in my library , " + remarks[(int)(Math.random() * remarks.length)];
		
		return answer;
	}
	
	private String personInfo(List<Customer> cus, String name, String command){
		String answer = "";
		String sqlAdd="";
    	String[] params = new String[0];
		if(cus.size()>1){
			answer = name + ", I have retrieved " + cus.size() +" names for " + command + ".";
					
					int x=1;
					for(int i=0; i<cus.size(); i++){
						if(x!=cus.size()){
							answer += cus.get(i).getFullname()+", ";
						}else{
							answer += cus.get(i).getFullname()+". ";
						}
						x++;
					}
					String[] tips = {
							"Please note that when searching a person name  please use info example info Mr. Bris instead of Mr. Bris.",
							"When looking for a name example Mr. Bris. You can type info Mr. Bris",
							"If you are looking for a person complete name please type example info Juan Miguel Agosto instead of Juan Miguel Agosto.",
							"For searching a name here is the format info firstname middlename lastname example info Juan Jose Jesus.",
							"For better result follow the right format when searching a name. Anyway, here is the format info firstname middlename lastname example info Michael Dizon Cruz."
							}; 
					answer += "Can you type which of the following would you like to know the information? " + tips[(int) (Math.random() * tips.length)];
		}else{
			Customer cuz = cus.get(0);
			int gender = Integer.valueOf(cuz.getGender());
			String[] mrmrs = {"", "Mr.", "Miss","Mrs."};
			String[] heshe = {"","He","She"};
			String[] hisher = {"","His","Her"};
			String[] himher = {"","Him","Her"};
			
			if(CivilStatus.MARRIED.getId()==cuz.getCivilStatus()){
				if(gender==2){
					answer = name + ", "+ mrmrs[gender+1] +" " + cuz.getFullname();
				}else{
					answer = name + ", "+ mrmrs[gender] +" " + cuz.getFullname();
				}
				
			}else{
			
				answer = name + ", "+ mrmrs[gender] +" " + cuz.getFullname();
			
			}
			
			String address = cuz.getPurok().getPurokName() +", " + cuz.getBarangay().getName() + ", " + cuz.getMunicipality().getName() + ", " + cuz.getProvince().getName();
			answer += " is a bonafide residence of " + address;		
			answer += ". We registered " + himher[gender].toLowerCase();
			answer +=" last " + cuz.getDateregistered() +".";
			
			//clearance
			sqlAdd =" AND cuz.fullname=?";
			params = new String[1];
			params[0] = cuz.getFullname();
			List<Clearance> clrs = Clearance.retrieve(sqlAdd, params);
			
			if(clrs.size()>0){
				int totalClr = clrs.size();
				int cntClr=1;
				answer +=" "+heshe[gender]+ " has already total of "+ totalClr + " clearance requested.";
				answer +=" " + hisher[gender] + " first clearance transaction was ";
				for(Clearance cl : clrs){
					if(cntClr==1){
						answer += " Requesting for " + Purpose.typeName(cl.getPurposeType()) + " issued on " + cl.getIssuedDate() + ", signed by kagawad " + cl.getEmployee().getFirstName() + " " + cl.getEmployee().getLastName();
					}
					if(cntClr>1 && cntClr==totalClr){
						answer += " and " + hisher[gender].toLowerCase() +" recent requested clearance ";
						if(cl.getIssuedDate().equalsIgnoreCase(DateUtils.getCurrentDateYYYYMMDD())){
							answer += "is today.";
						}else{
							answer += "was on " + cl.getIssuedDate() +".";
						}
						answer += " Requesting for " + Purpose.typeName(cl.getPurposeType()) + " issued on " + cl.getIssuedDate() + ", signed by kagawad " + cl.getEmployee().getFirstName() + " " + cl.getEmployee().getLastName();
					}
					cntClr++;
				}
				
				if(cntClr==2){//add this if only one transaction for clearance
					answer +=".";
				}
				
			}else{
				answer +=" So far we are not yet issued " + himher[gender].toLowerCase() + " a clearance.";
			}
			
			//id request
			sqlAdd =" AND cuz.fullname=?";
			params = new String[1];
			params[0] = cuz.getFullname();
			List<BCard> bcard = BCard.retrieve(sqlAdd, params);
			
			
			if(bcard.size()>0){
				
				int totalId = bcard.size();
				int cntId = 1;
				
				answer +=" "+heshe[gender]+ " has already total of "+ totalId + " ID requested.";
				answer +=" " + hisher[gender] + " first ID transaction was ";
				
				for(BCard cd : bcard){
					
					if(cntId==1){
						answer += cd.getDateTrans() + " that will expire on " + cd.getValidDateTo();
					}
					
					if(cntId>1 && cntId==totalId){
						answer += " and " + hisher[gender].toLowerCase() +" recent requested ID ";
						if(cd.getDateTrans().equalsIgnoreCase(DateUtils.getCurrentDateYYYYMMDD())){
							answer += "is today ";
						}else{
							answer += "was on ";
						}
						answer += cd.getDateTrans() + " that will expire on " + cd.getValidDateTo() + ".";
					}
					
					
					cntId++;
				}
				if(cntId==2){//add this if only one transaction for ID
					answer +=".";
				}
				
				
			}else{
				answer +=" So far we are not yet issued " + himher[gender].toLowerCase() + " an ID.";
			}
			
			answer +=" To know more about " + cuz.getFullname() +" please go to Profile page.";
		}
		return answer;
	}
	
	
	
	/*private String help(){
		String help = "";
		
		help = "barangay, date, clearance*today, clearance*yesterday, "
    			+ "firstname*middlename*lastname, age>desired number or age<desired number, male, female, single, married, divorced, widowed, registered, id";
		
		return help;
	}*/
	
	public String getWelcomeMsg() {
		
		String name = "";
    	UserDtls user = Login.getUserLogin().getUserDtls();
    	name = user.getFirstname();
		
		welcomeMsg= name + ", " + getGreetings() + ". Welcome to BRIS Software assistant. My name is Mr. Bris. " + "In order to communicate with me, please type 'help' for the words to use.";
		
		String[] questions = {"How are you today?", "What would you like me to do for you?", "May I help you?"};
		String[] tips = {
					name + ", Did you know that BRIS is stand for Barangay Recording Information System? BRIS software is authored by " + appInfo().getAuthor(),
					name + ", You are using the BRIS version " + appVersionChanges().getBuildversion(),
					name + ", If you have problem using this software please contact the developer on this number " + appInfo().getContactno()
				};
		welcomeMsg += tips[(int)(Math.random() * tips.length)] + ". Anyway, " +questions[(int)(Math.random() * questions.length)];
		
		return welcomeMsg;
	}
	
	public String getGreetings() {
		
		String[] greets = {"Top of the", "Good", "Hey there, hope you're having a fine", "Hope, you are having a wonderful","Cool", "Great"};
		String greetings = greets[(int) (Math.random() * greets.length)];
		
		Calendar c = Calendar.getInstance();
		int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
		if(timeOfDay >= 0 && timeOfDay <12){
			greetings = greetings + " morning";
		}else if(timeOfDay >= 12 && timeOfDay < 16){
			greetings = greetings + " afternoon";
		}else if(timeOfDay >= 16 && timeOfDay < 21){
			greetings = greetings + " evening";
		}else if(timeOfDay >= 21 && timeOfDay < 24){
			greetings = greetings + " night";
		}
		
		return greetings;
	}
	
	private ApplicationVersionController appVersionChanges(){
		
		ApplicationVersionController versionController;
		List<ApplicationFixes> fixes = Collections.synchronizedList(new ArrayList<ApplicationFixes>());
		
		String sql = "SELECT * FROM app_version_control ORDER BY timestamp DESC LIMIT 1";
		String[] params = new String[0];
		versionController = ApplicationVersionController.retrieve(sql, params).get(0);
		
		try{fixes = Collections.synchronizedList(new ArrayList<ApplicationFixes>());}catch(Exception e){}
		sql = "SELECT * FROM buildfixes WHERE buildid=?";
		params = new String[1];
		params[0] = versionController.getBuildid()+"";
		try{fixes = ApplicationFixes.retrieve(sql, params);}catch(Exception e){}
		
		return versionController;
	}
	
	private Copyright appInfo(){
		
		Copyright copyright;
		String sql = "SELECT * FROM copyright ORDER BY id desc limit 1";
		String[] params = new String[0];
		copyright = Copyright.retrieve(sql, params).get(0);
		
		return copyright;
	}
	
	private List<License> appLicense(){
		List<License> licenses = Collections.synchronizedList(new ArrayList<License>());
		
		String sql = "SELECT * FROM license";
		licenses = Collections.synchronizedList(new ArrayList<License>());
		licenses = License.retrieve(sql, new String[0]);
		
		return licenses;
	}
	
	
	public void setWelcomeMsg(String welcomeMsg) {
		this.welcomeMsg = welcomeMsg;
	}

	public int getCountingEmptyHit() {
		return countingEmptyHit;
	}

	public void setCountingEmptyHit(int countingEmptyHit) {
		this.countingEmptyHit = countingEmptyHit;
	}

	public int getCountingWrongWords() {
		return countingWrongWords;
	}

	public void setCountingWrongWords(int countingWrongWords) {
		this.countingWrongWords = countingWrongWords;
	}

	public String getSyntax() {
		return helpFunction();
	}

	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}

}

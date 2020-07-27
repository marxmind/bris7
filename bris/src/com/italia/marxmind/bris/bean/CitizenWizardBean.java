package com.italia.marxmind.bris.bean;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CaptureEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.Barangay;
import com.italia.marxmind.bris.controller.Customer;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.Municipality;
import com.italia.marxmind.bris.controller.Province;
import com.italia.marxmind.bris.controller.Purok;
import com.italia.marxmind.bris.controller.UserDtls;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.CivilStatus;
import com.italia.marxmind.bris.enm.DateFormat;
import com.italia.marxmind.bris.enm.Relationships;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.utils.DateUtils;

/**
 * 
 * @author Mark Italia
 * @since 12/08/2018
 * @version 1.0
 *
 */
@Named
//@org.omnifaces.cdi.ViewScoped
@SessionScoped
public class CitizenWizardBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 546587337561L;
	
	private Customer citizen = new Customer();
	private String photoId="camera";
	private final String sep = File.separator;
	private final String PRIMARY_DRIVE = System.getenv("SystemDrive");
	private final String IMAGE_PATH = PRIMARY_DRIVE + sep + "bris" + sep + "img" + sep;
	private List<String> shots = new ArrayList<>();//Collections.synchronizedList(new ArrayList<>());
	private List civils = new ArrayList<>();//Collections.synchronizedList(new ArrayList<>());
	private List genderList = new ArrayList<>();//Collections.synchronizedList(new ArrayList<>());
	
	private Date birthdate;
	
	//private List<Customer> contactPersons = Collections.synchronizedList(new ArrayList<Customer>());
	private int relationshipId;
	private List relationships;
	
	private String searchProvince;
	private String searchMunicipal;
	private String searchBarangay;
	private String searchPurok;
	
	private List<Province> provinces = new ArrayList<Province>();//Collections.synchronizedList(new ArrayList<Province>());
	private List<Municipality> municipals = new ArrayList<Municipality>();//Collections.synchronizedList(new ArrayList<Municipality>());
	private List<Barangay> barangays = new ArrayList<Barangay>();//Collections.synchronizedList(new ArrayList<Barangay>());
	private List<Purok> puroks = new ArrayList<Purok>();//Collections.synchronizedList(new ArrayList<Purok>());
	
	private Province provSelected;
	private Municipality munSelected;
	private Barangay barSelected;
	private Purok purSelected;
	
	private String BARANGAY = ReadConfig.value(Bris.BARANGAY);
	private String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
	private String PROVINCE = ReadConfig.value(Bris.PROVINCE);
	
	private boolean skip;
	
	private String confirmDetails;
	private boolean hideUnhideWizard=false;
	
	private String searchCustomer;
	private List<Customer> customers = new ArrayList<Customer>();//Collections.synchronizedList(new ArrayList<Customer>());
	private boolean requiredEmergencyPerson = false;
	
	private String emergencyContactPersonName;
	private String searchEmergencyPerson;
	private Customer emergencyContactPerson;
	private List<Customer> contactPersons = new ArrayList<Customer>();//Collections.synchronizedList(new ArrayList<Customer>());
	
	/**
	 * adding this code to forcefully reloading the init method @see at sidemenu.xhtml actionListener="#{mainBean.reloadinit}"
	 * this problem exist because of changing the scope from @org.omnifaces.cdi.ViewScoped to  javax.enterprise.context.SessionScoped;
	 * PostConstruct in enterprise.sessionScope call init method once only
	 */
	public void reloadinit() {
		System.out.println("Reloading init");
		setSearchCustomer(null);
		clearFlds();
		init();
		System.out.println("Reloading init end here");
	}
	
	@PostConstruct
	public void init() {
		
		try{
			String editProfileName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("editProfile");
			System.out.println("Check pass name: " + editProfileName);
			if(editProfileName!=null && !editProfileName.isEmpty() && !"null".equalsIgnoreCase(editProfileName)){
				setSearchCustomer(editProfileName);
			}
			}catch(Exception e){}
			
			
			String barangayCode = ReadConfig.value(Bris.IDCODE);
			
			String sql = " AND cus.cusisactive=1 ";
			
			if(getSearchCustomer()!=null && !getSearchCustomer().isEmpty()){
				
				String sanitize = getSearchCustomer();
				sanitize = sanitize.replace("--", "");
				sanitize = sanitize.replace("%", "");
				sanitize = sanitize.replace("/", "");
				sanitize = sanitize.replace("drop", "");
				sanitize = sanitize.replace("table", "");
				sanitize = sanitize.replace("select", "");
				sanitize = sanitize.replace(";", "");
				sanitize = sanitize.replace("'", "");
				
				int size = getSearchCustomer().length();
				if(size>=5){
					customers = new ArrayList<Customer>();//Collections.synchronizedList(new ArrayList<Customer>());
					if(getSearchCustomer().contains(barangayCode)){
						String code = getSearchCustomer().toUpperCase();
						sql += " AND cus.cuscardno like '%" + code +"%'";
						customers = Customer.retrieve(sql, new String[0]);
					}else{
						if(sanitize!=null || !sanitize.isEmpty()) {
							sql += " AND cus.fullname like '%" + sanitize +"%'";
							customers = Customer.retrieve(sql, new String[0]);
						}
					}
					
				}
			}else{
				customers = new ArrayList<Customer>();//Collections.synchronizedList(new ArrayList<Customer>());
				sql += " order by cus.customerid DESC limit 100;";
				customers = Customer.retrieve(sql, new String[0]);
			}
			
			
			PrimeFaces ins = PrimeFaces.current();
			if(customers!=null && customers.size()==1){
				clickItem(customers.get(0));
				ins.executeScript("$('#panelHide').show(1000);showButton();");
			}else{
				clearFlds();
				ins.executeScript("$('#panelHide').hide(1000);hideButton()");
			}
	}
	
	public void showWizard() {
		setHideUnhideWizard(true);
	}
	
	private UserDtls getUser() {
		return Login.getUserLogin().getUserDtls();
	}
	
	@Deprecated
	public String onFlowProcess(FlowEvent event) {
		updateFinalDetails();
        if(skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        }
        else {
            return event.getNewStep();
        }
    }
	
	@Deprecated
	public void updateFinalDetails() {
		String val="<p>Below are the details you have provided, please check for accurate information.</p><br/>";
		
		if(citizen!=null) {
			val +="<p>Name: <strong>";
			if("1".equalsIgnoreCase(citizen.getGender())) {
				val +="Mr. ";
			}else {
				if(CivilStatus.SINGLE.getId()==citizen.getCivilStatus() || CivilStatus.DIVORCED.getId()==citizen.getCivilStatus() || CivilStatus.LIVEIN.getId()==citizen.getCivilStatus() || CivilStatus.WIDOWED.getId()==citizen.getCivilStatus()) {
					val +="Miss ";
				}else {
					val +="Mrs. ";
				}
			}
			val += citizen.getLastname() +", "+citizen.getFirstname() + " " + citizen.getMiddlename();
			val +="</strong></p>";
			val +="<p>Civil Status: <strong>";
			val += CivilStatus.typeName(citizen.getCivilStatus());
			val +="</strong></p>";
			val +="<p>Birthday: <strong>";
			calculateAge();
			val += DateUtils.convertDateToMonthDayYear(DateUtils.convertDate(getBirthdate(), DateFormat.YYYY_MM_DD())) + " (" + citizen.getAge() +")";
			val +="</strong></p>";
			val +="<p>Address: <strong>";
			
		    String address = "<p style=\"color: rgb(230, 0, 0);\">Please provide address</p>";
		    		try{address = getPurSelected().getPurokName() +", " + getBarSelected().getName() +", " + getMunSelected().getName() + ", " + getProvSelected().getName();}catch(Exception e) {}
		    val +=address;
		    val +="</strong></p>";
		    if(citizen.getContactno()!=null && !citizen.getContactno().isEmpty()) {
		    	val +="<p>Telephone/Mobile No: <strong>";
		    	val +=citizen.getContactno();
		    	val +="</strong></p>";
		    }
		    if(getEmergencyContactPerson()!=null) {
		    	
		    	val +="<p>Emergency contact person: <strong>";
		    	val +=getEmergencyContactPerson().getFullname();
		    	val +="</strong></p>";
		    	if(getRelationshipId()>0) {
		    		val +="<p>Relationship: <strong>";
		    		val += Relationships.typeName(getRelationshipId());
		    		val +="</strong></p>";
			    }else {
			    	val +="<p style=\"color: rgb(230, 0, 0);\">Please provide specific relationship in order to save this information</p>";
			    	
			    }
		    	
		    }
		    
		    val += "<br/><p><strong>To proceed click SAVE button</strong></p>";
		    
		}
		setConfirmDetails(val);
		
	}
	
	public void saveButton() {
			Customer cit = getCitizen();
			boolean isOk=true;
			
			if(cit!=null) {
				
				if(cit.getFirstname()==null || cit.getFirstname().isEmpty()) {
					Application.addMessage(3, "Error", "Please provide First Name");
					isOk= false;
				}
				
				if(cit.getMiddlename()==null || cit.getMiddlename().isEmpty()) {
					Application.addMessage(3, "Error", "Please provide Middle Name at least dot (.) if no middle name");
					isOk= false;
				}
				
				if(cit.getLastname()==null || cit.getLastname().isEmpty()) {
					Application.addMessage(3, "Error", "Please provide Last Name");
					isOk= false;
				}
				
				if(cit.getCustomerid()==0 && isOk) {
					boolean isExist = Customer.validateNameEntry(cit.getFirstname(), cit.getMiddlename(), cit.getLastname());
					if(isExist){
						Application.addMessage(3, cit.getFirstname() + " " + cit.getLastname() + " is already recorded.", "");
						isOk= false;
					}
				}
				
				
				
				Municipality mun = new  Municipality();
				Barangay bar = new Barangay();
				Purok pur = new Purok();		
				if(getProvSelected()==null) {
					Application.addMessage(3, "Error", "Please provide address at least province name");
					isOk= false;
				}else {
					if(getMunSelected()!=null) {
						if(getBarSelected()!=null) {
							if(getPurSelected()==null) {
								Application.addMessage(2, "Warning", "Purok name will not be recorded");
								pur.setId(0);
								setPurSelected(pur);
							}
						}else {
							Application.addMessage(2, "Warning", "Barangay and Purok name will not be recorded");
							bar.setId(0);
							setBarSelected(bar);
							pur.setId(0);
							setPurSelected(pur);
						}
					}else {
						Application.addMessage(2, "Warning", "Municipality Barangay and Purok name will not be recorded");
						
						mun.setId(0);
						setMunSelected(mun);
						bar.setId(0);
						setBarSelected(bar);
						pur.setId(0);
						setPurSelected(pur);
					}
				}
				
				if(getRelationshipId()>0) {
					if(getEmergencyContactPerson()==null) {
						Application.addMessage(3, "Error", "Please provide contact person");
						isOk= false;
					}
				}
				
				if(isOk) {
				
				if("camera".equalsIgnoreCase(getPhotoId())){
					copyDefaultImage();
				}
				
				String f, m, l = "";
				f = cit.getFirstname().toUpperCase().strip();
				m = cit.getMiddlename().toUpperCase().strip();
				l = cit.getLastname().toUpperCase().strip();
				cit.setFirstname(f);
				cit.setMiddlename(m);
				cit.setLastname(l);
					
					
					
					cit.setPhotoid(getPhotoId());
					cit.setBirthdate(DateUtils.convertDate(getBirthdate(), DateFormat.YYYY_MM_DD()));
					cit.setFullname(cit.getFirstname() + " " + cit.getLastname());
					cit.setProvince(getProvSelected());
					cit.setAge(DateUtils.calculateAge(cit.getBirthdate()));
					cit.setMunicipality(getMunSelected());
					cit.setBarangay(getBarSelected());
					cit.setPurok(getPurSelected());
					
					cit.setEmergencyContactPerson(getEmergencyContactPerson());
					cit.setRelationship(getRelationshipId());
					cit.setIsactive(1);
					cit.setUserDtls(getUser());
					cit.save();
					
					Application.addMessage(1, "Success", "Successfully saved.");
					init();
					clearFlds();
					
					PrimeFaces pm = PrimeFaces.current();
					pm.executeScript("callTop();showButton();");
					
					 
				}
			}
	}
	
	public void updateContactPerson() {
		if(getEmergencyContactPerson()==null) {
			Application.addMessage(3, "Error", "No selected contact");
		}
	}
	
	public void updateAddressProv() {
			try {
			
			if(getProvSelected()!=null) {
				Municipality mun = new Municipality();
				mun.setId(0);
				Barangay bar = new Barangay();
				bar.setId(0);
				Purok pur = new Purok();
				pur.setId(0);
				setMunSelected(mun);
				setBarSelected(bar);
				setPurSelected(pur);
			}
			
			}catch(Exception e) {
				Application.addMessage(2, "Warning", "The province you have provided is not yet recorded. Please type again.");
			}
			
	}
	
	public void updateAddressMun() {
		try {
			if(getProvSelected()!=null && getMunSelected()!=null) {
				Barangay bar = new Barangay();
				bar.setId(0);
				Purok pur = new Purok();
				pur.setId(0);
				setBarSelected(bar);
				setPurSelected(pur);
			}
		}catch(Exception e) {
			Application.addMessage(2, "Warning", "The municipality you have provided is not yet recorded. Please type again or leave blank");
		}
	}
	
	public void updateAddressBar() {
		try {
			if(getProvSelected()!=null && getMunSelected()!=null && getBarSelected()!=null) {
				Purok pur = new Purok();
				pur.setId(0);
				setPurSelected(pur);
			}
		}catch(Exception e) {
			Application.addMessage(2, "Warning", "The barangay you have provided is not yet recorded. Please type again or leave blank");
		}
	}
	
	
	private void copyDefaultImage(){
		
		photoId = getRandomImageName() + DateUtils.getCurrentDateMMDDYYYYTIMEPlain();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        
        String cameralogo = IMAGE_PATH + "default.jpg";
        String driveImage =  IMAGE_PATH + ReadConfig.value(Bris.BARANGAY_NAME) + Bris.SEPERATOR.getName() + photoId + ".jpg";
        
        File file = new File(cameralogo);
        try{
			Files.copy(file.toPath(), (new File(driveImage)).toPath(),
			        StandardCopyOption.REPLACE_EXISTING);
		}catch(IOException e){}
        
        
        	String contextImageLoc = File.separator + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;
            
            
            
            String pathToSave = externalContext.getRealPath("") + contextImageLoc;
            File fileNew = new File(driveImage);
            try{
    			Files.copy(fileNew.toPath(), (new File(pathToSave + fileNew.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    		}catch(IOException e){}
        
        

}
	
	public void updateEmergenctContactName() {
		if(getRelationshipId()>0) {
			setRequiredEmergencyPerson(true);
		}
	}
	
	public void clickItem(Customer cus) {
		
		loadDefaultAddress();
		
		shots = new ArrayList<>();//clearing picture
		setCitizen(cus);
		
		setBirthdate(DateUtils.convertDateString(cus.getBirthdate(),"yyyy-MM-dd"));
		
		if(cus.getEmergencyContactPerson()!=null){
		Customer person = Customer.customer(cus.getEmergencyContactPerson().getCustomerid());
		setRelationshipId(cus.getRelationship());
		setEmergencyContactPerson(person);
		setEmergencyContactPersonName(person.getFullname());
		}else{
			setRelationshipId(0);
			setEmergencyContactPerson(new Customer());
		}
		if(cus.getPhotoid()!=null){
			copyPhoto(cus.getPhotoid()); 
			getShots().add(cus.getPhotoid());
		}
		
		setProvSelected(cus.getProvince());
		setMunSelected(cus.getMunicipality());
		setBarSelected(cus.getBarangay());
		setPurSelected(cus.getPurok());
		
		//updateFinalDetails();
	}
	
	public void deleteRow(Customer cus){
		
			cus.setUserDtls(Login.getUserLogin().getUserDtls());
			cus.delete();
			init();
			Application.addMessage(1, "Successfully deleted", "");
		
	}
	
	private String copyPhoto(String photoId){
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String driveImage =  IMAGE_PATH + ReadConfig.value(Bris.BARANGAY_NAME) + Bris.SEPERATOR.getName() + photoId + ".jpg";
        setPhotoId(photoId);
        String contextImageLoc = File.separator + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;
            String pathToSave = externalContext.getRealPath("") + contextImageLoc;
            File file = new File(driveImage);
            try{
    			Files.copy(file.toPath(), (new File(pathToSave + file.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    			}catch(IOException e){}
            return driveImage;
	}
	
	public void clearFlds() {
		setPhotoId("camera");
		setCitizen(new Customer());
		setEmergencyContactPerson(null);
		setRelationshipId(0);
		setPurSelected(null);
		setBirthdate(null);
		setEmergencyContactPersonName(null);
		loadDefaultAddress();
	}
	
	public void oncapture(CaptureEvent captureEvent) {
        
		photoId = getRandomImageName() + DateUtils.getCurrentDateMMDDYYYYTIMEPlain();
		shots.add(photoId);
    	
        System.out.println("Set picture name " + photoId);
        byte[] data = captureEvent.getData();
 
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        
        String driveImage =  IMAGE_PATH + ReadConfig.value(Bris.BARANGAY_NAME) + Bris.SEPERATOR.getName() + photoId + ".jpg";
        String contextImageLoc = File.separator + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;
        
        try{
        
        }catch(Exception e){}
        
        FileImageOutputStream imageOutput;
        try {
            imageOutput = new FileImageOutputStream(new File(driveImage));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();    
            
            
            String pathToSave = externalContext.getRealPath("") + contextImageLoc;
            File file = new File(driveImage);
            try{
    			Files.copy(file.toPath(), (new File(pathToSave + file.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    			}catch(IOException e){}
        }
        catch(IOException e) {
            throw new FacesException("Error in writing captured image.", e);
        }
    }
	
	public void selectedPhoto(String photoId){
		setPhotoId(photoId);
	}
	
	public void deleteTmpImages(){
		
		if(getShots()!=null && getShots().size()>0){
			deletingImages();
		}
	}
	
	/**
	 * Deleting old picture/s for the selected customer
	 */
	private void deletingImages(){
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        
        String deleteImg = externalContext.getRealPath("") + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;// + photoId + ".jpg";
        //removing if existing
        try{
        
       getShots().remove(getPhotoId());	
       for(String name : shots){ 	
        	if(!getPhotoId().equalsIgnoreCase(name)){
	        File img = new File(IMAGE_PATH + ReadConfig.value(Bris.BARANGAY_NAME) + Bris.SEPERATOR.getName() + name + ".jpg");
	        img.delete();
	       
	        img = new File(deleteImg + name + ".jpg");
	        img.delete();
        	}
       	}
        }catch(Exception e){}
	}
	
	public void fileUploadListener(FileUploadEvent event) {

        try {
            BufferedImage image = ImageIO
                    .read(event.getFile().getInputStream());
            if (image != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", outputStream);
                byte[] bytes = outputStream.toByteArray();
	            System.out.println("Bytes -> " + bytes.length);
	            writeImageToFile(image);
            } else {
                throw new IOException("FAILED TO CONVERT PICTURE");
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
	
	private void writeImageToFile(BufferedImage image){
		try{
			photoId = getRandomImageName() + DateUtils.getCurrentDateMMDDYYYYTIMEPlain();
			shots.add(photoId);
			File fileImg = new File(IMAGE_PATH + ReadConfig.value(Bris.BARANGAY_NAME) + Bris.SEPERATOR.getName() + photoId + ".jpg");
			
			if(image==null){
				fileImg = new File(IMAGE_PATH + ReadConfig.value(Bris.BARANGAY_NAME) + Bris.SEPERATOR.getName() + "noimageproduct.jpg");
				 image = ImageIO.read(fileImg);
			}
			
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			String contextImageLoc = "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;
			String pathToSave = externalContext.getRealPath("") + contextImageLoc;
           // File file = new File(driveImage);
            try{
            	ImageIO.write(image, "jpg", fileImg);
    			Files.copy(fileImg.toPath(), (new File(pathToSave + fileImg.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    			System.out.println("writing images....." + pathToSave);
    			}catch(IOException e){}
		
		}catch(IOException e){}
	}
	
	public void calculateAge(){
		String dateBirth = DateUtils.convertDate(getBirthdate(),"yyyy-MM-dd");
		getCitizen().setAge(Integer.valueOf(Math.round(DateUtils.calculateAgeNow(dateBirth))+""));
	}
	
	private String getRandomImageName() {
        int i = (int) (Math.random() * 10000000);
         
        return String.valueOf(i);
    }
	
	private void loadDefaultAddress(){
		try{
		String sql = " AND prv.provisactive=1 AND prv.provname=?";
		String[] params = new String[1];
		params[0] = PROVINCE;
		setProvSelected(Province.retrieve(sql, params).get(0));
		
		params = new String[2];
		sql = " AND mun.munisactive=1 AND prv.provid=? AND mun.munname=?";
		params[0] = getProvSelected().getId()+"";
		params[1] = MUNICIPALITY;
		setMunSelected(Municipality.retrieve(sql, params).get(0));
		
		params = new String[3];
		sql = " AND bgy.bgisactive=1 AND prv.provid=? AND mun.munid=? AND bgy.bgname=?";
		params[0] = getProvSelected().getId()+"";
		params[1] = getMunSelected().getId()+"";
		params[2] = BARANGAY;
		setBarSelected(Barangay.retrieve(sql, params).get(0));
		
		}catch(Exception e){}
	}
	
	public List<String> autoFirst(String query){
		int size = query.length();
		if(size>=2){
			return Customer.retrieve(query, "cusfirstname"," limit 5");
		}else{
			return new ArrayList<String>();//Collections.synchronizedList(new ArrayList<String>());
		}
	}
	public List<String> autoMiddle(String query){
		int size = query.length();
		if(size>=2){
			return Customer.retrieve(query, "cusmiddlename"," limit 5");
		}else{
			return new ArrayList<String>();//Collections.synchronizedList(new ArrayList<String>());
		}
	}
	public List<String> autoLast(String query){
		int size = query.length();
		if(size>=2){	
			return Customer.retrieve(query, "cuslastname"," limit 5");
		}else{
			return new ArrayList<String>();//Collections.synchronizedList(new ArrayList<String>());
		}	
	}
	
	public List<Customer> loadContactPerson(String value){
		try {
		String sql = " AND cus.cusisactive=1 ";
		
		if(value!=null && !value.isEmpty()){
			sql += " AND cus.fullname like '%" + value +"%'";
		}else{
			sql += " order by cus.customerid DESC limit 100;";
		}
		
		return Customer.retrieve(sql, new String[0]);
		}catch(Exception e) {}
		return new ArrayList<>();
	}
	
	public List<Province> loadProvince(String value){
		try {
		String sql = " AND prv.provisactive=1 ";
		String[] params = new String[0];
		if(value!=null && !value.isEmpty()){
			sql += " AND prv.provname like '%" + value +"%'";
		}else{
			sql += " ORDER BY prv.provname";
		}
		
		return Province.retrieve(sql, params);
		}catch(Exception e) {}
		return new ArrayList<>();
	}
	
	public List<Municipality> loadMunicipal(String value){
			try {
			String[] params = new String[1];
			String sql = " AND mun.munisactive=1 AND prv.provid=? ";
			params[0] = getProvSelected().getId()+"";
			
			if(value!=null && !value.isEmpty()){
				sql += " AND mun.munname like '%" + value +"%'";
			}else{
				sql += " ORDER BY mun.munname";
			}
			
		return Municipality.retrieve(sql, params);
			}catch(Exception e) {}
			return new ArrayList<>();
	}
	
	public List<Barangay> loadBarangay(String value){
		try {
		String[] params = new String[2];
		String sql = " AND bgy.bgisactive=1 AND prv.provid=? AND mun.munid=? ";
		params[0] = getProvSelected().getId()+"";
		params[1] = getMunSelected().getId()+"";
		if(value!=null && !value.isEmpty()){
			sql += " AND bgy.bgname like '%" + value +"%'";
		}else{
			sql += " ORDER BY bgy.bgname";
		}
		return Barangay.retrieve(sql, params);
		}catch(Exception e) {}
		return new ArrayList<>();
	}
	
	public List<Purok> loadPurok(String value){
		try {
		String[] params = new String[2];
		String sql = " AND pur.isactivepurok=1 AND mun.munid=? AND bgy.bgid=? ";
		params[0] = getMunSelected().getId()+"";
		params[1] = getBarSelected().getId()+"";
		
		if(value!=null && !value.isEmpty()){
			sql += " AND pur.purokname like '%" + value +"%'";
		}else{
			sql += " ORDER BY pur.purokname";
		}
		
		return Purok.retrieve(sql, params);
		}catch(Exception e) {}
		return new ArrayList<>();
	}

	public Customer getCitizen() {
		return citizen;
	}

	public void setCitizen(Customer citizen) {
		this.citizen = citizen;
	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	public String getPhotoId() {
		if(photoId==null){
			photoId="camera";
		}
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public List<String> getShots() {
		return shots;
	}

	public void setShots(List<String> shots) {
		this.shots = shots;
	}
	
	public Date getBirthdate() {
		if(birthdate==null){
			birthdate = DateUtils.getDateToday();
		}
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}
	
	public List getCivils() {
		civils = new ArrayList<>();
		
		for(CivilStatus cv : CivilStatus.values()){
			civils.add(new SelectItem(cv.getId(), cv.getName()));
		}
		
		return civils;
	}

	public void setCivils(List civils) {
		this.civils = civils;
	}
	
public List getGenderList() {
		
		genderList = Collections.synchronizedList(new ArrayList<>());
		genderList.add(new SelectItem("1","Male"));
		genderList.add(new SelectItem("2","Female"));
		
		return genderList;
	}

	public void setGenderList(List genderList) {
		this.genderList = genderList;
	}

	public Customer getEmergencyContactPerson() {
		return emergencyContactPerson;
	}

	public void setEmergencyContactPerson(Customer emergencyContactPerson) {
		this.emergencyContactPerson = emergencyContactPerson;
	}

	public int getRelationshipId() {
		return relationshipId;
	}

	public void setRelationshipId(int relationshipId) {
		this.relationshipId = relationshipId;
	}

	public List getRelationships() {
		relationships = new ArrayList<>();
		for(Relationships rel : Relationships.values()){
			relationships.add(new SelectItem(rel.getId(), rel.getName()));
		}
		return relationships;
	}

	public void setRelationships(List relationships) {
		this.relationships = relationships;
	}
	
	public String getConfirmDetails() {
		return confirmDetails;
	}

	public void setConfirmDetails(String confirmDetails) {
		this.confirmDetails = confirmDetails;
	}

	public Province getProvSelected() {
		return provSelected;
	}

	public void setProvSelected(Province provSelected) {
		this.provSelected = provSelected;
	}

	public Municipality getMunSelected() {
		return munSelected;
	}

	public void setMunSelected(Municipality munSelected) {
		this.munSelected = munSelected;
	}

	public Barangay getBarSelected() {
		return barSelected;
	}

	public void setBarSelected(Barangay barSelected) {
		this.barSelected = barSelected;
	}

	public Purok getPurSelected() {
		return purSelected;
	}

	public void setPurSelected(Purok purSelected) {
		this.purSelected = purSelected;
	}

	public boolean isHideUnhideWizard() {
		return hideUnhideWizard;
	}

	public void setHideUnhideWizard(boolean hideUnhideWizard) {
		this.hideUnhideWizard = hideUnhideWizard;
	}

	public String getSearchCustomer() {
		return searchCustomer;
	}

	public void setSearchCustomer(String searchCustomer) {
		this.searchCustomer = searchCustomer;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	public boolean isRequiredEmergencyPerson() {
		return requiredEmergencyPerson;
	}

	public void setRequiredEmergencyPerson(boolean requiredEmergencyPerson) {
		this.requiredEmergencyPerson = requiredEmergencyPerson;
	}
	
	public void clickContact(Customer person){
		setEmergencyContactPerson(person);
		setEmergencyContactPersonName(person.getFullname());
	}
	
	public void loadContactPerson(){
		contactPersons = new ArrayList<Customer>();//Collections.synchronizedList(new ArrayList<Customer>());
		String sql = " AND cus.cusisactive=1 ";
		
		if(getSearchEmergencyPerson()!=null && !getSearchEmergencyPerson().isEmpty()){
			int size = getSearchEmergencyPerson().length();
			if(size>=5){
				sql += " AND cus.fullname like '%" + getSearchEmergencyPerson() +"%'";
				contactPersons = Customer.retrieve(sql, new String[0]);
			}
		}else{
			sql += " order by cus.customerid DESC limit 10;";
			contactPersons = Customer.retrieve(sql, new String[0]);
		}
		
		
	}
	
	public void removeEmergencyPerson(){
		setEmergencyContactPerson(null);
		setEmergencyContactPersonName(null);
		setRelationshipId(0);
		Application.addMessage(1, "Successfully removed.", "");
	}
	
	public void loadProvince(){
		provinces = new ArrayList<Province>();//Collections.synchronizedList(new ArrayList<Province>());
		try{
		String sql = " AND prv.provisactive=1";
		String[] params = new String[0];
		if(getSearchProvince()!=null){
			sql += " AND prv.provname like '%"+ getSearchProvince().replace("--", "") +"%'";
		}
		provinces = Province.retrieve(sql, params);
		Collections.reverse(provinces);
		}catch(Exception e){}
	}
	
	public void loadMunicipal(){
		municipals = new ArrayList<Municipality>();//Collections.synchronizedList(new ArrayList<Municipality>());
		try{
			String[] params = new String[1];
			String sql = " AND mun.munisactive=1 AND prv.provid=?";
			params[0] = getProvSelected().getId()+"";
			if(getSearchMunicipal()!=null){
				sql += " AND mun.munname like '%"+ getSearchMunicipal().replace("--", "") +"%'";
			}
			municipals = Municipality.retrieve(sql, params);
			Collections.reverse(municipals);
		}catch(Exception e){}
	}
	
	public void loadBarangay(){
		barangays = new ArrayList<Barangay>();//Collections.synchronizedList(new ArrayList<Barangay>());
		try{
		String[] params = new String[2];
		String sql = " AND bgy.bgisactive=1 AND prv.provid=? AND mun.munid=?";
		params[0] = getProvSelected().getId()+"";
		params[1] = getMunSelected().getId()+"";
		if(getSearchBarangay()!=null){
			sql += " AND bgy.bgname like '%"+ getSearchBarangay().replace("--", "") +"%'";
		}
		barangays = Barangay.retrieve(sql, params);
		Collections.reverse(barangays);
		}catch(Exception e){}
	}
	
	public void loadPurok(){
		puroks = new ArrayList<Purok>();//Collections.synchronizedList(new ArrayList<Purok>());
		try{
		String[] params = new String[2];
		String sql = " AND pur.isactivepurok=1 AND mun.munid=? AND bgy.bgid=? ";
		params[0] = getMunSelected().getId()+"";
		params[1] = getBarSelected().getId()+"";
		if(getSearchPurok()!=null){
			sql += " AND pur.purokname like '%"+ getSearchPurok().replace("--", "") +"%'";
		}
		puroks = Purok.retrieve(sql, params);
		Collections.reverse(puroks);
		}catch(Exception e){}
	}
	
	public void clickItemPopup(Object obj){
		if(obj instanceof Province){
			Province prov = (Province)obj;
			setProvSelected(prov);
			setMunSelected(null);
			setBarSelected(null);
			setPurSelected(null);
		}else if(obj instanceof Municipality){
			Municipality mun = (Municipality)obj;
			setMunSelected(mun);
			setBarSelected(null);
			setPurSelected(null);
		}else if(obj instanceof Barangay){
			Barangay bar = (Barangay)obj;
			setBarSelected(bar);
			setPurSelected(null);
		}else if(obj instanceof Purok){
			Purok pur = (Purok)obj;
			setPurSelected(pur);
		}
		
	}
	
	public List<Customer> getContactPersons() {
		return contactPersons;
	}

	public void setContactPersons(List<Customer> contactPersons) {
		this.contactPersons = contactPersons;
	}

	public String getEmergencyContactPersonName() {
		if(emergencyContactPersonName==null) {
			emergencyContactPersonName = "";
		}
		return emergencyContactPersonName;
	}

	public String getSearchEmergencyPerson() {
		return searchEmergencyPerson;
	}

	public void setEmergencyContactPersonName(String emergencyContactPersonName) {
		this.emergencyContactPersonName = emergencyContactPersonName;
	}

	public void setSearchEmergencyPerson(String searchEmergencyPerson) {
		this.searchEmergencyPerson = searchEmergencyPerson;
	}

	public String getSearchProvince() {
		return searchProvince;
	}

	public String getSearchMunicipal() {
		return searchMunicipal;
	}

	public String getSearchBarangay() {
		return searchBarangay;
	}

	public String getSearchPurok() {
		return searchPurok;
	}

	public List<Province> getProvinces() {
		return provinces;
	}

	public List<Municipality> getMunicipals() {
		return municipals;
	}

	public List<Barangay> getBarangays() {
		return barangays;
	}

	public List<Purok> getPuroks() {
		return puroks;
	}

	public void setSearchProvince(String searchProvince) {
		this.searchProvince = searchProvince;
	}

	public void setSearchMunicipal(String searchMunicipal) {
		this.searchMunicipal = searchMunicipal;
	}

	public void setSearchBarangay(String searchBarangay) {
		this.searchBarangay = searchBarangay;
	}

	public void setSearchPurok(String searchPurok) {
		this.searchPurok = searchPurok;
	}

	public void setProvinces(List<Province> provinces) {
		this.provinces = provinces;
	}

	public void setMunicipals(List<Municipality> municipals) {
		this.municipals = municipals;
	}

	public void setBarangays(List<Barangay> barangays) {
		this.barangays = barangays;
	}

	public void setPuroks(List<Purok> puroks) {
		this.puroks = puroks;
	}
}

package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.AccessAllowed;
import com.italia.marxmind.bris.controller.Department;
import com.italia.marxmind.bris.controller.Features;
import com.italia.marxmind.bris.controller.Job;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.UserAccessLevel;
import com.italia.marxmind.bris.controller.UserDtls;
import com.italia.marxmind.bris.enm.UserAccess;
import com.italia.marxmind.bris.security.SecureChar;
import com.italia.marxmind.bris.utils.DateUtils;
import com.italia.marxmind.bris.utils.LogU;
/**
 * 
 * @author mark italia
 * @since 09/29/2016
 *@version 1.0
 */
@ManagedBean(name="auserBean", eager=true)
@ViewScoped
public class AdminUserBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1094801425228386363L;
	private List<UserDtls> users = Collections.synchronizedList(new ArrayList<UserDtls>());
	private UserDtls userdtls;
	
	private String regdate;
	private String firstname;
	private String middlename; 
	private String lastname;
	private String address;
	private String contactno;;
	private String age = "0";
	
	private String username;;
	private String password;
	private String accesslevedid;
	private List accesslevellist = Collections.synchronizedList(new ArrayList<>());
	
	private Job job;
	private Department department;
	private List ageList = Collections.synchronizedList(new ArrayList<>());
	private List jobList = Collections.synchronizedList(new ArrayList<>());
	private List departmentList = Collections.synchronizedList(new ArrayList<>());
	private String jobId;
	private String departmentId;
	private String genderId;
	private List genderList = Collections.synchronizedList(new ArrayList<>());
	
	private List<UserDtls> selectedUser;
	
	private List<Features> modules = Collections.synchronizedList(new ArrayList<Features>());
	private Map<Integer, AccessAllowed> allowedModuleMap = Collections.synchronizedMap(new HashMap<Integer, AccessAllowed>());
	
	private String searchName;
	
	private String oldTempPassword;
	private String oldPassword;
	
	public void deactivateUser(){
		
			System.out.println("checking selected user " + getSelectedUser().size());
			if(getSelectedUser()!=null){
				for(UserDtls user : getSelectedUser()){
					LogU.add("Deactivating user " + " id : " + user.getUserdtlsid() + " " + user.getFirstname() + " " + user.getMiddlename() + " " + user.getLastname());
					user.delete(true);
					user.getLogin().delete(true);
				}
				selectedUser = Collections.synchronizedList(new ArrayList<>());
				init();
			}
		
	}
	
	public void changeActivation(Features fet){
		Login in = Login.getUserLogin();
		
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel() || 
				UserAccess.CAPTAIN.getId()==in.getAccessLevel().getLevel() || 
				UserAccess.KAGAWAD.getId()==in.getAccessLevel().getLevel()){
				
				if(getUserdtls()!=null){
					
					AccessAllowed acc = new AccessAllowed();
					if(getAllowedModuleMap()!=null){
						if(getAllowedModuleMap().containsKey(fet.getId())){
							acc = getAllowedModuleMap().get(fet.getId());
							acc.setIsActiveAccess(fet.isChecked()==true? 1 : 0);
							acc.setFeatures(fet);
							acc.setUserDtls(getUserdtls());
							acc = AccessAllowed.save(acc);
							getAllowedModuleMap().put(fet.getId(), acc);
						}else{
							acc.setIsActiveAccess(1);
							acc.setFeatures(fet);
							acc.setUserDtls(getUserdtls());
							acc = AccessAllowed.save(acc);
							getAllowedModuleMap().put(fet.getId(), acc);
						}
					}else{
						acc.setIsActiveAccess(1);
						acc.setFeatures(fet);
						acc.setUserDtls(getUserdtls());
						acc = AccessAllowed.save(acc);
						getAllowedModuleMap().put(fet.getId(), acc);
					}
					
					Application.addMessage(1, fet.getModuleName() + " has been " + (fet.isChecked()==true? "Activated." : "Deactivated."), "");
				
				}else{
					Application.addMessage(2, "Error: No user has been selected", "");
				}
			
		}else{
			Application.addMessage(3, "Restricted to unauthorized person", "");
			modules = Collections.synchronizedList(new ArrayList<Features>());
			modules = Features.retrieve("SELECT * FROM features WHERE isActive=1", new String[0]);
		}
		
	}
	
	@PostConstruct
	public void init(){
		//if(Login.checkUserStatus()){
			
			Login in = Login.getUserLogin();
		
			System.out.println("initialize AdminUserBean");
			users = Collections.synchronizedList(new ArrayList<UserDtls>());
			//users = UserDtls.retrieve("SELECT * FROM userdtls WHERE isactive=1", new String[0]);
			
			UserDtls user = new UserDtls();
			user.setIsActive(1);
			
			if(getSearchName()!=null && !getSearchName().isEmpty()){
				user.setFirstname(getSearchName());
			}
			
			if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
				users = UserDtls.retrieve(user);
			}else{
				for(UserDtls us : UserDtls.retrieve(user)){
					if(UserAccess.DEVELOPER.getId()!=us.getLogin().getAccessLevel().getLevel()){
						users.add(us);
					}
				}
			}
			//users = UserDtls.retrieve(user);
			
			if(users!=null && users.size()==1){
				clickItem(users.get(0));
			}else{
				clearFields();
				Collections.reverse(users);
			}
		//}
	}
	
	public void print(){
		System.out.println("Print");
	}
	
	public void printAll(){
		System.out.println("Print All");
	}
	
	public void close(){
		System.out.println("close");
		clearFields();
	}
	
	public void save(){
		System.out.println("Save");
		
		UserDtls user = new UserDtls();
		Login in = new Login();
		
		boolean isOk = true;
		
		if(getFirstname()==null || getFirstname().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide first name");
		}
		
		if(getMiddlename()==null || getMiddlename().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide middle name");
		}
		
		if(getLastname()==null || getLastname().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide last name");
		}
		
		if(getAddress()==null || getAddress().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide address");
		}
		
		if(getGenderId()==null){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide gender");
		}
		
		if(getUsername()==null || getUsername().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide username");
		}
		
		
		
		if(getJobId()==null){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide job");
		}
		
		System.out.println("password: " + getPassword());
		
		if(getOldTempPassword()!=null) {
			if(getOldPassword()==null || getOldPassword().isEmpty()) {
				isOk = false;
				Application.addMessage(3, "Error", "Please provide your old password");
			}else {
					if(!getOldPassword().equalsIgnoreCase(getOldTempPassword())) {
						isOk = false;
						Application.addMessage(3, "Error", "Old Password is incorrect");
					}else {
						if(getPassword()!=null && !getPassword().isEmpty() && !getPassword().equalsIgnoreCase(getOldTempPassword())) {
							int len = getPassword().length();
							if(len<=4){
								isOk = false;
								Application.addMessage(3, "Error", "Please provide password minimum of 5 characters");
							}
						}else {
							setPassword(getOldTempPassword());
						}
					}
			}
		}else {
			if(getPassword()==null || getPassword().isEmpty()){
				isOk = false;
				Application.addMessage(3, "Error", "Please provide password");
			}else {
				int len = getPassword().length();
				if(len<=4){
					isOk = false;
					Application.addMessage(3, "Error", "Please provide password minimum of 5 characters");
				}
			}
		}
		
		
		
		/*Login loginUser = Login.getUserLogin();
		
		if(UserAccess.CAPTAIN.getId()!=loginUser.getAccessLevel().getLevel() || 
				UserAccess.SECRETRARY.getId()!=loginUser.getAccessLevel().getLevel() || 
				UserAccess.DEVELOPER.getId()!=loginUser.getAccessLevel().getLevel()) {
			isOk = false;
			Application.addMessage(3, "Error", "You have no authorized to add/modify the information. Please ask your head to add or modify user information");
		}*/
		
		if(isOk){
		String pass = getPassword();
		pass = SecureChar.encodePassword(pass);
		if(getUserdtls()!=null){
			user = getUserdtls();
			in = user.getLogin();
			in.setUsername(getUsername());
			in.setPassword(pass);
			in.setAccessLevel(UserAccessLevel.userAccessLevel(getAccesslevedid()));
			in.save();
			
			System.out.println("addedby " + user.getUserDtls().getFirstname());
			
		}else{
			user.setRegdate(getRegdate());
			user.setIsActive(1);
			user.setUserDtls(Login.getUserLogin().getUserDtls());
			
			in.setUsername(getUsername());
			in.setPassword(pass);
			in.setIsOnline(0);
			in.setAccessLevel(UserAccessLevel.userAccessLevel(getAccesslevedid()));
			
			long id = Login.getInfo(in.getLogid()==null? Login.getLatestId()+1 : in.getLogid());
			UserDtls userD = new UserDtls();
			userD.setUserdtlsid(Login.getLatestId()+1);
			in.setUserDtls(userD);
			if(id==1){
				in = Login.insertData(in, "1");
			}else if(id==3){
				in = Login.insertData(in, "3");
			}
			
		}
		user.setLogin(in);
		user.setFirstname(getFirstname());
		user.setMiddlename(getMiddlename());
		user.setLastname(getLastname());
		user.setAddress(getAddress());
		user.setContactno(getContactno());
		user.setAge(Integer.valueOf(getAge()));
		user.setGender(Integer.valueOf(getGenderId()));
		user.setJob(Job.job(getJobId()));
		user.setDepartment(Department.department(getDepartmentId()));
		user = UserDtls.save(user);
		
		init();
		setUserdtls(user);
		clickItem(getUserdtls());
		//clearFields();
		Application.addMessage(1, "Success", "Information has been saved");
		}
		
	}
	
	public void clearFields(){
		setOldPassword(null);
		setOldTempPassword(null);
		setUserdtls(null);
		setRegdate(null);
		setFirstname(null);
		setMiddlename(null);
		setLastname(null);
		setAddress(null);
		setContactno(null);
		setAge(null);
		setGenderId(null);
		setJobId(null);
		setDepartmentId(null);
		
		setUsername(null);
		setPassword(null);
		setAccesslevedid(null);
		modules = Collections.synchronizedList(new ArrayList<Features>());
		allowedModuleMap = Collections.synchronizedMap(new HashMap<Integer, AccessAllowed>());
	}
	
	public void clickItem(UserDtls userDtls){
		allowedModuleMap = Collections.synchronizedMap(new HashMap<Integer, AccessAllowed>());
		modules = Collections.synchronizedList(new ArrayList<Features>());
		
		System.out.println("clickItem");
		setUserdtls(userDtls);
		setRegdate(userDtls.getRegdate());
		setFirstname(userDtls.getFirstname());
		setMiddlename(userDtls.getMiddlename());
		setLastname(userDtls.getLastname());
		setAddress(userDtls.getAddress());
		setContactno(userDtls.getContactno());
		setAge(userDtls.getAge()+"");
		setGenderId(userDtls.getGender()+"");
		setJobId(userDtls.getJob().getJobid()+"");
		setDepartmentId(userDtls.getDepartment().getDepid()+"");
		
		Login in = userDtls.getLogin();
		setUsername(in.getUsername());
		Login user = Login.getUserLogin();
		
		if(UserAccess.CAPTAIN.getId()==user.getAccessLevel().getLevel() || 
				UserAccess.SECRETRARY.getId()==user.getAccessLevel().getLevel() || 
				UserAccess.DEVELOPER.getId()==user.getAccessLevel().getLevel()) {
			setPassword(SecureChar.decodePassword(in.getPassword()));
			
			setOldTempPassword(SecureChar.decodePassword(in.getPassword()));
		}else {
			if(in.getLogid()==user.getLogid()) {
				setPassword(SecureChar.decodePassword(in.getPassword()));
				setOldTempPassword(SecureChar.decodePassword(in.getPassword()));
			}else {
				setPassword("********");
				setOldTempPassword(SecureChar.decodePassword(in.getPassword()));
			}
		}
		
		setAccesslevedid(in.getAccessLevel().getUseraccesslevelid()+"");
		
		allowedModuleMap = Collections.synchronizedMap(new HashMap<Integer, AccessAllowed>());
		for(AccessAllowed acc : AccessAllowed.retrieve(" AND usr.userdtlsid="+userDtls.getUserdtlsid(), new String[0])){
			allowedModuleMap.put(acc.getFeatures().getId(), acc);
		}
		for(Features fet : Features.retrieve("SELECT * FROM features WHERE isActive=1", new String[0])){
			int id = fet.getId();
			if(getAllowedModuleMap()!=null){
				if(getAllowedModuleMap().containsKey(id)){
					fet.setChecked(getAllowedModuleMap().get(id).getIsActiveAccess()==1? true : false);
				}else{
					fet.setChecked(false);
				}
			}else{
				fet.setChecked(false);
			}
			modules.add(fet);
		}
		
	}
	
	public void deleteRow(UserDtls userDtls){
			LogU.add("Deactivating user " + " id : " + userDtls.getUserdtlsid() + " " + userDtls.getFirstname() + " " + userDtls.getMiddlename() + " " + userDtls.getLastname());
			userDtls.delete(true);
			userDtls.getLogin().delete(true);
			init();
	}

	public List<UserDtls> getUsers() {
		return users;
	}

	public void setUsers(List<UserDtls> users) {
		this.users = users;
	}

	public UserDtls getUserdtls() {
		return userdtls;
	}

	public void setUserdtls(UserDtls userdtls) {
		this.userdtls = userdtls;
	}
	

	public String getFirstname() {
		return firstname;
	}

	public String getRegdate() {
		if(regdate==null)
			regdate = DateUtils.getCurrentDateYYYYMMDD();
		return regdate;
	}

	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getMiddlename() {
		return middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactno() {
		return contactno;
	}

	public void setContactno(String contactno) {
		this.contactno = contactno;
	}

	public Job getJob() {
		return job;
	}

	public String getAge() {
		return age;
	}

	public List getAgeList() {
		ageList = Collections.synchronizedList(new ArrayList<>());
		
		for(int i=1; i<=100;i++){
			ageList.add(new SelectItem(i+"",i+""));
		}
		
		return ageList;
	}

	public void setAgeList(List ageList) {
		this.ageList = ageList;
	}

	public List getJobList() {
		jobList = Collections.synchronizedList(new ArrayList<>());
		String sql = "SELECT * FROM jobtitle";
		
		Login in = Login.getUserLogin();
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			sql = "SELECT * FROM jobtitle";
		}else{
			sql = "SELECT * FROM jobtitle WHERE jobtitleid!="+UserAccess.DEVELOPER.getId();
		}
		
		for(Job job : Job.retrieve(sql, new String[0])){
			jobList.add(new SelectItem(job.getJobid()+"",job.getJobname()));
		}
		
		return jobList;
	}

	public String getJobId() {
		return jobId;
	}

	public String getGenderId() {
		return genderId;
	}

	public void setGenderId(String genderId) {
		this.genderId = genderId;
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

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
 
	public void setJobList(List jobList) {
		this.jobList = jobList;
	} 

	public List getDepartmentList() {
		departmentList = Collections.synchronizedList(new ArrayList<>());
		String sql = "SELECT * FROM department";
		for(Department dep : Department.retrieve(sql, new String[0])){
			departmentList.add(new SelectItem(dep.getDepid()+"",dep.getDepartmentName()));
		}
		return departmentList;
	}

	public void setDepartmentList(List departmentList) {
		this.departmentList = departmentList;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAccesslevedid() {
		return accesslevedid;
	}

	public void setAccesslevedid(String accesslevedid) {
		this.accesslevedid = accesslevedid;
	}

	public List getAccesslevellist() {
		/*accesslevellist = Collections.synchronizedList(new ArrayList<>());
		String sql = "SELECT * FROM useraccesslevel";
		for(UserAccessLevel lvl : UserAccessLevel.retrieve(sql, new String[0])){
			accesslevellist.add(new SelectItem(lvl.getUseraccesslevelid()+"",lvl.getName()));
		}
		
		return accesslevellist;*/
		
		accesslevellist = Collections.synchronizedList(new ArrayList<>());
		
		String sql = "SELECT * FROM useraccesslevel";
		
		Login in = Login.getUserLogin();
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			sql = "SELECT * FROM useraccesslevel";
		}else{
			sql = "SELECT * FROM useraccesslevel WHERE level!="+UserAccess.DEVELOPER.getId();
		}
		
		for(UserAccessLevel lvl : UserAccessLevel.retrieve(sql, new String[0])){
				accesslevellist.add(new SelectItem(lvl.getUseraccesslevelid()+"",lvl.getName()));
		}
		
		return accesslevellist;
		
	}

	public void setAccesslevellist(List accesslevellist) {
		this.accesslevellist = accesslevellist;
	}

	public List<UserDtls> getSelectedUser() {
		if(selectedUser!=null){
			System.out.println("User selected " +selectedUser.size());
		}
		return selectedUser;
	}

	public void setSelectedUser(List<UserDtls> selectedUser) {
		this.selectedUser = selectedUser;
	}

	public List<Features> getModules() {
		return modules;
	}

	public void setModules(List<Features> modules) {
		this.modules = modules;
	}

	public Map<Integer, AccessAllowed> getAllowedModuleMap() {
		return allowedModuleMap;
	}

	public void setAllowedModuleMap(Map<Integer, AccessAllowed> allowedModuleMap) {
		this.allowedModuleMap = allowedModuleMap;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public String getOldTempPassword() {
		return oldTempPassword;
	}

	public void setOldTempPassword(String oldTempPassword) {
		this.oldTempPassword = oldTempPassword;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	
}

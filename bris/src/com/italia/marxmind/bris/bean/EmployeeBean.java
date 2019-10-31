package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.Barangay;
import com.italia.marxmind.bris.controller.Committee;
import com.italia.marxmind.bris.controller.Employee;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.Municipality;
import com.italia.marxmind.bris.controller.Position;
import com.italia.marxmind.bris.controller.Province;
import com.italia.marxmind.bris.controller.Purok;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.DateFormat;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @version 1.0
 * @since 07/06/2017
 *
 */
@ManagedBean(name="empBean", eager=true)
@ViewScoped
public class EmployeeBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 16867694353L;
	
	private Date dateRegistered;
	private Date dateResigned;
	private String firstName;
	private String middleName;
	private String lastName;
	private int age;
	
	private String contactNo;
	private String purok;
	
	private boolean official;
	private boolean resigned;
	
	private List genders;
	private int genderId;
	
	private Employee employeeSelected;
	private List<Employee> employees = Collections.synchronizedList(new ArrayList<Employee>());
	
	private List positions;
	private int positionId;
	
	private List committees;
	private int committeeId;
	
	/*private List barangays;
	private int barangayId;
	
	private List municipalitys;
	private int municipalityId;
	
	private List provinces;
	private int provinceId;*/
	
	private String searchName;
	
	
	private Map<Integer, Position> positionsData = Collections.synchronizedMap(new HashMap<Integer, Position>());
	private Map<Integer, Committee> commiteeData = Collections.synchronizedMap(new HashMap<Integer, Committee>());
	private Map<Integer, Barangay> bgyData = Collections.synchronizedMap(new HashMap<Integer, Barangay>());
	private Map<Integer, Municipality> munData = Collections.synchronizedMap(new HashMap<Integer, Municipality>());
	private Map<Integer, Province> provData = Collections.synchronizedMap(new HashMap<Integer, Province>());
	
	private String searchProvince;
	private String searchMunicipal;
	private String searchBarangay;
	
	private List<Province> provinces = Collections.synchronizedList(new ArrayList<Province>());
	private List<Municipality> municipals = Collections.synchronizedList(new ArrayList<Municipality>());
	private List<Barangay> barangays = Collections.synchronizedList(new ArrayList<Barangay>());
	
	private Province provinceSelected;
	private Municipality municipalSelected;
	private Barangay barangaySelected;
	
	private String BARANGAY = ReadConfig.value(Bris.BARANGAY);
	private String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
	private String PROVINCE = ReadConfig.value(Bris.PROVINCE);
	
	@PostConstruct
	public void init(){
		employees = Collections.synchronizedList(new ArrayList<Employee>());
		
		String sql = "";
		String[] params = new String[0];
		
		if(getSearchName()!=null && !getSearchName().isEmpty()){
			sql = " AND emp.isactiveemp=1 AND (emp.firstname like '%" + getSearchName() +"%' OR emp.middlename like '%" + getSearchName() + "%' OR emp.lastname like '%" + getSearchName() +"%')";
		}else{
			sql = " AND emp.isactiveemp=1 ORDER BY emp.empid DESC limit 10";
		}
		
		employees = Employee.retrieve(sql, params);
		
		if(employees!=null && employees.size()==1){
			clickItem(employees.get(0));
		}else{
			clearFields();
		}
		
	}
	
	private void loadDefaultAddress(){
		try{
		String sql = " AND prv.provisactive=1 AND prv.provname=?";
		String[] params = new String[1];
		params[0] = PROVINCE;
		provinces = Province.retrieve(sql, params);
		setProvinceSelected(provinces.get(0));
		
		params = new String[2];
		sql = " AND mun.munisactive=1 AND prv.provid=? AND mun.munname=?";
		params[0] = getProvinceSelected().getId()+"";
		params[1] = MUNICIPALITY;
		municipals = Municipality.retrieve(sql, params);
		setMunicipalSelected(municipals.get(0));
		
		params = new String[3];
		sql = " AND bgy.bgisactive=1 AND prv.provid=? AND mun.munid=? AND bgy.bgname=?";
		params[0] = getProvinceSelected().getId()+"";
		params[1] = getMunicipalSelected().getId()+"";
		params[2] = BARANGAY;
		barangays = Barangay.retrieve(sql, params);
		setBarangaySelected(barangays.get(0));
		}catch(Exception e){}
	}
	
	public List<String> autoPurokName(String query){
		
		
		List<String> result = new ArrayList<>();
		
		String[] params = new String[2];
		String sql = " AND pur.isactivepurok=1 AND mun.munid=? AND bgy.bgid=? ";
		params[0] = getMunicipalSelected().getId()+"";
		params[1] = getBarangaySelected().getId()+"";
		if(query!=null && !query.isEmpty()){
			sql += " AND pur.purokname like '%"+ query.replace("--", "") +"%'";
		}
		for(Purok p : Purok.retrieve(sql, params)){
			result.add(p.getPurokName());
		}
		
		return result;
	}
	
	public void saveData(){
		Employee em = new Employee();
		
		boolean isOk = true;
		
		if(getPositionId()==0){
			Application.addMessage(3, "Please provide position.", "");
			isOk= false;
		}
		
		if(getAge()==0){
			Application.addMessage(3, "Please provide age.", "");
			isOk= false;
		}
		
		if(getFirstName()==null || getFirstName().isEmpty()){
			Application.addMessage(3, "Please provide First name.", "");
			isOk= false;
		}
		
		if(getMiddleName()==null || getMiddleName().isEmpty()){
			Application.addMessage(3, "Please provide Middle name.", "");
			isOk= false;
		}
		
		if(getLastName()==null || getLastName().isEmpty()){
			Application.addMessage(3, "Please provide Last name.", "");
			isOk= false;
		}
		
		if(getProvinceSelected()==null){
			Application.addMessage(3, "Please provide province.", "");
			isOk= false;
		}
		
		if(getMunicipalSelected()==null){
			Application.addMessage(3, "Please provide municipality.", "");
			isOk= false;
		}
		
		if(getBarangaySelected()==null){
			Application.addMessage(3, "Please provide barangay.", "");
			isOk= false;
		}
		
		if(getPurok()==null || getPurok().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Please provide purok or sitio", "");
		}
		
		if(isOk){
			
			if(getEmployeeSelected()!=null){
				em = getEmployeeSelected();
			}
			
			if(isResigned()){
				em.setDateResigned(DateUtils.convertDate(getDateResigned(), DateFormat.YYYY_MM_DD()));
			}else{
				em.setDateResigned(null);
			}
			
			em.setDateRegistered(DateUtils.convertDate(getDateRegistered(), DateFormat.YYYY_MM_DD()));
			em.setFirstName(getFirstName());
			em.setMiddleName(getMiddleName());
			em.setLastName(getLastName());
			em.setAge(getAge());
			em.setContactNo(getContactNo());
			em.setGender(getGenderId());
			em.setPurok(getPurok());
			em.setIsActiveEmployee(1);
			
			em.setIsOfficial(isOfficial()==true? 1 : 0);
			em.setIsResigned(isResigned()==true? 1 : 0);
			
			
			em.setPosition(getPositionsData().get(getPositionId()));
			em.setCommittee(getCommiteeData().get(getCommitteeId()));
			
			/*em.setBarangay(getBgyData().get(getBarangayId()));
			em.setMunicipality(getMunData().get(getMunicipalityId()));
			em.setProvince(getProvData().get(getProvinceId()));*/
			em.setBarangay(getBarangaySelected());
			em.setMunicipality(getMunicipalSelected());
			em.setProvince(getProvinceSelected());
			
			em = Employee.save(em);
			setEmployeeSelected(em);
			//clearFields();
			init();
			Application.addMessage(1, "Successfully saved.", "");
		}
	}
	
	public void clickItem(Employee em){
		setDateRegistered(DateUtils.convertDateString(em.getDateRegistered(),DateFormat.YYYY_MM_DD()));
		setDateResigned(DateUtils.convertDateString(em.getDateResigned(),DateFormat.YYYY_MM_DD()));
		setFirstName(em.getFirstName());
		setMiddleName(em.getMiddleName());
		setLastName(em.getLastName());
		setAge(em.getAge());
		setOfficial(em.getIsOfficial()==1? true : false);
		setResigned(em.getIsResigned()==1? true : false);
		setGenderId(em.getGender());
		setEmployeeSelected(em);
		setPositionId(em.getPosition().getId());
		setCommitteeId(em.getCommittee().getId());
		setContactNo(em.getContactNo());
		setPurok(em.getPurok());
		/*setBarangayId(em.getBarangay().getId());
		setMunicipalityId(em.getMunicipality().getId());
		setProvinceId(em.getProvince().getId());*/
		setProvinceSelected(em.getProvince());
		setMunicipalSelected(em.getMunicipality());
		setBarangaySelected(em.getBarangay());
	}
	
	public void clickItemPopup(Object obj){
		if(obj instanceof Province){
			Province prov = (Province)obj;
			setProvinceSelected(prov);
			setMunicipalSelected(null);
			setBarangaySelected(null);
		}else if(obj instanceof Municipality){
			Municipality mun = (Municipality)obj;
			setMunicipalSelected(mun);
			setBarangaySelected(null);
		}else if(obj instanceof Barangay){
			Barangay bar = (Barangay)obj;
			setBarangaySelected(bar);
		}
		
	}
	
	public void loadProvince(){
		provinces = Collections.synchronizedList(new ArrayList<Province>());
		String sql = " AND prv.provisactive=1";
		String[] params = new String[0];
		if(getSearchProvince()!=null){
			sql += " AND prv.provname like '%"+ getSearchProvince().replace("--", "") +"%'";
		}
		provinces = Province.retrieve(sql, params);
		Collections.reverse(provinces);
	}
	
	public void loadMunicipal(){
		municipals = Collections.synchronizedList(new ArrayList<Municipality>());
		String[] params = new String[1];
		String sql = " AND mun.munisactive=1 AND prv.provid=?";
		params[0] = getProvinceSelected().getId()+"";
		if(getSearchMunicipal()!=null){
			sql += " AND mun.munname like '%"+ getSearchMunicipal().replace("--", "") +"%'";
		}
		municipals = Municipality.retrieve(sql, params);
		Collections.reverse(municipals);
	}
	
	public void loadBarangay(){
		barangays = Collections.synchronizedList(new ArrayList<Barangay>());
		String[] params = new String[2];
		String sql = " AND bgy.bgisactive=1 AND prv.provid=? AND mun.munid=?";
		params[0] = getProvinceSelected().getId()+"";
		params[1] = getMunicipalSelected().getId()+"";
		if(getSearchBarangay()!=null){
			sql += " AND bgy.bgname like '%"+ getSearchBarangay().replace("--", "") +"%'";
		}
		barangays = Barangay.retrieve(sql, params);
		Collections.reverse(barangays);
	}
	
	public void deleteRow(Employee emp){
		if(Login.checkUserStatus()){
			emp.delete();
			clearFields();
			init();
			Application.addMessage(1, "successfully removed employee.","");
		}
	}
	
	
	
	public void clearFields(){
		setDateRegistered(null);
		setDateResigned(null);
		setFirstName(null);
		setMiddleName(null);
		setLastName(null);
		setAge(0);
		setOfficial(false);
		setResigned(false);
		setGenderId(1);
		setEmployeeSelected(null);
		setPositionId(0);
		setCommitteeId(0);
		setSearchName(null);
		setContactNo(null);
		setPurok(null);
		/*setBarangayId(0);
		setMunicipalityId(0);
		setProvinceId(0);*/
		setProvinceSelected(null);
		setMunicipalSelected(null);
		setBarangaySelected(null);
		
		setSearchProvince(null);
		setSearchMunicipal(null);
		setSearchBarangay(null);
	
		loadDefaultAddress();
	}
	
	public Date getDateRegistered() {
		if(dateRegistered==null){
			dateRegistered = DateUtils.getDateToday();
		}
		return dateRegistered;
	}
	public void setDateRegistered(Date dateRegistered) {
		this.dateRegistered = dateRegistered;
	}
	public Date getDateResigned() {
		return dateResigned;
	}
	public void setDateResigned(Date dateResigned) {
		this.dateResigned = dateResigned;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public boolean isOfficial() {
		return official;
	}
	public void setOfficial(boolean official) {
		this.official = official;
	}
	public boolean isResigned() {
		return resigned;
	}
	public void setResigned(boolean resigned) {
		this.resigned = resigned;
	}
	public List getGenders() {
		genders = new ArrayList<>();
		genders.add(new SelectItem(1, "Male"));
		genders.add(new SelectItem(2, "Female"));
		return genders;
	}
	public void setGenders(List genders) {
		this.genders = genders;
	}
	public int getGenderId() {
		return genderId;
	}
	public void setGenderId(int genderId) {
		this.genderId = genderId;
	}
	public Employee getEmployeeSelected() {
		return employeeSelected;
	}
	public void setEmployeeSelected(Employee employeeSelected) {
		this.employeeSelected = employeeSelected;
	}
	public List<Employee> getEmployees() {
		return employees;
	}
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}
	public List getPositions() {
		positions = new ArrayList<>();
		positionsData = Collections.synchronizedMap(new HashMap<Integer, Position>());
		for(Position ps : Position.retrieve("SELECT * FROM empposition WHERE isactivepos=1 ", new String[0])){
			positions.add(new SelectItem(ps.getId(), ps.getName()));
			positionsData.put(ps.getId(), ps);
		}
		
		return positions;
	}
	public void setPositions(List positions) {
		this.positions = positions;
	}
	public int getPositionId() {
		return positionId;
	}
	public void setPositionId(int positionId) {
		this.positionId = positionId;
	}
	public List getCommittees() {
		committees = new ArrayList<>();
		commiteeData = Collections.synchronizedMap(new HashMap<Integer, Committee>());
		for(Committee com : Committee.retrieve("SELECT * FROM committee WHERE isactivecom=1 ", new String[0])){
			committees.add(new SelectItem(com.getId(), com.getName()));
			commiteeData.put(com.getId(), com);
		}
		return committees;
	}
	public void setCommittees(List committees) {
		this.committees = committees;
	}
	public int getCommitteeId() {
		return committeeId;
	}
	public void setCommitteeId(int committeeId) {
		this.committeeId = committeeId;
	}


	public String getSearchName() {
		return searchName;
	}


	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	/*public List getBarangays() {
		
		barangays = new ArrayList<>();
		bgyData = Collections.synchronizedMap(new HashMap<Integer, Barangay>());
		for(Barangay bg : Barangay.retrieve(" AND bgy.bgisactive=1", new String[0])){
			barangays.add(new SelectItem(bg.getId(), bg.getName()));
			bgyData.put(bg.getId(), bg);
		}
		
		return barangays;
	}

	public void setBarangays(List barangays) {
		this.barangays = barangays;
	}

	public int getBarangayId() {
		return barangayId;
	}

	public void setBarangayId(int barangayId) {
		this.barangayId = barangayId;
	}

	public List getMunicipalitys() {
		municipalitys = new ArrayList<>();
		munData = Collections.synchronizedMap(new HashMap<Integer, Municipality>());
		for(Municipality mun : Municipality.retrieve(" AND mun.munisactive=1 ", new String[0])){
			municipalitys.add(new SelectItem(mun.getId(), mun.getName()));
			munData.put(mun.getId(), mun);
		}
		return municipalitys;
	}

	public void setMunicipalitys(List municipalitys) {
		this.municipalitys = municipalitys;
	}

	public int getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(int municipalityId) {
		this.municipalityId = municipalityId;
	}

	public List getProvinces() {
		provinces = new ArrayList<>();
		provData = Collections.synchronizedMap(new HashMap<Integer, Province>());
		for(Province pr : Province.retrieve(" AND prv.provisactive=1 ", new String[0])){
			provinces.add(new SelectItem(pr.getId(), pr.getName()));
			provData.put(pr.getId(), pr);
		}
		return provinces;
	}

	public void setProvinces(List provinces) {
		this.provinces = provinces;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}*/

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getPurok() {
		return purok;
	}

	public void setPurok(String purok) {
		this.purok = purok;
	}

	public Map<Integer, Position> getPositionsData() {
		return positionsData;
	}

	public void setPositionsData(Map<Integer, Position> positionsData) {
		this.positionsData = positionsData;
	}

	public Map<Integer, Committee> getCommiteeData() {
		return commiteeData;
	}

	public void setCommiteeData(Map<Integer, Committee> commiteeData) {
		this.commiteeData = commiteeData;
	}

	public Map<Integer, Barangay> getBgyData() {
		return bgyData;
	}

	public void setBgyData(Map<Integer, Barangay> bgyData) {
		this.bgyData = bgyData;
	}

	public Map<Integer, Municipality> getMunData() {
		return munData;
	}

	public void setMunData(Map<Integer, Municipality> munData) {
		this.munData = munData;
	}

	public Map<Integer, Province> getProvData() {
		return provData;
	}

	public void setProvData(Map<Integer, Province> provData) {
		this.provData = provData;
	}

	public String getSearchProvince() {
		return searchProvince;
	}

	public void setSearchProvince(String searchProvince) {
		this.searchProvince = searchProvince;
	}

	public String getSearchMunicipal() {
		return searchMunicipal;
	}

	public void setSearchMunicipal(String searchMunicipal) {
		this.searchMunicipal = searchMunicipal;
	}

	public String getSearchBarangay() {
		return searchBarangay;
	}

	public void setSearchBarangay(String searchBarangay) {
		this.searchBarangay = searchBarangay;
	}

	public List<Province> getProvinces() {
		return provinces;
	}

	public void setProvinces(List<Province> provinces) {
		this.provinces = provinces;
	}

	public List<Municipality> getMunicipals() {
		return municipals;
	}

	public void setMunicipals(List<Municipality> municipals) {
		this.municipals = municipals;
	}

	public List<Barangay> getBarangays() {
		return barangays;
	}

	public void setBarangays(List<Barangay> barangays) {
		this.barangays = barangays;
	}

	public Province getProvinceSelected() {
		return provinceSelected;
	}

	public void setProvinceSelected(Province provinceSelected) {
		this.provinceSelected = provinceSelected;
	}

	public Municipality getMunicipalSelected() {
		return municipalSelected;
	}

	public void setMunicipalSelected(Municipality municipalSelected) {
		this.municipalSelected = municipalSelected;
	}

	public Barangay getBarangaySelected() {
		return barangaySelected;
	}

	public void setBarangaySelected(Barangay barangaySelected) {
		this.barangaySelected = barangaySelected;
	}

}

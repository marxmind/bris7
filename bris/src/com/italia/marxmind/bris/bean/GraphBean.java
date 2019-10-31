package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LegendPlacement;

import com.italia.marxmind.bris.controller.BCard;
import com.italia.marxmind.bris.controller.Barangay;
import com.italia.marxmind.bris.controller.Customer;
import com.italia.marxmind.bris.controller.Education;
import com.italia.marxmind.bris.controller.EducationTrans;
import com.italia.marxmind.bris.controller.Features;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.Municipality;
import com.italia.marxmind.bris.controller.Province;
import com.italia.marxmind.bris.controller.Purok;
import com.italia.marxmind.bris.controller.RacesTrans;
import com.italia.marxmind.bris.controller.ReligionTrans;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.CivilStatus;
import com.italia.marxmind.bris.enm.EducationLevel;
import com.italia.marxmind.bris.enm.Feature;
import com.italia.marxmind.bris.enm.Religion;
import com.italia.marxmind.bris.enm.UserAccess;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 08/06/2017
 * @version 1.0
 *
 */

@ManagedBean(name="graphBean", eager=true)
@ViewScoped
public class GraphBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6556854641L;

	private int filterId;
	private List filters;
	
	private BarChartModel barModel;
	private String BARANGAY = ReadConfig.value(Bris.BARANGAY);
	
	private int APP_START_YEAR = 2017;
	
	private LegendPlacement legentPosition = LegendPlacement.OUTSIDEGRID;
	
	public Barangay getBarangay(){
		Barangay bg = new Barangay();
		try{
		String[] params = new String[1];
		params[0] = BARANGAY;
		String sql = " AND bgy.bgisactive=1 AND bgy.bgname=?";
		bg = Barangay.retrieve(sql, params).get(0);
		}catch(Exception e){
			bg.setName("UNKNOWN");
		}
		return bg;
	}
	
	@PostConstruct
	public void init(){
		Login in = Login.getUserLogin();
		boolean allow = false;
		if(UserAccess.DEVELOPER.getId() == in.getAccessLevel().getLevel()){
    		allow = true;
    	}else{
    		allow = Features.isEnabled(Feature.GRAPH);
    	}
		
		if(allow){
		APP_START_YEAR = Customer.firstYearData();
		getBarangay();
			
		switch(getFilterId()){
			case 0 :
				loadTempInfo();
				break;
			case 1 :
				loadAllRegistered();
				break;
			case 2 :
				loadMaleRegistered();
				break;
			case 3:
				loadFemaleRegistered();
				break;
			case 4:
				loadMaleFemaleRegistered();
				break;
			case 5:
				loadSingle();
				break;
			case 6:
				loadMarried();
				break;
			case 7:
				loadDivorced();
				break;
			case 8:
				loadWidowed();
				break;
			case 9:
				loadLivin();
				break;		
			case 10:
				loadSeperated();
				break;
			case 11:
				loadCommonLaw();
				break;
			case 12:
				loadCivilStatus();
				break;
			case 13:
				loadAgeBracket();
				break;
			case 14:
				loadIDGenerated();
				break;
			case 15:
				loadReligion();
				break;
			case 16:
				loadEducation();
				break;
			case 17:
				loadRaces();
				break;
		}
			
		}else{
			loadTempInfo();
		}
		
	}
	
	private void loadIDGenerated(){
		BarChartModel model = new BarChartModel();
		
		int appStartYear = APP_START_YEAR;
		int currentYear = DateUtils.getCurrentYear();
		Map<Integer, Integer> customerYear = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		
		for(int year=appStartYear; year<=currentYear; year++){
		
			String sql = " AND crd.isactivebid=1 AND ( crd.datetrans>=? AND crd.datetrans<=?)";
			String[] params = new String[2];
			params[0] = year+"-01-01";
			params[1] = year+"-12-31";
		
			//get the year info
			List<BCard> cards = BCard.retrieve(sql, params);
			customerYear.put(year, cards.size());
		
		}
		
		ChartSeries person = new ChartSeries();
		 person.setLabel("ID Generated");
		for(int year : customerYear.keySet()){
			 person.set(year, customerYear.get(year));
			 countData.put(customerYear.get(year), customerYear.get(year));
		}
		model.addSeries(person);
		
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of All ID Generated Per Year");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Year");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+5);
       
        
       xAxis.setTickFormat("%'d");
        
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of ID Created");
	}
	
	private void loadAllRegistered(){
		BarChartModel model = new BarChartModel();
		
		int appStartYear = APP_START_YEAR;
		int currentYear = DateUtils.getCurrentYear();
		Map<Integer, Integer> customerYear = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		
		for(int year=appStartYear; year<=currentYear; year++){
		
			String sql = " AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
			String[] params = new String[3];
			params[0] = year+"-01-01";
			params[1] = year+"-12-31";
			params[2] = getBarangay().getId()+"";
			
		
			//get the year info
			List<Customer> customers = Customer.retrieve(sql, params);
			customerYear.put(year, customers.size());
		
		}
		
		ChartSeries person = new ChartSeries();
		 person.setLabel("Male & Female");
		for(int year : customerYear.keySet()){
			 person.set(year, customerYear.get(year));
			 countData.put(customerYear.get(year), customerYear.get(year));
		}
		model.addSeries(person);
		
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of All Registered Person");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Year");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+5);
       
        
       xAxis.setTickFormat("%'d");
        
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Registered Person");
		
	}
	
	private void loadMaleRegistered(){
		BarChartModel model = new BarChartModel();
		
		int appStartYear = APP_START_YEAR;
		int currentYear = DateUtils.getCurrentYear();
		Map<Integer, Integer> customerYear = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		
		for(int year=appStartYear; year<=currentYear; year++){
		
			String sql = " AND cus.cusgender='1' AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
			String[] params = new String[3];
			params[0] = year+"-01-01";
			params[1] = year+"-12-31";
			params[2] = getBarangay().getId()+"";
		
			//get the year info
			List<Customer> customers = Customer.retrieve(sql, params);
			customerYear.put(year, customers.size());
		
		}
		
		ChartSeries person = new ChartSeries();
		 person.setLabel("Male");
		for(int year : customerYear.keySet()){
			 person.set(year, customerYear.get(year));
			 countData.put(customerYear.get(year), customerYear.get(year));
		}
		model.addSeries(person);
		
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of All Male Registered Person");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Year");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+5);
       
        
       xAxis.setTickFormat("%'d");
        
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Registered Male Person");
		
	}
	
	private void loadFemaleRegistered(){
		BarChartModel model = new BarChartModel();
		
		int appStartYear = APP_START_YEAR;
		int currentYear = DateUtils.getCurrentYear();
		Map<Integer, Integer> customerYear = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		
		for(int year=appStartYear; year<=currentYear; year++){
		
			String sql = " AND cus.cusgender='2' AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
			String[] params = new String[3];
			params[0] = year+"-01-01";
			params[1] = year+"-12-31";
			params[2] = getBarangay().getId()+"";
		
			//get the year info
			List<Customer> customers = Customer.retrieve(sql, params);
			customerYear.put(year, customers.size());
		
		}
		
		ChartSeries person = new ChartSeries();
		 person.setLabel("Female");
		for(int year : customerYear.keySet()){
			 person.set(year, customerYear.get(year));
			 countData.put(customerYear.get(year), customerYear.get(year));
		}
		model.addSeries(person);
		
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of All Female Registered Person");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Year");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+5);
       
        
       xAxis.setTickFormat("%'d");
        
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Registered Female Person");
		
	}
	
	private void loadMaleFemaleRegistered(){
		BarChartModel model = new BarChartModel();
		
		int appStartYear = APP_START_YEAR;
		int currentYear = DateUtils.getCurrentYear();
		Map<Integer, Integer> customerMale = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> customerFemale = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		
		for(int gender=1; gender<=2;gender++){
			for(int year=appStartYear; year<=currentYear; year++){
			
				String sql = " AND cus.cusgender='"+ gender +"' AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
				String[] params = new String[3];
				params[0] = year+"-01-01";
				params[1] = year+"-12-31";
				params[2] = getBarangay().getId()+"";
			
				//get the year info
				List<Customer> customers = Customer.retrieve(sql, params);
				if(gender==1){
					customerMale.put(year, customers.size());
				}else{
					customerFemale.put(year, customers.size());
				}
			
			}
		}
		
		for(int gender=1; gender<=2;gender++){
			ChartSeries person = new ChartSeries();
			
			if(gender==1){
				person.setLabel("Male");
				for(int year : customerMale.keySet()){
					 person.set(year, customerMale.get(year));
					 countData.put(customerMale.get(year), customerMale.get(year));
				}
			}else {
				person.setLabel("Female");
				for(int year : customerFemale.keySet()){
					 person.set(year, customerFemale.get(year));
					 countData.put(customerFemale.get(year), customerFemale.get(year));
				}
			}
			
			 model.addSeries(person);
		}
		
		
		
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of All Male & Female Registered Person");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
       // barModel.setStacked(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Year");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+5);
       
        
       xAxis.setTickFormat("%'d");
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Registered Person");
        
        
	}
	
	private void loadReligion(){
		
		BarChartModel model = new BarChartModel();
		String[] params = new String[0];
		String sql = " AND trn.isactiverel=1 AND trn.ispresentrel=1 group by trn.relid";
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		for(ReligionTrans  rels : ReligionTrans.retrieve(sql, params)){
			
			ChartSeries person = new ChartSeries();
			
			sql = " AND trn.isactiverel=1 AND trn.ispresentrel=1 AND trn.relid=" + rels.getReligionId();
			int total = ReligionTrans.retrieve(sql, params).size();
			person.setLabel(Religion.typeName(rels.getReligionId()));
			person.set(" ", total);
			
			 model.addSeries(person);
			 countData.put(total, total);
		}
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of All Religion");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
       // barModel.setStacked(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Religion");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+2);
       
        
       xAxis.setTickFormat("%'d");
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Religion");
		
		
	}
	
	private void loadEducation(){
		
		BarChartModel model = new BarChartModel();
		String[] params = new String[0];
		String sql = " AND trn.isactiveedtran=1 AND trn.ispresented=1 GROUP BY trn.levelid";
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		for(EducationTrans  eds : EducationTrans.retrieve(sql, params)){
			
			ChartSeries person = new ChartSeries();
			
			sql = " AND trn.isactiveedtran=1 AND trn.ispresented=1 AND trn.levelid=" + eds.getLevelId();
			int total = EducationTrans.retrieve(sql, params).size();
			person.setLabel(EducationLevel.typeName(eds.getLevelId()));
			person.set(" ", total);
			
			 model.addSeries(person);
			 countData.put(total, total);
		}
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of All Education");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
       // barModel.setStacked(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Education");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+2);
       
        
       xAxis.setTickFormat("%'d");
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Education");
		
		
	}
	
	private void loadRaces(){
		
		BarChartModel model = new BarChartModel();
		String[] params = new String[0];
		String sql = " AND trn.isactiveracetrans=1 GROUP BY rc.raceid";
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		for(RacesTrans  rcs : RacesTrans.retrieve(sql, params)){
			
			ChartSeries person = new ChartSeries();
			
			sql = " AND trn.isactiveracetrans=1 AND rc.raceid=" + rcs.getRaces().getId();
			int total = RacesTrans.retrieve(sql, params).size();
			person.setLabel(rcs.getRaces().getName());
			person.set(" ", total);
			
			 model.addSeries(person);
			 countData.put(total, total);
		}
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of All Tribes");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
       // barModel.setStacked(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Tribes");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+2);
       
        
       xAxis.setTickFormat("%'d");
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Tribes");
		
		
	}
	
	private void loadSingle(){
		
		BarChartModel model = new BarChartModel();
		
		int appStartYear = APP_START_YEAR;
		int currentYear = DateUtils.getCurrentYear();
		Map<Integer, Integer> customerYear = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		
		for(int year=appStartYear; year<=currentYear; year++){
		
			String sql = " AND cus.civilstatus="+ CivilStatus.SINGLE.getId() +" AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
			String[] params = new String[3];
			params[0] = year+"-01-01";
			params[1] = year+"-12-31";
			params[2] = getBarangay().getId()+"";
		
			//get the year info
			List<Customer> customers = Customer.retrieve(sql, params);
			customerYear.put(year, customers.size());
		
		}
		
		ChartSeries person = new ChartSeries();
		 person.setLabel("Single");
		for(int year : customerYear.keySet()){
			 person.set(year, customerYear.get(year));
			 countData.put(customerYear.get(year), customerYear.get(year));
		}
		model.addSeries(person);
		
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of All Single Registered Person");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Year");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+5);
       
        
       xAxis.setTickFormat("%'d");
        
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Registered Single Person");
	}
	
	private void loadMarried(){
		
		BarChartModel model = new BarChartModel();
		
		int appStartYear = APP_START_YEAR;
		int currentYear = DateUtils.getCurrentYear();
		Map<Integer, Integer> customerYear = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		
		for(int year=appStartYear; year<=currentYear; year++){
		
			String sql = " AND cus.civilstatus="+ CivilStatus.MARRIED.getId() +" AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
			String[] params = new String[3];
			params[0] = year+"-01-01";
			params[1] = year+"-12-31";
			params[2] = getBarangay().getId()+"";
		
			//get the year info
			List<Customer> customers = Customer.retrieve(sql, params);
			customerYear.put(year, customers.size());
		
		}
		
		ChartSeries person = new ChartSeries();
		person.setLabel("Married");
		for(int year : customerYear.keySet()){
			 person.set(year, customerYear.get(year));
			 countData.put(customerYear.get(year), customerYear.get(year));
		}
		model.addSeries(person);
		
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of All Married Registered Person");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Year");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+5);
       
        
       xAxis.setTickFormat("%'d");
        
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Registered Married Person");
	}
	
	private void loadDivorced(){
		
		BarChartModel model = new BarChartModel();
		
		int appStartYear = APP_START_YEAR;
		int currentYear = DateUtils.getCurrentYear();
		Map<Integer, Integer> customerYear = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		
		for(int year=appStartYear; year<=currentYear; year++){
		
			String sql = " AND cus.civilstatus="+ CivilStatus.DIVORCED.getId() +" AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
			String[] params = new String[23];
			params[0] = year+"-01-01";
			params[1] = year+"-12-31";
			params[2] = getBarangay().getId()+"";
		
			//get the year info
			List<Customer> customers = Customer.retrieve(sql, params);
			customerYear.put(year, customers.size());
		
		}
		 ChartSeries person = new ChartSeries();
		 person.setLabel("Divorced");
		for(int year : customerYear.keySet()){
			 person.set(year, customerYear.get(year));
			 countData.put(customerYear.get(year), customerYear.get(year));
		}
		model.addSeries(person);
		
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of All Divorced Registered Person");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Year");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+5);
       
        
       xAxis.setTickFormat("%'d");
        
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Registered Divorced Person");
	}
	
	private void loadWidowed(){
		
		BarChartModel model = new BarChartModel();
		
		int appStartYear = APP_START_YEAR;
		int currentYear = DateUtils.getCurrentYear();
		Map<Integer, Integer> customerYear = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		
		for(int year=appStartYear; year<=currentYear; year++){
		
			String sql = " AND cus.civilstatus="+ CivilStatus.WIDOWED.getId() +" AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
			String[] params = new String[3];
			params[0] = year+"-01-01";
			params[1] = year+"-12-31";
			params[2] = getBarangay().getId()+"";
		
			//get the year info
			List<Customer> customers = Customer.retrieve(sql, params);
			customerYear.put(year, customers.size());
		
		}
			ChartSeries person = new ChartSeries();
			 person.setLabel("Widowed");
		for(int year : customerYear.keySet()){
			 person.set(year, customerYear.get(year));
			 countData.put(customerYear.get(year), customerYear.get(year));
		}
		model.addSeries(person);
		
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of All Widowed Registered Person");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Year");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+5);
       
        
       xAxis.setTickFormat("%'d");
        
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Registered Widowed Person");
	}
	
	private void loadLivin(){
		
		BarChartModel model = new BarChartModel();
		
		int appStartYear = APP_START_YEAR;
		int currentYear = DateUtils.getCurrentYear();
		Map<Integer, Integer> customerYear = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		
		for(int year=appStartYear; year<=currentYear; year++){
		
			String sql = " AND cus.civilstatus="+ CivilStatus.LIVEIN.getId() +" AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
			String[] params = new String[23];
			params[0] = year+"-01-01";
			params[1] = year+"-12-31";
			params[2] = getBarangay().getId()+"";
		
			//get the year info
			List<Customer> customers = Customer.retrieve(sql, params);
			customerYear.put(year, customers.size());
		
		}
			ChartSeries person = new ChartSeries();
			 person.setLabel("Live In");
		for(int year : customerYear.keySet()){
			 person.set(year, customerYear.get(year));
			 countData.put(customerYear.get(year), customerYear.get(year));
		}
		model.addSeries(person);
		
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of All Live in Registered Person");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Year");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+5);
       
        
       xAxis.setTickFormat("%'d");
        
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Registered Live in Person");
	}
	
	private void loadSeperated(){
		
		BarChartModel model = new BarChartModel();
		
		int appStartYear = APP_START_YEAR;
		int currentYear = DateUtils.getCurrentYear();
		Map<Integer, Integer> customerYear = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		
		for(int year=appStartYear; year<=currentYear; year++){
		
			String sql = " AND cus.civilstatus="+ CivilStatus.SEPERATED.getId() +" AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
			String[] params = new String[3];
			params[0] = year+"-01-01";
			params[1] = year+"-12-31";
			params[2] = getBarangay().getId()+"";
		
			//get the year info
			List<Customer> customers = Customer.retrieve(sql, params);
			customerYear.put(year, customers.size());
		
		}
			ChartSeries person = new ChartSeries();
			 person.setLabel("Seperated");
		for(int year : customerYear.keySet()){
			 person.set(year, customerYear.get(year));
			 countData.put(customerYear.get(year), customerYear.get(year));
		}
		model.addSeries(person);
		
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of All Seperated Registered Person");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Year");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+5);
       
        
       xAxis.setTickFormat("%'d");
        
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Registered Seperated Person");
	}
	
	private void loadCommonLaw(){
		
		BarChartModel model = new BarChartModel();
		
		int appStartYear = APP_START_YEAR;
		int currentYear = DateUtils.getCurrentYear();
		Map<Integer, Integer> customerYear = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		
		for(int year=appStartYear; year<=currentYear; year++){
		
			String sql = " AND cus.civilstatus="+ CivilStatus.COMMON_LAW.getId() +" AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
			String[] params = new String[3];
			params[0] = year+"-01-01";
			params[1] = year+"-12-31";
			params[2] = getBarangay().getId()+"";
		
			//get the year info
			List<Customer> customers = Customer.retrieve(sql, params);
			customerYear.put(year, customers.size());
		
		}
			ChartSeries person = new ChartSeries();
			 person.setLabel("Common Law");
		for(int year : customerYear.keySet()){
			 person.set(year, customerYear.get(year));
			 countData.put(customerYear.get(year), customerYear.get(year));
		}
		model.addSeries(person);
		
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of All Common Law Registered Person");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Year");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+5);
       
        
       xAxis.setTickFormat("%'d");
        
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Registered Common Law Person");
	}
	
	private void loadCivilStatus(){
		
		BarChartModel model = new BarChartModel();
		
		int appStartYear = APP_START_YEAR;
		int currentYear = DateUtils.getCurrentYear();
		Map<Integer, Integer> customerSingle = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> customerMarried = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> customerDivorced = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> customerWidowed = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> customerLivin = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> customerSeperated = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> customerCommonLaw = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		
		for(CivilStatus status : CivilStatus.values()){
		
			for(int year=appStartYear; year<=currentYear; year++){
			
				String sql = " AND cus.civilstatus="+ status.getId() +" AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
				String[] params = new String[3];
				params[0] = year+"-01-01";
				params[1] = year+"-12-31";
				params[2] = getBarangay().getId()+"";
			
				//get the year info
				List<Customer> customers = Customer.retrieve(sql, params);
				switch(status.getId()){
				case 1:
					customerSingle.put(year, customers.size());
					break;
				case 2:
					customerMarried.put(year, customers.size());
					break;
				case 3:
					customerDivorced.put(year, customers.size());
					break;
				case 4:
					customerWidowed.put(year, customers.size());
					break;
				case 5:
					customerLivin.put(year, customers.size());
					break;	
				case 6:
					customerSeperated.put(year, customers.size());
					break;
				case 7:
					customerCommonLaw.put(year, customers.size());
					break;	
				}
				
			
			}
		
		}
		for(CivilStatus status : CivilStatus.values()){
			ChartSeries person = new ChartSeries();
			 person.setLabel(status.getName());
				
				switch(status.getId()){
				case 1:
					for(int year : customerSingle.keySet()){
						 person.set(year, customerSingle.get(year));
						 countData.put(customerSingle.get(year), customerSingle.get(year));
					}
					break;
				case 2:
					for(int year : customerMarried.keySet()){
						 person.set(year, customerMarried.get(year));
						 countData.put(customerMarried.get(year), customerMarried.get(year));
					}
					break;
				case 3:
					for(int year : customerDivorced.keySet()){
						 person.set(year, customerDivorced.get(year));
						 countData.put(customerDivorced.get(year), customerDivorced.get(year));
					}
					break;
				case 4:
					for(int year : customerWidowed.keySet()){
						 person.set(year, customerWidowed.get(year));
						 countData.put(customerWidowed.get(year), customerWidowed.get(year));
					}
					break;
				case 5:
					for(int year : customerCommonLaw.keySet()){
						 person.set(year, customerCommonLaw.get(year));
						 countData.put(customerCommonLaw.get(year), customerSeperated.get(year));
					}
					break;		
				case 6:
					for(int year : customerSeperated.keySet()){
						 person.set(year, customerSeperated.get(year));
						 countData.put(customerSeperated.get(year), customerSeperated.get(year));
					}
					break;		
				case 7:
					for(int year : customerCommonLaw.keySet()){
						 person.set(year, customerCommonLaw.get(year));
						 countData.put(customerCommonLaw.get(year), customerCommonLaw.get(year));
					}
					break;		
				}
				
			model.addSeries(person);
		}
		
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of All Civil Status Registered Person");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Year");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+5);
       
        
       xAxis.setTickFormat("%'d");
        
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Registered Civil Status Person");
	}
	
	
	private void loadAgeBracket(){
		
		BarChartModel model = new BarChartModel();
		
		int appStartYear = APP_START_YEAR;
		int currentYear = DateUtils.getCurrentYear();
		
		Map<Integer, Integer> customer0005 = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> customer0615 = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		
		Map<Integer, Integer> customer1625 = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> customer2635 = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> customer3645 = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> customer4655 = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> customer5665 = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> customer6675 = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		Map<Integer, Integer> customer7685 = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		
		Map<Integer, Integer> countData = Collections.synchronizedMap(new HashMap<Integer,Integer>());
		String[] age = {"0-5","6-15","16-25","26-35","36-45","46-55","56-65","66-75","76-85"};
		
		for(String ag : age){
			String sql ="";
			for(int year=appStartYear; year<=currentYear; year++){
				
				String[] params = new String[3];
				params[0] = year+"-01-01";
				params[1] = year+"-12-31";
				params[2] = getBarangay().getId()+"";
				List<Customer> customers = null;
				switch(ag){
				case "0-5":
					sql = " AND (cus.cusage>=0 AND cus.cusage<=5) AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
					customers = Customer.retrieve(sql, params);
					customer0005.put(year, customers.size());
					break;
				case "6-15":
					sql = " AND (cus.cusage>=6 AND cus.cusage<=15) AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
					customers = Customer.retrieve(sql, params);
					customer0615.put(year, customers.size());
					break;
				case "16-25":
					sql = " AND (cus.cusage>=16 AND cus.cusage<=25) AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
					customers = Customer.retrieve(sql, params);
					customer1625.put(year, customers.size());
					break;
				case "26-35":
					sql = " AND (cus.cusage>=26 AND cus.cusage<=35) AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
					customers = Customer.retrieve(sql, params);
					customer2635.put(year, customers.size());
					break;
				case "36-45":
					sql = " AND (cus.cusage>=36 AND cus.cusage<=45) AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
					customers = Customer.retrieve(sql, params);
					customer3645.put(year, customers.size());
					break;	
				case "46-55":
					sql = " AND (cus.cusage>=46 AND cus.cusage<=55) AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
					customers = Customer.retrieve(sql, params);
					customer4655.put(year, customers.size());
					break;	
				case "56-65":
					sql = " AND (cus.cusage>=56 AND cus.cusage<=65) AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
					customers = Customer.retrieve(sql, params);
					customer5665.put(year, customers.size());
					break;	
				case "66-75":
					sql = " AND (cus.cusage>=66 AND cus.cusage<=75) AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
					customers = Customer.retrieve(sql, params);
					customer6675.put(year, customers.size());
					break;	
				case "76-85":
					sql = " AND (cus.cusage>=76 AND cus.cusage<=85) AND cus.cusisactive=1 AND ( cus.cusdateregistered>=? AND cus.cusdateregistered<=?) AND bar.bgid=?";
					customers = Customer.retrieve(sql, params);
					customer7685.put(year, customers.size());
					break;	
				}
				 
					
			
			}
		}
		
		for(String ag : age){
		
			ChartSeries person = new ChartSeries();
			person.setLabel(ag);
			
			switch(ag){
			case "0-5":
				for(int year : customer0005.keySet()){
					 person.set(year, customer0005.get(year));
					 countData.put(customer0005.get(year), customer0005.get(year));
				}
				break;
			case "6-15":
				for(int year : customer0615.keySet()){
					 person.set(year, customer0615.get(year));
					 countData.put(customer0615.get(year), customer0615.get(year));
				}
				break;
			case "16-25":
				for(int year : customer1625.keySet()){
					 person.set(year, customer1625.get(year));
					 countData.put(customer1625.get(year), customer1625.get(year));
				}
				break;
			case "26-35":
				for(int year : customer2635.keySet()){
					 person.set(year, customer2635.get(year));
					 countData.put(customer2635.get(year), customer2635.get(year));
				}
				break;
			case "36-45":
				for(int year : customer3645.keySet()){
					 person.set(year, customer3645.get(year));
					 countData.put(customer3645.get(year), customer3645.get(year));
				}
				break;	
			case "46-55":
				for(int year : customer4655.keySet()){
					 person.set(year, customer4655.get(year));
					 countData.put(customer4655.get(year), customer4655.get(year));
				}
				break;	
			case "56-65":
				for(int year : customer5665.keySet()){
					 person.set(year, customer5665.get(year));
					 countData.put(customer5665.get(year), customer5665.get(year));
				}
				break;	
			case "66-75":
				for(int year : customer6675.keySet()){
					 person.set(year, customer6675.get(year));
					 countData.put(customer6675.get(year), customer6675.get(year));
				}
				break;	
			case "76-85":
				for(int year : customer7685.keySet()){
					 person.set(year, customer7685.get(year));
					 countData.put(customer7685.get(year), customer7685.get(year));
				}
				break;	
			}
			
		
			model.addSeries(person);
		
		}
		
		Map<Integer, Integer> sortNumber = new TreeMap<Integer, Integer>(countData);
		int maxCount = 0;
		for(Integer count : sortNumber.values()){
			maxCount = count;//get last value
		}
		
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Graphical Presentation of Age Bracket for Registered Person");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Year");
       yAxis.setMin(1);
       yAxis.setMax(maxCount+5);
       
        
       xAxis.setTickFormat("%'d");
        
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Registered Person");
	}
	
	private void loadTempInfo(){
		BarChartModel model = new BarChartModel();
		 
		 int thisYear = DateUtils.getCurrentYear();
		 int yearsAgo = thisYear - 9;
		 int[] years = new int[10];
		 int x=0;
		 for(int y=yearsAgo; y<=thisYear;y++){
			 years[x++] = y;
		 }
		 
		 String[] genders = {"Male", "Female"};
		 
		 
		
		 for(String gender : genders){	 
			 
			 ChartSeries person = new ChartSeries();
			 person.setLabel(gender);
			 
			 int value=100;
			 /*for(int month=1; month<=12;month++){
				 person.set(month, value++);
			 }*/
			 
			 for(int year=0;year<10;year++){
				 person.set(years[year], value);
				 value+=80;
			 }
			 
			 model.addSeries(person);
		 }
		
		 
		
		barModel = model;
		
		//barModel.setShowPointLabels(true);
		barModel.setTitle("Example Report");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(legentPosition);
        barModel.setZoom(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Graph Presentation Ten Years Ago");
       yAxis.setMin(1);
       yAxis.setMax(1000);
       
        
       xAxis.setTickFormat("%'d");
        
        
        yAxis.setTickFormat("%'d");
        yAxis.setLabel("Number of Registered Person");
	}
	
	public int getFilterId() {
		return filterId;
	}
	public void setFilterId(int filterId) {
		this.filterId = filterId;
	}
	public List getFilters() {
		
		filters = new ArrayList<>();
		filters.add(new SelectItem(0, "Example"));
		filters.add(new SelectItem(1, "Person Registered for Barangay " + BARANGAY));
		filters.add(new SelectItem(2, "Male Registered"));
		filters.add(new SelectItem(3, "Female Registered"));
		filters.add(new SelectItem(4, "Male & Female Registered"));
		filters.add(new SelectItem(5, "Single"));
		filters.add(new SelectItem(6, "Married"));
		filters.add(new SelectItem(7, "Divorced"));
		filters.add(new SelectItem(8, "Widowed"));
		filters.add(new SelectItem(9, "Live In"));
		filters.add(new SelectItem(10, "Seperated"));
		filters.add(new SelectItem(11, "Common Law"));
		filters.add(new SelectItem(12, "Civil Status"));
		filters.add(new SelectItem(13, "Age Bracket"));
		filters.add(new SelectItem(14, "ID Generated"));
		filters.add(new SelectItem(15, "Religion"));
		filters.add(new SelectItem(16, "Education"));
		filters.add(new SelectItem(17, "Tribes"));
		
		return filters;
	}
	public void setFilters(List filters) {
		this.filters = filters;
	}


	public BarChartModel getBarModel() {
		return barModel;
	}


	public void setBarModel(BarChartModel barModel) {
		this.barModel = barModel;
	}

	public LegendPlacement getLegentPosition() {
		return legentPosition;
	}

	public void setLegentPosition(LegendPlacement legentPosition) {
		this.legentPosition = legentPosition;
	}
	
}

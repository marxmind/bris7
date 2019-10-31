package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.MeterGaugeChartModel;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.Chequedtls;
import com.italia.marxmind.bris.controller.IRA;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.MOOE;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.DateFormat;
import com.italia.marxmind.bris.enm.IraType;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.reports.ReadXML;
import com.italia.marxmind.bris.reports.ReportTag;
import com.italia.marxmind.bris.utils.Currency;
import com.italia.marxmind.bris.utils.DateUtils;
/**
 * 
 * @author mark italia
 * @since 08/09/2017
 * @version 1.0
 *
 */
@ManagedBean(name="meterBean", eager=true)
@ViewScoped
public class BudgetMeterBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 545456869691L;
	private MeterGaugeChartModel model;
	private Date dateTrans;
	private List days;
	private int day;
	private double amount;
	private IRA iraDataSelected;
	
	private Date dateTransMo;
	private int codeMo;
	private String nameMo;
	private double amountMo;
	private MOOE selectedMOOE;
	private List yearsMo;
	private int yearMo;
	private List mos = Collections.synchronizedList(new ArrayList<MOOE>());
	
	private List iras = Collections.synchronizedList(new ArrayList<IRA>());
	
	private HorizontalBarChartModel mooeModel;
	
	private List types;
	private int type;
	private String descriptionIra;
	
	private List yearsIra;
	private int yearIra;
	
	private List years;
	private int year;
	
	private String remainingMOOEUseable;
	
	@PostConstruct
	public void init(){
		ChartBean(DateUtils.getCurrentYear());
		loadIra();
		loadMooe();
		ChartMooeBean(DateUtils.getCurrentYear());
		calculateUseableBudget(DateUtils.getCurrentYear());
	}
	
	public void calculateUseableBudget(int year){
		double amount = 0;
		try{
			
			amount = IRA.iraAmount(year);
			amount -= MOOE.mooeAmount(year);
			
		}catch(Exception e){}
		setRemainingMOOEUseable(Currency.formatAmount(amount));
	}
	
	public void loadChartIra(){
		ChartBean(getYear());
	}
	
	public void loadIra(){
		iras = Collections.synchronizedList(new ArrayList<IRA>());
		
		String sql = " AND ir. iraisactive=1 AND ir.irayear=?";
		String[] params = new String[1];
		params[0] = getYearIra()+"";
		
		iras=  IRA.retrieve(sql, params);
	}
	
	public void clearFlds(){
		setDateTrans(null);
		setDay(0);
		setAmount(0.00);
		setIraDataSelected(null);
		setType(1);
		setDescriptionIra(null);
	}
	
	public void saveIra(){
		boolean isOk = true;
		IRA ira = new IRA();
		if(getIraDataSelected()!=null){
			ira = getIraDataSelected();
		}else{
			ira.setIsActive(1);
			ira.setYear(DateUtils.getCurrentYear());
			
			if(IRA.isIRAExist(DateUtils.getCurrentYear()) && IraType.IRA.getId()==getType()){
				isOk = false;
				Application.addMessage(3, "Error", "IRA is already existed");
			}
			
		}
		
		if(getDay()==0){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide cycle of IRA every month");
		}
		
		if(getAmount()<=0){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide amount");
		}
		
		if(getDescriptionIra()==null || getDescriptionIra().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide description");
		}
		
		if(isOk){
			ira.setDateTrans(DateUtils.convertDate(getDateTrans(),DateFormat.YYYY_MM_DD()));
			ira.setCycleDate(getDay());
			ira.setAmount(getAmount());
			ira.setUserDtls(Login.getUserLogin().getUserDtls());
			ira.setType(getType());
			ira.setDescription(getDescriptionIra());
			ira.save();
			//ChartBean(ira.getYear());
			loadIra();
			clearFlds();
			Application.addMessage(1, "Success", "Successfully saved");
		}
	}
	
	public void clickIra(IRA ira){
		setIraDataSelected(ira);
		setDateTrans(DateUtils.convertDateString(ira.getDateTrans(),DateFormat.YYYY_MM_DD()));
		setDay(ira.getCycleDate());
		setAmount(ira.getAmount());
		//ChartBean(ira.getYear());
		setType(ira.getType());
		setDescriptionIra(ira.getDescription());
	}
	
	public void deleteIra(IRA ira){
		ira.delete();
		init();
		clearFlds();
		Application.addMessage(1, "Success", "Successfully deleted");
	}
	
	public void resetIRA(){
		ChartBean(DateUtils.getCurrentYear());
		Application.addMessage(1, "Success", "Successfully reseted to current year");
	}
	
	public void saveMooe(){
		MOOE mo = new MOOE();
		boolean isOk = true;
		if(getSelectedMOOE()!=null){
			mo = getSelectedMOOE();
		}else{
			mo.setIsActive(1);
			mo.setYear(DateUtils.getCurrentYear());
		}
		
		if(getCodeMo()<=0){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide code");
		}
		if(getNameMo()==null || getNameMo().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide name");
		}
		if(getAmountMo()<=0){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide amount");
		}
		
		if(isOk){
			mo.setDateTrans(DateUtils.convertDate(getDateTransMo(), DateFormat.YYYY_MM_DD()));
			mo.setCode(getCodeMo());
			mo.setName(getNameMo());
			mo.setUserDtls(Login.getUserLogin().getUserDtls());
			mo.setAmount(getAmountMo());
			mo.save();
			loadMooe();
			clearFldsMooe();
			Application.addMessage(1, "Success", "Successfully saved");
		}
	}
	
	public void loadMooe(){
		mos = Collections.synchronizedList(new ArrayList<MOOE>());
		String sql = " AND mo.moisactive AND mo.moyear=" + getYearMo();
		mos = MOOE.retrieve(sql, new String[0]);
	}
	
	public void clickMooe(MOOE mo){
		setSelectedMOOE(mo);
		setDateTransMo(DateUtils.convertDateString(mo.getDateTrans(), DateFormat.YYYY_MM_DD()));
		setCodeMo(mo.getCode());
		setNameMo(mo.getName());
		setAmountMo(mo.getAmount());
		ChartMooeBean(mo.getYear());
		calculateUseableBudget(DateUtils.getCurrentYear());
	}
	
	public void deleteMooe(MOOE mo){
		mo.delete();
		loadMooe();
		clearFldsMooe();
		Application.addMessage(1, "Success", "Successfully deleted");
	}
	
	public void clearFldsMooe(){
		setSelectedMOOE(null);
		setDateTransMo(null);
		setCodeMo(0);
		setNameMo(null);
		setAmountMo(0.00);
		calculateUseableBudget(DateUtils.getCurrentYear());
	}
	
	private double checkMeter(int year){
		double amount = 0d;
		String sql = " AND chk.isactivechk=1 AND (chk.datetrans>=? AND chk.datetrans<=?)";
		String[] params = new String[2];
		/*params[0] = DateUtils.getCurrentYear() +"-01-01";
		params[1] = DateUtils.getCurrentDateYYYYMMDD();*/
		params[0] = year +"-01-01";
		params[1] = year + "-12-31";
		
		for(Chequedtls chk : Chequedtls.retrieve(sql, params)){
			amount += chk.getAmount();
		}
		
		return amount;
		
	}
	
	private double checkMooe(int year, long moid){
		double amount = 0d;
		String sql = " AND chk.isactivechk=1 AND (chk.datetrans>=? AND chk.datetrans<=?) AND chk.moid=?";
		String[] params = new String[3];
		params[0] = year +"-01-01";
		params[1] = year + "-12-31";
		params[2] = moid+"";
		
		for(Chequedtls chk : Chequedtls.retrieve(sql, params)){
			amount += chk.getAmount();
		}
		
		return amount;
		
	}
	
	public void ChartBean(int year) {
		
		//double maxAmnt = Double.valueOf(ReadConfig.value(Bris.YEARLY_BUDGET));
		double maxAmnt = IRA.iraAmount(year);
		
		double sliceAmnt = maxAmnt/4;
	/*List<Number> intervals = new ArrayList<Number>(){
		{
			add(intervalAmnt);
			add(10000000);
			add(15000000);
			add(20000000);
		}
	};*/
	
	List<Number> intervals = new ArrayList<Number>();
	for(int i=1;i<=4;i++){
		intervals.add(sliceAmnt*i);
	}
		double usedbudget =  checkMeter(year);
		model = new MeterGaugeChartModel(usedbudget, intervals);
		model.setTitle("Used Budget for the year " + year);
		model.setSeriesColors("66cc66,93b75f,E7E658,cc6666");
		model.setGaugeLabel("MOE Running");
		model.setGaugeLabel("Used Budget Amount Php " + Currency.formatAmount(usedbudget));
		model.setGaugeLabelPosition("bottom");
		//model.setShowTickLabels(true);
		model.setLabelHeightAdjust(110);
		model.setIntervalOuterRadius(130);
		model.setMin(1000);
		model.setMax(maxAmnt);
		//model.setMouseoverHighlight(true);
		//model.setExtender("meterGauge");
	}
	
	public void ChartMooeBean(int year){
		mooeModel = new HorizontalBarChartModel();
		
		String sql = "SELECT * FROM mooe WHERE moisactive=1 AND moyear=?";
		String[] params = new String[1];
		params[0] = year+"";
		if(getSelectedMOOE()!=null){
			params = new String[3];
			 sql += " AND mocode=? AND moid=?";
			 params[0] = year+"";
			 params[1] = getSelectedMOOE().getCode()+"";
			 params[2] = getSelectedMOOE().getId()+"";
		}else{
			sql += " limit 1";
		}
		
		MOOE mo = MOOE.retrieveMOOE(sql, params);
		
		double amountExpense = 0;
		try{amountExpense = checkMooe(mo.getYear(), mo.getId());}catch(Exception e){
			mo.setAmount(0);
			mo.setName("No MOOE Yet");
		}
		ChartSeries bud = new ChartSeries();
		bud.setLabel("Used");
		bud.set(mo.getYear()+"", amountExpense);
		
		
		ChartSeries used = new ChartSeries();
		used.setLabel("Budget");
		used.set(mo.getYear()+"", mo.getAmount());
		
		
		mooeModel.addSeries(bud);
		mooeModel.addSeries(used);
		
		try{mooeModel.setTitle(mo.getName().toUpperCase());}catch(NullPointerException e) {mooeModel.setTitle("N/A");}
		mooeModel.setLegendPosition("e");
		mooeModel.setStacked(true);
		
		mooeModel.setLegendPlacement(LegendPlacement.OUTSIDE);
		
		Axis xAxis = mooeModel.getAxis(AxisType.X);
		xAxis.setLabel("Expenses Php " + Currency.formatAmount(amountExpense));
		xAxis.setMin(100);
		xAxis.setTickFormat("%'d");
		xAxis.setMax(mo.getAmount());
		Axis yAxis = mooeModel.getAxis(AxisType.Y);
		yAxis.setLabel("Year");
	}
	
	
	public MeterGaugeChartModel getModel() { 
		return model; 
	}

	public Date getDateTrans() {
		if(dateTrans==null){
			dateTrans = DateUtils.getDateToday();
		}
		return dateTrans;
	}

	public void setDateTrans(Date dateTrans) {
		this.dateTrans = dateTrans;
	}

	public List getDays() {
		days = new ArrayList<>();
		for(int x=1; x<=31; x++){
			days.add(new SelectItem(x, x+""));
		}
		return days;
	}

	public void setDays(List days) {
		this.days = days;
	}

	public int getDay() {
		if(day==0){
			day=10;
		}
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setModel(MeterGaugeChartModel model) {
		this.model = model;
	}

	public IRA getIraDataSelected() {
		return iraDataSelected;
	}

	public void setIraDataSelected(IRA iraDataSelected) {
		this.iraDataSelected = iraDataSelected;
	}

	public List getIras() {
		return iras;
	}

	public void setIras(List iras) {
		this.iras = iras;
	}

	public Date getDateTransMo() {
		if(dateTransMo==null){
			dateTransMo = DateUtils.getDateToday();
		}
		return dateTransMo;
	}

	public void setDateTransMo(Date dateTransMo) {
		this.dateTransMo = dateTransMo;
	}

	public int getCodeMo() {
		return codeMo;
	}

	public void setCodeMo(int codeMo) {
		this.codeMo = codeMo;
	}

	public String getNameMo() {
		return nameMo;
	}

	public void setNameMo(String nameMo) {
		this.nameMo = nameMo;
	}

	public double getAmountMo() {
		return amountMo;
	}

	public void setAmountMo(double amountMo) {
		this.amountMo = amountMo;
	}

	public List getMos() {
		return mos;
	}

	public void setMos(List mos) {
		this.mos = mos;
	}

	public MOOE getSelectedMOOE() {
		return selectedMOOE;
	}

	public void setSelectedMOOE(MOOE selectedMOOE) {
		this.selectedMOOE = selectedMOOE;
	}

	public List getYearsMo() {
		yearsMo = new ArrayList<MOOE>();
		int yr = MOOE.startYear();
			yr = yr==0? DateUtils.getCurrentYear() : yr; 
		for(int x=yr; x<=DateUtils.getCurrentYear(); x++){
			yearsMo.add(new SelectItem(x, x+""));
		}
		return yearsMo;
	}

	public void setYearsMo(List yearsMo) {
		this.yearsMo = yearsMo;
	}

	public int getYearMo() {
		if(yearMo==0){
			yearMo = DateUtils.getCurrentYear();
		}
		return yearMo;
	}

	public void setYearMo(int yearMo) {
		this.yearMo = yearMo;
	}

	public HorizontalBarChartModel getMooeModel() {
		return mooeModel;
	}

	public void setMooeModel(HorizontalBarChartModel mooeModel) {
		this.mooeModel = mooeModel;
	}

	public List getTypes() {
		types = new ArrayList<>();
		for(IraType t : IraType.values()){
			types.add(new SelectItem(t.getId(), t.getName()));
		}
		return types;
	}

	public void setTypes(List types) {
		this.types = types;
	}

	public int getType() {
		if(type==0){
			type = 1;
		}
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDescriptionIra() {
		return descriptionIra;
	}

	public void setDescriptionIra(String descriptionIra) {
		this.descriptionIra = descriptionIra;
	}

	public List getYearsIra() {
		
		yearsIra = new ArrayList<IRA>();
		int yr = IRA.startYear();
			yr = yr==0? DateUtils.getCurrentYear() : yr; 
		for(int x=yr; x<=DateUtils.getCurrentYear(); x++){
			yearsIra.add(new SelectItem(x, x+""));
		}
		
		return yearsIra;
	}

	public void setYearsIra(List yearsIra) {
		this.yearsIra = yearsIra;
	}

	public int getYearIra() {
		if(yearIra==0){
			yearIra = DateUtils.getCurrentYear();
		}
		return yearIra;
	}

	public void setYearIra(int yearIra) {
		this.yearIra = yearIra;
	}

	public List getYears() {
		
		years = new ArrayList<IRA>();
		int yr = IRA.startYear();
			yr = yr==0? DateUtils.getCurrentYear() : yr; 
		for(int x=yr; x<=DateUtils.getCurrentYear(); x++){
			years.add(new SelectItem(x, x+""));
		}
		
		return years;
	}

	public void setYears(List years) {
		this.years = years;
	}

	public int getYear() {
		if(year==0){
			year = DateUtils.getCurrentYear();
		}
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getRemainingMOOEUseable() {
		return remainingMOOEUseable;
	}

	public void setRemainingMOOEUseable(String remainingMOOEUseable) {
		this.remainingMOOEUseable = remainingMOOEUseable;
	}
	
}

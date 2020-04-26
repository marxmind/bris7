package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.axes.radial.RadialScales;
import org.primefaces.model.charts.axes.radial.linear.RadialLinearAngleLines;
import org.primefaces.model.charts.axes.radial.linear.RadialLinearPointLabels;
import org.primefaces.model.charts.axes.radial.linear.RadialLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.bubble.BubbleChartDataSet;
import org.primefaces.model.charts.bubble.BubbleChartModel;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.hbar.HorizontalBarChartDataSet;
import org.primefaces.model.charts.hbar.HorizontalBarChartModel;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.elements.Elements;
import org.primefaces.model.charts.optionconfig.elements.ElementsLine;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.optionconfig.tooltip.Tooltip;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import org.primefaces.model.charts.polar.PolarAreaChartDataSet;
import org.primefaces.model.charts.polar.PolarAreaChartModel;
import org.primefaces.model.charts.radar.RadarChartDataSet;
import org.primefaces.model.charts.radar.RadarChartModel;
import org.primefaces.model.charts.radar.RadarChartOptions;

import com.italia.marxmind.bris.application.ReadDashboardInfo;
import com.italia.marxmind.bris.utils.Currency;
import com.italia.marxmind.bris.utils.DateUtils;

@Named
@javax.faces.view.ViewScoped
public class ChartJsView implements Serializable {
     
	//MOOE Budget
    private PieChartModel pieModel;
     
    private PolarAreaChartModel polarAreaModel;
     
    private LineChartModel lineModel;
     
    private LineChartModel cartesianLinerModel;
     
    private BarChartModel barModel;
    
    //check disbursement per month compared last year
    private BarChartModel barModel2;
    
    //citizen per zone
    private HorizontalBarChartModel hbarModel;
     
    private BarChartModel stackedBarModel;
     
    private BarChartModel stackedGroupBarModel;
     
    private RadarChartModel radarModel;
     
    private RadarChartModel radarModel2;
     
    private BubbleChartModel bubbleModel;
     
    private BarChartModel mixedModel;
     
    private DonutChartModel donutModel;
 
    @PostConstruct
    public void init() {
        createPieModel();
        createPolarAreaModel();
        createLineModel();
        //createCartesianLinerModel();
        //createBarModel();
        createBarModel2();
        createHorizontalBarModel();
        //createStackedBarModel();
        //createStackedGroupBarModel();
        //createRadarModel();
        //createRadarModel2();
        //createBubbleModel();
        //createMixedModel();
        createDonutModel();
    }
     
    private void createPieModel() {
        pieModel = new PieChartModel();
        ChartData data = new ChartData();
         
        PieChartDataSet dataSet = new PieChartDataSet();
        
        List<Number> values = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        
        Map<String, Double> info = ReadDashboardInfo.getInfo("mooe");
        int indexColor=0;
        String[] rgb = rgbColors();
        
		for(String key : info.keySet()) {
			double amount = info.get(key);
			values.add(amount);
			bgColors.add(rgb[indexColor++]);
			labels.add(key+"("+Currency.formatAmount(amount)+")");
		}
		
		dataSet.setData(values);
		dataSet.setBackgroundColor(bgColors);
		data.addChartDataSet(dataSet);
		data.setLabels(labels);
		
        pieModel.setData(data);
    }
     
    private String[] rgbColors() {
    	String[] rgb = {"rgb(255, 99, 132)","rgb(250, 53, 95)","rgb(183, 83, 104)","rgb(123, 70, 82)","rgb(99, 3, 23)",
				"rgb(54, 162, 235)","rgb(14, 122, 195)","rgb(8, 73, 116)","rgb(88, 109, 124)","rgb(195, 227, 249)",
				"rgb(255, 205, 86)","rgb(228, 165, 14)","rgb(169, 144, 84)","rgb(244, 227, 187)","rgb(180, 177, 170)",
				"rgb(116, 243, 143)","rgb(117, 165, 127)","rgb(9, 48, 17)","rgb(123, 118, 240)","rgb(14, 2, 249)",
				"rgb(138, 132, 246)","rgb(67, 62, 171)","rgb(46, 238, 200)","rgb(21, 190, 156)","rgb(6, 147, 119)",
				"rgb(4, 70, 57)","rgb(26, 75, 65)","rgb(121, 172, 162)","rgb(204, 248, 239)","rgb(234, 204, 248)",
				"rgb(219, 158, 247)","rgb(203, 111, 246)","rgb(188, 58, 248)","rgb(167, 6, 242)","rgb(128, 20, 178)",
				"rgb(178, 20, 143)","rgb(190, 55, 160)","rgb(217, 106, 192)","rgb(239, 156, 220)","rgb(247, 218, 241)",
				"rgb(253, 247, 233)","rgb(248, 223, 169)","rgb(234, 191, 100)","rgb(207, 152, 32)","rgb(144, 120, 69)",
				"rgb(156, 142, 113)","rgb(141, 137, 130)","rgb(175, 213, 205)","rgb(110, 181, 167)","rgb(57, 143, 126)"
				};
    	
    	return rgb;
    }
    
    private void createPolarAreaModel() {
        polarAreaModel = new PolarAreaChartModel();
        ChartData data = new ChartData();
         
        PolarAreaChartDataSet dataSet = new PolarAreaChartDataSet();
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Map<String, Double> ages = ReadDashboardInfo.getInfo("ages");
        List<String> bgColors = new ArrayList<>();
        int i=0;
        String[] rgb = {"rgb(255, 99, 132)","rgb(75, 192, 192)","rgb(255, 205, 86)","rgb(201, 203, 207)","rgb(54, 162, 235)","rgb(141, 137, 130)","rgb(204, 248, 239)","rgb(117, 165, 127)","rgb(180, 177, 170)"};
        for(String age : ages.keySet()) {
        	values.add(ages.get(age));
        	bgColors.add(rgb[i++]);
        	labels.add(age+"("+ages.get(age)+")");
        }
        dataSet.setData(values);
        dataSet.setBackgroundColor(bgColors);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
         
        polarAreaModel.setData(data);
    }
     
    public void createLineModel() {
        lineModel = new LineChartModel();
        ChartData data = new ChartData();
         
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> values = new ArrayList<>();
        
        
        
        LineChartDataSet dataSet2 = new LineChartDataSet();
        List<Object> values2 = new ArrayList<>();
        
        
        
        
        Map<String, Double> lastYear = ReadDashboardInfo.getInfo("cases-months-last-year");
        Map<String, Double> thisYear = ReadDashboardInfo.getInfo("cases-months-this-year");
        
        List<String> labels = new ArrayList<>();
        for(int month=1; month<=12; month++) {
        	
        	labels.add(DateUtils.getMonthName(month));
        	
        	if(lastYear!=null && lastYear.containsKey(month+"")) {
        		values.add(lastYear.get(month+""));
        	}else {
        		values.add(0);
        	}
        	
        	if(thisYear!=null && thisYear.containsKey(month+"")) {
        		values2.add(thisYear.get(month+""));
        	}else {
        		values2.add(0);
        	}
        	
        }
        
        int yearToday = DateUtils.getCurrentYear();
        int prevYear = yearToday - 1;
        
        dataSet.setData(values);
        dataSet.setFill(false);
        dataSet.setLabel(""+prevYear);
        dataSet.setBorderColor("rgba(255, 99, 132, 0.2)");
        dataSet.setLineTension(0.1);
        
        dataSet2.setData(values2);
        dataSet2.setFill(false);
        dataSet2.setLabel(""+yearToday);
        dataSet2.setBorderColor("rgb(75, 192, 192)");
        dataSet2.setLineTension(0.1);
        
        data.addChartDataSet(dataSet);
        data.addChartDataSet(dataSet2);
        data.setLabels(labels);
         
        //Options
        LineChartOptions options = new LineChartOptions();        
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Case Recorded");
        options.setTitle(title);
         
        lineModel.setOptions(options);
        lineModel.setData(data);
    }
     
    public void createCartesianLinerModel() {
        cartesianLinerModel = new LineChartModel();
        ChartData data = new ChartData();
         
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> values = new ArrayList<>();
        values.add(20);
        values.add(50);
        values.add(100);
        values.add(75);
        values.add(25);
        values.add(0);
        dataSet.setData(values);
        dataSet.setLabel("Left Dataset");
        dataSet.setYaxisID("left-y-axis");
         
        LineChartDataSet dataSet2 = new LineChartDataSet();
        List<Object> values2 = new ArrayList<>();
        values2.add(0.1);
        values2.add(0.5);
        values2.add(1.0);
        values2.add(2.0);
        values2.add(1.5);
        values2.add(0);
        dataSet2.setData(values2);
        dataSet2.setLabel("Right Dataset");
        dataSet2.setYaxisID("right-y-axis");
         
        data.addChartDataSet(dataSet);
        data.addChartDataSet(dataSet2);
         
        List<String> labels = new ArrayList<>();
        labels.add("Jan");
        labels.add("Feb");
        labels.add("Mar");
        labels.add("Apr");
        labels.add("May");
        labels.add("Jun");
        data.setLabels(labels);
        cartesianLinerModel.setData(data);
         
        //Options
        LineChartOptions options = new LineChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setId("left-y-axis");
        linearAxes.setPosition("left");
        CartesianLinearAxes linearAxes2 = new CartesianLinearAxes();
        linearAxes2.setId("right-y-axis");
        linearAxes2.setPosition("right");
         
        cScales.addYAxesData(linearAxes);
        cScales.addYAxesData(linearAxes2);
        options.setScales(cScales);    
         
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Cartesian Linear Chart");
        options.setTitle(title);
         
        cartesianLinerModel.setOptions(options);
    }
     
    public void createBarModel() {
        barModel = new BarChartModel();
        ChartData data = new ChartData();
         
        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("My First Dataset");
         
        List<Number> values = new ArrayList<>();
        values.add(65);
        values.add(59);
        values.add(80);
        values.add(81);
        values.add(56);
        values.add(55);
        values.add(40);
        barDataSet.setData(values);
         
        List<String> bgColor = new ArrayList<>();
        bgColor.add("rgba(255, 99, 132, 0.2)");
        bgColor.add("rgba(255, 159, 64, 0.2)");
        bgColor.add("rgba(255, 205, 86, 0.2)");
        bgColor.add("rgba(75, 192, 192, 0.2)");
        bgColor.add("rgba(54, 162, 235, 0.2)");
        bgColor.add("rgba(153, 102, 255, 0.2)");
        bgColor.add("rgba(201, 203, 207, 0.2)");
        barDataSet.setBackgroundColor(bgColor);
         
        List<String> borderColor = new ArrayList<>();
        borderColor.add("rgb(255, 99, 132)");
        borderColor.add("rgb(255, 159, 64)");
        borderColor.add("rgb(255, 205, 86)");
        borderColor.add("rgb(75, 192, 192)");
        borderColor.add("rgb(54, 162, 235)");
        borderColor.add("rgb(153, 102, 255)");
        borderColor.add("rgb(201, 203, 207)");
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);
         
        data.addChartDataSet(barDataSet);
         
        List<String> labels = new ArrayList<>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        labels.add("July");
        data.setLabels(labels);
        barModel.setData(data);
         
        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
         
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Bar Chart");
        options.setTitle(title);
 
        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("top");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("bold");
        legendLabels.setFontColor("#2980B9");
        legendLabels.setFontSize(24);
        legend.setLabels(legendLabels);
        options.setLegend(legend);
 
        barModel.setOptions(options);
    }
     
    public void createBarModel2() {
    	int yearToday = DateUtils.getCurrentYear();
        int prevYear = yearToday - 1;
        barModel2 = new BarChartModel();
        ChartData data = new ChartData();
         
        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel(""+prevYear);
        barDataSet.setBackgroundColor("rgba(255, 99, 132, 0.2)");
        barDataSet.setBorderColor("rgb(255, 99, 132)");
        barDataSet.setBorderWidth(1);
        List<Number> values = new ArrayList<>();
        
        BarChartDataSet barDataSet2 = new BarChartDataSet();
        barDataSet2.setLabel(""+yearToday);
        barDataSet2.setBackgroundColor("rgba(255, 159, 64, 0.2)");
        barDataSet2.setBorderColor("rgb(255, 159, 64)");
        barDataSet2.setBorderWidth(1);
        
        List<Number> values2 = new ArrayList<>();
       
         
        List<String> labels = new ArrayList<>();
        
        
        Map<String, Double> info1 = ReadDashboardInfo.getInfo("check-months-last-year");
        Map<String, Double> info2 = ReadDashboardInfo.getInfo("check-months-this-year");
        
        /*for(String key : info1.keySet()) {
        	
        	labels.add(DateUtils.getMonthName(key));
        	
        	if(info1.containsKey(key)) {
        		values.add(info1.get(key));
        	}else {
        		values.add(0);
        	}
        	if(info2.containsKey(key)) {
        		values2.add(info2.get(key));
        	}else {
        		values2.add(0);
        	}
        	 
        }*/
        
        for(int month=1; month <=12; month++) {
        	labels.add(DateUtils.getMonthName(month+""));
        	
        	if(info1.containsKey(month+"")) {
        		values.add(info1.get(month+""));
        	}else {
        		values.add(0);
        	}
        	if(info2.containsKey(month+"")) {
        		values2.add(info2.get(month+""));
        	}else {
        		values2.add(0);
        	}
        }
        
        barDataSet.setData(values);
        barDataSet2.setData(values2);
        
        data.addChartDataSet(barDataSet);
        data.addChartDataSet(barDataSet2);
        
        data.setLabels(labels);
        barModel2.setData(data);
         
        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
         
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Compared Check Disbursement From last year");
        options.setTitle(title);
         
        barModel2.setOptions(options);
    }
     
    public void createHorizontalBarModel() {
        hbarModel = new HorizontalBarChartModel();
        ChartData data = new ChartData();
         
        HorizontalBarChartDataSet hbarDataSet = new HorizontalBarChartDataSet();
        
        hbarDataSet.setLabel("Recorded since " + ReadDashboardInfo.getInfo("start").get("Start"));
         
        List<Number> values = new ArrayList<>();
       /* values.add(65);
        values.add(59);
        values.add(80);
        values.add(81);
        values.add(56);
        values.add(55);
        values.add(40);
       */
         
        List<String> bgColor = new ArrayList<>();
        /*bgColor.add("rgba(255, 99, 132, 0.2)");
        bgColor.add("rgba(255, 159, 64, 0.2)");
        bgColor.add("rgba(255, 205, 86, 0.2)");
        bgColor.add("rgba(75, 192, 192, 0.2)");
        bgColor.add("rgba(54, 162, 235, 0.2)");
        bgColor.add("rgba(153, 102, 255, 0.2)");
        bgColor.add("rgba(201, 203, 207, 0.2)");*/
        
         
        List<String> borderColor = new ArrayList<>();
       /* borderColor.add("rgb(255, 99, 132)");
        borderColor.add("rgb(255, 159, 64)");
        borderColor.add("rgb(255, 205, 86)");
        borderColor.add("rgb(75, 192, 192)");
        borderColor.add("rgb(54, 162, 235)");
        borderColor.add("rgb(153, 102, 255)");
        borderColor.add("rgb(201, 203, 207)");*/
       
         
        
         
        List<String> labels = new ArrayList<>();
        
        
        Map<String, Double> zones = ReadDashboardInfo.getInfo("zones");
        int rgbColor = 0;
        String[] rgbs = rgbColors();
        for(String key : zones.keySet()) {
        	double number = zones.get(key); 
        	values.add(number);
        	bgColor.add(rgbs[rgbColor++]);
        	borderColor.add(rgbs[rgbColor]);
        	labels.add(key);
        	//rgbColor++;
        }
        hbarDataSet.setData(values);
        hbarDataSet.setBackgroundColor(bgColor);
        data.addChartDataSet(hbarDataSet);
        hbarDataSet.setBorderColor(borderColor);
        hbarDataSet.setBorderWidth(1);
        data.setLabels(labels);
        hbarModel.setData(data);
         
        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addXAxesData(linearAxes);
        options.setScales(cScales);
         
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Citizen Recorded Graph");
        options.setTitle(title);
         
        hbarModel.setOptions(options);
    }
     
    public void createStackedBarModel() {
        stackedBarModel = new BarChartModel();
        ChartData data = new ChartData();
         
        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Dataset 1");
        barDataSet.setBackgroundColor("rgb(255, 99, 132)");
        List<Number> dataVal = new ArrayList<>();
        dataVal.add(62);
        dataVal.add(-58);
        dataVal.add(-49);
        dataVal.add(25);
        dataVal.add(4);
        dataVal.add(77);
        dataVal.add(-41);
        barDataSet.setData(dataVal);
         
        BarChartDataSet barDataSet2 = new BarChartDataSet();
        barDataSet2.setLabel("Dataset 2");
        barDataSet2.setBackgroundColor("rgb(54, 162, 235)");
        List<Number> dataVal2 = new ArrayList<>();
        dataVal2.add(-1);
        dataVal2.add(32);
        dataVal2.add(-52);
        dataVal2.add(11);
        dataVal2.add(97);
        dataVal2.add(76);
        dataVal2.add(-78);
        barDataSet2.setData(dataVal2);
         
        BarChartDataSet barDataSet3 = new BarChartDataSet();
        barDataSet3.setLabel("Dataset 3");
        barDataSet3.setBackgroundColor("rgb(75, 192, 192)");
        List<Number> dataVal3 = new ArrayList<>();
        dataVal3.add(-44);
        dataVal3.add(25);
        dataVal3.add(15);
        dataVal3.add(92);
        dataVal3.add(80);
        dataVal3.add(-25);
        dataVal3.add(-11);
        barDataSet3.setData(dataVal3);
         
        data.addChartDataSet(barDataSet);
        data.addChartDataSet(barDataSet2);
        data.addChartDataSet(barDataSet3);
         
        List<String> labels = new ArrayList<>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        labels.add("July");
        data.setLabels(labels);
        stackedBarModel.setData(data);
         
        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setStacked(true);    
        cScales.addXAxesData(linearAxes);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
         
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Bar Chart - Stacked");
        options.setTitle(title);
         
        Tooltip tooltip = new Tooltip();
        tooltip.setMode("index");
        tooltip.setIntersect(false);
        options.setTooltip(tooltip);  
         
        stackedBarModel.setOptions(options);
    }
     
    public void createStackedGroupBarModel() {
        stackedGroupBarModel = new BarChartModel();
        ChartData data = new ChartData();
         
        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Dataset 1");
        barDataSet.setBackgroundColor("rgb(255, 99, 132)");
        barDataSet.setStack("Stack 0");
        List<Number> dataVal = new ArrayList<>();
        dataVal.add(-32);
        dataVal.add(-70);
        dataVal.add(-33);
        dataVal.add(30);
        dataVal.add(-49);
        dataVal.add(23);
        dataVal.add(-92);
        barDataSet.setData(dataVal);
         
        BarChartDataSet barDataSet2 = new BarChartDataSet();
        barDataSet2.setLabel("Dataset 2");
        barDataSet2.setBackgroundColor("rgb(54, 162, 235)");
        barDataSet2.setStack("Stack 0");
        List<Number> dataVal2 = new ArrayList<>();
        dataVal2.add(83);
        dataVal2.add(18);
        dataVal2.add(86);
        dataVal2.add(8);
        dataVal2.add(34);
        dataVal2.add(46);
        dataVal2.add(11);
        barDataSet2.setData(dataVal2);
         
        BarChartDataSet barDataSet3 = new BarChartDataSet();
        barDataSet3.setLabel("Dataset 3");
        barDataSet3.setBackgroundColor("rgb(75, 192, 192)");
        barDataSet3.setStack("Stack 1");
        List<Number> dataVal3 = new ArrayList<>();
        dataVal3.add(-45);
        dataVal3.add(73);
        dataVal3.add(-25);
        dataVal3.add(65);
        dataVal3.add(49);
        dataVal3.add(-18);
        dataVal3.add(46);
        barDataSet3.setData(dataVal3);
         
        data.addChartDataSet(barDataSet);
        data.addChartDataSet(barDataSet2);
        data.addChartDataSet(barDataSet3);
         
        List<String> labels = new ArrayList<>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        labels.add("July");
        data.setLabels(labels);
        stackedGroupBarModel.setData(data);
         
        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setStacked(true);    
        cScales.addXAxesData(linearAxes);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
         
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Bar Chart - Stacked Group");
        options.setTitle(title);
         
        Tooltip tooltip = new Tooltip();
        tooltip.setMode("index");
        tooltip.setIntersect(false);
        options.setTooltip(tooltip);  
         
        stackedGroupBarModel.setOptions(options);
    }
     
    public void createRadarModel() {
        radarModel = new RadarChartModel();
        ChartData data = new ChartData();
         
        RadarChartDataSet radarDataSet = new RadarChartDataSet();
        radarDataSet.setLabel("My First Dataset");
        radarDataSet.setFill(true);
        radarDataSet.setBackgroundColor("rgba(255, 99, 132, 0.2)");
        radarDataSet.setBorderColor("rgb(255, 99, 132)");
        radarDataSet.setPointBackgroundColor("rgb(255, 99, 132)");
        radarDataSet.setPointBorderColor("#fff");
        radarDataSet.setPointHoverBackgroundColor("#fff");
        radarDataSet.setPointHoverBorderColor("rgb(255, 99, 132)");
        List<Number> dataVal = new ArrayList<>();
        dataVal.add(65);
        dataVal.add(59);
        dataVal.add(90);
        dataVal.add(81);
        dataVal.add(56);
        dataVal.add(55);
        dataVal.add(40);
        radarDataSet.setData(dataVal);
         
        RadarChartDataSet radarDataSet2 = new RadarChartDataSet();
        radarDataSet2.setLabel("My Second Dataset");
        radarDataSet2.setFill(true);
        radarDataSet2.setBackgroundColor("rgba(54, 162, 235, 0.2)");
        radarDataSet2.setBorderColor("rgb(54, 162, 235)");
        radarDataSet2.setPointBackgroundColor("rgb(54, 162, 235)");
        radarDataSet2.setPointBorderColor("#fff");
        radarDataSet2.setPointHoverBackgroundColor("#fff");
        radarDataSet2.setPointHoverBorderColor("rgb(54, 162, 235)");
        List<Number> dataVal2 = new ArrayList<>();
        dataVal2.add(28);
        dataVal2.add(48);
        dataVal2.add(40);
        dataVal2.add(19);
        dataVal2.add(96);
        dataVal2.add(27);
        dataVal2.add(100);
        radarDataSet2.setData(dataVal2);
         
        data.addChartDataSet(radarDataSet);
        data.addChartDataSet(radarDataSet2);
         
        List<String> labels = new ArrayList<>();
        labels.add("Eating");
        labels.add("Drinking");
        labels.add("Sleeping");
        labels.add("Designing");
        labels.add("Coding");
        labels.add("Cycling");
        labels.add("Running");
        data.setLabels(labels);
         
        /* Options */
        RadarChartOptions options = new RadarChartOptions();
        Elements elements = new Elements();
        ElementsLine elementsLine = new ElementsLine();
        elementsLine.setTension(0);
        elementsLine.setBorderWidth(3);
        elements.setLine(elementsLine);
        options.setElements(elements);
         
        radarModel.setOptions(options);
        radarModel.setData(data);
    }
     
    public void createRadarModel2() {
        radarModel2 = new RadarChartModel();
        ChartData data = new ChartData();
         
        RadarChartDataSet radarDataSet = new RadarChartDataSet();
        radarDataSet.setLabel("P.Practitioner");
        radarDataSet.setLineTension(0.1);
        radarDataSet.setBackgroundColor("rgba(102, 153, 204, 0.2)");
        radarDataSet.setBorderColor("rgba(102, 153, 204, 1)");
        radarDataSet.setPointBackgroundColor("rgba(102, 153, 204, 1)");
        radarDataSet.setPointBorderColor("#fff");
        radarDataSet.setPointHoverRadius(5);
        radarDataSet.setPointHoverBackgroundColor("#fff");
        radarDataSet.setPointHoverBorderColor("rgba(102, 153, 204, 1)");
        List<Number> dataVal = new ArrayList<>();
        dataVal.add(2);
        dataVal.add(3);
        dataVal.add(2);
        dataVal.add(1);
        dataVal.add(3);
        radarDataSet.setData(dataVal);
 
        RadarChartDataSet radarDataSet2 = new RadarChartDataSet();
        radarDataSet2.setLabel("P.Manager");
        radarDataSet2.setLineTension(0.1);
        radarDataSet2.setBackgroundColor("rgba(255, 204, 102, 0.2)");
        radarDataSet2.setBorderColor("rgba(255, 204, 102, 1)");
        radarDataSet2.setPointBackgroundColor("rgba(255, 204, 102, 1)");
        radarDataSet2.setPointBorderColor("#fff");
        radarDataSet2.setPointHoverRadius(5);
        radarDataSet2.setPointHoverBackgroundColor("#fff");
        radarDataSet2.setPointHoverBorderColor("rgba(255, 204, 102, 1)");
        List<Number> dataVal2 = new ArrayList<>();
        dataVal2.add(2);
        dataVal2.add(3);
        dataVal2.add(3);
        dataVal2.add(2);
        dataVal2.add(3);
        radarDataSet2.setData(dataVal2);
         
        data.addChartDataSet(radarDataSet);
        data.addChartDataSet(radarDataSet2);
         
        List<List<String>> labels = new ArrayList<>();
        labels.add(new ArrayList(Arrays.asList("Process","Excellence")));
        labels.add(new ArrayList(Arrays.asList("Problem","Solving")));
        labels.add(new ArrayList(Arrays.asList("Facilitation")));
        labels.add(new ArrayList(Arrays.asList("Project","Mgmt")));
        labels.add(new ArrayList(Arrays.asList("Change","Mgmt")));
        data.setLabels(labels);
 
        /* Options */
        RadarChartOptions options = new RadarChartOptions();
        RadialScales rScales = new RadialScales();
         
        RadialLinearAngleLines angelLines = new RadialLinearAngleLines();
        angelLines.setDisplay(true);
        angelLines.setLineWidth(0.5);
        angelLines.setColor("rgba(128, 128, 128, 0.2)");
        rScales.setAngelLines(angelLines);
         
        RadialLinearPointLabels pointLabels = new RadialLinearPointLabels();
        pointLabels.setFontSize(14);
        pointLabels.setFontStyle("300");
        pointLabels.setFontColor("rgba(204, 204, 204, 1)");
        pointLabels.setFontFamily("Lato, sans-serif");
         
        RadialLinearTicks ticks = new RadialLinearTicks();
        ticks.setBeginAtZero(true);
        ticks.setMaxTicksLimit(3);
        ticks.setMin(0);
        ticks.setMax(3);
        ticks.setDisplay(false);
 
        options.setScales(rScales);
         
        radarModel2.setOptions(options);
        radarModel2.setData(data);
        radarModel2.setExtender("skinRadarChart");
    }
    
    /*
    public void createBubbleModel() {
        bubbleModel = new BubbleChartModel();
        ChartData data = new ChartData();
         
        BubbleChartDataSet dataSet = new BubbleChartDataSet();
        List<BubblePoint> values = new ArrayList<>();
        values.add(new BubblePoint(20, 30, 15));
        values.add(new BubblePoint(40, 10, 10));
        dataSet.setData(values);
        dataSet.setBackgroundColor("rgb(255, 99, 132)");
        dataSet.setLabel("First Dataset");
        data.addChartDataSet(dataSet);
        bubbleModel.setData(data);
    }*/
     
    public void createMixedModel() {
        mixedModel = new BarChartModel();
        ChartData data = new ChartData();
         
        BarChartDataSet dataSet = new BarChartDataSet();
        List<Number> values = new ArrayList<>();
        values.add(10);
        values.add(20);
        values.add(30);
        values.add(40);
        dataSet.setData(values);
        dataSet.setLabel("Bar Dataset");
        dataSet.setBorderColor("rgb(255, 99, 132)");
        dataSet.setBackgroundColor("rgba(255, 99, 132, 0.2)");
         
        LineChartDataSet dataSet2 = new LineChartDataSet();
        List<Object> values2 = new ArrayList<>();
        values2.add(50);
        values2.add(50);
        values2.add(50);
        values2.add(50);
        dataSet2.setData(values2);
        dataSet2.setLabel("Line Dataset");
        dataSet2.setFill(false);
        dataSet2.setBorderColor("rgb(54, 162, 235)");
         
        data.addChartDataSet(dataSet);
        data.addChartDataSet(dataSet2);
         
        List<String> labels = new ArrayList<>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        data.setLabels(labels);
         
        mixedModel.setData(data);
         
        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
         
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
        mixedModel.setOptions(options);
    }
     
    public void createDonutModel() {
        donutModel = new DonutChartModel();
        ChartData data = new ChartData();
         
        DonutChartDataSet dataSet = new DonutChartDataSet();
       
        List<Number> values = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        
        Map<String, Double> info = ReadDashboardInfo.getInfo("doc-common");
        String[] rgb = {"rgb(255, 99, 132)","rgb(4, 70, 57)","rgb(75, 192, 192)","rgb(255, 205, 86)","rgb(201, 203, 207)","rgb(123, 118, 240)","rgb(54, 162, 235)","rgb(141, 137, 130)","rgb(204, 248, 239)","rgb(117, 165, 127)","rgb(180, 177, 170)"};
        int color = 0;
        for(String key : info.keySet()) {
        	values.add(info.get(key));
        	bgColors.add(rgb[color++]);
        	labels.add(key);
        }
        dataSet.setData(values);
        dataSet.setBackgroundColor(bgColors);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        donutModel.setData(data);
    }
 
    public void itemSelect(ItemSelectEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item selected",
                "Item Index: " + event.getItemIndex() + ", DataSet Index:" + event.getDataSetIndex());
 
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
     
    public PieChartModel getPieModel() {
        return pieModel;
    }
 
    public void setPieModel(PieChartModel pieModel) {
        this.pieModel = pieModel;
    }
 
    public PolarAreaChartModel getPolarAreaModel() {
        return polarAreaModel;
    }
 
    public void setPolarAreaModel(PolarAreaChartModel polarAreaModel) {
        this.polarAreaModel = polarAreaModel;
    }
 
    public LineChartModel getLineModel() {
        return lineModel;
    }
 
    public void setLineModel(LineChartModel lineModel) {
        this.lineModel = lineModel;
    }
 
    public LineChartModel getCartesianLinerModel() {
        return cartesianLinerModel;
    }
 
    public void setCartesianLinerModel(LineChartModel cartesianLinerModel) {
        this.cartesianLinerModel = cartesianLinerModel;
    }
     
    public BarChartModel getBarModel() {
        return barModel;
    }
 
    public void setBarModel(BarChartModel barModel) {
        this.barModel = barModel;
    }
 
    public BarChartModel getBarModel2() {
        return barModel2;
    }
 
    public void setBarModel2(BarChartModel barModel2) {
        this.barModel2 = barModel2;
    }
 
    public HorizontalBarChartModel getHbarModel() {
        return hbarModel;
    }
 
    public void setHbarModel(HorizontalBarChartModel hbarModel) {
        this.hbarModel = hbarModel;
    }
 
    public BarChartModel getStackedBarModel() {
        return stackedBarModel;
    }
 
    public void setStackedBarModel(BarChartModel stackedBarModel) {
        this.stackedBarModel = stackedBarModel;
    }
 
    public BarChartModel getStackedGroupBarModel() {
        return stackedGroupBarModel;
    }
 
    public void setStackedGroupBarModel(BarChartModel stackedGroupBarModel) {
        this.stackedGroupBarModel = stackedGroupBarModel;
    }
 
    public RadarChartModel getRadarModel() {
        return radarModel;
    }
 
    public void setRadarModel(RadarChartModel radarModel) {
        this.radarModel = radarModel;
    }
 
    public RadarChartModel getRadarModel2() {
        return radarModel2;
    }
 
    public void setRadarModel2(RadarChartModel radarModel2) {
        this.radarModel2 = radarModel2;
    }
 
    public BubbleChartModel getBubbleModel() {
        return bubbleModel;
    }
 
    public void setBubbleModel(BubbleChartModel bubbleModel) {
        this.bubbleModel = bubbleModel;
    }
 
    public BarChartModel getMixedModel() {
        return mixedModel;
    }
 
    public void setMixedModel(BarChartModel mixedModel) {
        this.mixedModel = mixedModel;
    }
 
    public DonutChartModel getDonutModel() {
        return donutModel;
    }
 
    public void setDonutModel(DonutChartModel donutModel) {
        this.donutModel = donutModel;
    }
}

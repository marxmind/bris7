package com.italia.marxmind.bris.application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.italia.marxmind.bris.enm.Bris;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 06/19/2019
 *
 */
public class RCDReader {

	private static final String XML_FILE = Bris.PRIMARY_DRIVE.getName() + Bris.SEPERATOR.getName() + 
			Bris.APP_FOLDER.getName() + Bris.SEPERATOR.getName() +
			Bris.REPORT.getName() + Bris.SEPERATOR.getName() + "xml" + Bris.SEPERATOR.getName() + "Poblacion-LakeSebu" + Bris.SEPERATOR.getName() + "Hector Umek-2019-06-001.xml";
	
	private static final String XML_FILE_FOLDER = Bris.PRIMARY_DRIVE.getName() + Bris.SEPERATOR.getName() + 
			Bris.APP_FOLDER.getName() + Bris.SEPERATOR.getName() +
			Bris.APP_CONFIG_FLDR.getName() + Bris.SEPERATOR.getName();
	
	/*
	 * private static final String APPLICATION_FILE = Bris.PRIMARY_DRIVE.getName() +
	 * Bris.SEPERATOR.getName() + Bris.APP_FOLDER.getName() +
	 * Bris.SEPERATOR.getName() + Bris.APP_CONFIG_FLDR.getName() +
	 * Bris.SEPERATOR.getName() + Bris.APP_CONFIG_FILE.getName();
	 */
	
	public static void main(String[] args) {
		RCDReader rcd = RCDReader.readXML(XML_FILE);
		/*
		 * System.out.println(rcd.getBrisFile());
		 * System.out.println(rcd.getDateCreated()); System.out.println(rcd.getFund());
		 * System.out.println(rcd.getAccountablePerson());
		 * System.out.println(rcd.getSeriesReport());
		 * System.out.println(rcd.getBeginningBalancesAmount());
		 * System.out.println(rcd.getAddAmount());
		 * System.out.println(rcd.getLessAmount());
		 * System.out.println(rcd.getBalanceAmount());
		 * System.out.println(rcd.getCertificationPerson());
		 * System.out.println(rcd.getVerifierPerson());
		 * System.out.println(rcd.getDateVerified());
		 * 
		 * for(RCDFormDetails f : rcd.getRcdFormDtls()) {
		 * System.out.println(f.getFormId()); System.out.println(f.getName());
		 * System.out.println(f.getSeriesFrom()); System.out.println(f.getSeriesTo());
		 * System.out.println(f.getAmount()); }
		 * 
		 * for(RCDFormSeries s : rcd.getRcdFormSeries()) {
		 * System.out.println(s.getId()); System.out.println(s.getName());
		 * 
		 * System.out.println(s.getBeginningQty());
		 * System.out.println(s.getBeginningFrom());
		 * System.out.println(s.getBeginningTo());
		 * 
		 * System.out.println(s.getReceiptQty());
		 * System.out.println(s.getReceiptFrom()); System.out.println(s.getReceiptTo());
		 * 
		 * System.out.println(s.getIssuedQty()); System.out.println(s.getIssuedFrom());
		 * System.out.println(s.getIssuedTo());
		 * 
		 * System.out.println(s.getEndingQty()); System.out.println(s.getEndingFrom());
		 * System.out.println(s.getEndingTo());
		 * System.out.println("==========================="); }
		 */
		
		//RCDReader.saveXML(rcd, "test", XML_FILE_FOLDER);
		saveXML(rcd, "testXML", XML_FILE_FOLDER);
	}
	
	public static void saveXML(RCDReader rcd, String fileName, String fileSaveLocation) {
		try {
				
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("rcd");
		root.addElement("bris-file").addText(rcd.getBrisFile());
		root.addElement("date-created").addText(rcd.getDateCreated());
		root.addElement("fund").addText(rcd.getFund());
		root.addElement("accountable-person").addText(rcd.getAccountablePerson());
		root.addElement("series-report").addText(rcd.getSeriesReport());
		root.addElement("beginning-balances").addText(rcd.getBeginningBalancesAmount());
		root.addElement("add").addText(rcd.getAddAmount());
		root.addElement("less").addText(rcd.getLessAmount());
		root.addElement("balance").addText(rcd.getBalanceAmount());
		root.addElement("certification-person").addText(rcd.getCertificationPerson());
		root.addElement("verification-person").addText(rcd.getVerifierPerson());
		root.addElement("date-verified").addText(rcd.getDateVerified());
		root.addElement("treasurer").addText(rcd.getTreasurer());
		
		Element formDetails = root.addElement("form-details");
		int id=0;
		for(RCDFormDetails f : rcd.getRcdFormDtls()) {
			Element form = formDetails.addElement("form").addAttribute("id", id+"");
			form.addElement("name").addText(f.getName());
			form.addElement("series-from").addText(f.getSeriesFrom());
			form.addElement("series-to").addText(f.getSeriesTo());
			form.addElement("amount").addText(f.getAmount());
			id++;		
		}
		
		Element formSeries = root.addElement("form-series");
		id=0;
		for(RCDFormSeries s : rcd.getRcdFormSeries()) {
			Element form = formSeries.addElement("line").addAttribute("id", id+"");
			form.addElement("name").addText(s.getName());
			
			form.addElement("beginning-qty").addText(s.getBeginningQty());
			form.addElement("beginning-from").addText(s.getBeginningFrom());
			form.addElement("beginning-to").addText(s.getBeginningTo());
			
			form.addElement("receipt-qty").addText(s.getReceiptQty());
			form.addElement("receipt-from").addText(s.getReceiptFrom());
			form.addElement("receipt-to").addText(s.getReceiptTo());
			
			form.addElement("issued-qty").addText(s.getIssuedQty());
			form.addElement("issued-from").addText(s.getIssuedFrom());
			form.addElement("issued-to").addText(s.getIssuedTo());
			
			form.addElement("ending-qty").addText(s.getEndingQty());
			form.addElement("ending-from").addText(s.getEndingFrom());
			form.addElement("ending-to").addText(s.getEndingTo());
			
			id++;
		}
		
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(
				  new FileWriter(new File(fileSaveLocation + fileName)), format);
				writer.write(document);
				writer.close();
		}catch(IOException e) {}
	}
	
	private String brisFile="";
	private String dateCreated="";
	private String fund="";
	private String accountablePerson="";
	private String seriesReport="";
	private List<RCDFormDetails> rcdFormDtls = Collections.synchronizedList(new ArrayList<RCDFormDetails>()); 
	private List<RCDFormSeries> rcdFormSeries = Collections.synchronizedList(new ArrayList<RCDFormSeries>());
	private String beginningBalancesAmount="";
	private String addAmount="";
	private String lessAmount="";
	private String balanceAmount="";
	private String certificationPerson="";
	private String verifierPerson="";
	private String dateVerified="";
	private String treasurer="";
	
	
	/**
	 * Old saving xml
	 * @param rcd
	 * @param fileName
	 * @param fileSaveLocation
	 * @return
	 */
	public static String saveXMLOld(RCDReader rcd, String fileName, String fileSaveLocation) {
		System.out.println("saving xml");
		try {
		File file = new File(fileSaveLocation);
		
		if(!file.isDirectory()) {
			file.mkdir();
		}
		
		String tags="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		
		tags += "<rcd>" +"\n";
		tags += "\t<bris-file>"+rcd.getBrisFile() +"</bris-file>\n";
		tags += "\t<date-created>"+rcd.getDateCreated()+"</date-created>\n";
		tags += "\t<fund>"+rcd.getFund()+"</fund>\n";
		tags += "\t<accountable-person>"+rcd.getAccountablePerson()+"</accountable-person>\n";
		tags += "\t<series-report>"+rcd.getSeriesReport()+"</series-report>\n";
		tags += "\t<beginning-balances>"+rcd.getBeginningBalancesAmount()+"</beginning-balances>\n";
		tags += "\t<add>"+rcd.getAddAmount()+"</add>\n";
		tags += "\t<less>"+rcd.getLessAmount()+"</less>\n";
		tags += "\t<balance>"+rcd.getBalanceAmount()+"</balance>\n";
		tags += "\t<certification-person>"+rcd.getCertificationPerson()+"</certification-person>\n";
		tags += "\t<verification-person>"+rcd.getVerifierPerson()+"</verification-person>\n";
		tags += "\t<date-verified>"+rcd.getDateVerified()+"</date-verified>\n";
		tags += "\t<treasurer>"+rcd.getTreasurer()+"</treasurer>\n";
		
		tags += "\t<form-details>" +"\n";
		int id=0;
		for(RCDFormDetails f : rcd.getRcdFormDtls()) {
			tags += "\t\t<form id=\""+ id +"\">\n";
			tags +="\t\t\t<name>"+f.getName()+"</name>\n";
			tags +="\t\t\t<series-from>"+f.getSeriesFrom()+"</series-from>\n";
			tags +="\t\t\t<series-to>"+f.getSeriesTo()+"</series-to>\n";
			tags +="\t\t\t<amount>"+f.getAmount()+"</amount>\n";
			tags +="\t\t</form>\n";
			id++;
		}
		tags += "\t</form-details>" +"\n";
		
		tags += "\t<form-series>" +"\n";
		id=0;
		for(RCDFormSeries s : rcd.getRcdFormSeries()) {
			tags += "\t\t<line id=\""+ id +"\">\n";
			tags +="\t\t\t<name>"+ s.getName()+"</name>\n";
			
			tags +="\t\t\t<beginning-qty>"+ s.getBeginningQty()+"</beginning-qty>\n";
			tags +="\t\t\t<beginning-from>"+ s.getBeginningFrom()+"</beginning-from>\n";
			tags +="\t\t\t<beginning-to>"+ s.getBeginningTo()+"</beginning-to>\n";
			
			tags +="\t\t\t<receipt-qty>"+ s.getReceiptQty()+"</receipt-qty>\n";
			tags +="\t\t\t<receipt-from>"+ s.getReceiptFrom()+"</receipt-from>\n";
			tags +="\t\t\t<receipt-to>"+ s.getReceiptTo()+"</receipt-to>\n";
			
			tags +="\t\t\t<issued-qty>"+ s.getIssuedQty()+"</issued-qty>\n";
			tags +="\t\t\t<issued-from>"+ s.getIssuedFrom()+"</issued-from>\n";
			tags +="\t\t\t<issued-to>"+ s.getIssuedTo()+"</issued-to>\n";
			
			tags +="\t\t\t<ending-qty>"+ s.getEndingQty()+"</ending-qty>\n";
			tags +="\t\t\t<ending-from>"+ s.getEndingFrom()+"</ending-from>\n";
			tags +="\t\t\t<ending-to>"+ s.getEndingTo()+"</ending-to>\n";
			
			tags +="\t\t</line>\n";
			id++;
		}
		tags += "\t</form-series>" +"\n";
		tags += "</rcd>" +"\n";
		
		
		
		File xml = new File(fileSaveLocation + fileName + ".xml");
		PrintWriter pw = new PrintWriter(new FileWriter(xml));
		pw.println(tags);
		pw.flush();
		pw.close();
		System.out.println("completed saving xml");
		
		return tags;
		}catch(Exception e) {}
		return null;
	}
	
	public static RCDReader readXML(String xml) {
		RCDReader rcd = new RCDReader();
		try {
		
		
		File file = new File(xml);
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		
		List<Node> bris = document.selectNodes("/rcd");
		for(Node node : bris) {
			rcd.setBrisFile(node.selectSingleNode("bris-file").getText());
			rcd.setDateCreated(node.selectSingleNode("date-created").getText());
			rcd.setFund(node.selectSingleNode("fund").getText());
			rcd.setAccountablePerson(node.selectSingleNode("accountable-person").getText());
			rcd.setSeriesReport(node.selectSingleNode("series-report").getText());
			
			rcd.setBeginningBalancesAmount(node.selectSingleNode("beginning-balances").getText());
			rcd.setAddAmount(node.selectSingleNode("add").getText());
			rcd.setLessAmount(node.selectSingleNode("less").getText());
			rcd.setBalanceAmount(node.selectSingleNode("balance").getText());
			rcd.setCertificationPerson(node.selectSingleNode("certification-person").getText());
			rcd.setVerifierPerson(node.selectSingleNode("verification-person").getText());
			rcd.setDateVerified(node.selectSingleNode("date-verified").getText());
			rcd.setTreasurer(node.selectSingleNode("treasurer").getText());
		}
		
//////////////////FORM DETAILS/////////////////////////
		List<Node> nodes = document.selectNodes("/rcd/form-details/form");
		List<RCDFormDetails> frmDtls = Collections.synchronizedList(new ArrayList<RCDFormDetails>());
		for(Node node : nodes) {
			RCDFormDetails d = new RCDFormDetails();
			d.setFormId(node.valueOf("@id"));
			d.setName(node.selectSingleNode("name").getText());
			d.setSeriesFrom(node.selectSingleNode("series-from").getText());
			d.setSeriesTo(node.selectSingleNode("series-to").getText());
			d.setAmount(node.selectSingleNode("amount").getText());
			frmDtls.add(d);
		}
		rcd.setRcdFormDtls(frmDtls);

		///////////////////SERIES////////////////////////////////
		List<RCDFormSeries> srsDtls = Collections.synchronizedList(new ArrayList<RCDFormSeries>());
		List<Node> nod = document.selectNodes("/rcd/form-series/line");
		for(Node node : nod) {
			RCDFormSeries d = new RCDFormSeries();
			d.setId(node.valueOf("@id"));
			d.setName(node.selectSingleNode("name").getText());
			
			d.setBeginningQty(node.selectSingleNode("beginning-qty").getText());
			d.setBeginningFrom(node.selectSingleNode("beginning-from").getText());
			d.setBeginningTo(node.selectSingleNode("beginning-to").getText());
			
			d.setReceiptQty(node.selectSingleNode("receipt-qty").getText());
			d.setReceiptFrom(node.selectSingleNode("receipt-from").getText());
			d.setReceiptTo(node.selectSingleNode("receipt-to").getText());
			
			d.setIssuedQty(node.selectSingleNode("issued-qty").getText());
			d.setIssuedFrom(node.selectSingleNode("issued-from").getText());
			d.setIssuedTo(node.selectSingleNode("issued-to").getText());
			
			d.setEndingQty(node.selectSingleNode("ending-qty").getText());
			d.setEndingFrom(node.selectSingleNode("ending-from").getText());
			d.setEndingTo(node.selectSingleNode("ending-to").getText());
			
			srsDtls.add(d);
		}
		rcd.setRcdFormSeries(srsDtls);
		
		
		}catch(DocumentException e) {}
		return rcd;
	}

	public String getBrisFile() {
		if(brisFile==null) {
			brisFile="";
		}
		return brisFile;
	}

	public String getDateCreated() {
		if(dateCreated==null) {
			dateCreated="";
		}
		return dateCreated;
	}

	public String getFund() {
		if(fund==null) {
			fund = "";
		}
		return fund;
	}

	public String getAccountablePerson() {
		if(accountablePerson==null) {
			accountablePerson="";
		}
		return accountablePerson;
	}

	public String getSeriesReport() {
		if(seriesReport==null) {
			seriesReport="";
		}
		return seriesReport;
	}

	public List<RCDFormDetails> getRcdFormDtls() {
		return rcdFormDtls;
	}

	public List<RCDFormSeries> getRcdFormSeries() {
		return rcdFormSeries;
	}

	public String getBeginningBalancesAmount() {
		if(beginningBalancesAmount==null) {
			beginningBalancesAmount="";
		}
		return beginningBalancesAmount;
	}

	public String getAddAmount() {
		if(addAmount==null) {
			addAmount="";
		}
		return addAmount;
	}

	public String getLessAmount() {
		if(lessAmount==null) {
			lessAmount="";
		}
		return lessAmount;
	}

	public String getBalanceAmount() {
		if(balanceAmount==null) {
			balanceAmount="";
		}
		return balanceAmount;
	}

	public String getCertificationPerson() {
		if(certificationPerson==null) {
			certificationPerson="";
		}
		return certificationPerson;
	}

	public String getVerifierPerson() {
		if(verifierPerson==null) {
			verifierPerson="";
		}
		return verifierPerson;
	}

	public String getDateVerified() {
		if(dateVerified==null) {
			dateVerified="";
		}
		return dateVerified;
	}

	public void setBrisFile(String brisFile) {
		this.brisFile = brisFile;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setFund(String fund) {
		this.fund = fund;
	}

	public void setAccountablePerson(String accountablePerson) {
		this.accountablePerson = accountablePerson;
	}

	public void setSeriesReport(String seriesReport) {
		this.seriesReport = seriesReport;
	}

	public void setRcdFormDtls(List<RCDFormDetails> rcdFormDtls) {
		this.rcdFormDtls = rcdFormDtls;
	}

	public void setRcdFormSeries(List<RCDFormSeries> rcdFormSeries) {
		this.rcdFormSeries = rcdFormSeries;
	}

	public void setBeginningBalancesAmount(String beginningBalancesAmount) {
		this.beginningBalancesAmount = beginningBalancesAmount;
	}

	public void setAddAmount(String addAmount) {
		this.addAmount = addAmount;
	}

	public void setLessAmount(String lessAmount) {
		this.lessAmount = lessAmount;
	}

	public void setBalanceAmount(String balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public void setCertificationPerson(String certificationPerson) {
		this.certificationPerson = certificationPerson;
	}

	public void setVerifierPerson(String verifierPerson) {
		this.verifierPerson = verifierPerson;
	}

	public void setDateVerified(String dateVerified) {
		this.dateVerified = dateVerified;
	}
	
	public String getTreasurer() {
		if(treasurer==null) {
			treasurer="";
		}
		return treasurer;
	}

	public void setTreasurer(String treasurer) {
		this.treasurer = treasurer;
	}
}

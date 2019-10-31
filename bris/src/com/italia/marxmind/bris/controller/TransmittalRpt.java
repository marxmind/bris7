package com.italia.marxmind.bris.controller;

/**
 * 
 * @author Mark Italia
 * @since 08/02/2018
 * @version 1.0
 *
 */
public class TransmittalRpt {
	
	private long id;
	private String dvDate;
	private String dvNumber;
	private String checkDate;
	private String checkNumber;
	private String payee;
	private String amount;
	private String pbcDate;
	private String pbcNumber;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDvDate() {
		return dvDate;
	}
	public void setDvDate(String dvDate) {
		this.dvDate = dvDate;
	}
	public String getDvNumber() {
		return dvNumber;
	}
	public void setDvNumber(String dvNumber) {
		this.dvNumber = dvNumber;
	}
	public String getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(String checkDate) {
		this.checkDate = checkDate;
	}
	public String getCheckNumber() {
		return checkNumber;
	}
	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}
	public String getPayee() {
		return payee;
	}
	public void setPayee(String payee) {
		this.payee = payee;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getPbcDate() {
		return pbcDate;
	}
	public void setPbcDate(String pbcDate) {
		this.pbcDate = pbcDate;
	}
	public String getPbcNumber() {
		return pbcNumber;
	}
	public void setPbcNumber(String pbcNumber) {
		this.pbcNumber = pbcNumber;
	}
	
}

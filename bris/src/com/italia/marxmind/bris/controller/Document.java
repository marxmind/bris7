package com.italia.marxmind.bris.controller;

import java.util.List;

/**
 * 
 * @author mark italia
 * @since 02/06/2018
 * @version 1.0
 *
 */
public class Document {

	private int id;
	private String name;
	
	private List<Document> listDocs;
	private Clearance clearance;
	private Customer customer;

	
	
	public List<Document> getListDocs() {
		return listDocs;
	}

	public void setListDocs(List<Document> listDocs) {
		this.listDocs = listDocs;
	}

	public Clearance getClearance() {
		return clearance;
	}

	public void setClearance(Clearance clearance) {
		this.clearance = clearance;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}

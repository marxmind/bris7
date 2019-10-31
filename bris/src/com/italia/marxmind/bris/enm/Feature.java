package com.italia.marxmind.bris.enm;

/**
 * 
 * @author mark italia
 * @since 08/05/2017
 * @version 1.0
 *
 */

public enum Feature {

	CLEARANCE("CLEARANCE"),
	ID_GENERATION("ID GENERATION"),
	BUSINESS("BUSINESS"),
	ASSISTANT("ASSISTANT"),
	GRAPH("GRAPH"),
	ORGANIZATION("ORGANIZATION"),
	CHEQUE("CHEQUE"),
	MOE_METER("MOE METER"),
	BLOTTERS("BLOTTERS"),
	PROFILE_DIRECTORY("PROFILE DIRECTORY"),
	CITIZEN_REGISTRATION("CITIZEN REGISTRATION"),
	APPLICATION_USER("APPLICATION USER"),
	EMPLOYEE("EMPLOYEE"),
	OR_LISTING("OR LISTING"),
	CEDULA_LISTING("CEDULA LISTING"),
	PBC_BANK_CREATION("PBC BANK CREATION"),
	EXPLORER("FILE EXPLORER"),
	CEDULA("CEDULA"),
	ORLISTING("OR LISTING"),
	REPORTING("REPORTING");
	
	private String name;
	
	public String getName(){
		return name;
	}
	
	private Feature(String name){
		this.name = name;
	}
	
}

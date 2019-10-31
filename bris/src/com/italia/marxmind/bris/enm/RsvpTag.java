package com.italia.marxmind.bris.enm;

public enum RsvpTag {
	ID("id"),
	RSVP("rsvp"),
	DESC("description"),
	START_DATE("startDate"),
	END_DATE("endDate");
	
	private String name;
	
	
	private RsvpTag(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
}

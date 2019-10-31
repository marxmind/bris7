package com.italia.marxmind.bris.enm;


/**
 * 
 * @author mark italia
 * @since 03/06/2018
 * @version 1.0
 *
 */
public enum CaseStatus {

	NEW(1, "NEW"),
	ON_HOLD(2,"ON-HOLD"),
	IN_PROGRESS(3,"IN-PROGRESS"),
	MOVED_IN_HIGHER_COURT(4,"ENDORSED TO HIGHER COURT"),
	SETTLED(5,"SETTLED"),
	CANCELLED(6,"CANCELLED");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private CaseStatus(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(CaseStatus type : CaseStatus.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return CaseStatus.NEW.getName();
	}
	public static int typeId(String name){
		for(CaseStatus type : CaseStatus.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return CaseStatus.NEW.getId();
	}
	
}


package com.italia.marxmind.bris.enm;

/**
 * 
 * @author mark italia
 * @since 09/16/2017
 * @version 1.0
 *
 */
public enum BlotterStatus {

	NEW(1, "NEW"),
	ON_HOLD(2,"ON-HOLD"),
	IN_PROGRESS(3,"IN-PROGRESS"),
	MOVED_IN_HIGHER_COURT(4,"ENDORSED TO HIGHER COURT"),
	CLOSED(5,"SETTLED");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private BlotterStatus(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(BlotterStatus type : BlotterStatus.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return BlotterStatus.NEW.getName();
	}
	public static int typeId(String name){
		for(BlotterStatus type : BlotterStatus.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return BlotterStatus.NEW.getId();
	}
	
}


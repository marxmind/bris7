package com.italia.marxmind.bris.enm;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 06-12-2019
 *
 */
public enum FormStatus {

	HANDED(1, "HANDED"),
	ALL_ISSUED(2, "ALL ISSUED"),
	NOT_ALL_ISSUED(3, "NOT ALL ISSUED"),
	ENCODED(4, "ENCODED"),
	CANCELLED(5, "CANCELLED");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private FormStatus(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String nameId(int id){
		
		for(FormStatus type : FormStatus.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		
		return FormStatus.HANDED.getName();
	}
	
	public static int idName(String name){
		
		for(FormStatus type : FormStatus.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		
		return FormStatus.HANDED.getId();
	}
	
}


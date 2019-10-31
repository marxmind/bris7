package com.italia.marxmind.bris.enm;

/**
 * 
 * @author mark italia
 * @since 03/24/2018
 * @version 1.0
 *
 */

public enum IraType {

	IRA(1, "IRA"),
	INCOME(2,"INCOME"),
	ADD_BUDGET(3, "ADDITIONAL BUDGET");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private IraType(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(IraType type : IraType.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return IraType.IRA.getName();
	}
	public static int typeId(String name){
		for(IraType type : IraType.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return IraType.IRA.getId();
	}
	
}

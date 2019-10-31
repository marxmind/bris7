package com.italia.marxmind.bris.enm;
/**
 * 
 * @author mark italia
 * @since 01/22/2018
 * @version 1.0
 *
 */
public enum LandTypes {

	HOME_LOT(1, "Home Lot"),
	AGRICULTURAL(2, "Agricultural Lot"),
	COMMERCIAL(3, "Commercial Lot"),
	INDUSTRIAL(4, "Industrial Lot");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private LandTypes(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(LandTypes type : LandTypes.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return LandTypes.HOME_LOT.getName();
	}
	public static int typeId(String name){
		for(LandTypes type : LandTypes.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return LandTypes.HOME_LOT.getId();
	}
	
	
}

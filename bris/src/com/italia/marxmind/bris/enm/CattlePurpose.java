package com.italia.marxmind.bris.enm;

/**
 * 
 * @author mark italia
 * @since 10/23/2017
 * @version 1.0
 *
 */
public enum CattlePurpose {

	NA(0, "N/A"),
	SELL(1, "TO SELL"),
	TRANSFER_OWNER(2,"TRANSFER OF OWNERSHIP"),
	TRAVEL_PERMIT(3, "TRAVEL PERMIT");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private CattlePurpose(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(CattlePurpose type : CattlePurpose.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return CattlePurpose.NA.getName();
	}
	public static int typeId(String name){
		for(CattlePurpose type : CattlePurpose.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return CattlePurpose.NA.getId();
	}
	
}

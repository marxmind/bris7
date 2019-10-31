package com.italia.marxmind.bris.enm;

/**
 * 
 * @author mark italia
 * @since 10/23/2017
 * @version 1.0
 *
 */
public enum CattleKind {
	
	NA(0, "N/A"),
	COW(1,"COW"),
	CARABAO(2, "CARABAO"),
	HORSE(3, "HORSE"),
	OTHER(4, "OTHERS");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private CattleKind(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(CattleKind type : CattleKind.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return CattleKind.NA.getName();
	}
	public static int typeId(String name){
		for(CattleKind type : CattleKind.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return CattleKind.NA.getId();
	}
	
}


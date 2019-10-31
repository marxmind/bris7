package com.italia.marxmind.bris.enm;

public enum IncidentType {
	
	MORTGAGE(1, "NOT PAYING FOR MORTGAGE"),
	LOAN_REPORT(2,"NOT PAYING FOR LOAN AND LIKES"),
	DEATH_THREAT(3, "DEATH THREAT"),
	ACQUIRED_APPLIANCES(4, "NOT PAYING FOR APPLIANCES"),
	ACQUIRED_VEHICLES(5, "NOT PAYING FOR VEHICLES"),
	ACQUIRED_LAND(6, "NOT PAYING FOR LAND"),
	HOME_RENTING(7, "NOT PAYING FOR RENTING A HOUSE"),
	VEHICLE_RENTING(8, "NOT PAYING FOR RENTING A VEHICLE"),
	WRONG_INFO(9, "DELIVERING A WRONG NEWS OR INFORMATION TO THE PUBLIC"),
	FIGHTING_AND_INJURED(10, "FIGHTING AND INJURED"),
	RAPE(11,"RAPE CASE"),
	VALUABLE_THINGS(12,"STEALING OF VALUABLE THINGS"),
	ACCUSED_WRONG_PERSON(13, "VICTIMS OF WRONG ACCUSATIONS"),
	VALUABLE_FIRING(14, "BURNING OF VALUABLE THINGS AND LIKES"),
	BULLYING(15,"BULLYING"),
	LIFE_THREATENING(16,"THREATENING OF LIFE"),
	HUMAN_ASSAULT(17, "HUMAN ASSAULT"),
	ANIMAL_BITE(18,"ANIMAL BITE"),
	SEXUAL_ASSAULT(19,"SEXUAL ASSAULT"),
	VEHICULAR_ACCIDENT(20, "VEHICULAR ACCIDENT"),
	DEMAND_RETURN_PROPERTY(21, "DEMAND FOR RETURN OF PROPERTY"),
	;
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private IncidentType(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(IncidentType type : IncidentType.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return IncidentType.MORTGAGE.getName();
	}
	public static int typeId(String name){
		for(IncidentType type : IncidentType.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return IncidentType.MORTGAGE.getId();
	}
}
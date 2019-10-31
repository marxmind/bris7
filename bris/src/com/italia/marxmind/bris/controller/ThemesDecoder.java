package com.italia.marxmind.bris.controller;

import com.italia.marxmind.bris.security.SecureChar;

/**
 * 
 * @author mark italia
 * @since 09/18/2017
 * @version 1.0
 *
 */
public class ThemesDecoder {
	
	public static void main(String[] args) {
		//System.out.println(ThemesDecoder.themeEncode(1));
		//System.out.println(ThemesDecoder.themeDecode("luna-amber"));
		
		//to endoce use util in com.italia.marxmind.bris.security
		//SecureChar.encode(); method
		System.out.println(ThemesDecoder.decodeEncryptedThemes("bWFyeG1pbmRsdW5hbWFyay1hbWJlcml0YWxpYQ=="));
	}
	
	public static String themeEncode(int id){
		return getThemes()[id].split("<*>")[1];
	}
	
	/**
	 * 
	 * @param decoded value from themes.xml
	 * @return
	 */
	public static String decodeEncryptedThemes(String themes){
		themes = SecureChar.decode(themes);
		themes = themes.replace("mark", "");
		themes = themes.replace("rivera", "");
		themes = themes.replace("italia", "");
		themes = themes.replace("bris", "");
		themes = themes.replace("marxmind", "");
		return themes;
	}
	
	/**
	 * 
	 * @param decode value in application.xml
	 * @return
	 */
	public static String themeDecode(String code){
		String themeName = "";
		for(String val : getThemes()){
			String xcode = val.split("<*>")[1];
			String encodedThemes = val.split("<*>")[0];
			
			if(code.equalsIgnoreCase(xcode)){
				
				encodedThemes = encodedThemes.replace("mark", "");
				encodedThemes = encodedThemes.replace("rivera", "");
				encodedThemes = encodedThemes.replace("italia", "");
				encodedThemes = encodedThemes.replace("bris", "");
				encodedThemes = encodedThemes.replace("marxmind", "");
				encodedThemes = encodedThemes.replace("<*>", "");
				encodedThemes = encodedThemes.replace("<*", "");
				encodedThemes = encodedThemes.replace("*>", "");
				encodedThemes = encodedThemes.replace("*", "");
				encodedThemes = encodedThemes.replace("<", "");
				encodedThemes = encodedThemes.replace(">", "");
				themeName = encodedThemes;
			}
			
		}
		return themeName;
	}
	
	private static String[] getThemes(){
		String[] themes = {"brisaftermarxminddarkmarxmind<*>145239",
				"marxmindafteritalianoonmarxmind<*>246240",
				"marxmindaftermarxmindworkrivera<*>347241",
				"italiaariitaliastomark<*>448242",
				"brisblack-marxmindtierivera<*>549243",
				"marxmindblitriverazerbris<*>650244",
				"riverabluemarkskymark<*>751245",
				"riverabootbrisstraprivera<*>852246",
				"italiacasabrisblancamarxmind<*>953247",
				"riveracrumarxmindzeitalia<*>1054248",
				"riveracupermarxmindtinomarxmind<*>1155249",
				"brisdark-markhivebris<*>1256250",
				"italiademarkltarivera<*>1357251",
				"brisdot-marxmindluvmark<*>1458252",
				"marxmindeggbrisplantbris<*>1559253",
				"brisexcite-marxmindbikemark<*>1660254",
				"brisflitaliaickmark<*>1761255",
				"marxmindglassmarxmind-xrivera<*>1862256",
				"brishoitaliameitalia<*>1963257",
				"italiahot-brissneaksmark<*>2064258",
				"italiahumabrisnitymarxmind<*>2165259",
				"italiale-brisfrogbris<*>2266260",
				"italiamidriveranightbris<*>2367261",
				"brismint-riverachocmarxmind<*>2468262",
				"markovermarxmindcastrivera<*>2569263",
				"markpepper-marxmindgrinderitalia<*>2670264",
				"markreditaliamondmarxmind<*>2771265",
				"marxmindrockriveraetbris<*>2872266",
				"marksamarkmitalia<*>2973267",
				"brissmootmarkhnessmarxmind<*>3074268",
				"riverasouth-marxmindstreetmarxmind<*>3175269",
				"marxmindstamarxmindrtmark<*>3276270",
				"marxmindsunmarxmindnyrivera<*>3377271",
				"italiaswanky-markpursebris<*>3478272",
				"italiatronriveratasticmarxmind<*>3579273",
				"riverauimarxmind-darknessrivera<*>3680274",
				"riverauimark-lightnessmark<*>3781275",
				"marxmindvadbrisermarxmind<*>3882276",
				"marxmindadminbrisitalia<*>3882277",
				"marxmindlunamark-amberitalia<*>3882278",
				"brisluna-italiabluemark<*>3882279",
				"italialuna-brisgreenmark<*>3882280",
				"marxmindluna-italiapinkbris<*>3882281",
				"marknova-italiacoloredmarxmind<*>3882282",
				"marknovaitalia-darkmarxmind<*>3882283",
				"marknovabris-lightmarxmind<*>3882284"};
		
		return themes;
	}
	
}

package utils;

import play.Play;

/**
 * 用于读取配置文件信息
 * @author woderchen
 *
 */
public class ConfigHelper {

//	private static Config cheboleConfig;
//	
//	static{
//		cheboleConfig =  ConfigFactory.load("chebole.properties");
//	}
//	
//	public static String getString(String key){
//		return cheboleConfig.getString(key);
//	}
	
	public static String getString(String key){
		return Play.application().configuration().getString(key);
	}
	
}

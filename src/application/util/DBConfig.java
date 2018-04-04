package application.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 数据库配置文件读取方法
 * 
 * @author Allen.Wang
 *
 */
public class DBConfig {

	protected static Logger logger = LogManager.getLogger(DBConfig.class);
	private String driver;
	private String url;
	private String userName;
	private String password;

	public DBConfig() {
		//配置文件工具集
		Config config = new Config();
		this.driver = config.get("driver");
		this.url = config.get("url");
		this.userName = config.get("username");
		this.password = config.get("password");

	}

	public String getDriver() {
		return driver;
	}

	public String getUrl() {
		return url;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

}
package application.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 操作配置文件工具类
 * @author diyagea- Allen.Wang
 * 
 * 默认属性
 * projectPath=项目根目录
 * configPath=/resources/config/
 * fileName=config.ini
 */
public class Config {
	// 项目路径
	private String projectPath = System.getProperty("user.dir").replace("\\", "/");
	// 配置文件路径
	private String configPath = "/resources/config/";
	// 配置文件名称
	private String fileName = "config.ini";

	// 对象初始化
	private File f = null;
	private Properties properties = null;
	private FileInputStream fis = null;
	private OutputStream fos = null;

	public static void main(String[] args) {
		Config c = new Config();
		System.out.println(c.projectPath);
	}

	/**
	 * 默认构造器
	 */
	public Config() {
		properties = new Properties();
		try {
			f = new File(projectPath + configPath + fileName);
			if(!f.exists()){
				f.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 文件名构造器
	 */
	public Config(String name) {
		properties = new Properties();
		try {
			f = new File(projectPath + configPath + name);
			if(!f.exists()){
				f.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 路径构造器
	 */
	public Config(String filePath, String fileName) {
		properties = new Properties();
		try {
			f = new File(filePath + fileName);
			if(!f.exists()){
				f.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		try {
			fis = new FileInputStream(f);
			properties.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				fis.close();
				fis = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Object object = properties.get(key);
		return object.toString();
	}

	/**
	 * 设置
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		try {
			fos = new FileOutputStream(f);
			properties.setProperty(key, value);
			properties.store(fos, null);
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				fos.close();
				fos = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

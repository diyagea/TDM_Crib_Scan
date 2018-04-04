package application.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import application.modal.User;
import application.util.Common;
import application.util.JdbcHelper;

/**
 * userDAO
 * @author diyagea- Allen.Wang
 *
 */
@SuppressWarnings({ "static-access", "rawtypes" })
public class UserDAO {

	JdbcHelper jdbc = new JdbcHelper();
	
	/**
	 * user login
	 * @return true / false
	 */
	public boolean checkUser(String username, String password) throws SQLException{
		//by username
		String passwordDB = (String) jdbc.getSingle("SELECT PASSWORD FROM TCS_USER WHERE USERNAME=?", username);
		if(password.equals(passwordDB)){
			return true;
		}
		return false;
	}
	
	/**
	 * query user 
	 * type: 0-id, 1-username, 2-pincode
	 * @return user object
	 */
	public User queryUser(int id, String key, int type) throws SQLException{
		User u = null;
		String sql = "";
		switch(type){
			case 0:
				sql = "SELECT * FROM TCS_USER WHERE ID=? ";
				break;
			case 1:
				sql = "SELECT * FROM TCS_USER WHERE USERNAME=? ";
				break;
			case 2:
				sql = "SELECT * FROM TCS_USER WHERE PINCODE=? ";
				break;
		}
		
		List results = jdbc.query(sql, key);
		for(Object o : results){
			 u = convertMapToUser((Map)o);
		}
		return u;
	}
	/**
	 * query all user for table
	 * @return
	 * @throws Exception
	 */
	public List<User> getAll() throws Exception {
		List<User> userList = new ArrayList<User>();
		List mapList = jdbc.query("SELECT * FROM TCS_USER WHERE TYPE != 0 ");
		for(Object o : mapList){
			userList.add(convertMapToUser((Map) o));
		}
		return userList;
	}
	
	/**
	 * add new user
	 * @param u
	 * @throws Exception
	 */
	public int add(User u) throws Exception {
		//max pincode in database
		//Object pincode = jdbc.getSingle("SELECT MAX(PINCODE)+1 FROM TCS_USER");
		//Object pincode = Common.getTimestamp();
		
		String sql = "INSERT INTO TCS_USER (TYPE, PINCODE, USERNAME, PASSWORD, TDMINITIALS, NAME, DEPARTMENT, WORK, MOBILE, EMAIL, INFO, INFO2) VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ";
		Object key = jdbc.update(sql, u.getType(), u.getPincode(), u.getUsername(), u.getPassword(), u.getTdmInitials(), u.getName(), u.getDepartment(), u.getWork(), u.getMobile(), u.getEmail(), u.getInfo(), u.getInfo2());
		return Common.objectToInt(key);
	}

	/**
	 * update user info
	 * @param u
	 * @return modify line in database table
	 * @throws Exception
	 */
	public int update(User u) throws Exception {
		String sql = "UPDATE TCS_USER SET TYPE=?,USERNAME=?, PASSWORD=?, TDMINITIALS=?, PINCODE=?, NAME=?, DEPARTMENT=?, WORK=?, MOBILE=?, EMAIL=?, INFO=?, INFO2=? WHERE USERID=? ";
		return jdbc.update(sql,u.getType(),u.getUsername(), u.getPassword(), u.getTdmInitials(), u.getPincode(), u.getName(), u.getDepartment(), u.getWork(), u.getMobile(), u.getEmail(), u.getInfo(), u.getInfo2(), u.getUserID());
	}

	/**
	 * delete user by username
	 * @param u
	 * @return modify line in database table
	 * @throws Exception
	 */
	public int delete(User u) throws Exception {
		String sql = "DELETE FROM TCS_USER WHERE USERNAME=? ";
		return jdbc.update(sql, u.getUsername());
	}
	

	/**
	 * convert data map to user object
	 * @param data map
	 * @return user object
	 */
	private User convertMapToUser(Map m){
		User u = new User(
				 Common.objectToInt(m.get("USERID")), 
				 Common.objectToInt(m.get("TYPE")),
				 Common.objectToString(m.get("PINCODE")), 
				 Common.objectToString(m.get("USERNAME")), 
				 Common.objectToString(m.get("PASSWORD")), 
				 Common.objectToString(m.get("TDMINITIALS")), 
				 Common.objectToString(m.get("NAME")), 
				 Common.objectToString(m.get("DEPARTMENT")), 
				 Common.objectToString(m.get("WORK")), 
				 Common.objectToString(m.get("MOBILE")), 
				 Common.objectToString(m.get("EMAIL")),
				 Common.objectToString(m.get("INFO")), 
				 Common.objectToString(m.get("INFO2"))
				);
		
		return u;
	}
	

}

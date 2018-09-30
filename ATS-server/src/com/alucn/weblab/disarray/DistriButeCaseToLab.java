package com.alucn.weblab.disarray;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import com.alucn.casemanager.server.common.CaseConfigurationCache;
import com.alucn.casemanager.server.common.ConfigProperites;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.model.ServerMate;
import com.alucn.casemanager.server.common.model.ServerType;
import com.alucn.casemanager.server.common.util.HttpReq;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.casemanager.server.common.util.SendMail;
import com.alucn.weblab.service.CaseSearchService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DistriButeCaseToLab {
	public static Logger logger = Logger.getLogger(DistriButeCaseToLab.class);

	String specialRelease = "28A,28C,28F,28G,28H";
	
	int counterUninsRS=0;
	Map<String, Integer> idleNum = new HashMap<String, Integer>();
	Map<String, String> reInstallFailList = new HashMap<String, String>();
	boolean reInstallNext = true;
	private DistriButeCaseToLab(){};

	static DistriButeCaseToLab instance = null;

	public static DistriButeCaseToLab getDistriButeCaseToLab() {
		if (instance == null) {
			instance = new DistriButeCaseToLab();
		}
		return instance;
	}

	/**
	 * <pre>
	 * Example: GetServerInfoFromDB();
	 * Description: 从数据库里面获取当前server
	 * Arguments: null
	 * Return: JSONArray 返回所有数据库内的server
	 * Variable：null
	 * </pre>
	 */
	private JSONArray GetServerInfoFromDB() {

		Connection connection = null;
		Statement state = null;

		JSONArray ServerInfos = new JSONArray();
		try {
			Class.forName("org.sqlite.JDBC");
			String CaseInfoDB = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
			connection = DriverManager.getConnection("jdbc:sqlite:" + CaseInfoDB);
			state = connection.createStatement();
			String query_sql = "select * from serverList;";
			ResultSet result = state.executeQuery(query_sql);
			ResultSetMetaData metaData = result.getMetaData();
			int ColumnCount = metaData.getColumnCount();
			while (result.next()) {
				JSONObject ServerInfo = new JSONObject();
				for (int i = 1; i <= ColumnCount; i++) {
					ServerInfo.put(metaData.getColumnName(i), result.getString(i));
				}
				ServerInfos.add(ServerInfo);
			}

		} catch (SQLException e1) {
			logger.error(e1);
		} catch (Exception e2) {
			logger.error(e2);

		} finally {
			try {
				if (state != null) {
					state.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e3) {

				logger.error(e3);
			}

		}
		return ServerInfos;

	}

	private boolean IsInJSONArray(Object value, JSONArray list) {
		for (int i = 0; i < list.size(); i++) {
			if (String.valueOf(value).equals(String.valueOf(list.get(i)))) {
				return true;
			}
		}
		return false;
	}

	private boolean isSameJSONArrayWithJSONArray(JSONArray value, JSONArray list) {
		if (value.size() != list.size()) {
			return false;
		}
		for (int i = 0; i < value.size(); i++) {
			if (!IsInJSONArray(value.getString(i), list)) {
				return false;
			}
		}
		return true;
	}

	private boolean IsInJSONArrayWithoutCase(Object value, JSONArray list) {
		for (int i = 0; i < list.size(); i++) {
			String A_value = String.valueOf(list.get(i));
			if (A_value.equalsIgnoreCase(String.valueOf(value))
					|| A_value.equalsIgnoreCase(String.valueOf(value) + "DB")
					|| A_value.equalsIgnoreCase(String.valueOf(value) + "RTDB")
					|| A_value.equalsIgnoreCase(String.valueOf(value) + "V7")
					|| A_value.equalsIgnoreCase(String.valueOf(value) + "7")) {
				return true;
			}
		}
		return false;
	}

	private boolean isLabListContainsCaseList(JSONArray A, JSONArray B) {
		for (int i = 0; i < B.size(); i++) {
			if (!IsInJSONArrayWithoutCase(B.getString(i), A)) {
				return false;
			}
		}
		return true;
	}

	private int postionInJSONArray(Object value, JSONArray list) {
		for (int i = 0; i < list.size(); i++) {
			String A_value = String.valueOf(list.get(i));
			if (A_value.equalsIgnoreCase(String.valueOf(value))) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * <pre>
	 * Example: updateKvmDB();
	 * Description: 去内存中获取server信息，同步到数据库，
	 * Arguments: null
	 * Return: JSONArray 返回更新过的server
	 * Variable：
	 * </pre>
	 */
	private JSONArray updateKvmDB() {
		JSONArray changedList = new JSONArray();
		JSONArray changedKvmList = new JSONArray();
		JSONArray needDeleteKvmList = new JSONArray();
		JSONArray Servers = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true,
				null);
		JSONArray ServersInDB = GetServerInfoFromDB();
		JSONObject ServerDB;
		JSONObject ServerMem;
		boolean IsExist;
		for (int i = 0; i < ServersInDB.size(); i++) {
			ServerDB = ServersInDB.getJSONObject(i);
			IsExist = false;
			for (int j = 0; j < Servers.size(); j++) {
				ServerMem = Servers.getJSONObject(j).getJSONObject(Constant.LAB);
				if (ServerDB.getString("serverName").equals(ServerMem.getString(Constant.SERVERNAME))) {
					IsExist = true;
					if (!ServerDB.getString("protocol").equals(ServerMem.getString(Constant.SERVERPROTOCOL))) {
						changedKvmList.add(ServerMem);
						changedList.add(ServerMem.getString(Constant.SERVERNAME));
						break;
					}
					if (!isSameJSONArrayWithJSONArray(JSONArray.fromObject(ServerDB.getString("SPA")),
							ServerMem.getJSONArray(Constant.SERVERSPA))) {
						changedKvmList.add(ServerMem);
						changedList.add(ServerMem.getString(Constant.SERVERNAME));
						break;
					}
					if (!isSameJSONArrayWithJSONArray(JSONArray.fromObject(ServerDB.getString("RTDB")),
							ServerMem.getJSONArray(Constant.SERVERRTDB))) {
						changedKvmList.add(ServerMem);
						changedList.add(ServerMem.getString(Constant.SERVERNAME));
						break;
					}
				}
			}
			if (!IsExist) {
				needDeleteKvmList.add(ServerDB.getString("serverName"));
				changedList.add(ServerDB.getString("serverName"));
			}

		}

		for (int i = 0; i < Servers.size(); i++) {
			ServerMem = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
			IsExist = false;
			for (int j = 0; j < ServersInDB.size(); j++) {
				if (ServersInDB.getJSONObject(j).getString("serverName")
						.equals(ServerMem.getString(Constant.SERVERNAME))) {
					IsExist = true;
					break;
				}
			}
			if (!IsExist) {
				changedKvmList.add(ServerMem);
				changedList.add(ServerMem.getString(Constant.SERVERNAME));
			}
		}

		if (needDeleteKvmList.size() > 0 || changedKvmList.size() > 0) {
			Connection connection = null;
			Statement state = null;

			try {
				Class.forName("org.sqlite.JDBC");
				String CaseInfoDB = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
				connection = DriverManager.getConnection("jdbc:sqlite:" + CaseInfoDB);

				if (needDeleteKvmList.size() > 0) {
					PreparedStatement prep = connection
							.prepareStatement("delete from serverList where serverName = ?;");

					for (int i = 0; i < needDeleteKvmList.size(); i++) {
						prep.setString(1, needDeleteKvmList.getString(i));
						prep.addBatch();
					}

					connection.setAutoCommit(false);
					prep.executeBatch();
					connection.setAutoCommit(true);
				}

				if (changedKvmList.size() > 0) {
					PreparedStatement prep = connection
							.prepareStatement("replace into serverList values( ?, ?, ?, ?);");

					for (int i = 0; i < changedKvmList.size(); i++) {
						ServerMem = changedKvmList.getJSONObject(i);

						prep.setString(1, ServerMem.getString(Constant.SERVERNAME));
						prep.setString(2, ServerMem.getString(Constant.SERVERPROTOCOL));
						prep.setString(3, ServerMem.getJSONArray(Constant.SERVERSPA).toString());
						prep.setString(4, ServerMem.getJSONArray(Constant.SERVERRTDB).toString());
						// prep.setString(5, ServerMem.getStatus());
						// prep.setString(5, "");
						prep.addBatch();
					}

					connection.setAutoCommit(false);
					prep.executeBatch();
					connection.setAutoCommit(true);
				}

			} catch (SQLException e1) {
				logger.error(e1);
			} catch (Exception e2) {
				logger.error(e2);

			} finally {
				try {
					if (state != null) {
						state.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e3) {

					logger.error(e3);
				}

			}
		}

		return changedList;
	}

	/*
	 * private boolean isNumeric(String str){ Pattern pattern =
	 * Pattern.compile("[0-9]*"); return pattern.matcher(str).matches(); }
	 */
	/**
	 * <pre>
	 * Example: GetReleaseList();
	 * Description: 从一个网站上获取版本信息
	 * Arguments: null
	 * Return: JSONArray 版本信息集合
	 * Variable：
	 * </pre>
	 */
	private JSONArray GetReleaseList() {
		JSONArray releaseArray = new JSONArray();
		URL url;
		try {
			url = new URL("http://135.251.249.250/hg/SurepayDraft/rawfile/tip/.info/TagConfig.json");
			InputStream inputStream = null;
			InputStreamReader inputStreamReader = null;
			BufferedReader reader = null;
			String tempLine, response = "";
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = connection.getInputStream();
				inputStreamReader = new InputStreamReader(inputStream);
				reader = new BufferedReader(inputStreamReader);

				while ((tempLine = reader.readLine()) != null) {
					response += tempLine;

				}

				JSONObject tagInfos = JSONObject.fromObject(response);
				JSONArray SingleList = tagInfos.getJSONArray("single");
				for (int i = 0; i < SingleList.size(); i++) {
					if (SingleList.getJSONObject(i).getString("name").equals("release")) {
						releaseArray = SingleList.getJSONObject(i).getJSONArray("value");
						break;
					}
				}

			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return releaseArray;
	}

	/**
	 * <pre>
	 * Example: RemoveRelease(list);
	 * Description: 从静态变量list的release内剔除传入的list内的release
	 * Arguments: JSONArray 要剔除的release的集合
	 * Return: JSONArray
	 * Variable：specialRelease
	 * </pre>
	 */
	private JSONArray RemoveRelease(JSONArray list) {

		JSONArray jsonList = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			String temp = String.valueOf(list.get(i)).replaceAll("\\d\\w+$", "");
			for (int j = 0; j < specialRelease.split(",").length; j++) {
				if (temp.endsWith(specialRelease.split(",")[j])) {
					temp = temp.substring(temp.indexOf(specialRelease.split(",")[j]), temp.length());
				}
			}
			jsonList.add(temp);
		}

		return jsonList;
	}

	/**
	 * <pre>
	 * Example: 
	 * Description: 
	 * Arguments: 
	 * Return: void
	 * Variable：
	 * </pre>
	 */
	private void UpdateCaseStatusDB(JSONArray caseList) {
		Connection connection = null;
		Statement state = null;

		Connection connection_DftCaseDB = null;
		Statement state_DftCaseDB = null;

		String QuerySql = null;
		String CaseInfoDB = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		String DftCaseDB = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		String caseListStr = caseList.toString().replace("\"", "'").replace("[", "(").replace("]", ")");
		int GroupId = 0;
		try {
			Class.forName("org.sqlite.JDBC");

			connection = DriverManager.getConnection("jdbc:sqlite:" + CaseInfoDB);
			connection_DftCaseDB = DriverManager.getConnection("jdbc:sqlite:" + DftCaseDB);
			state = connection.createStatement();
			state_DftCaseDB = connection_DftCaseDB.createStatement();

			QuerySql = "select max(group_id) from toDistributeCases where case_name not in " + caseListStr;
			try {
				ResultSet result = state.executeQuery(QuerySql);
				while (result.next()) {
					try {
						GroupId = result.getInt("max(group_id)");
					} catch (Exception e) {
						GroupId = 0;
						logger.error(e);
					}

				}
			} catch (Exception e) {
				logger.error(e);
				GroupId = 0;

			}

			QuerySql = "select D.case_name, D.base_data, D.special_data, D.lab_number, D.mate, D.customer, D.release, D.porting_release, C.SPA, C.DB as RTDB, C.SecData as second_data "
					+ "from " + CaseSearchService.dataBase
					+ " as D, CaseDepends as C where D.case_name = C.case_name and D.case_name in " + caseListStr
					+ " order by D.lab_number, D.mate, D.special_data,  D.base_data, C.SecData, D.case_name;";
			// int change_num = state.executeUpdate(UpdateSql);
			// logger.error(QuerySql);
			String caseName;
			String old_lab_number = "INIT", new_lab_number;
			String old_mate = "INIT", new_mate;
			String old_special_data = "INIT", new_special_data;
			String old_base_data = "INIT", new_base_data;
			String old_second_data = "INIT", new_second_data;
			String SPA, RTDB;
			String customer, release, porting_release;
			JSONArray spaArray, rtdbArray;
			boolean IsSame = true;

			JSONArray releaseList = GetReleaseList();
			logger.debug("releaseList: " + releaseList.toString());

			JSONArray Servers = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,
					true, null);
			ResultSet result2 = null;
			if (CaseSearchService.dataBase.equals("DailyCase")) {
				result2 = state.executeQuery(QuerySql);
			} else {
				result2 = state_DftCaseDB.executeQuery(QuerySql);
			}

			PreparedStatement prep = connection
					.prepareStatement("replace into toDistributeCases values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

			boolean isExcute = false;

			while (result2.next()) {
				caseName = result2.getString("case_name");
				new_lab_number = result2.getString("lab_number");
				new_mate = result2.getString("mate");
				new_special_data = result2.getString("special_data");
				new_base_data = result2.getString("base_data");
				new_second_data = caseName.split("/")[0] + "/" + result2.getString("second_data");
				customer = result2.getString("customer");
				release = result2.getString("release");
				porting_release = result2.getString("porting_release");
				SPA = result2.getString("SPA");
				RTDB = result2.getString("RTDB");
				// lineMate = result2.getString("LineMate");
				// groupMate = result2.getString("GroupMate");
				spaArray = JSONArray.fromObject(SPA.split(","));
				rtdbArray = JSONArray.fromObject(RTDB.split(","));
				JSONArray kvmList = new JSONArray();
				String serverName = null;
				Map<String, Set<Map<String, JSONObject>>> serversMap = getServers(Servers);
				JSONArray infos = new JSONArray();

				if (new_mate.equals(Constant.MATEN)) {
					if (new_lab_number.equals("2")) {// L/G >=3 do not
						for (String key : serversMap.keySet()) {
							Iterator<Map<String, JSONObject>> iterator = serversMap.get(key).iterator();
							while (iterator.hasNext()) {
								for (JSONObject value : iterator.next().values()) {
									if (ServerType.LINE.getName()
											.equals(value.getJSONObject(Constant.LAB).getString(Constant.SERVERTYPE))) {
										infos.add(value);
									}
								}
							}
						}
						serverName = gerServer(infos, customer, release, porting_release, releaseList, spaArray,
								rtdbArray);
					} else if (new_lab_number.equals("1")) {// lab 1
						JSONArray infosLine = new JSONArray();
						for (String key : serversMap.keySet()) {
							Iterator<Map<String, JSONObject>> iterator = serversMap.get(key).iterator();
							while (iterator.hasNext()) {
								for (JSONObject value : iterator.next().values()) {
									if (ServerType.STANDALONE.getName()
											.equals(value.getJSONObject(Constant.LAB).getString(Constant.SERVERTYPE))
											&& ServerMate.N.getName().equals(
													value.getJSONObject(Constant.LAB).getString(Constant.SERVERMATE))) {
										infos.add(value);// Standalone
									}
									if (ServerType.LINE.getName()
											.equals(value.getJSONObject(Constant.LAB).getString(Constant.SERVERTYPE))) {
										infosLine.add(value);// L/G nomate
									}
								}
							}
						}
						serverName = gerServer(infos, customer, release, porting_release, releaseList, spaArray,
								rtdbArray);
						if ("".equals(serverName)) {
							serverName = gerServer(infosLine, customer, release, porting_release, releaseList, spaArray,
									rtdbArray);
						}
					}
				} else if (new_mate.equals(Constant.MATEY)) {
					if (Integer.parseInt(new_lab_number) >= 3) {
						// if(groupMate.equals("N")){
						for (String key : serversMap.keySet()) {
							Iterator<Map<String, JSONObject>> iterator = serversMap.get(key).iterator();
							while (iterator.hasNext()) {
								for (JSONObject value : iterator.next().values()) {
									if (ServerType.LINE.getName()
											.equals(value.getJSONObject(Constant.LAB).getString(Constant.SERVERTYPE))
											&& ServerMate.PRIMARY.getName().equals(
													value.getJSONObject(Constant.LAB).getString(Constant.SERVERMATE))) {
										infos.add(value);
									}
								}
							}
						}
						serverName = gerServer(infos, customer, release, porting_release, releaseList, spaArray,
								rtdbArray);
						/*
						 * }else { JSONArray infos = new JSONArray(); for(String key :
						 * serversMap.keySet()){ Iterator<Map<String, JSONObject>> iterator =
						 * serversMap.get(key).iterator(); while(iterator.hasNext()){ for(JSONObject
						 * value : iterator.next().values()){
						 * if("L".equals(value.getJSONObject(Constant.BODY).getJSONObject(Constant.LAB).
						 * getString(Constant.SERVERTPYE)) &&
						 * "P".equals(value.getJSONObject(Constant.BODY).getJSONObject(Constant.LAB).
						 * getString(Constant.SERVERMATE))){ infos.add(value); } } } } serverName =
						 * gerServer(infos, customer, release, porting_release, releaseList, spaArray,
						 * rtdbArray); }
						 */
					}
				}
				if ("".equals(serverName)) {
					continue;
				}

				// logger.error("ServerName:"+serverName + "---" + "CaseName:"+caseName +
				// "new_lab_number:"+new_lab_number + "new_mate:"+new_mate +
				// "customer:"+customer + "release:"+release+
				// "porting_release:"+porting_release);
				kvmList.add(serverName);
				if (!new_lab_number.equals(old_lab_number)) {
					IsSame = false;
					// logger.error(old_lab_number + " --- " + new_lab_number);
				}
				if (!new_mate.equals(old_mate)) {
					IsSame = false;
					// logger.error(old_mate + " --- " + new_mate);
				}
				if (!new_special_data.equals(old_special_data)) {
					IsSame = false;
					// logger.error(old_special_data + " --- " +
					// new_special_data);
				}
				if (!new_base_data.equals(old_base_data)) {
					IsSame = false;
					// logger.error(old_base_data + " --- " + new_base_data);
				}
				if (!new_second_data.equals(old_second_data)) {
					IsSame = false;
					// logger.error(old_second_data + " --- " +
					// new_second_data);
				}

				if (!IsSame) {
					old_lab_number = new_lab_number;
					old_mate = new_mate;
					old_special_data = new_special_data;
					old_base_data = new_base_data;
					old_second_data = new_second_data;
					GroupId++;
					IsSame = true;
				}

				prep.setString(1, caseName);
				prep.setString(2, new_lab_number);
				prep.setString(3, new_mate);
				prep.setString(4, new_special_data);
				prep.setString(5, new_base_data);
				prep.setString(6, new_second_data);
				prep.setString(7, release);
				prep.setString(8, porting_release);
				prep.setString(9, spaArray.toString());
				prep.setString(10, rtdbArray.toString());
				prep.setString(11, kvmList.toString());
				prep.setString(12, customer);
				prep.setInt(13, GroupId);
				prep.addBatch();
				isExcute = true;
			}
			if (isExcute) {
				connection.setAutoCommit(false);
				prep.executeBatch();
				connection.setAutoCommit(true);
			}
		} catch (SQLException e1) {
			logger.error(e1);
		} catch (Exception e2) {
			logger.error(e2);

		} finally {
			try {
				if (state != null) {
					state.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e3) {

				e3.printStackTrace();
			}

		}
	}

	/**
	 * <pre>
	 * Example: getServers(infos)；
	 * Description: 解析JSONArray转换成map类型的server
	 * Arguments: infos 包含server信息的JSONArray
	 * Return: Map<String,Set<Map<String,JSONObject>>>
	 * Variable：none
	 * </pre>
	 */
	public Map<String, Set<Map<String, JSONObject>>> getServers(JSONArray infos) {
		Map<String, Set<Map<String, JSONObject>>> setMap = new HashMap<String, Set<Map<String, JSONObject>>>();
		for (int i = 0; i < infos.size(); i++) {
			JSONObject info = infos.getJSONObject(i);
			JSONObject lib = info.getJSONObject(Constant.LAB);
			String setName = lib.getString(Constant.SETNAME);
			String serverName = lib.getString(Constant.SERVERNAME);
			String mateServer = lib.getString(Constant.MATESERVER);
			if (setMap.get(setName) == null) {
				Set<Map<String, JSONObject>> serverSet = new HashSet<Map<String, JSONObject>>();
				if (mateServer.equals("N")) {
					Map<String, JSONObject> serverMap = new HashMap<String, JSONObject>();
					serverMap.put(serverName, info);
					serverSet.add(serverMap);
					setMap.put(setName, serverSet);
				} else {
					Map<String, JSONObject> mateMap = new HashMap<String, JSONObject>();
					mateMap.put(mateServer, info);
					serverSet.add(mateMap);
					setMap.put(setName, serverSet);
				}
			} else {
				Set<Map<String, JSONObject>> serverSet = setMap.get(setName);
				Iterator<Map<String, JSONObject>> iterator = serverSet.iterator();
				boolean isMate = false;
				while (iterator.hasNext()) {
					Map<String, JSONObject> mateOrServerMap = iterator.next();
					if (mateOrServerMap.get(serverName) != null) {
						JSONObject mateinfo = mateOrServerMap.get(serverName);
						mateOrServerMap.put(mateServer, mateinfo);
						mateOrServerMap.put(serverName, info);
						isMate = true;
					}
				}
				if (!isMate) {
					if (mateServer.equals("N")) {
						Map<String, JSONObject> serverMap = new HashMap<String, JSONObject>();
						serverMap.put(serverName, info);
						serverSet.add(serverMap);
						setMap.put(setName, serverSet);
					} else {
						Map<String, JSONObject> mateMap = new HashMap<String, JSONObject>();
						mateMap.put(mateServer, info);
						serverSet.add(mateMap);
						setMap.put(setName, serverSet);
					}
				}
			}
		}
		return setMap;
	}

	/**
	 * <pre>
	 * Example: 
	 * Description: 这个方法是干啥的？
	 * 				对比数据库中case的运行条件和现有内存中server的条件，看是否符合
	 * 				如果符合，返回server名称
	 * Arguments: 
	 * Return: String
	 * Variable：
	 * </pre>
	 */
	public String gerServer(JSONArray Servers, String customer, String release, String porting_release,
			JSONArray releaseList, JSONArray spaArray, JSONArray rtdbArray) {
		String serverName = "";
		String serverNameTmp = "";
		for (int i = 0; i < Servers.size(); i++) {
			JSONObject ServerMem = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
			serverName = ServerMem.getString(Constant.SERVERNAME);
			JSONArray spaList = RemoveRelease(ServerMem.getJSONArray(Constant.SERVERSPA));
			JSONArray rtdbList = RemoveRelease(ServerMem.getJSONArray(Constant.SERVERRTDB));
			String serverProtocol = ServerMem.getString(Constant.SERVERPROTOCOL);
			String serverRelease = ServerMem.getString(Constant.SERVERRELEASE);
			// logger.error("Server: " + spaList.toString() + " ---- " +
			// rtdbList.toString());
			// logger.error(" ---------------------------- " + serverName + "
			// ------------------------------");
			if (!isLabListContainsCaseList(spaList, spaArray)) {
				continue;
			}
			if (!isLabListContainsCaseList(rtdbList, rtdbArray)) {
				// logger.error("Server: " + rtdbList.toString() + "case: " +
				// rtdbArray.toString());
				continue;
			}
			if ((serverProtocol.equals("ANSI") && !customer.equalsIgnoreCase("VZW"))
					|| (serverProtocol.equals("ITU") && customer.equalsIgnoreCase("VZW"))) {
				// logger.error("serverProtocol: " + serverProtocol + " customer: " + customer);
				continue;
			}
			boolean isReleaseMath = false;
			// logger.error(serverRelease + " --- " + release);
			if (serverRelease.equals(release)) {
				isReleaseMath = true;
				return serverName;
			} else {
				JSONArray portingReleaseList = JSONArray
						.fromObject("[\"" + porting_release.replace("+", "").replace(",", "\",\"") + "\"]");
				// logger.error(serverRelease + " --- " + portingReleaseList);
				if (IsInJSONArray(serverRelease, portingReleaseList)) {
					isReleaseMath = true;
				} else {
					if (porting_release.endsWith("+")) {
						int serverReleasePostion = postionInJSONArray(serverRelease, releaseList);
						// logger.error(serverRelease + " --- " + releaseList);
						if (serverReleasePostion != -1) {
							int LastReleasePostion = postionInJSONArray(porting_release.substring(
									porting_release.lastIndexOf(",") + 1, porting_release.length() - 1), releaseList);
							if (LastReleasePostion != -1 && serverReleasePostion >= LastReleasePostion) {
								isReleaseMath = true;
							}
						}
					}
				}

			}

			if (isReleaseMath) {
				serverNameTmp = serverName;
			} else {
				// logger.debug("case_name: " + caseName + " Server: " + serverName);
				continue;
			}
		}

		return serverNameTmp;
	}

	public static void main(String[] args) throws Exception {
		DistriButeCaseToLab test = new DistriButeCaseToLab();
		// JSONArray getServerInfoFromDB = test.GetServerInfoFromDB();
		// System.out.println(getServerInfoFromDB);
		JSONObject distributeCases = test.GetDistributeCases();
		System.out.println(distributeCases);
		/*
		 * JSONArray releaseList = test.GetReleaseList();
		 * System.out.println(releaseList.toString()); String porting_release =
		 * "SP18.3,SP18.9+"; String serverRelease = "SP17.3"; boolean
		 * isReleaseMath=false; JSONArray portingReleaseList = JSONArray
		 * .fromObject("[\"" + porting_release.replace("+", "").replace(",", "\",\"") +
		 * "\"]"); System.out.println(portingReleaseList.toString()); if
		 * (test.IsInJSONArray(serverRelease, portingReleaseList)) { isReleaseMath =
		 * true; } else { if (porting_release.endsWith("+")) { int serverReleasePostion
		 * = test.postionInJSONArray(serverRelease, releaseList);
		 * //logger.error(serverRelease + " --- " + releaseList); if
		 * (serverReleasePostion != -1) {
		 * System.out.println(porting_release.substring(porting_release.lastIndexOf(",")
		 * + 1, porting_release.length() - 1)); int LastReleasePostion =
		 * test.postionInJSONArray(
		 * porting_release.substring(porting_release.lastIndexOf(",") + 1,
		 * porting_release.length() - 1), releaseList);
		 * System.out.println(serverReleasePostion); if (LastReleasePostion != -1 &&
		 * serverReleasePostion >= LastReleasePostion) { isReleaseMath = true; } } } }
		 */

	}

	private void UpdatedistributeDB(JSONArray KVMList) {
		JSONArray caseList = new JSONArray();

		Connection connection = null;
		Statement state = null;

		Connection connection_DftCaseDB = null;
		Statement state_DftCaseDB = null;

		try {
			Class.forName("org.sqlite.JDBC");
			String CaseInfoDB = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
			String DftCaseDB = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
			connection = DriverManager.getConnection("jdbc:sqlite:" + CaseInfoDB);
			connection_DftCaseDB = DriverManager.getConnection("jdbc:sqlite:" + DftCaseDB);
			state = connection.createStatement();
			state_DftCaseDB = connection_DftCaseDB.createStatement();
			String query_sql;
			if (KVMList.size() > 0) {
				query_sql = "select case_name from toDistributeCases where ";
				for (int i = 0; i < KVMList.size(); i++) {
					query_sql += "server like '%" + KVMList.getString(i) + "%' or ";
				}
				query_sql = query_sql.substring(0, query_sql.length() - 4) + ";";
				logger.debug(query_sql);
				ResultSet result = state.executeQuery(query_sql);
				while (result.next()) {
					caseList.add(result.getString("case_name"));
				}
			}

			if (CaseSearchService.sqlAdmin.equals("")) {
				query_sql = "select case_name from DailyCase where case_status = 'I' order by lab_number asc,special_data asc;";
				// query_sql = "select case_name from DailyCase where case_name not
				// in (select case_name from toDistributeCases);";
				ResultSet result2 = state.executeQuery(query_sql);
				while (result2.next()) {
					caseList.add(result2.getString("case_name"));
				}

				query_sql = "delete from toDistributeCases where case_name in (select case_name from toDistributeCases where case_name not in (select case_name from DailyCase where case_status = 'I'));";
				state.executeUpdate(query_sql);
			} else {
				try {
					String attachDatabase = "ATTACH '" + DftCaseDB + "' As dt;";
					state.execute(attachDatabase);
				} catch (Exception e) {
				} finally {
					String syncCaseDepends = "replace Into CaseDepends(case_name, SPA, DB, SecData) select case_name, SPA, DB, SecData from dt.CaseDepends;";
					state.execute(syncCaseDepends);
				}

				query_sql = CaseSearchService.sqlAdmin;
				if (query_sql.contains("DailyCase")) {
					// query_sql = "select case_name from DailyCase where case_name not
					// in (select case_name from toDistributeCases);";
					ResultSet result2 = state.executeQuery(query_sql);
					while (result2.next()) {
						caseList.add(result2.getString("case_name"));
					}

					query_sql = "delete from toDistributeCases where case_name in (select case_name from toDistributeCases where case_name not in ("
							+ CaseSearchService.sqlAdmin + "));";
					state.executeUpdate(query_sql);
				} else if (query_sql.contains("DftTag")) {
					ResultSet result2 = state_DftCaseDB.executeQuery(query_sql);
					String tmp = "";
					while (result2.next()) {
						tmp += "\"";
						caseList.add(result2.getString("case_name"));
						tmp += result2.getString("case_name");
						tmp += "\"";
						tmp += ",";
					}
					tmp = tmp.substring(0, tmp.length() - 1);
					query_sql = "delete from toDistributeCases where case_name in (select case_name from toDistributeCases where case_name not in ("
							+ tmp + "));";
					state.executeUpdate(query_sql);
				}
			}

			// caselist里面放的是曾经跑过的case但是现在serverinfo改动了，和一直没有跑的case
			if (caseList.size() > 0) {
				UpdateCaseStatusDB(caseList);
			}

		} catch (SQLException e1) {
			logger.error(e1);
		} catch (Exception e2) {

			logger.error(e2);
		} finally {
			try {
				if (state != null) {
					state.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e3) {

				e3.printStackTrace();
			}

		}
	}

	/**
	 * <pre>
	 * Example: genCaseListToLab('BJRMS21C');
	 * Description: 获取一定数量的case，从将要运行表（toDistributeCases）内，不在表（DistributedCaseTbl）内
	 * 		规则是group_id相同的一组，数量不大于配置文件中最大case数
	 * Arguments: ServerName server名称
	 * Return: JSONArray
	 * Variable：
	 * </pre>
	 */
	private JSONArray genCaseListToLab(String ServerName) {
		JSONArray caseList = new JSONArray();

		Connection connection = null;
		Statement state = null;
		int old_group = -1, new_group;
		try {
			Class.forName("org.sqlite.JDBC");
			String CaseInfoDB = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
			connection = DriverManager.getConnection("jdbc:sqlite:" + CaseInfoDB);
			state = connection.createStatement();
			/*
			 * String query_case_sql = "select case_name from DistributedCaseTbl;";
			 * ResultSet result2 = state.executeQuery(query_case_sql); JSONArray
			 * case_name_list = new JSONArray(); while (result2.next()) { String Case_name =
			 * result2.getString("case_name"); case_name_list.add(Case_name); }
			 * System.err.println("case_name_list:=========="+case_name_list.toString());
			 */
			String query_sql = "select case_name, group_id from toDistributeCases where server like '%" + ServerName
					+ "%' and case_name not in " + "(select distinct case_name from DistributedCaseTbl)"
					/*
					 * + case_name_list.toString().replace("\"", "'").replace("[", "(").replace("]",
					 * ")")
					 */
					+ " order by group_id";
			System.out.println("query_sql:========" + query_sql);
			ResultSet result = state.executeQuery(query_sql);
			int CaseCount = 0;
			while (result.next()) {
				// ↓↓↓↓↓↓↓↓↓↓↓只获取一组group_id 相同的一组case↓↓↓↓↓↓↓↓↓↓↓
				new_group = result.getInt("group_id");
				if (new_group != old_group) {
					if (old_group != -1) {
						break;
					}
					old_group = new_group;
				}
				// ↑↑↑↑↑↑↑↑↑↑↑只获取一组group_id 相同的一组case↑↑↑↑↑↑↑↑↑↑↑
				caseList.add(result.getString("case_name"));
				CaseCount++;
				if (CaseCount >= Integer.valueOf(ConfigProperites.getInstance().getMaxCaseSizeForOneLab())) {
					break;
				}

			}
			logger.info(ServerName + " case: " + query_sql);
			logger.info(ServerName + " case: " + caseList.toString());

		} catch (SQLException e1) {
			logger.error(e1);
		} catch (Exception e2) {
			logger.error(e2);

		} finally {
			try {
				if (state != null) {
					state.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e3) {

				e3.printStackTrace();
			}

		}
		return caseList;
	}

	/**
	 * <pre>
	 * Example: DistriButeCaseToLab.getDistriButeCaseToLab().GetDistributeCases().getJSONObject(Constant.AVAILABLECASE);
	 * Description:这个方法存在的意义？
	 * Arguments: none
	 * Return: JSONObject  {"availableCase":{}}
	 * Variable：
	 * </pre>
	 */
	public JSONObject GetDistributeCases() throws Exception {
		JSONObject AvailableCases = new JSONObject();
		JSONObject Cases = new JSONObject();
		// 更新serverlistinfo数据库使其里面的信息与程序缓存的server信息保持一致，并返回更改的servername
		JSONArray changedKvmList = updateKvmDB();
		// 找到曾经跑过，但是server信息更新了的case和没有跑过的case
		UpdatedistributeDB(changedKvmList);
		// 从内存中读取server信息
		JSONArray Servers = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true,null);
		JSONObject ServerMem;
		int idleServerNum = 0;
		int caseListNull = 0;
		//--------------------new-----------------------------------------
		for (int i = 0; i < Servers.size(); i++) {
			ServerMem = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
			String serverName = ServerMem.getString(Constant.SERVERNAME);
			if (Servers.getJSONObject(i).getJSONObject(Constant.TASKSTATUS).getString(Constant.STATUS).equals(Constant.CASESTATUSIDLE) && !ServerMem.getJSONArray(Constant.SERVERSPA).getString(0).equals("SPALIST_XXX")) {
				idleServerNum++;
				if (null == idleNum.get(serverName)){
					idleNum.put(serverName, 1);
				}else{
					int numTmp = idleNum.get(serverName)+1;
					idleNum.put(serverName, numTmp);
				}
				logger.debug("idle server: " + serverName);
				JSONArray caseList = genCaseListToLab(serverName);
				if(caseList.size()==0){
					caseListNull++;
				}
				JSONObject labInfo = new JSONObject();
				labInfo.put("uuid", UUID.randomUUID().toString());
				labInfo.put("case_list", caseList);
				DbOperation.UpdateDistributedCase(caseList, serverName);
				Cases.put(serverName, labInfo);
			}else{
				idleNum.put(serverName, 0);
			}
		}
		//--------------------new-----------------------------------------
		//--------------------old-----------------------------------------
		/*for (int i = 0; i < Servers.size(); i++) {
			// 判断是否是idle状态
			if (Servers.getJSONObject(i).getJSONObject(Constant.TASKSTATUS).getString(Constant.STATUS)
					.equals(Constant.CASESTATUSIDLE)) {
				idleServerNum++;
				ServerMem = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
				String serverName = ServerMem.getString(Constant.SERVERNAME);

				logger.debug("idle server: " + serverName);
				// idle server: BJRMS21C

				JSONArray caseList = genCaseListToLab(serverName);
				// select case_name from toDistributeCases where server like '%BJRMS21C%' or
				// server like '%BJRMS21D%' or server like '%BJRMS21A%' or server like
				// '%BJRMS21E%' or server like '%BJRMS21F%' or server like '%BJRMS21B%';
				// System.err.println("genCaseListToLab caseList:======="+caseList);
				// genCaseListToLab
				// caseList:=======["78232/fs8169.json","78232/fs8170.json","78232/fs8171.json","78232/fs8175.json","78232/fs8177.json","78232/fs8180.json","78232/fs8182.json","78232/fs9146.json","78232/fs9170.json","78232/fs9171.json","78232/fs9175.json","78232/fs9180.json","78232/fs9183.json","78232/fs9185.json","78232/fs9188.json","78232/ft0201.json","78232/ft0202.json","78232/ft0608.json","78232/ft0611.json"]
				if (caseList.size() == 0) {
					caseListNull++;
				}
				JSONObject labInfo = new JSONObject();
				labInfo.put("uuid", UUID.randomUUID().toString());
				labInfo.put("case_list", caseList);
				// 更新数据库内的分发表(只有两个字段，case_name，server_name)用的replace into方法
				DbOperation.UpdateDistributedCase(caseList, serverName);
				Cases.put(serverName, labInfo);
			}
		}*/
		//--------------------old-----------------------------------------
		// Original send email logic reservation,Change == to > never go.
		if (idleServerNum > Servers.size() && caseListNull == Servers.size()) {
			if (counterUninsRS == 8640) {
				logger.debug("send e-mail of Missing spa and rtdb!");
				counterUninsRS = 0;
				Map<String, Map<String, Map<String, Map<String, String>>>> uninstallRSANSI = getUninstallRS2(Servers,"ANSI");
				Map<String, Map<String, Map<String, Map<String, String>>>> uninstallRSITU = getUninstallRS2(Servers,"ITU");
				// send mail
				JSONArray cc_list = new JSONArray();
				JSONArray to_list = new JSONArray();
				cc_list.add("lei.k.huang@alcatel-lucent.com");
				cc_list.add("Haiqi.Wang@alcatel-lucent.com");
				to_list.add("chen.k.wang@nokia-sbell.com");
				SendMail.genReport(cc_list, to_list, uninstallRSANSI, uninstallRSITU);
			}
			counterUninsRS++;
		}
		//Automatically determine case dependencies and send installed requests.
		for(String serverName: idleNum.keySet()){
			int currentServerNum = idleNum.get(serverName);
			logger.debug(serverName+" of idle status num "+currentServerNum + "-- reInstallNext "+reInstallNext);
            if(currentServerNum > 10  && reInstallNext){
				reInstallNext = false;
				for (int i = 0; i < Servers.size(); i++) {
					JSONObject serverBody = Servers.getJSONObject(i);
					JSONObject serverMem = serverBody.getJSONObject(Constant.LAB);
					JSONObject serverStatus = serverBody.getJSONObject(Constant.TASKSTATUS);
					String serverName2 = serverMem.getString(Constant.SERVERNAME);
					if (serverName2.equals(serverName) && Servers.getJSONObject(i).getJSONObject(Constant.TASKSTATUS).getString(Constant.STATUS).equals(Constant.CASESTATUSIDLE)) {
						/*String serverProtocol = serverMem.getString(Constant.SERVERPROTOCOL);
						String serverRelease = serverMem.getString(Constant.SERVERRELEASE);
						JSONArray serverSpa = serverMem.getJSONArray(Constant.SERVERSPA);
						JSONArray serverRtdb = serverMem.getJSONArray(Constant.SERVERRTDB);
						JSONArray tempArray = new JSONArray();
						tempArray.add(Servers.getJSONObject(i));
						Map<String,Map<String,Map<String,Map<String,String>>>> unInstallRSANSI = getUninstallRS2(tempArray, serverProtocol);
						Set<String> unInstallSpa = unInstallRSANSI.get("SPA").get(serverName).get("SPA").keySet();
						Set<String> unInstallRtdb = unInstallRSANSI.get("RTDB").get(serverName).get("RTDB").keySet();
						if(unInstallSpa.size()==0 && unInstallRtdb.size()==0){*/
							logger.debug("no matching case can run on " + serverName +" need reinstall");
							Thread installLabThread = new Thread(new Runnable() {
								@Override
								public void run() {
									String reqData = "";
									try {
										if(null == reInstallFailList.get(serverName)){
											String[] cmd = new String[] { "/bin/sh", "-c", "python /home/huanglei/DB/QueryCaseDepends.py /home/huanglei/DB/caseinfo.db "+serverName };
								            Process ps = Runtime.getRuntime().exec(cmd);
								            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
								            StringBuffer sb = new StringBuffer();
								            String line;
								            while ((line = br.readLine()) != null) {
								                sb.append(line);
								            }
								            String resultCaseDependsString = sb.toString();
								            if ("".equals(resultCaseDependsString)){
								            	logger.warn("case server no case need runned...");
								            }else{
								            	JSONObject resultCaseDepends = JSONObject.fromObject(resultCaseDependsString.replace("u'", "'"));
									            reqData = "{\"protocol\": \""+resultCaseDepends.getString("protocl")+"\", \"labname\": [\""+serverName+"\"], \"DB\": "+resultCaseDepends.getString("DB")+", \"mate\": \"N\", \"release\": \""+resultCaseDepends.getString("release")+"\", \"SPA\": "+resultCaseDepends.getString("SPA")+", \"ins_flag\": \"1\"}";
								            }
										}else{
											reqData = reInstallFailList.get(serverName);
											reInstallFailList.remove(serverName);
										}
							            String resResult = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/certapi/certtask.json", reqData);
										logger.debug("lab reinstall... " + serverName+" response result "+resResult);
										if(Constant.REINSTALLLABOK.equals(resResult)){
											while(true){
												String installLabResult = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/labapi/dailylab/"+serverName+".json").getJSONArray("content").getJSONObject(0).getString("status");
												logger.debug("lab reinstall... " + serverName+" response is "+installLabResult);
												serverStatus.put(Constant.STATUS, installLabResult);
			                                    CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,serverBody);
												if(Constant.REINSTALLLABSUCCESS.equals(installLabResult)){
													idleNum.put(serverName, 0);
													reInstallNext = true;
													logger.debug("lab reinstall completed... " + serverName+" response result "+installLabResult);
													serverStatus.put(Constant.STATUS, Constant.CASESTATUSIDLE);
				                                    CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,serverBody);
													/*if(getProcess(serverName)){
														String[] cmd = new String[] { "/bin/sh", "-c", "sh /home/huanglei/ATC_"+serverName+"/start.sh"};
											            Runtime.getRuntime().exec(cmd);
														logger.debug("lab reinstall completed... " + serverName+" is started");
													}*/
													break;
												}else if(Constant.REINSTALLLABFAIL.equals(installLabResult)){
													logger.debug("lab reinstall completed... " + serverName+" response result "+installLabResult+" and add reqData :"+reqData+" to failed list");
													if (!"".equals(reqData)){
														reInstallFailList.put(serverName, reqData);
													}
													reInstallNext = true;
													JSONArray cc_list = new JSONArray();
													JSONArray to_list = new JSONArray();
													cc_list.add("Haiqi.Wang@alcatel-lucent.com");
													to_list.add("xiuyun.he@nokia-sbell.com");
													SendMail.genReport(cc_list, to_list, serverName, reqData);
													break;
												}
													Thread.sleep(100000);
											}
										}else{
											logger.debug("lab reinstall... " + serverName+" response result "+resResult +" and add reqData :"+reqData+" to failed list");
											reInstallNext = true;
											if (!"".equals(reqData)){
												reInstallFailList.put(serverName, reqData);
											}
										}
									} catch (Exception e) {
										logger.error("lab reinstall exception... " + serverName, e);
										if (!"".equals(reqData)){
											reInstallFailList.put(serverName, reqData);
										}
										reInstallNext = true;
									}
								}
							});
							installLabThread.start();
						/*}else{
							Iterator<String> iteratorSpa = unInstallSpa.iterator();
							while(iteratorSpa.hasNext()){
								serverSpa.add(iteratorSpa.next());
							}
							Iterator<String> iteratorRtdb = unInstallSpa.iterator();
							while(iteratorRtdb.hasNext()){
								serverRtdb.add(iteratorRtdb.next());
							}
							String reqData = "{'protocol': '"+serverProtocol+"', 'labname': ['"+serverName+"'], 'DB': "+serverRtdb.toString()+", 'mate': 'N', 'release': '"+serverRelease+"', 'SPA': "+unInstallSpa.toString()+"}";
							String resResult = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/certapi/certtask.json", reqData);
							while("OK".equals(resResult)){
								String installLabResult = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/labapi/dailylab/"+serverName+".json").getJSONArray("content").getJSONObject(0).getString("status");
								logger.debug("lab reinstall response is "+installLabResult);
								if("SUCCESS".equals(installLabResult) || !"Installing".equals(installLabResult)){
									idleNum.put(serverName, 0);
									logger.debug("lab reinstall completed... " + serverName+" response result "+installLabResult);
									break;
								}
								Thread.sleep(100000);
							}
						}*/
					}
				}
			}
			Thread.sleep(100000);
		}
		AvailableCases.put("availableCase", Cases);
		return AvailableCases;
	}

	/*private Map<String,Map<String,String>> getUninstallRS(JSONArray Servers, String protocol) throws Exception{
		Map<String,String> rtdbMap = new HashMap<String,String>();
		Map<String,String> spaMap = new HashMap<String,String>();
		Map<String,Map<String,String>> unInstallRS = new HashMap<String,Map<String,String>>();
		Map<String,String> unInstallRtdb = new HashMap<String,String>();
		Map<String,String> unInstallSpa = new HashMap<String,String>();
		String sql="SELECT DISTINCT SPA, RTDB FROM toDistributeCases WHERE server='[]'";
		for (int i = 0; i < Servers.size(); i++) {
			JSONObject serverMem=Servers.getJSONObject(i).getJSONObject(Constant.LAB);
			String serverProtocol = serverMem.getString(Constant.SERVERPROTOCOL);
			if(protocol.equals(serverProtocol)){
				JSONArray serverSpa = serverMem.getJSONArray(Constant.SERVERSPA);
				for (int m=0; m<serverSpa.size(); m++) {
					String serSpa = mattch(serverSpa.getString(m), "(^[A-Z,a-z]+)");
					if(!serSpa.equals("")){
						spaMap.put(serSpa, "");
					}else{
						continue;
					}
				}
				JSONArray serverRtdb = serverMem.getJSONArray(Constant.SERVERRTDB);
				for (int n=0; n<serverRtdb.size(); n++) {
					String serRtdb = mattch(serverRtdb.getString(n), "(^[A-Z,a-z]+)");
					if(!serRtdb.equals("")){
						rtdbMap.put(serRtdb, "");
					}else{
						continue;
					}
				}
			}
		}
		if(protocol.equals("ANSI")){
			sql+=" and customer='VZW'";
		}else{
			sql+=" and customer!='VZW'";
		}
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		ArrayList<HashMap<String, Object>> list = jdbc.query(sql);
		for (int i = 0; i < list.size(); i++) {
			JSONArray serverSpa = JSONArray.fromObject(list.get(i).get("SPA"));
			for (int m=0; m<serverSpa.size(); m++) {
				String serSpa = mattch(serverSpa.getString(m), "(^[A-Z,a-z]+)");
				if(!serSpa.equals("")){
					if(!spaMap.containsKey(serSpa)){
						unInstallSpa.put(serSpa,"");
					}
				}else{
					continue;
				}
			}
			
			JSONArray serverRtdb = JSONArray.fromObject(list.get(i).get("RTDB"));
			for (int n=0; n<serverRtdb.size(); n++) {
				String serRtdb = mattch(serverRtdb.getString(n), "(^[A-Z,a-z]+)");
				if(!serRtdb.equals("")){
					if(!rtdbMap.containsKey(serRtdb)){
						unInstallRtdb.put(serRtdb, "");
					}
				}else{
					continue;
				}
			}
			
		}
		unInstallRS.put("SPA", unInstallSpa);
		unInstallRS.put("RTDB", unInstallRtdb);
		return unInstallRS;
	}
*/
	private Map<String, Map<String, Map<String, Map<String, String>>>> getUninstallRS2(JSONArray Servers,
			String protocol) throws Exception {
		Map<String, Map<String, Map<String, Map<String, String>>>> unInstallRS = new HashMap<String, Map<String, Map<String, Map<String, String>>>>();
		Map<String, Map<String, Map<String, String>>> unInstallRtdb = new HashMap<String, Map<String, Map<String, String>>>();
		Map<String, Map<String, Map<String, String>>> unInstallSpa = new HashMap<String, Map<String, Map<String, String>>>();
		String sql = "SELECT DISTINCT SPA, RTDB FROM toDistributeCases WHERE server='[]'";
		if (protocol.equals("ANSI")) {
			sql += " and customer='VZW'";
		} else {
			sql += " and customer!='VZW'";
		}
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		ArrayList<HashMap<String, Object>> list = jdbc.query(sql);
		for (int j = 0; j < list.size(); j++) {
			Map<String, String> caseRtdbMap = new HashMap<String, String>();
			Map<String, String> caseSpaMap = new HashMap<String, String>();
			List<String> caseSpaAndRtdbList = new ArrayList<String>();
			JSONArray caseSpa = JSONArray.fromObject(list.get(j).get("SPA"));
			for (int m = 0; m < caseSpa.size(); m++) {
				String serSpa = mattch(caseSpa.getString(m), "(^[A-Z,a-z]+)");
				if (!serSpa.equals("")) {
					caseSpaAndRtdbList.add(serSpa);
					caseSpaMap.put(serSpa, "");
				} else {
					continue;
				}
			}

			JSONArray caseRtdb = JSONArray.fromObject(list.get(j).get("RTDB"));
			for (int n = 0; n < caseRtdb.size(); n++) {
				String serRtdb = mattch(caseRtdb.getString(n), "(^[A-Z,a-z]+)");
				if (!serRtdb.equals("")) {
					caseSpaAndRtdbList.add(serRtdb);
					caseRtdbMap.put(serRtdb, "");
				} else {
					continue;
				}
			}

			Map<String, Double> maxMap = new HashMap<String, Double>();
			Map<String, Map<String, Map<String, String>>> serverLaRSMap = new HashMap<String, Map<String, Map<String, String>>>();
			for (int i = 0; i < Servers.size(); i++) {
				Map<String, String> rtdbMap = new HashMap<String, String>();
				Map<String, String> spaMap = new HashMap<String, String>();
				JSONObject serverMem = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
				String serverName = serverMem.getString(Constant.SERVERNAME);
				String serverProtocol = serverMem.getString(Constant.SERVERPROTOCOL);
				if (protocol.equals(serverProtocol)) {
					List<String> spaAndRtdbList = new ArrayList<String>();
					JSONArray serverSpa = serverMem.getJSONArray(Constant.SERVERSPA);
					for (int m = 0; m < serverSpa.size(); m++) {
						String serSpa = mattch(serverSpa.getString(m), "(^[A-Z,a-z]+)");
						if (!serSpa.equals("")) {
							spaMap.put(serSpa, "");
							spaAndRtdbList.add(serSpa);
						} else {
							continue;
						}
					}
					JSONArray serverRtdb = serverMem.getJSONArray(Constant.SERVERRTDB);
					for (int n = 0; n < serverRtdb.size(); n++) {
						String serRtdb = mattch(serverRtdb.getString(n), "(^[A-Z,a-z]+)");
						if (!serRtdb.equals("")) {
							rtdbMap.put(serRtdb, "");
							spaAndRtdbList.add(serRtdb);
						} else {
							continue;
						}
					}
					Map<String, Map<String, String>> SPAandRTDB = new HashMap<String, Map<String, String>>();
					SPAandRTDB.put("SPA", spaMap);
					SPAandRTDB.put("RTDB", rtdbMap);
					serverLaRSMap.put(serverName, SPAandRTDB);
					spaAndRtdbList.retainAll(caseSpaAndRtdbList);
					double inters = getIntersection(spaAndRtdbList, caseSpaAndRtdbList).size();
					double union = getUnion(spaAndRtdbList, caseSpaAndRtdbList).size();
					double num = getTwo(inters / union);
					maxMap.put(serverName, num);
				}
			}
			String serverName = getMaxValue(maxMap);
			Map<String, String> serverSpa = serverLaRSMap.get(serverName).get("SPA");
			Map<String, String> serverRTDB = serverLaRSMap.get(serverName).get("RTDB");
			for (String key : caseSpaMap.keySet()) {
				if (!serverSpa.containsKey(key)) {
					if (null == unInstallSpa.get(serverName)) {
						Map<String, String> unInSpaList = new HashMap<String, String>();
						unInSpaList.put(key, "");
						Map<String, Map<String, String>> unInSpa = new HashMap<String, Map<String, String>>();
						unInSpa.put("SPA", unInSpaList);
						unInstallSpa.put(serverName, unInSpa);
					} else {
						unInstallSpa.get(serverName).get("SPA").put(key, "");
					}
				}
			}

			for (String key : caseRtdbMap.keySet()) {
				if (!serverRTDB.containsKey(key)) {
					if (null == unInstallRtdb.get(serverName)) {
						Map<String, String> unInRtdbList = new HashMap<String, String>();
						unInRtdbList.put(key, "");
						Map<String, Map<String, String>> unInRtdb = new HashMap<String, Map<String, String>>();
						unInRtdb.put("RTDB", unInRtdbList);
						unInstallRtdb.put(serverName, unInRtdb);
					} else {
						unInstallRtdb.get(serverName).get("RTDB").put(key, "");
					}
				}
			}
		}
		unInstallRS.put("SPA", unInstallSpa);
		unInstallRS.put("RTDB", unInstallRtdb);
		return unInstallRS;
	}

	public static String getMaxValue(Map<String, Double> map) {
		double value = 0;
		String maxKey = null;
		List<Double> list = new ArrayList<Double>();
		Iterator<Entry<String, Double>> ite = map.entrySet().iterator();
		while (ite.hasNext()) {
			Map.Entry<String, Double> entry = (Map.Entry<String, Double>) ite.next();
			value = Double.parseDouble(entry.getValue().toString());
			list.add(entry.getValue());
			Collections.sort(list);

			if (value == Double.parseDouble(list.get(list.size() - 1).toString())) {
				maxKey = entry.getKey().toString();
			}
		}
		return maxKey;
	}

	private String mattch(String input, String regex) {
		String mattchString = "";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			mattchString = matcher.group(1);
		}
		return mattchString;
	}

	private List<String> getIntersection(List<String> listOne, List<String> listTwo) {
		listOne.retainAll(listTwo);
		return listOne;
	}

	private List<String> getUnion(List<String> listOne, List<String> listTwo) {
		listTwo.removeAll(listOne);
		listOne.addAll(listTwo);
		return listOne;
	}
	public boolean getProcess(String jName) throws IOException{
		String[] cmd = {
				"/bin/sh",
				"-c",
				"ps -ef | grep "+jName
				};
		boolean flag=false;
		Process p = Runtime.getRuntime().exec(cmd);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream os = p.getInputStream();
		byte b[] = new byte[256];
		while(os.read(b)> 0)
			baos.write(b);
		String s = baos.toString().replaceAll("grep "+jName, "");
		if(s.indexOf(jName) >= 0){
			flag=true;
		}else{
			flag=false;
		}
		return flag;
	}
	private double getTwo(double num) {
		BigDecimal bigd = new BigDecimal(num);
		double n = bigd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return n;
	}
}

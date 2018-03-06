package com.alucn.casemanager.server.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.casemanager.server.listener.MainListener;

public class GetTimeCase {
	private static final Logger logger = Logger.getLogger(GetTimeCase.class);
	private List<JSONObject> caseJsonVarList;
	private int time_sensitive = 0;

	public void analysisCase(int index, String caseDir, String caseName, String featureId, boolean isCase, int level,
			String casePath, boolean isBase) {
		int newLevel = level;
		int newLevel2;
		String caseFilePath;
		boolean isBaseModule;
		String nextCasePath = "";
		if (isCase) {
			caseFilePath = caseDir + File.separator + caseName;
		} else {
			caseFilePath = casePath + File.separator + caseName;
		}
		File caseFile = new File(caseFilePath);
		if (caseFile.exists()) {
			try {
				String jsonData = ReadFile(caseFilePath);
				JSONObject jsonObject = new JSONObject(jsonData);
				String Names[] = JSONObject.getNames(jsonObject);

				if (isCase) {
					try {
						JSONObject jsonVar = jsonObject.getJSONObject("_var");
						analysisCaseVar(jsonVar.toString());
					} catch (Exception e) {
						logger.error("no _var in  case" + featureId + "/" + caseName + ". Error: " + e.toString());
						analysisCaseVar("{}");
					}

				} else {
					try {
						newLevel++;
						JSONObject jsonVar = jsonObject.getJSONObject("_var");
						analysisModuleVar(jsonVar.toString(), newLevel);

					} catch (Exception e) {
						logger.warn("no _var in  module " + featureId + "/" + caseName + ". Error: " + e.toString());
					}

				}
				
				boolean is = false;
				JSONObject jsonObjectItem;
				String taskName, moudleName, newmoudleName;
				for (int i = 0; i < Names.length; i++) {
					if (!Names[i].startsWith("_")) {
						jsonObjectItem = jsonObject.getJSONObject(Names[i]);
						taskName = jsonObjectItem.getString("task");
							
						if(taskName.equals("DateTimeTask")) {
							JdbcUtil jdbc_cf = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB"));
							String sql = "update DftTag set time_sensitive = 'Y' where case_name = '"+ featureId + "/" + caseName+"'";
							//System.out.println("++++++++++"+sql+"++"+caseDir+"++"+casePath);
							jdbc_cf.executeSql(sql);
							++time_sensitive;
							is = true;
							break;
						}
					}
				}
				
				if(!is && level==0){
					for (int i = 0; i < Names.length; i++) {
						newLevel2 = newLevel;
						if (!Names[i].startsWith("_")) {
							jsonObjectItem = jsonObject.getJSONObject(Names[i]);
							taskName = jsonObjectItem.getString("task");
							if (taskName.equals("ImportTask")) {
								moudleName = jsonObjectItem.getString("module");
		
								if (moudleName.contains("{") && moudleName.contains("}")) {
									if (moudleName.startsWith("{")) {
										newmoudleName = "";
									} else {
										newmoudleName = moudleName.substring(0, moudleName.indexOf("{"));
									}
									int start = 0;
									while (moudleName.indexOf("{", start) >= 0) {
										newmoudleName = newmoudleName
												+ getJsonVar(moudleName.substring(moudleName.indexOf("{", start) + 1,
														moudleName.indexOf("}", start)));
										start = moudleName.indexOf("}", start) + 1;
									}
									if (start < moudleName.length()) {
										newmoudleName = newmoudleName + moudleName.substring(start);
									}
		
									moudleName = newmoudleName;
		
								}
								if (moudleName.startsWith("lib_")) {
									nextCasePath = caseDir;
		
								} else if (!moudleName.startsWith("data/") && !moudleName.startsWith("data\\")
										&& !moudleName.startsWith("module/") && !moudleName.startsWith("module\\")
										&& !moudleName.startsWith("template/") && !moudleName.startsWith("template\\")
										&& !moudleName.trim().equals("") && isCase) {
									logger.error("Import task moudle error: " + caseFilePath + " --" + moudleName);
									continue;
								}
		
								isBaseModule = false;
		
								if (!moudleName.equals("")) {
									if (isBase) {
										if (!moudleName.startsWith("lib_")) {
											nextCasePath = new File(caseFilePath).getParent();
											isBaseModule = true;
										}
									}
									if (isPublic(moudleName)) {
										isBaseModule = true;
										nextCasePath = "/home/huanglei/SurepayDraft";
									}
									analysisCase(-1, caseDir, moudleName, featureId, false, newLevel2, nextCasePath,
											isBaseModule);
								}
							}
						}
					}
				}

				if (!isCase) {
					return;
				}
			} catch (Exception e) {
				logger.error(caseFilePath + " -- " + e);
			}
		} else {
			logger.error(featureId + " " + caseName + " not exist!");
		}
	}

	private static String ReadFile(String Path) {
		BufferedReader reader = null;
		String laststr = "";
		try {
			FileInputStream fileInputStream = new FileInputStream(Path);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				laststr += tempString;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return laststr;
	}

	private String getJsonVar(String key) {
		String result = "";
		String lkey = key;
		while (true) {
			result = getDeepJsonVar(lkey);
			if (result.startsWith("{")) {
				lkey = result.substring(1, result.indexOf("}")).trim();
			} else {
				break;
			}
		}

		return result;
	}

	private String getDeepJsonVar(String key) {
		String result = "";
		for (int j = 0; j < caseJsonVarList.size(); j++) {
			if (caseJsonVarList.get(j).has(key)) {
				try {
					result = caseJsonVarList.get(j).getString(key);
				} catch (Exception e) {

				}
				break;
			}
		}
		return result;
	}

	private void analysisModuleVar(String jsonStr, int level) {
		for (int i = caseJsonVarList.size() - 1; i >= level; i--) {
			caseJsonVarList.remove(i);
		}
		JSONObject MoudleVar = new JSONObject(jsonStr);
		caseJsonVarList.add(MoudleVar);
	}

	private void analysisCaseVar(String jsonStr) {
		caseJsonVarList = new ArrayList<JSONObject>();
		caseJsonVarList.add(new JSONObject(jsonStr));
	}

	private boolean isPublic(String jsonName) {
		boolean isPublic = false;
		String fileName = jsonName.replace("\\\\", "/");

		if (fileName.startsWith("data/") || fileName.startsWith("module/") || fileName.startsWith("template/")) {
			isPublic = true;
		}
		return isPublic;
	}
	
	public static void main(String[] args) throws Exception {
		String configPath = System.getenv("WEBLAB_CONF");
		String[] argss = { configPath };
		MainListener.init(argss);
		JdbcUtil jdbc_cf = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB"));
		String sql = "select case_name from DftTag;";
		GetTimeCase getTimeCase = new GetTimeCase();
		ArrayList<HashMap<String, Object>> query = jdbc_cf.query(sql);
		for(int i=0; i<query.size(); i++){
			String caseName = (String)query.get(i).get("case_name");
			getTimeCase.analysisCase(0, "/home/surepayftp/DftCase/"+caseName.split("/")[0], caseName.split("/")[1], caseName.split("/")[0], true, 0,"", false) ;
		}
		System.out.println("+++++++++++++++++++++++++++++++++++finshed"+getTimeCase.time_sensitive+"++++++++++++++++++++++++++++++++");
	}
		
}

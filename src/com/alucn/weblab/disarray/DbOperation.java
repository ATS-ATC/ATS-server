package com.alucn.weblab.disarray;

import java.sql.*;
import org.apache.log4j.Logger;
import com.alucn.casemanager.server.common.util.ParamUtil;
import net.sf.json.JSONArray;

public class DbOperation {

	public static Logger logger = Logger.getLogger(DbOperation.class);

	public static void DeleteDistributedCase(JSONArray UnNeedServers) {
		String CaseInfoDB = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		Connection connection = null;
		Statement stat = null;
		try {
			Class.forName("org.sqlite.JDBC");
			// connection=DriverManager.getConnection("jdbc:sqlite:"+Dbpath);
			connection = DriverManager.getConnection("jdbc:sqlite:" + CaseInfoDB);
			stat = connection.createStatement();
			String UpdateSql = "Delete from DistributedCaseTbl where server_name in (";
			for (int i = 0; i < UnNeedServers.size(); i++) {
				UpdateSql += "'" + UnNeedServers.getString(i) + "', ";
			}
			UpdateSql = UpdateSql.substring(0, UpdateSql.length() - 2) + ");";

			stat.executeUpdate(UpdateSql);

			// delete

		} catch (SQLException e1) {
			logger.error(e1);
		} catch (Exception e2) {
			logger.error(e2);

		} finally {
			try {
				if (stat != null) {
					stat.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e3) {

				e3.printStackTrace();
			}

		}
	}

	public static void SyncDailyCaseFromDftTag() {
		String CaseInfoDB = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		String DftTagDB = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		Connection connection_dc = null;
		Statement stat_dc = null;

		Connection connection_df = null;
		Statement stat_df = null;
		try {
			Class.forName("org.sqlite.JDBC");
			// connection=DriverManager.getConnection("jdbc:sqlite:"+Dbpath);
			connection_dc = DriverManager.getConnection("jdbc:sqlite:" + CaseInfoDB);
			stat_dc = connection_dc.createStatement();
			String Query_dc = "select case_name from DailyCase";
			ResultSet result_dc = stat_dc.executeQuery(Query_dc);
			JSONArray caseList_dc = new JSONArray();
			while (result_dc.next()) {
				caseList_dc.add(result_dc.getString("case_name"));
			}

			connection_df = DriverManager.getConnection("jdbc:sqlite:" + DftTagDB);
			stat_df = connection_df.createStatement();
			String Query_df = "select case_name from DftTag where case_name in "
					+ caseList_dc.toString().replace("[", "(").replace("]", ")").replace("\"", "'");
			ResultSet result_df = stat_dc.executeQuery(Query_df);
			JSONArray caseList_df = new JSONArray();
			while (result_df.next()) {
				caseList_df.add(result_df.getString("case_name"));
			}
			String Delete_dc = "delete from DailyCase where case_name not in "
					+ caseList_df.toString().replace("[", "(").replace("]", ")").replace("\"", "'");
			stat_dc.executeUpdate(Delete_dc);

		} catch (SQLException e1) {
			logger.error(e1);
		} catch (Exception e2) {
			logger.error(e2);

		} finally {
			try {
				if (stat_dc != null) {
					stat_dc.close();
				}
				if (connection_dc != null) {
					connection_dc.close();
				}
				if (stat_df != null) {
					stat_df.close();
				}
				if (connection_df != null) {
					connection_df.close();
				}
			} catch (SQLException e3) {

				e3.printStackTrace();
			}

		}

	}

	public static void UpdateDistributedCase(JSONArray CaseList, String server_name) {
		String CaseInfoDB = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		Connection connection = null;
		Statement stat = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + CaseInfoDB);
			PreparedStatement prep = connection
					.prepareStatement("replace into DistributedCaseTbl(case_name, server_name) values (?, ?);");

			for (int k = 0; k < CaseList.size(); k++) {
				prep.setString(1, CaseList.getString(k));
				prep.setString(2, server_name);
				prep.addBatch();
			}

			connection.setAutoCommit(false);
			prep.executeBatch();
			connection.setAutoCommit(true);

		} catch (SQLException e1) {
			logger.error(e1);
		} catch (Exception e2) {
			logger.error(e2);

		} finally {
			try {
				if (stat != null) {
					stat.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e3) {

				e3.printStackTrace();
			}

		}
	}

}

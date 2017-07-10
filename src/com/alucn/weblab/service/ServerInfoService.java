package com.alucn.weblab.service;

import java.io.File;
import org.springframework.stereotype.Service;
import com.alucn.casemanager.server.common.CaseConfigurationCache;
import com.alucn.casemanager.server.common.ConfigProperites;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.Fifowriter;
import com.alucn.casemanager.server.common.util.FileUtil;
import com.alucn.weblab.model.Server;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author haiqiw
 * 2017��6��5�� ����6:28:09
 * desc:ServerInfoService
 */
@Service("serverInfoService")
public class ServerInfoService {
	
	public JSONArray getServerInfo(){
		JSONArray infos = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true, null);
		return infos;
	}
	
	public void addServerDetails(Server server) throws Exception{
		String filePath = ConfigProperites.getInstance().getCaseClientSftpSourcePath();
		Fifowriter.writerFile(filePath, Constant.SPAANDRTDB, JSONObject.fromObject(server).toString());
		String sftpTargetPath = ConfigProperites.getInstance().getCaseClientSftpTargetPath();
		String shellName = ConfigProperites.getInstance().getCaseClientSftpSendShellName();
		String userName = ConfigProperites.getInstance().getCaseClientSftpUserName();
		String password = ConfigProperites.getInstance().getCaseClientSftpPassword();
		int port = Integer.parseInt(ConfigProperites.getInstance().getCaseClientSftpPort());
		FileUtil.upLoadFile(FileUtil.createSession(server.getServerIp(), userName, password, port), filePath, sftpTargetPath);
		String[] cmds = new String[] {"sh "+sftpTargetPath+File.separator+shellName};
		String[] result = FileUtil.execShellCmdBySSH(server.getServerIp(), port, userName, password, cmds);
		for(String str : result){
			System.out.println(str);
		}
	}
	
	public void removeServerInfo(){
	}
	
	public void removeServerDetails(){
	}
	
	public void updateServerInfo(){
	}
	
	public void cancel(){
	}
}

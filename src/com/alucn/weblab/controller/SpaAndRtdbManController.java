package com.alucn.weblab.controller;

import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alucn.weblab.service.SpaAndRtdbManService;

/**
 * @author haiqiw
 * 2017年6月2日 下午5:39:08
 * desc: spaAndRtdbManController
 */

@Controller
//@RequestMapping("/spaAndRtdbMan")
public class SpaAndRtdbManController {
	
	@Autowired(required=true)
	private SpaAndRtdbManService spaAndRtdbManService;
	
	@RequestMapping(path = "/getSpaAndRtdbInfo")
	public String getSpaAndRtdbInfo(Model model) throws Exception{
		model.addAllAttributes(spaAndRtdbManService.getSpaAndRtdbInfo());
		return "spaAndRtdbMan";
	}

	@RequestMapping(path = "/addSpaAndRtdbInfo", method = RequestMethod.POST)
	public void addSpaAndRtdbInfo( String click, String spa, String rtdb, PrintWriter out) throws Exception{
		out.write(spaAndRtdbManService.addSpaAndRtdbInfo(spa, rtdb));
	}
	
	@RequestMapping(path = "/removeSpaAndRtdbInfo", method = RequestMethod.POST)
	public void removeSpaAndRtdbInfo(String click, String spa, String rtdb, PrintWriter out) throws Exception{
		out.write(spaAndRtdbManService.removeSpaAndRtdbInfo(spa, rtdb));
	}
	
	public SpaAndRtdbManService getSpaAndRtdbManService() {
		return spaAndRtdbManService;
	}

	public void setSpaAndRtdbManService(SpaAndRtdbManService spaAndRtdbManService) {
		this.spaAndRtdbManService = spaAndRtdbManService;
	}
}

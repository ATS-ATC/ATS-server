package com.alucn.weblab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.alucn.weblab.service.MainService;

/**
 * @author haiqiw
 * 2017年6月6日 下午4:47:32
 * desc:MainController
 */
@Controller
//@RequestMapping("/main")
public class MainController {
	
	@Autowired
	private MainService mainService;
	
	@RequestMapping(path = "/getStatistics")
    public String getStatistics(Model model) throws Exception {
		model.addAllAttributes(mainService.getStatistics());
        return "main";
    }
	
	public MainService getMainService() {
		return mainService;
	}

	public void setMainService(MainService mainService) {
		this.mainService = mainService;
	}
}

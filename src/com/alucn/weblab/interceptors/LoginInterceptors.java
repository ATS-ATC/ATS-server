package com.alucn.weblab.interceptors;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alucn.weblab.exception.WebAuthException;

/**
 * @author haiqiw
 * 2017年6月5日 上午10:16:05
 * desc:Interceptor
 */
public class LoginInterceptors implements HandlerInterceptor{
	
	private List<String> excludedUrls;
	
	public void setExcludeUrls(List<String> excludeUrls) {
		this.excludedUrls = excludeUrls;
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse arg1, Object arg2) throws Exception {
		String requestUri = request.getRequestURI();
		for (String url : excludedUrls) {
			if (requestUri.endsWith(url)) {
				return true;
			}
		}
		HttpSession session = request.getSession();
		if (session.getAttribute("login")==null) {
			throw new WebAuthException();
		} else {
			return true;
		}
	}

}

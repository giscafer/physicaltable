package com.giscafer.physicaltable.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.giscafer.physicaltable.Constant.ConfigConstant;

public class LoginFilter implements Filter {

	private FilterConfig filterConfig;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	@Override
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

		String noFilterTagString = filterConfig
				.getInitParameter("noFilterTags");
		boolean enable=Boolean.parseBoolean(filterConfig.getInitParameter("enable"));
		//不起用的情况下直接通过
		if(!enable){
			filterChain.doFilter(httpServletRequest,
					httpServletResponse);
			return ;
		}
		String[] noFilterTags = noFilterTagString.split(";");

		String uri = httpServletRequest.getRequestURI();
//		System.out.println("过滤路径:"+uri);
		// 配置文件中允许放行的关键字
		if (noFilterTags != null) {
			for (String noFilterTag : noFilterTags) {

				if (noFilterTag == null || "".equals(noFilterTag.trim())) {
					continue;
				}

				if (uri.indexOf(noFilterTag.trim()) != -1) {
					filterChain.doFilter(httpServletRequest,
							httpServletResponse);
					return;
				}
			}
		}
		
		Cookie[] cookies=httpServletRequest.getCookies();
		
		System.out.println("path:"+uri);
		Object islogin=httpServletRequest.getSession().getAttribute(ConfigConstant.ISLOGIN);
		if ( islogin!= null&&Boolean.parseBoolean(islogin.toString())) {//已登录
			filterChain.doFilter(httpServletRequest, httpServletResponse);
		} else if(cookies!=null){//未登录，查询cookie
			for(Cookie cookie:cookies){
//				System.out.println("cookiename:"+cookie.getName());
				if(cookie.getName().equals(ConfigConstant.USERNAME)){
					System.out.println("cookie:"+ToStringBuilder.reflectionToString(cookie));
					httpServletRequest.getSession().setAttribute(ConfigConstant.ISLOGIN, true);
					httpServletRequest.getSession().setAttribute(ConfigConstant.USERNAME, cookie.getValue());
					if(uri.endsWith(ConfigConstant.PROJECTNAME+"/")){
						httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/reportcard");
					}else{
						filterChain.doFilter(httpServletRequest, httpServletResponse);
					}
					return ;
				}
			}
			httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login");
		}else{
			httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login");
		}
	}
	
	@Override
	public void destroy() {

	}
}

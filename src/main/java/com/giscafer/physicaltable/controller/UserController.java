package com.giscafer.physicaltable.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.giscafer.physicaltable.interceptor.TimeInterceptor;
import com.giscafer.physicaltable.model.User;
import com.giscafer.physicaltable.service.IUserService;
import com.giscafer.physicaltable.service.impl.UserServiceImpl;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheInterceptor;

/**
 * user控制器
 * @author netbuffer
 *
 */
@Before({ TimeInterceptor.class })
public class UserController extends Controller {
	private static IUserService userservice=Enhancer.enhance(UserServiceImpl.class);
	private static Logger userLog= LoggerFactory
			.getLogger(UserController.class);

	public void index() {
		render("login.html");
	}
	
	/**
	 * 开启缓存
	 */
	@Before({CacheInterceptor.class})
	public void userlist() {
		int limit=getParaToInt("limit");
		int offset=getParaToInt("offset");
		Map<String, Object> data=userservice.getUserList(offset, limit);
		if(data.size()>0){
			renderJson(data);
		}else{
			renderJson();
		}
		
	}
	public void delete(){
		User.dao.deleteById(getPara("id"));
		setAttr("status", "success");
		renderJson();
	}
}

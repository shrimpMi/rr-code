/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.renren.service.SysGeneratorService;
import io.renren.utils.GenUtils;
import io.renren.utils.PageUtils;
import io.renren.utils.Query;
import io.renren.utils.R;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.DelayQueue;

/**
 * 代码生成器
 * 
 * @author Mark sunlightcs@gmail.com
 */
@Controller
@RequestMapping("/sys/generator")
public class SysGeneratorController {
	@Autowired
	private SysGeneratorService sysGeneratorService;
	
	/**
	 * 列表
	 */
	@ResponseBody
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils pageUtil = sysGeneratorService.queryList(new Query(params));
		return R.ok().put("page", pageUtil);
	}
	
	/**
	 * 生成代码
	 */
	@RequestMapping("/code")
	public void code(String dbName,String tables, HttpServletResponse response) throws IOException{
		byte[] data = sysGeneratorService.generatorCode(dbName,tables.split(","));
		
		response.reset();  
        response.setHeader("Content-Disposition", "attachment; filename=\"wuse-code.zip\"");
        response.addHeader("Content-Length", "" + data.length);  
        response.setContentType("application/octet-stream; charset=UTF-8");  
  
        IOUtils.write(data, response.getOutputStream());  
	}

	/**
	 * 生成代码
	 */
	@ResponseBody
	@RequestMapping("/dblist")
	public R dblist(){
		List<String> db = sysGeneratorService.dblist();
		return R.ok().put("db", db);
	}

	/**
	 * 生成代码
	 */
	@ResponseBody
	@RequestMapping("/config")
	public R config(){
		Map<String,Object> data = new HashMap<>();
		Configuration config = GenUtils.getConfig();
		Iterator<String> iterator = config.getKeys();
		while(iterator.hasNext()){
			String key = iterator.next();
			data.put(key,config.getString(key));
		}
		List<Map<String,String>> packages = new ArrayList<>();
		List<String> dbs = sysGeneratorService.dblist();
		for(String db:dbs){
			Map<String,String> row = new HashMap<>(1);
			row.put("dbName",db);
			row.put("package",GenUtils.packages.get(db));
			packages.add(row);
		}
		data.put("packages",packages);
		return R.ok().put("data",data);
	}

	/**
	 * 生成代码
	 */
	@ResponseBody
	@RequestMapping("/config/save")
	public R saveConfig(HttpServletRequest request, String json){
		System.out.println(json);
		try{
			json = IOUtils.toString(request.getInputStream(),"UTF-8");
			System.out.println(json);
			json = URLDecoder.decode(json,"UTF-8");
			System.out.println(json);
		}catch (Exception e){
			e.printStackTrace();
		}

		JSONObject data = JSON.parseObject(json);
		Configuration config = GenUtils.getConfig();
		config.setProperty("package",data.getString("package"));
		JSONArray array = data.getJSONArray("packages");
		for(int i = 0 ; i < array.size() ; i++){
			JSONObject row = array.getJSONObject(i);
			if(StringUtils.isNotBlank(row.getString("package"))
				&&StringUtils.isNotBlank(row.getString("dbName"))){
				GenUtils.packages.put(row.getString("dbName"),row.getString("package"));
			}
		}
		return R.ok();
	}
}

/**
 * 
 */
package com.mingsoft.base.action.web;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mingsoft.base.action.BaseAction;
import com.mingsoft.util.FileUtil;
import com.mingsoft.util.StringUtil;

import net.mingsoft.base.util.BaseUtil;

/**
 * 
 * 
 * 
 * <p>
 * <b>铭飞科技</b>
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014 - 2015
 * </p>
 * 
 * @author killfen
 * 
 *         <p>
 *         Comments:错误页面定义
 *         </p>
 * 
 *         <p>
 *         Create Date:2015-4-26
 *         </p>
 * 
 *         <p>
 *         Modification history:
 *         </p>
 */
@Controller("baseErrorAction")
@RequestMapping("/base/error")
public class ErrorAction extends BaseAction {
	/**
	 * 返回404页面
	 */
	@RequestMapping("/{code}")
	@ResponseBody
	public void code(@PathVariable("code") String code, HttpServletRequest req, HttpServletResponse resp,
			Exception ex) {
		String content = FileUtil.readFile(this.getRealPath(req, "/error/" + code + ".htm"));
		if (StringUtil.isBlank(content)) {
			content = FileUtil.readFile(this.getRealPath(req, "/error/error.htm"));
		}
		content = content.replace("{code/}", code);
		Object obj = BaseUtil.getSession("ms_exception");
		if (obj != null) {
			Exception e = (Exception) obj;
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			content = content.replace("{ms_exception/}", sw.toString());
		}
		this.outString(resp, content);
	}
}

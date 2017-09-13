package net.mingsoft.base.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.mingsoft.base.entity.ResultJson;

import net.mingsoft.base.util.BaseUtil;
import net.mingsoft.base.util.SpringUtil;

/**
 * 统一异常处理方式
 * 
 * @author Administrator
 *
 */
public class SimpleMappingExceptionResolver implements HandlerExceptionResolver {
	private final static Logger LOG = LoggerFactory.getLogger(SimpleMappingExceptionResolver.class);
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object object,
			Exception exception) {
		// 判断是否ajax请求
		if (!(request.getHeader("accept").indexOf("application/json") > -1
				|| (request.getHeader("X-Requested-With") != null
						&& request.getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1))) {
			// 如果不是ajax，JSP格式返回
			// 为安全起见，只有业务异常我们对前端可见，否则否则统一归为系统异常
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("result", false);
			if (exception instanceof BusinessException) {
				map.put("resultMsg", exception.getMessage());
			} else if (exception instanceof UnauthorizedException) {
				map.put("resultMsg","err.not.permissions");
				return new ModelAndView("redirect:/error/403.do", map);
			} else {
				map.put("resultMsg", exception.getMessage());
			}
			BaseUtil.setSession("ms_exception", exception);
			return new ModelAndView("redirect:/error/500.do", map);
		} else {
			// 如果是ajax请求，JSON格式返回
			try {
				response.setContentType("application/json;charset=UTF-8");
				PrintWriter writer = response.getWriter();
				ResultJson result = new ResultJson();
				result.setResult(false);
				// 为安全起见，只有业务异常我们对前端可见，否则统一归为系统异常
				if (exception instanceof BusinessException) {
					result.setResultMsg(exception.getMessage());
				} else if (exception instanceof UnauthorizedException) {
					result.setResultMsg("err.not.permissions");
				} else {
					result.setResultMsg("error");
				}
				writer.write(JSONObject.toJSONString(result));
				writer.flush();
				writer.close();
				LOG.debug("ajax请求 异常");
			} catch (IOException e) {
				e.printStackTrace();
				LOG.error(e.getMessage());
			}
		}
		return null;
	}
}

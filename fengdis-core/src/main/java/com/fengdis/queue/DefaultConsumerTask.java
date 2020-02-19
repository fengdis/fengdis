package com.fengdis.queue;

import com.alibaba.fastjson.JSON;
import com.fengdis.util.SpringBeanUtils;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @version 1.0
 * @Descrittion: 具体结果处理实现，统一集成ConsumerTask，实现其handle方法
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class DefaultConsumerTask extends ConsumerTask {

	private static final Logger logger = LoggerFactory.getLogger(DefaultConsumerTask.class);

	private BeetlGroupUtilConfiguration bgConf;
	private GroupTemplate gt;

	public DefaultConsumerTask(String taskId) {
		super(taskId);
		this.bgConf = SpringBeanUtils.getBean(BeetlGroupUtilConfiguration.class);
		this.gt = bgConf.getGroupTemplate();
	}

	@Override
	public String handle(TaskResult result) {
		String myTemplates = "<%var a = params;var b = result.~size;%>${params.id}-${result.title}-${b}";
		Template template = gt.getTemplate(myTemplates);
		logger.info("接收方模版：" + template);

		Map<String, Object> paramsMap = super.getParams(result);
		Map<String, Object> resultMap = super.getResult(result);
		template.binding("params", paramsMap);
		template.binding("result", resultMap);

		String msgText = "";
		try {
			logger.debug("绑定模版参数params：" + JSON.toJSONString(paramsMap));
			logger.debug("绑定模版参数result：" + JSON.toJSONString(resultMap));
			msgText = template.render();
			logger.info("渲染结果：" + msgText);
		} catch (Exception e) {
			logger.error("渲染出错:", e);
		}
		return msgText;
	}

}

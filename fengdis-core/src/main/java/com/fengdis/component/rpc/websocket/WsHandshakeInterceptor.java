package com.fengdis.component.rpc.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * @version 1.0
 * @Descrittion: webscoket建立连接（握手）和断开（挥手）拦截器
 * @author: fengdi
 * @since: 2018/8/10 0010 21:02
 */
public class WsHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		String userId = ((ServletServerHttpRequest) request).getServletRequest().getParameter(WebSocketConfig.WS_USERID_KEY);
		if(userId != null){
			attributes.put(WebSocketConfig.WS_USERID_KEY, userId);
		}
		return super.beforeHandshake(request, response, wsHandler, attributes);
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
		super.afterHandshake(request, response, wsHandler, exception);
	}

}

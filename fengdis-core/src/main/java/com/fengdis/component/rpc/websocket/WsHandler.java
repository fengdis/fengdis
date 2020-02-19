package com.fengdis.component.rpc.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fengdis.queue.ThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @version 1.0
 * @Descrittion: webscoket处理器
 * @author: fengdi
 * @since: 2018/8/10 0010 21:02
 */
@Component
public class WsHandler extends TextWebSocketHandler {

	private final Logger logger = LoggerFactory.getLogger(WsHandler.class);

	/**
	 * 建立连接完成执行（保存userId和WebSocketSession映射关系，发送未读消息）
	 * @param session
	 * @throws Exception
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		logger.info("连接成功...");

		String userId = session.getAttributes().get(WebSocketConfig.WS_USERID_KEY).toString();
		//响应客户端
		session.sendMessage(new TextMessage(JSON.toJSONString(new Message(Message.FROM_SYSTEM,userId,String.format("%s已连接到websocket",userId),new Date()))));

		//建立连接时，保存userId和WebSocketSession映射关系到WSSessionCache中
		WsSessionCache.getInstance().put(userId, session);
		logger.info("{}已加入到会话池",userId);
		logger.info("当前会话数：{}",WsSessionCache.getInstance().getSessionSize());

		//此处实现自己业务，比如，当用户登录后，会把离线消息推送给用户，查库中未提醒、未读消息，发送给userId用户
		String messageGenaral = null;
		String messageMajor = null;
		String messageMinor = null;
		if (null != messageGenaral) {
			session.sendMessage(new TextMessage(JSON.toJSONString("")));
		}
		if (null != messageMajor) {
			session.sendMessage(new TextMessage(JSON.toJSONString("")));
		}
		if (null != messageMinor) {
			session.sendMessage(new TextMessage(JSON.toJSONString("")));
		}

	}

	/**
	 * 消息处理（在客户端通过Websocket API发送的消息会经过这里，然后进行相应的处理，如果要发送某个用户则在这里解析并处理）
	 * @param session
	 * @param message
	 * @throws Exception
	 */
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		logger.info("处理消息...");
		if(message.getPayloadLength() == 0)
			return;
		logger.info(message.getPayload().toString());
		Message msg = JSONObject.parseObject(message.getPayload().toString(),Message.class);
		msg.setDate(new Date());
		//发送到自己
		//session.sendMessage(new TextMessage(message.getPayload().toString()));
		//发送到指定user
		sendMessage2User(msg.getTo(), new TextMessage(JSON.toJSONString(msg)));
	}

	/**
	 * 消息传输出错执行
	 * @param webSocketSession
	 * @param throwable
	 * @throws Exception
	 */
	@Override
	public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
		logger.error("消息传输出错...");
		/*if (webSocketSession.isOpen()) {
			webSocketSession.close();
		}*/
	}

	/**
	 * 连接关闭执行（移除userId和WebSocketSession映射关系）
	 * @param session
	 * @param closeStatus
	 * @throws Exception
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		WsSessionCache.getInstance().remove(session);
		logger.info("{}已断开websocket连接",session.getId());
		logger.info("当前会话数：{}",WsSessionCache.getInstance().getSessionSize());
	}

	/**
	 * 群发所有在线用户（WebSocketSession在线）
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage2AllSession(final TextMessage message) {
		Collection<WebSocketSession> allSession = WsSessionCache.getInstance().findAllSession();

		//所有session同时发送（存在一用户多session）
		for(WebSocketSession webSocketSession : allSession){
			final WebSocketSession webSocketSession4userId = webSocketSession;
			// 多线程群发
			ThreadPoolUtil.getExcutorService().submit(new Runnable() {

				public void run() {
					try {
						if (webSocketSession4userId.isOpen()) {
							webSocketSession4userId.sendMessage(message);
						}
					} catch (IOException e) {
						logger.error("消息发送失败");
						e.printStackTrace();
					}
				}

			});
		}
	}

	/**
	 * 给指定用户发送消息
	 * @param userId
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage2User(String userId, TextMessage message) {
		List<WebSocketSession> webSocketSessions = WsSessionCache.getInstance().get(userId);
		//一个用户多终端多session
		for(WebSocketSession webSocketSession : webSocketSessions){
			if (webSocketSession != null && webSocketSession.isOpen()) {
				try {
					webSocketSession.sendMessage(message);
				} catch (IOException e) {
					logger.error("消息发送失败");
					e.printStackTrace();
				}
			}
		}
	}

}
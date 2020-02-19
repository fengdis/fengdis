package com.fengdis.component.rpc.websocket;

import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Descrittion: 缓存websocket会话， 单例类（静态内部类实现）；
 *               支持一个用户可能在不同的设备连上websocket，因此一个用户可能存在多个session，限制用户在一个浏览器多个标签页共享一个session在客户端限制；
 *               为了支持快速查找，本类使用了2个map容器
 * @author: fengdi
 * @since: 2018/8/10 0010 21:02
 */
public class WsSessionCache {

	// 存储sessinId与userId键值对
	private final Map<String, String> sessionId2UserIdMap = new ConcurrentHashMap<>();
	// 存储sessinId与WebSocketsession键值对
	private final Map<String, WebSocketSession> sessionId2SessionMap = new ConcurrentHashMap<>();

	private WsSessionCache() {
	}

	private static class WSSessionHolder {
		public static final WsSessionCache cache = new WsSessionCache();
	}

	public static WsSessionCache getInstance() {
		return WSSessionHolder.cache;
	}

	public boolean isExist(String userId) {
		return this.sessionId2UserIdMap.containsValue(userId);
	}

	public void put(String userId, WebSocketSession session) {
		String sessionId = session.getId();
		sessionId2UserIdMap.put(sessionId, userId);
		sessionId2SessionMap.put(sessionId, session);
	}

	public void remove(WebSocketSession session) {
		String sessionId = session.getId();
		sessionId2UserIdMap.remove(sessionId);
		sessionId2SessionMap.remove(sessionId);
	}

	public List<WebSocketSession> get(String userId) {
		List<WebSocketSession> sessionList = new ArrayList<WebSocketSession>(3);
		Set<Entry<String, String>> set = sessionId2UserIdMap.entrySet();
		Iterator<Entry<String, String>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entery = iterator.next();
			if (entery.getValue().equals(userId)) {
				sessionList.add(sessionId2SessionMap.get(entery.getKey()));
			}
		}
		return sessionList;
	}

	public Collection<WebSocketSession> findAllSession() {
		return this.sessionId2SessionMap.values();
	}

	public Collection<String> findAllUserId() {
		return this.sessionId2UserIdMap.values();
	}

	public int getSessionSize() {
		return this.sessionId2SessionMap.size();
	}

	public int getUserSize() {
		return this.sessionId2UserIdMap.size();
	}

}

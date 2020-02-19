package com.fengdis.component.rpc.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @version 1.0
 * @Descrittion: webscoket服务接口
 * @author: fengdi
 * @since: 2018/8/10 0010 21:02
 */
@Configuration
@PropertySource("classpath:/websocket.properties")
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig/* extends WebMvcConfigurationSupport*/ implements WebSocketConfigurer {

	@Value("${websocket.server.cors.uri}")
	private String uri;
	@Value("${websocket.server.cors.origins}")
	private String origins;
	@Value("${websocket.server.maxTextBufferSize}")
	private String wsMaxTextBufferSize;
	@Value("${websocket.server.uri}")
	private String wsUri;
	@Value("${websocket.server.allowsOrigins}")
	private String wsAllowedOrigins;
	@Value("${websocket.server.sockJsUri}")
	private String wsSockJsUri;

	public static final String WS_USERID_KEY = "userId";

	/*@Bean
	public ServletServerContainerFactoryBean createWebSocketContainer() {
		ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
		container.setMaxTextMessageBufferSize(Integer.parseInt(wsMaxTextBufferSize));
		container.setMaxBinaryMessageBufferSize(Integer.parseInt(wsMaxTextBufferSize));
		return container;
	}*/

	/*@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping(uri).allowedMethods("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE")
				.allowedOrigins(origins);
	}*/

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(webSocketHandler(), wsUri).addInterceptors(new WsHandshakeInterceptor()).setAllowedOrigins(wsAllowedOrigins);
		registry.addHandler(webSocketHandler(), wsSockJsUri).addInterceptors(new WsHandshakeInterceptor()).withSockJS();
	}

	@Bean
	public WebSocketHandler webSocketHandler() {
		return new WsHandler();
	}

}

package com.fengdis.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Descrittion: web全局配置
 * @author: fengdi
 * @since: 2019/08/28 17:26
 */
@Configuration
@EnableWebMvc
@ComponentScan
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*").allowCredentials(true).maxAge(3600);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry){
		//注册跨域拦截器
		//registry.addInterceptor(new ResponseInterceptor()).addPathPatterns("/**");
		//注册权限拦截器
		registry.addInterceptor(new AuthInterceptor()).excludePathPatterns("/login/**").excludePathPatterns("/swagger-resources/**","/webjars/**","/v2/**","/swagger-ui.html/**");
	}

	//注册过滤器
	@Bean
	public FilterRegistrationBean AddFilter(){
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new TestFilter());
		List<String> urls = new ArrayList<>();
		urls.add("/*");
		filterRegistrationBean.setUrlPatterns(urls);
		return filterRegistrationBean;
	}

	//注册Xss过滤器
	@Bean
	public FilterRegistrationBean AddXssFilter(){
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new XssFilter());
		List<String> urls = new ArrayList<>();
		urls.add("/*");
		filterRegistrationBean.setUrlPatterns(urls);
		return filterRegistrationBean;
	}

}

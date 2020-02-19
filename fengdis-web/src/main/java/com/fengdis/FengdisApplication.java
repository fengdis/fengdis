package com.fengdis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/** 
 * @version 1.0
 * @Descrittion: 指定项目为springboot，由此类当作程序入口，自动装配 web 依赖的环境
 * @author: fengdi
 * @since: 2018/7/23 0023 21:46
 *
 */
@SpringBootApplication
//@MapperScan("com.fengdis.mapper")
//@EntityScan("com.fengdis.entity")
@EnableJpaRepositories("com.fengdis")
@Configuration
@ComponentScan(basePackages = {"com.fengdis","com.fengdis.api"})
@EnableCaching
@EnableJpaAuditing
@EnableScheduling
@EnableTransactionManagement
@EnableAsync
@EnableWebMvc
public class FengdisApplication extends SpringBootServletInitializer {

    /**
     * 为打包准备
     * @param builder
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(FengdisApplication.class);
    }


	public static void main(String[] args) {
        /**
         * Springboot整合Elasticsearch在项目启动前设置一下的属性，防止报错
         * 解决netty冲突后初始化client时还会抛出异常
         * java.lang.IllegalStateException: availableProcessors is already set to [4], rejecting [4]
         */
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication application = new SpringApplication(FengdisApplication.class);
        application.run(args);
    }
}

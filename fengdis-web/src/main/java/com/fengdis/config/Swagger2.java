package com.fengdis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Descrittion: Swagger2配置
 * @author: fengdi
 * @since: 2019/08/28 17:26
 */
@Configuration
@EnableSwagger2
public class Swagger2 {
    // swagger自动加载api的包路径
    private static final String BASEPACKAGE = "com.fengdis.api";
    private static final String SWAGGER_TITLE = "微服务";
    private static final String SWAGGER_DESCRIPTION = "微服务";
    private static final String CONTACT = "fengdis";

    @Value("${swagger.enabled}")
    private Boolean enabled;

    @Value("${api.version}")
    private String version;

    @Bean
    public Docket createRestApi() {
        /*ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        ticketPar.name(tokenHeader).description("token")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .defaultValue("Bearer ")
                .required(true)
                .build();
        pars.add(ticketPar.build());*/
        // 在这里可以设置请求的统一前缀;
        return new Docket(DocumentationType.SWAGGER_2).enable(enabled).apiInfo(apiInfo()).select()
            .apis(RequestHandlerSelectors.basePackage(BASEPACKAGE)).paths(PathSelectors.any()).build().pathMapping("/");
    }

    ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(SWAGGER_TITLE).description(SWAGGER_DESCRIPTION).termsOfServiceUrl("")
            .contact(CONTACT).version(version).build();
    }
}

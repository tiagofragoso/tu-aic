package group3.aic_middleware.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
public class SpringFoxConfig {
    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any()).paths(
                PathSelectors.any()).build().pathMapping("/").apiInfo(new ApiInfoBuilder()
                .title("Federated Storage Middleware")
                .description("Federated Storage for IOT Sensing Events")
                .build()).select().apis(RequestHandlerSelectors.basePackage("group3.aic_middleware")).build();
    }

//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//
//        // Allow anyone and anything access. Probably ok for Swagger spec
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("*");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//
////        source.registerCorsConfiguration("/api/event-history/current/traffic-lights", config);
////        source.registerCorsConfiguration("/api/event-history/current/cars", config);
////        source.registerCorsConfiguration("/api/event-history/*", config);
//
////        source.registerCorsConfiguration("/api/**", config);
////        source.registerCorsConfiguration("*/**", config);
////        source.registerCorsConfiguration("/**", config);
////        source.registerCorsConfiguration("/v2/api-docs", config);
//        return new CorsFilter(source);
//    }
}

package cn.sarahw.draw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .allowedOrigins("*")
//                .allowedHeaders("CLK", "token", "X-Auth-Token")
//                .exposedHeaders("CLK", "token", "X-Auth-Token")
                .maxAge(3600);
    }
}
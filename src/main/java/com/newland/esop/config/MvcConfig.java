package com.newland.esop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig {
    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
                registry.addResourceHandler("/font/**").addResourceLocations("classpath:/static/font/");
                registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
                registry.addResourceHandler("/picture/**").addResourceLocations("classpath:/static/picture/");
                registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
                registry.addResourceHandler("/image/**").addResourceLocations("file:G:/ftp/");
            }
        };
    }
}
package com.example.imageRetrieval.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.awt.*;

/**
 * @description:
 * @author: ShiJi
 * @time: 2023/3/18 19:47
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ImageConfig imageConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/image/**").addResourceLocations("file:" + imageConfig.getImgLocation() + "//");
    }

}

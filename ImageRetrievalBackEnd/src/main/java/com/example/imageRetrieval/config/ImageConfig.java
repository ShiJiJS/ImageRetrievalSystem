package com.example.imageRetrieval.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author ShiJi
 * @create 2023/3/9 14:29
 */
@Configuration
@ConfigurationProperties(prefix = "image-config")
@Data
public class ImageConfig {
    private String imgLocation;
    private String urlPrefix;
    private String annoImgLocation;
    private Boolean startAnno;
    private Double colorMomentsMaxValue;
    private Double orbMinValue;
}

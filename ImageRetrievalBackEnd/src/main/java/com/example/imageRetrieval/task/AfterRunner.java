package com.example.imageRetrieval.task;

import com.example.imageRetrieval.anno.ImageAnnoLoader;
import com.example.imageRetrieval.config.ImageConfig;
import com.example.imageRetrieval.image.ImgScheduler;
import com.example.imageRetrieval.service.ImageAnnoService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Author ShiJi
 * @create 2023/3/10 9:34
 */
//容器加载完成后执行

@Component
@Order(value = 1)
public class AfterRunner implements ApplicationRunner {

    private final ImgScheduler imgScheduler;
    private final ImageAnnoLoader imageAnnoLoader;
    private final ImageConfig imageConfig;
    private final ImageAnnoService imageAnnoService;

    public AfterRunner(ImgScheduler imgScheduler, ImageAnnoLoader imageAnnoLoader, ImageConfig imageConfig, ImageAnnoService imageAnnoService) {
        this.imgScheduler = imgScheduler;
        this.imageAnnoLoader = imageAnnoLoader;
        this.imageConfig = imageConfig;
        this.imageAnnoService = imageAnnoService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(imageConfig.getStartAnno()){
            imageAnnoLoader.loadImages();
            imageAnnoService.calculateColorMomentsAndORBAnno();
        }else {
            imgScheduler.init();
        }
//        imageAnnoLoader.loadImages();
//        imgScheduler.init();

    }
}

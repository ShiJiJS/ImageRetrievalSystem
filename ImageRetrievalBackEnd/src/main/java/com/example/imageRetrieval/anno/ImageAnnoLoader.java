package com.example.imageRetrieval.anno;

import com.example.imageRetrieval.config.ImageConfig;
import com.example.imageRetrieval.mapper.ImageAnnoTableMapper;
import com.example.imageRetrieval.pojo.ImageAnnoTable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author ShiJi
 * @create 2023/4/27 14:09
 */
@Component
public class ImageAnnoLoader {
    @Resource
    private ImageConfig imageConfig;
    @Resource
    private ImageAnnoTableMapper imageAnnoTableMapper;

    public void loadImages(){
        String urlPrefix = imageConfig.getUrlPrefix();
        File annoImageDir = new File(imageConfig.getAnnoImgLocation());
        File[] imagesArray = annoImageDir.listFiles();
        if(imagesArray == null){
            throw new RuntimeException("需要标注的图片的文件夹为空");
        }
        List<ImageAnnoTable> annoTableList = new ArrayList<>();

        for(int i = 0;i < imagesArray.length;i++){
            for(int j = 0; j < i;j++){
                ImageAnnoTable imageAnnoTable = new ImageAnnoTable();
                //设置location
                imageAnnoTable.setImageLocation1(imagesArray[i].getAbsolutePath());
                imageAnnoTable.setImageLocation2(imagesArray[j].getAbsolutePath());
                //设置url
                imageAnnoTable.setImageURL1(urlPrefix + imagesArray[i].getName());
                imageAnnoTable.setImageURL2(urlPrefix + imagesArray[j].getName());
                //放入list中
                annoTableList.add(imageAnnoTable);
            }
        }

        //遍历并插入数据库
        for (ImageAnnoTable imageAnnoTable : annoTableList) {
            imageAnnoTableMapper.insert(imageAnnoTable);
        }
    }
}

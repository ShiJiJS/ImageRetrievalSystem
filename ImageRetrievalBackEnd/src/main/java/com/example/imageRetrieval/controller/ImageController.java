package com.example.imageRetrieval.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.imageRetrieval.colorMoments.ColorMoments;
import com.example.imageRetrieval.isoData.Point;
import com.example.imageRetrieval.orb.ORBAlgorithm;
import com.example.imageRetrieval.pojo.Code;
import com.example.imageRetrieval.pojo.Result;
import com.example.imageRetrieval.pojo.SearchResult;
import com.example.imageRetrieval.service.SearchService;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @description:
 * @author: ShiJi
 * @time: 2023/3/18 9:52
 */

@RestController
public class ImageController {

    @Autowired
    private SearchService searchService;


    @PostMapping("/searchImage")
    public Result searchImage(HttpServletRequest request) throws IOException {
        MultipartHttpServletRequest params=((MultipartHttpServletRequest) request);
        // 获取文件和文件名
        MultipartFile multipartFile = ((MultipartHttpServletRequest) request).getFile("file");
        String fileName = params.getParameter("name");
        //判断文件是否为空
        if (multipartFile == null || multipartFile.isEmpty()) {
            return new Result(null, Code.SAVE_ERR,"Please select a file to upload");
        }

        //将文件临时保存到工作目录的temp文件夹中
        // 拼接保存文件的路径
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "temp");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        // 构造保存文件的完整路径
        Path filePath = uploadPath.resolve(fileName);

        // 将上传的文件保存到本地
        multipartFile.transferTo(filePath);

        //计算两种算法的特征值
        double[] colorMoments = ColorMoments.calculateColorMoments(filePath.toString());
        Mat orbMat = ORBAlgorithm.extractDescriptors(filePath.toString());


        //从数据库中查询并匹配
        //颜色矩
        if(colorMoments == null) {
            throw new RuntimeException("ERROR: ColorMoments is NULL");
        }
        List<SearchResult> cmSearchResults = searchService.searchColorMoments(new Point(9, colorMoments));
        //ORB
        List<SearchResult> orbSearchResults = searchService.searchORB(orbMat);

        JSONObject returnResult = new JSONObject();
        returnResult.put("cmSearchResults",cmSearchResults);
        returnResult.put("orbSearchResults",orbSearchResults);


        //删除临时文件
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            System.out.println("文件删除失败：" + e.getMessage());
        }

        return new Result(returnResult,Code.GET_OK);
    }

}

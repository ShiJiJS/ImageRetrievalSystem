package com.example.imageRetrieval.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.imageRetrieval.mapper.ImageAnnoTableMapper;
import com.example.imageRetrieval.pojo.*;
import com.example.imageRetrieval.service.ImageAnnoService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author ShiJi
 * @create 2023/4/27 14:42
 */
@RestController
public class ImageAnnoController {

    @Resource
    private ImageAnnoTableMapper imageAnnoTableMapper;
    @Resource
    private ImageAnnoService imageAnnoService;

    @GetMapping("/annoInfo/{pageSize}/{currentPage}")
    public Result getAnnoInfo(@PathVariable Integer pageSize, @PathVariable Integer currentPage){
        List<ImageAnnoTable> records = imageAnnoTableMapper.selectPage(new Page<>(pageSize, currentPage), null).getRecords();
        ArrayList<ImageAnnoVO> imageAnnoVOs = new ArrayList<>();
        for (ImageAnnoTable record : records) {
            ImageAnnoVO imageAnnoVO = new ImageAnnoVO(record.getPairId(), record.getImageURL1(), record.getImageURL2());
            imageAnnoVOs.add(imageAnnoVO);
        }
        return new Result(imageAnnoVOs, Code.GET_OK);
    }

    @PostMapping("/humanAnno")
    public Result setHumanAnno(@RequestBody JSONObject jsonObject){
        Integer pairId = jsonObject.getInteger("pairId");
        Boolean isSimilar = jsonObject.getBoolean("isSimilar");

        UpdateWrapper<ImageAnnoTable> wrapper = new UpdateWrapper<>();
        wrapper.eq("pair_id",pairId);
        ImageAnnoTable imageAnnoTable = new ImageAnnoTable();
        imageAnnoTable.setHumanAnno(isSimilar);

        imageAnnoTableMapper.update(imageAnnoTable,wrapper);
        return new Result(null,Code.SAVE_OK);
    }

    @GetMapping("/calculate")
    public Result calculateResult(){
        CalculateResultVO calculateResultVO = imageAnnoService.calculateResult();
        return new Result(calculateResultVO,Code.GET_OK);
    }

    @GetMapping("/allAnnoInfo/{pageSize}/{currentPage}")
    public Result getAllAnnoInfo(@PathVariable Integer pageSize, @PathVariable Integer currentPage){
        List<ImageAnnoTable> records = imageAnnoTableMapper.selectPage(new Page<>(pageSize, currentPage), null).getRecords();
        return new Result(records, Code.GET_OK);
    }
}

package com.example.imageRetrieval.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.imageRetrieval.mapper.ColorMomentsClusterMapper;
import com.example.imageRetrieval.mapper.ColorMomentsTableMapper;
import com.example.imageRetrieval.mapper.ORBTableMapper;
import com.example.imageRetrieval.pojo.*;
import com.example.imageRetrieval.utils.SerializationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ShiJi
 * @create 2023/3/10 10:41
 */
@RestController
@RequestMapping("/colorMoments")
public class ColorMomentsController {

    @Autowired
    private ColorMomentsTableMapper colorMomentsTableMapper;

    @Autowired
    private ColorMomentsClusterMapper colorMomentsClusterMapper;



    @GetMapping("/getAllImage/{pageSize}/{currentPage}")
    public Result getAllImage(@PathVariable Integer pageSize,@PathVariable Integer currentPage){
        List<ColorMomentsTable> results = colorMomentsTableMapper.selectPage(new Page<>(currentPage, pageSize), null).getRecords();
        //反序列化其中的对象
        List<JSONObject> returnList = new ArrayList<>();
        for (ColorMomentsTable result : results) {
            JSONObject jsonObject = SerializationUtil.unSerializeColorMomentsTable(result);
            returnList.add(jsonObject);
        }
        return new Result(returnList,Code.GET_OK);
    }

    @PostMapping("/getPointsByClusterId/{pageSize}/{currentPage}")
    public Result getPointsByClusterId(@PathVariable Integer pageSize,@PathVariable Integer currentPage,@RequestBody JSONObject jsonObject){
        String clusterId = jsonObject.getString("clusterId");
        QueryWrapper<ColorMomentsTable> wrapper = new QueryWrapper<>();
        wrapper.eq("clusterId",clusterId);
        List<ColorMomentsTable> results = colorMomentsTableMapper.selectPage(new Page<>(currentPage, pageSize), wrapper).getRecords();

        //反序列化其中的对象
        List<JSONObject> returnList = new ArrayList<>();
        for (ColorMomentsTable result : results) {
            returnList.add(SerializationUtil.unSerializeColorMomentsTable(result));
        }

        return new Result(returnList,Code.GET_OK);
    }

    @GetMapping("/getClusterInfos/{pageSize}/{currentPage}")
    public Result getClusterInfos(@PathVariable Integer pageSize,@PathVariable Integer currentPage){
        List<ColorMomentsCluster> records = colorMomentsClusterMapper.selectPage(new Page<>(currentPage, pageSize), null).getRecords();

        //反序列化其中的对象
        List<JSONObject> returnList = new ArrayList<>();
        for (ColorMomentsCluster record : records) {
            JSONObject jsonObject = SerializationUtil.unSerializeColorMomentsCluster(record);
            returnList.add(jsonObject);
        }

        return new Result(returnList,Code.GET_OK);
    }



}

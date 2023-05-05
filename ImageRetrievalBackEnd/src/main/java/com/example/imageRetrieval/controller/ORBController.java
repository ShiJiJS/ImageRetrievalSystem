package com.example.imageRetrieval.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.imageRetrieval.mapper.ORBTableMapper;
import com.example.imageRetrieval.pojo.Code;
import com.example.imageRetrieval.pojo.ORBTable;
import com.example.imageRetrieval.pojo.Result;
import com.example.imageRetrieval.utils.SerializationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: ShiJi
 * @time: 2023/3/18 9:18
 */
@RestController
@RequestMapping("/orb")
public class ORBController {

    @Autowired
    private ORBTableMapper orbTableMapper;

    @GetMapping("/getOrbInfos/{pageSize}/{currentPage}")
    public Result getOrbInfos(@PathVariable Integer pageSize, @PathVariable Integer currentPage){
        List<ORBTable> records = orbTableMapper.selectPage(new Page<>(currentPage, pageSize), null).getRecords();

        //反序列化其中的对象
        List<JSONObject> returnList = new ArrayList<>();
        for (ORBTable record : records) {
            JSONObject jsonObject = SerializationUtil.unSerializeORBTable(record);
            returnList.add(jsonObject);
        }
        return new Result(returnList, Code.GET_OK);
    }


}

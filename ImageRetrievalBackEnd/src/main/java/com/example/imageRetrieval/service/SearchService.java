package com.example.imageRetrieval.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.imageRetrieval.colorMoments.ColorMoments;
import com.example.imageRetrieval.image.ImgScheduler;
import com.example.imageRetrieval.isoData.Cluster;
import com.example.imageRetrieval.isoData.ISOData;
import com.example.imageRetrieval.isoData.Point;
import com.example.imageRetrieval.mapper.ColorMomentsTableMapper;
import com.example.imageRetrieval.mapper.ORBTableMapper;
import com.example.imageRetrieval.orb.ORBAlgorithm;
import com.example.imageRetrieval.pojo.ColorMomentsTable;
import com.example.imageRetrieval.pojo.ORBTable;
import com.example.imageRetrieval.pojo.SearchResult;
import com.example.imageRetrieval.utils.SerializationUtil;

import org.apache.commons.collections4.ListUtils;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @description:
 * @author: ShiJi
 * @time: 2023/3/18 10:04
 */
@Service
public class SearchService {

    @Autowired
    private ImgScheduler imgScheduler;

    @Autowired
    private ORBTableMapper orbTableMapper;
    @Autowired
    private ColorMomentsTableMapper colorMomentsTableMapper;



    //最后返回的匹配的数量
    private Integer MACHING_QUANTITY = 10;
    //从数据库中查找的数量
    private Integer SEARCHING_QUANTITY = 1000;

    public List<SearchResult> searchColorMoments(Point point){
        //获取聚类列表
        List<Cluster> clusterList = imgScheduler.getClusterList();
        //判断是否正在添加或更新，是则返回null
        if(clusterList == null){
            return null;
        }
        //找到距离最小的聚类中心
        Cluster minDistanceCluster = null;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < clusterList.size(); i++) {
            double distance = clusterList.get(i).getCenter().distance(point);
            if(distance < minDistance){
                minDistanceCluster = clusterList.get(i);
                minDistance = distance;
            }
        }
        //获取该聚类的所有图片
        String clusterId = minDistanceCluster.getId();
        QueryWrapper<ColorMomentsTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("clusterId",clusterId);
        List<ColorMomentsTable> records = colorMomentsTableMapper.selectPage(new Page<>(1, SEARCHING_QUANTITY), queryWrapper).getRecords();

        //存储返回结果用
        List<SearchResult> results = new ArrayList<>();

        //将该点的颜色矩向量转变为数组
        double[] colorMomentsVectorOfPoint = point.getCoordinate().stream().mapToDouble(Double::doubleValue).toArray();
        //遍历每张图片，算出相似度
        for (ColorMomentsTable record : records) {
            List<Double> colorMomentsVectorList = SerializationUtil.unSerializedAsDoubleList(record.getCmVector());
            double[] colorMomentsVectorOfRecord = colorMomentsVectorList.stream().mapToDouble(Double::doubleValue).toArray();
            double similarityScore = ColorMoments.calculateSimilarityScore(colorMomentsVectorOfRecord, colorMomentsVectorOfPoint);
            //将信息存入返回结果中
            SearchResult searchResult = new SearchResult();
            searchResult.setImageId(record.getImageId());
            searchResult.setUrl(record.getUrl());
            searchResult.setSimilarityScore(similarityScore);
            results.add(searchResult);
        }

        //排序
        results.sort(new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult o1, SearchResult o2) {
                return -o1.compareTo(o2);
            }
        });

        //裁剪
        if(results.size() <= MACHING_QUANTITY){
            return results;
        }else {
            return ListUtils.partition(results,MACHING_QUANTITY).get(0);
        }


    }

    public List<SearchResult> searchORB(Mat searchMat){

        List<ORBTable> records = orbTableMapper.selectPage(new Page<>(1, SEARCHING_QUANTITY), null).getRecords();

        //存储返回结果用
        List<SearchResult> results = new ArrayList<>();

        for (ORBTable record : records) {
            //获取mat，比较相似度
            Mat recordMat = SerializationUtil.unSerializeMat(record.getMat());
            double similarity = ORBAlgorithm.calculateSimilarity(searchMat, recordMat);
            //添加返回结果信息
            SearchResult searchResult = new SearchResult();
            searchResult.setImageId(record.getImageId());
            searchResult.setUrl(record.getUrl());
            searchResult.setSimilarityScore(similarity);
            results.add(searchResult);
        }

        //排序
        results.sort(new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult o1, SearchResult o2) {
                return o1.compareTo(o2);
            }
        });

        //裁剪
        if(results.size() <= MACHING_QUANTITY){
            return results;
        }else {
            return ListUtils.partition(results,MACHING_QUANTITY).get(0);
        }
    }


}

package com.example.imageRetrieval.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.imageRetrieval.colorMoments.ColorMoments;
import com.example.imageRetrieval.config.ImageConfig;
import com.example.imageRetrieval.mapper.ImageAnnoTableMapper;
import com.example.imageRetrieval.orb.ORBAlgorithm;
import com.example.imageRetrieval.pojo.CalculateResultDTO;
import com.example.imageRetrieval.pojo.CalculateResultVO;
import com.example.imageRetrieval.pojo.ConfusionMatrixParam;
import com.example.imageRetrieval.pojo.ImageAnnoTable;
import org.opencv.core.Mat;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author ShiJi
 * @create 2023/4/27 16:17
 */
@Service
public class ImageAnnoService {
    @Resource
    private ImageAnnoTableMapper mapper;
    @Resource
    private ImageConfig imageConfig;

    public void calculateColorMomentsAndORBAnno(){
        List<ImageAnnoTable> records = mapper.selectPage(new Page<>(2000, 1), null).getRecords();
        for (ImageAnnoTable record : records) {
            //计算颜色矩是否匹配
            double[] image1CM = ColorMoments.calculateColorMoments(record.getImageLocation1());
            double[] image2CM = ColorMoments.calculateColorMoments(record.getImageLocation2());
            double similarityCM = ColorMoments.calculateSimilarityScore(image1CM, image2CM);
            record.setColorMomentsAnno(similarityCM < imageConfig.getColorMomentsMaxValue());
            //计算ORB是否匹配
            Mat image1ORB = ORBAlgorithm.extractDescriptors(record.getImageLocation1());
            Mat image2ORB = ORBAlgorithm.extractDescriptors(record.getImageLocation2());
            double similarityORB = ORBAlgorithm.calculateSimilarity(image1ORB, image2ORB);
            record.setOrbAnno(similarityORB > imageConfig.getOrbMinValue());
        }
        //更改数据库中的值
        for (ImageAnnoTable record : records) {
            UpdateWrapper<ImageAnnoTable> wrapper = new UpdateWrapper<>();
            wrapper.eq("pair_id",record.getPairId());
            mapper.update(record,wrapper);
        }
    }


    public CalculateResultVO calculateResult(){
        List<ImageAnnoTable> records = mapper.selectPage(new Page<>(2000, 1), null).getRecords();
        ConfusionMatrixParam cmParam = getCMParam(records);
        ConfusionMatrixParam orbParam = getORBParam(records);
        CalculateResultDTO cmResult = calculateResult(cmParam);
        CalculateResultDTO orbResult = calculateResult(orbParam);
        return new CalculateResultVO(cmResult, orbResult);
    }

    private CalculateResultDTO calculateResult(ConfusionMatrixParam param){
        CalculateResultDTO calculateResultDTO = new CalculateResultDTO();
        Double accuracy = (param.TP + param.TN) * 1.0/(param.TP + param.FN + param.FP + param.TN);
        Double precision = (param.TP * 1.0) / (param.TP + param.FP);
        Double recall = (param.TP * 1.0) / (param.TP + param.FN);
        Double F_measure = ((precision * recall) / (precision + recall) )*2;
        calculateResultDTO.setAccuracy(accuracy);
        calculateResultDTO.setPrecision(precision);
        calculateResultDTO.setRecall(recall);
        calculateResultDTO.setF_measure(F_measure);
        return calculateResultDTO;
    }



    private ConfusionMatrixParam getCMParam(List<ImageAnnoTable> records){
        ConfusionMatrixParam param = new ConfusionMatrixParam();

        for (ImageAnnoTable record : records) {
            Boolean colorMomentsAnno = record.getColorMomentsAnno();
            Boolean humanAnno = record.getHumanAnno();
            param.TP += humanAnno && colorMomentsAnno ? 1 : 0;
            param.FN += humanAnno && !colorMomentsAnno ? 1 : 0;
            param.FP += !humanAnno && colorMomentsAnno ? 1 : 0;
            param.TN += !humanAnno && !colorMomentsAnno ? 1 : 0;
        }
        return param;
    }
    private ConfusionMatrixParam getORBParam(List<ImageAnnoTable> records){
        ConfusionMatrixParam param = new ConfusionMatrixParam();

        for (ImageAnnoTable record : records) {
            Boolean orbAnno = record.getOrbAnno();
            Boolean humanAnno = record.getHumanAnno();
            param.TP += humanAnno && orbAnno ? 1 : 0;
            param.FN += humanAnno && !orbAnno ? 1 : 0;
            param.FP += !humanAnno && orbAnno ? 1 : 0;
            param.TN += !humanAnno && !orbAnno ? 1 : 0;
        }
        return param;
    }

}

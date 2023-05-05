package com.example.imageRetrieval.pojo;

import lombok.Data;

/**
 * @Author ShiJi
 * @create 2023/4/27 17:19
 */
@Data
public class CalculateResultVO {
    private Double accuracy_cm;    //精度
    private Double precision_cm;   //精度
    private Double recall_cm;      //再现率
    private Double F_measure_cm;   //F值
    private Double accuracy_orb;    //精度
    private Double precision_orb;   //精度
    private Double recall_orb;      //再现率
    private Double F_measure_orb;   //F值

    public CalculateResultVO(CalculateResultDTO cmResult,CalculateResultDTO orbResult) {
        this.accuracy_cm = cmResult.getAccuracy();
        this.precision_cm = cmResult.getPrecision();
        this.recall_cm = cmResult.getRecall();
        this.F_measure_cm = cmResult.getF_measure();

        this.accuracy_orb = orbResult.getAccuracy();
        this.precision_orb = orbResult.getPrecision();
        this.recall_orb = orbResult.getRecall();
        this.F_measure_orb = orbResult.getF_measure();
    }
}

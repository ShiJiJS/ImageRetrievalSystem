package com.example.imageRetrieval.pojo;

import lombok.Data;

/**
 * @Author ShiJi
 * @create 2023/4/27 16:43
 */
@Data
public class CalculateResultDTO {
    private Double accuracy;    //精度
    private Double precision;   //精度
    private Double recall;      //再现率
    private Double F_measure;   //F值
}

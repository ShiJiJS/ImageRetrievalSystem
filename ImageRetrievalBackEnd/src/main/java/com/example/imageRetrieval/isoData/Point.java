package com.example.imageRetrieval.isoData;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author ShiJi
 * @create 2023/3/8 10:35
 */
@Data
public class Point {
    private Integer id;//点的id
    private final List<Double> coordinate; //点的坐标
    private final Integer dimension; //点的维度

    //通过直接输入坐标的list来构造点
    public Point(Integer dimension,List<Double> coordinate) {
        this.coordinate = coordinate;
        this.dimension = dimension;
        if(coordinate.size() != dimension){
            throw new RuntimeException("ERROR: the length of coordinate list is not equal to dimension!");
        }
    }

    //通过按顺序输入每个点的坐标来构造点
    public Point(Integer dimension,Double... nums) {
        this.dimension = dimension;
        if(nums.length != dimension){
            throw new RuntimeException("ERROR: the length of coordinate list is not equal to dimension!");
        }
        this.coordinate = new ArrayList<>();
        coordinate.addAll(Arrays.asList(nums));
    }

    public Point(Integer dimension,double[] nums) {
        this.dimension = dimension;
        this.coordinate = new ArrayList<>();
        for (double num : nums) {
            coordinate.add(num);
        }
    }

    //拷贝构造
    public Point(Point point){
        this.dimension = point.dimension;
        this.coordinate = new ArrayList<>();
        coordinate.addAll(point.getCoordinate());
    }

    //计算两点之间的距离
    public Double distance(Point point2){
        if(!point2.getDimension().equals(this.dimension)){
            throw new RuntimeException("ERROR: the dimension of point2 is not equal to this point's dimension");
        }
        double sum = 0.0;
        for (int i = 0; i < dimension; i++) {
            sum += Math.pow((point2.getCoordinate().get(i) - this.coordinate.get(i)),2);
        }
        return Math.sqrt(sum);
    }


    //偏移点的位置
    public void setOffset(Integer dimension,Double offset){
        this.coordinate.set(dimension,this.coordinate.get(dimension) + offset);
    }

    //按照指定倍率缩放坐标
    public void scalingCoordinate(Double coefficient){
        for (int i = 0; i < this.dimension; i++) {
            this.coordinate.set(i,this.coordinate.get(i) * coefficient);
        }
    }

    //将两点坐标相加，得到新的点
    public Point addPoint(Point point2){
        List<Double> coordinates = new ArrayList<>();
        for (int i = 0; i < dimension; i++) {
            coordinates.add(this.coordinate.get(i) + point2.coordinate.get(i));
        }
        return new Point(dimension,coordinates);
    }

    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder("(");
        for (int i = 0; i < dimension; i++) {
            //只打印小数点后两位
            returnString.append(String.format("%.2f",this.coordinate.get(i)));
            if(i != dimension - 1) returnString.append(",");
        }
        returnString.append(")");
        return returnString.toString();
    }
}

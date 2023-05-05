package com.example.imageRetrieval.isoData;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author: ShiJi
 * @time: 2023/3/8 14:08
 */
@Data
public class Cluster {

    private Point center;//中心
    private List<Point> members = new ArrayList<>();//类内成员
    private String id;//聚类的id
    private static AtomicInteger idNumber = new AtomicInteger(1);//用于存储聚类的序号

    public Cluster(Point center) {
        this.center = center;
        this.id = "Cluster-" + idNumber.getAndIncrement();
    }


    public void addPoint(Point point){
        this.members.add(point);
    }

    public void removePoint(Point point){
        this.members.remove(point);
    }

    //更新聚类的中心
    public void updateClusterCenter(){
        Integer dimension = center.getDimension();
        //分别遍历每一个维度
        List<Double> averageList = new ArrayList<>();
        for (int i = 0; i < dimension; i++) {
            //对于每个维度，找出该维度所有点的平均值
            double sum = 0;
            for (Point member : members) {
                sum += member.getCoordinate().get(i);
            }
            //将记录该维度的平均值
            averageList.add(sum / members.size());
        }
        //构造新的聚类中心并赋值
        this.center = new Point(dimension,averageList);
    }

    //计算所有维度最大的方差
    //返回维度和对应的方差值
    public Pair<Integer,Double> calMaxStandardDeviation(){
        Integer dimension = this.center.getDimension();
        double maxVariance = Double.MIN_VALUE;
        int maxDimension = 0;
        for (int i = 0; i < dimension; i++) {
            double currentVariance = calStandardDeviation(i);
            if( currentVariance > maxVariance){
                maxVariance = currentVariance;
            }
        }
        return Pair.of(maxDimension,maxVariance);
    }

    //计算某一维度的方差
    private Double calStandardDeviation(Integer dimension){
        double sum = 0;
        for (Point member : members) {
            sum += Math.pow((member.getCoordinate().get(dimension) - center.getCoordinate().get(dimension)),2);
        }
        return sum / members.size();
    }

    //得到两个聚类的新的中心点位置
    public Point getNewCenter(Cluster cluster2){
        //获取两个聚类的中心点
        Point center1 = new Point(this.getCenter());
        Point center2 = new Point(cluster2.getCenter());
        //计算系数
        Double coefficient1 = (this.members.size())* 1.0 / (this.members.size() + cluster2.getMembers().size());
        Double coefficient2 = (cluster2.getMembers().size())* 1.0 / (this.members.size() + cluster2.getMembers().size());
        //根据系数变换坐标
        center1.scalingCoordinate(coefficient1);
        center2.scalingCoordinate(coefficient2);
        //将两点坐标相加,得到新的中心
        return center1.addPoint(center2);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.id);
        stringBuilder.append(":{");
        for (Point member : this.members) {
            stringBuilder.append(member.toString());
            stringBuilder.append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("} Center:");
        stringBuilder.append(this.center.toString());
        return stringBuilder.toString();
    }
}

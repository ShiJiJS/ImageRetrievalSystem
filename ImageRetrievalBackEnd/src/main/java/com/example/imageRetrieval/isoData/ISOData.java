package com.example.imageRetrieval.isoData;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @description:
 * @author: ShiJi
 * @time: 2023/3/8 14:13
 */
public class ISOData {

    private final Integer INIT_CLUSTER_COUNT = 10;//初始的聚类中心的数量
    private final Integer CLUSTER_MIN_COUNT = 2;//聚类中最少拥有的个体数量
    private final Double MAX_STANDARD_DEVIATION = 1.0;//聚类所允许的最大标准差
    private final Double MIN_CLUSTER_CENTER_DISTANCE = 100.0;//聚类中心之间的最短距离
    private final Integer MAX_ITERATIONS = 4;//允许迭代的次数
    private List<Cluster> clusterList = new ArrayList<>();//聚类的列表
    private List<Point> initPoints;//初始化使用的点集

    public ISOData(List<Point> initPoints) {
        this.initPoints = initPoints;
    }

    //用点集来初始化聚类
    public List<Pair<Point, String>> init(){
        if(initPoints.size() < INIT_CLUSTER_COUNT){
            throw new RuntimeException("ERROR: The number of initial clusters is greater than the number of initial points given");
        }

        //根据指定的初始数量初始化聚类并随机指定中心点
        Random random = new Random();
        List<Integer> randomIndexList = new ArrayList<>();
        for (int i = 0; i < INIT_CLUSTER_COUNT; i++) {
            int rand = random.nextInt(initPoints.size());
            if(!randomIndexList.contains(rand)){
                randomIndexList.add(rand);
            }else {
                i--;
            }
        }
        for (Integer rand : randomIndexList) {
            clusterList.add(new Cluster(initPoints.get(rand)));
        }


        //将点加入聚类中
        List<Pair<Point, String>> pointAndClusterId = addPointsToCluster(this.initPoints);

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            if(i != 0){ //第一次循环不必执行
                updateClusterMembers();
            }
            //移除点数量不足的聚类
            discardSmallClusters();

            //重新计算聚类中心
            for (Cluster cluster : clusterList) {
                cluster.updateClusterCenter();
            }
            if(clusterList.size() < (INIT_CLUSTER_COUNT / 2)){ //类别数太少，前往分裂操作
                splitCluster();
            }else if(clusterList.size() >= INIT_CLUSTER_COUNT * 2){ //类别数太多，前往合并操作
                mergeCluster();
            }
        }

        //打印输出结果
        for (Cluster cluster : clusterList) {
            System.out.println(cluster);
        }
        return pointAndClusterId;

    }

    //内部调用
    //将点集加入聚类中
    private void addPointsToCluster(List<Point> points,List<Cluster> clusterList){
        //将点集中每个点都放入对应的聚类中
        for (int i = 0; i < points.size(); i++) {
            double minDistance = Double.MAX_VALUE;
            Cluster minDistanceCluster = null;
            //遍历每个聚类，找出和当前点距离最小的一个
            for (int j = 0; j < clusterList.size(); j++) {
                double currentDistance = points.get(i).distance(clusterList.get(j).getCenter());
                if(currentDistance < minDistance){
                    minDistance = currentDistance;
                    minDistanceCluster = clusterList.get(j);
                }
            }
            //将该点加入聚类
            assert minDistanceCluster != null;
            minDistanceCluster.addPoint(points.get(i));
        }
    }

    //内部调用
    //将点集加入聚类中
    //返回点和其所对应的聚类id
    private List<Pair<Point,String>> addPointsToCluster(List<Point> points){
        List<Pair<Point,String>> returnList = new ArrayList<>();
        //将点集中每个点都放入对应的聚类中
        for (int i = 0; i < points.size(); i++) {
            double minDistance = Double.MAX_VALUE;
            Cluster minDistanceCluster = null;
            //遍历每个聚类，找出和当前点距离最小的一个
            for (int j = 0; j < clusterList.size(); j++) {
                double currentDistance = points.get(i).distance(clusterList.get(j).getCenter());
                if(currentDistance < minDistance){
                    minDistance = currentDistance;
                    minDistanceCluster = clusterList.get(j);
                }
            }
            //将该点加入聚类
            assert minDistanceCluster != null;
            minDistanceCluster.addPoint(points.get(i));
            returnList.add(Pair.of(points.get(i),minDistanceCluster.getId()));
        }
        return returnList;
    }

    //外部调用
    //将单一点加入聚类中
    //返回加入的聚类id
    public String addPointToCluster(Point point){
        //遍历每个聚类，找出和当前点距离最小的一个
        double minDistance = Double.MAX_VALUE;
        Cluster minDistanceCluster = null;
        for (int j = 0; j < clusterList.size(); j++) {
            double currentDistance = point.distance(clusterList.get(j).getCenter());
            if(currentDistance < minDistance){
                minDistance = currentDistance;
                minDistanceCluster = clusterList.get(j);
            }
        }
        //将该点加入聚类
        assert minDistanceCluster != null;
        minDistanceCluster.addPoint(point);
        return minDistanceCluster.getId();
    }

    //丢弃掉点的数量小于规定的最小数量的聚类
    private void discardSmallClusters(){
        //记录要移除的聚类
//        List<Cluster> removeList = new ArrayList<>();
        for (int i = 0; i < clusterList.size(); i++) {
            if(clusterList.get(i).getMembers().size() < CLUSTER_MIN_COUNT){ //若其数量小于规定的最小数量
                List<Point> members = clusterList.get(i).getMembers();//保留其成员
                clusterList.remove(i);// 删除该cluster
                addPointsToCluster(members,clusterList);//将其成员加入其他的聚类中
            }
        }
    }

    //分裂聚类
    private void splitCluster(){
        //记录要移除的聚类和新添加的聚类
        List<Cluster> removeList = new ArrayList<>();
        List<Cluster> newClusters = new ArrayList<>();
        for (Cluster cluster : clusterList) {
            //获取最大的标准差和其对应的维度
            Pair<Integer, Double> maxPair = cluster.calMaxStandardDeviation();
            Double maxStandardDeviation = maxPair.getRight();
            Integer maxDimension = maxPair.getLeft();
            //标准差过大同时成员数量够多，则可以分裂
            if((maxStandardDeviation > MAX_STANDARD_DEVIATION) && (cluster.getMembers().size() > (2 * CLUSTER_MIN_COUNT))){
                //分裂后的中心点1
                Point center1 = new Point(cluster.getCenter());
                center1.setOffset(maxDimension,maxStandardDeviation);
                //分裂后的中心点2
                Point center2 = new Point(cluster.getCenter());
                center2.setOffset(maxDimension,-maxStandardDeviation);
                //存储分裂后的聚类
                newClusters.add(new Cluster(center1));
                newClusters.add(new Cluster(center2));
                //将剩余的点加入距离最近的聚类中
                addPointsToCluster(cluster.getMembers(),newClusters);
                //标记删除分裂前的聚类
                removeList.add(cluster);
            }
        }
        for (Cluster cluster : removeList) {
            this.clusterList.remove(cluster);
        }
        this.clusterList.addAll(newClusters);
    }

    //合并聚类
    private void mergeCluster(){
        Integer dimension = clusterList.get(0).getCenter().getDimension();
        //计算所有聚类中心两两之间的距离
        double[][] centerDistance = new double[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension - i; j++){
                //i和j两个聚类中心间的距离
                centerDistance[i][j] = clusterList.get(i).getCenter().distance(clusterList.get(j).getCenter());
            }
        }

        //存储要删除的聚类的序号
        List<Cluster> removeList = new ArrayList<>();
        //存储新增加的聚类
        List<Cluster> newClusters = new ArrayList<>();

        //用于标记是否已经合并过
        List<Integer> hasBeenMerged = new ArrayList<>();
        //遍历查找是否有小于最小距离的情况
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension - i; j++){
                //如果i或j对应下标的cluster已经被合并过了，则跳过
                if(hasBeenMerged.contains(i) || hasBeenMerged.contains(j)){
                    continue;
                }
                //如果小于最小距离
                if(centerDistance[i][j] < MIN_CLUSTER_CENTER_DISTANCE){
                    //新的中心位置
                    Point newCenter = clusterList.get(i).getNewCenter(clusterList.get(j));
                    //设置新的聚类
                    Cluster newCluster = new Cluster(newCenter);
                    List<Point> newMembers = new ArrayList<>();
                    newMembers.addAll(clusterList.get(i).getMembers());
                    newMembers.addAll(clusterList.get(j).getMembers());
                    newCluster.setMembers(newMembers);
                    //设置上面的存储列表
                    removeList.add(clusterList.get(i));
                    removeList.add(clusterList.get(j));
                    newClusters.add(newCluster);
                    //标记i和j的坐标，避免重复合并
                    hasBeenMerged.add(i);
                    hasBeenMerged.add(j);
                }
            }
        }

        //移除被合并的cluster
        for (Cluster cluster : removeList) {
            this.clusterList.remove(cluster);
        }
        //添加新的cluster
        this.clusterList.addAll(newClusters);
    }

    //更新每个点所在的聚类
    private void updateClusterMembers(){
        for (Cluster cluster : clusterList) {
            //记录需要移除的点
            List<Point> removeList = new ArrayList<>();
            for (Point point : cluster.getMembers()) { //遍历每一个点
                //查找距离的最小值
                double minDistance = cluster.getCenter().distance(point);
                //记录最小值对应的聚类
                Cluster minDistanceCluster = null;
                for (Cluster tempCluster : clusterList) {
                    double currentDistance = tempCluster.getCenter().distance(point);
                    if(currentDistance < minDistance){ //若距离小于最小值
                        minDistance = currentDistance;
                        minDistanceCluster = tempCluster;
                    }
                }
                //如果存在更小的距离
                if(minDistanceCluster != null){
                    //在新的聚类中添加该点
                    minDistanceCluster.addPoint(point);
                    //标记并等待删除
                    removeList.add(point);
                }
            }
            for (Point point : removeList) {
                cluster.removePoint(point);
            }
        }
    }

    //外部调用，更新聚类
    public List<Cluster> updateClusters(){
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            if(i != 0){
                //更新每个点所在的聚类
                updateClusterMembers();
            }
            //更新聚类中心
            for (Cluster cluster : clusterList) {
                cluster.updateClusterCenter();
            }
            if(clusterList.size() < (INIT_CLUSTER_COUNT / 2)){ //类别数太少，前往分裂操作
                splitCluster();
            }else if(clusterList.size() >= INIT_CLUSTER_COUNT * 2){ //类别数太多，前往合并操作
                mergeCluster();
            }
        }
        //打印输出结果
        for (Cluster cluster : clusterList) {
            System.out.println(cluster);
        }
        return this.clusterList;
    }

    public List<Cluster> getClusterList(){
        return this.clusterList;
    }

}

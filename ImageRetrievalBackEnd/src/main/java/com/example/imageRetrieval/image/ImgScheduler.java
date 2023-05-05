package com.example.imageRetrieval.image;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.imageRetrieval.config.ImageConfig;
import com.example.imageRetrieval.isoData.Cluster;
import com.example.imageRetrieval.isoData.ISOData;
import com.example.imageRetrieval.isoData.Point;
import com.example.imageRetrieval.mapper.ColorMomentsClusterMapper;
import com.example.imageRetrieval.mapper.ColorMomentsTableMapper;
import com.example.imageRetrieval.mapper.ORBTableMapper;
import com.example.imageRetrieval.orb.ORBAlgorithm;
import com.example.imageRetrieval.pojo.ColorMomentsCluster;
import com.example.imageRetrieval.pojo.ColorMomentsTable;
import com.example.imageRetrieval.pojo.ORBTable;
import com.example.imageRetrieval.utils.SerializationUtil;
import lombok.Data;
import org.apache.commons.io.file.Counters;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang3.tuple.Pair;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @description:
 * @author: ShiJi
 * @time: 2023/3/9 15:40
 */

@Component
@Data
public class ImgScheduler {

    //线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    //设置信息
    private ImageConfig imageConfig;
    //计算出的要导入的图片的颜色矩
    private final BlockingQueue<Pair<File, Point>> colorMomentsPointsQueue = new LinkedBlockingQueue<>();
    //url前缀
    private final String urlPrefix;
    //isodata算法执行类
    private ISOData isoData;
    private final ColorMomentsTableMapper colorMomentsTableMapper;
    private final ColorMomentsClusterMapper colorMomentsClusterMapper;
    private final ORBTableMapper orbTableMapper;

    //当前聚类中点的数量
    private AtomicLong CURRENT_POINT_COUNT;
    //上次更新时聚类中点的个数
    private AtomicLong LATEST_UPDATE_COUNT;
    //更新比例。当新增点的数量大于该比例时聚类会更新
    private Double UPDATE_RATIO = 0.2;
    //存储点id和图片id序号的变量
    private final AtomicInteger idNum = new AtomicInteger(10000);

    //准备加入集群的点的队列
    //private final BlockingQueue<Point> cmPointsToCluster = new LinkedBlockingQueue<>();

    //集群是否正在更新
    private boolean isUpdating;
    //是否正在添加点
    private boolean isAdding;
    //是否初始化完毕
    private boolean isInitializationCompleted = false;

    public ImgScheduler(ImageConfig imageConfig, ColorMomentsTableMapper colorMomentsTableMapper, ColorMomentsClusterMapper colorMomentsClusterMapper, ORBTableMapper orbTableMapper) {
        this.imageConfig = imageConfig;
        this.urlPrefix = imageConfig.getUrlPrefix();
        this.colorMomentsTableMapper = colorMomentsTableMapper;

        this.colorMomentsClusterMapper = colorMomentsClusterMapper;
        this.orbTableMapper = orbTableMapper;
    }

    public void init() throws Exception {
        //添加监听器
        File imgDir = new File(imageConfig.getImgLocation());
        FileAlterationObserver observer = new FileAlterationObserver(imgDir);
        ImgListener imgListener = new ImgListener(imgDir);
        observer.addListener(imgListener);
        FileAlterationMonitor monitor = new FileAlterationMonitor(500, observer);
        monitor.start();

        //记录当前文件夹中图片的数量，作为初始数量
        Counters.PathCounters pathCounters = PathUtils.countDirectory(Paths.get(imgDir.toURI()));
        long INIT_COUNT = pathCounters.getFileCounter().get();
        System.out.println("初始数量为 " + INIT_COUNT);

        //启动loader
        executorService.execute(new ColorMomentsLoader(imgListener.getImageQueue(),this.colorMomentsPointsQueue));
        executorService.execute(new ColorMomentsLoader(imgListener.getImageQueue(),this.colorMomentsPointsQueue));
        executorService.execute(new ColorMomentsLoader(imgListener.getImageQueue(),this.colorMomentsPointsQueue));
        executorService.execute(new ColorMomentsLoader(imgListener.getImageQueue(),this.colorMomentsPointsQueue));

        //当返回的点的数量和初始数量相同时，便可以开始初始化聚类
        List<Pair<File,Point>> initFilePointListTemp = new ArrayList<>();
        boolean isRunning = true;
        while(isRunning){
            if(colorMomentsPointsQueue.size() >= INIT_COUNT){
                //加同步锁，防止线程冲突
                synchronized (colorMomentsPointsQueue){
                    while (colorMomentsPointsQueue.size() != 0){
                        Pair<File, Point> pair = colorMomentsPointsQueue.take();
                        initFilePointListTemp.add(pair);
                    }
                }
                isRunning = false;
            }
            TimeUnit.MILLISECONDS.sleep(500);
        }

        //为点集中的点添加id,并将点单独记录
        List<Point> initPoints = new ArrayList<>();
        List<Pair<File,Point>> initFilePointList = new ArrayList<>();
        for (Pair<File, Point> filePointPair : initFilePointListTemp) {
            File file = filePointPair.getLeft();
            Point point = filePointPair.getRight();
            int id = idNum.getAndIncrement();
            point.setId(id);
            initFilePointList.add(Pair.of(file,point));
            initPoints.add(point);
            //将ORB的结果写入数据库
            //计算mat
            Mat mat = ORBAlgorithm.extractDescriptors(file.getPath());
            ORBTable orbTable = new ORBTable(id,SerializationUtil.serializeMat(mat),urlPrefix + file.getName());
            orbTableMapper.insert(orbTable);
        }





        //添加到聚类中
        isoData = new ISOData(initPoints);
        List<Pair<Point, String>> pointAndClusterId = isoData.init();

        //存入数据库
        List<ColorMomentsTable> colorMomentsTables = new ArrayList<>();
        for (Pair<File, Point> filePointPair : initFilePointList) {
            Point point = filePointPair.getValue();
            File file = filePointPair.getLeft();
            //根据点查找其对应的clusterId
            for (Pair<Point, String> pointStringPair : pointAndClusterId) {
                if(pointStringPair.getLeft().equals(point)){
                    ColorMomentsTable colorMomentsTable = new ColorMomentsTable(point.getId(), pointStringPair.getValue(), SerializationUtil.serialize(point.getCoordinate()), urlPrefix + file.getName());
                    colorMomentsTables.add(colorMomentsTable);
                }
            }
        }

        //设置当前聚类中点的个数
        this.CURRENT_POINT_COUNT = new AtomicLong(INIT_COUNT);
        this.LATEST_UPDATE_COUNT = new AtomicLong(INIT_COUNT);
        //将信息写入到数据库中
        for (ColorMomentsTable colorMomentsTable : colorMomentsTables) {
            colorMomentsTableMapper.insert(colorMomentsTable);
        }

        //获取当前聚类的信息,并加入到数据库中
        List<Cluster> clusterList = isoData.getClusterList();
        for (Cluster cluster : clusterList) {
            String clusterId = cluster.getId();
            int pointCount = cluster.getMembers().size();
            //获取中心点坐标，保留两位小数
            List<Double> coordinates = cluster.getCenter().getCoordinate();
            ColorMomentsCluster colorMomentsCluster = new ColorMomentsCluster(clusterId, pointCount, SerializationUtil.serialize(coordinates));
            colorMomentsClusterMapper.insert(colorMomentsCluster);
        }


        isInitializationCompleted = true;
    }


    //每100ms执行一次
    @Scheduled(fixedRate = 100)
    public void addPointToCluster() throws InterruptedException {
        //如果还没有完成初始化
        if(!isInitializationCompleted) return;
        isAdding = true;
        if(colorMomentsPointsQueue.size() != 0 && !isUpdating){
            //拿到loader返回的颜色矩信息
            Pair<File, Point> pair = colorMomentsPointsQueue.take();
            File file = pair.getLeft();
            Point point = pair.getRight();
            //为点添加id
            int id = idNum.getAndIncrement();
            point.setId(id);
            //将点添加到聚类中并获得聚类id
            String clusterId = isoData.addPointToCluster(point);
            //更新当前集群中点的数量
            this.CURRENT_POINT_COUNT.getAndIncrement();
            //向数据库中添加该点的信息
            colorMomentsTableMapper.insert(new ColorMomentsTable(point.getId(),clusterId,SerializationUtil.serialize(point.getCoordinate()), urlPrefix + file.getName()));
            System.out.println("已添加点 id: " + point.getId());
            //添加orb的信息
            //计算mat
            Mat mat = ORBAlgorithm.extractDescriptors(file.getPath());
            ORBTable orbTable = new ORBTable(id, SerializationUtil.serializeMat(mat), urlPrefix + file.getName());
            orbTableMapper.insert(orbTable);
        }
        isAdding = false;

    }

    //每5秒执行一次
    @Scheduled(fixedRate = 5000)
    public void checkIfUpdateClusters(){
        if(!isInitializationCompleted) return;
        isUpdating = true;
        //当新增点的个数大于更新比率且没有在新增点时进行更新
        if((CURRENT_POINT_COUNT.get() - LATEST_UPDATE_COUNT.get() > (UPDATE_RATIO * CURRENT_POINT_COUNT.get())) && ! isAdding){
            System.out.println("开始更新");
            //更新聚类
            List<Cluster> clusters = isoData.updateClusters();
            //获取聚类中的点
            List<Pair<Point,String>> pointPairs = new ArrayList<>();
            for (Cluster cluster : clusters) {
                for (Point point : cluster.getMembers()) {
                    pointPairs.add(Pair.of(point,cluster.getId()));
                }
            }
            //更新数据库中点的聚类信息
            for (Pair<Point, String> pointPair : pointPairs) {
                Integer id = pointPair.getLeft().getId();
                String clusterId = pointPair.getRight();
                UpdateWrapper<ColorMomentsTable> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("imageId",id);
                ColorMomentsTable colorMomentsTable = new ColorMomentsTable();
                colorMomentsTable.setClusterId(clusterId);
                colorMomentsTableMapper.update(colorMomentsTable,updateWrapper);
            }
            //更新上次更新的点的数量信息
            LATEST_UPDATE_COUNT = CURRENT_POINT_COUNT;



            //更新数据库中的聚类信息
            //先清除已有的聚类信息
            QueryWrapper<ColorMomentsCluster> wrapper = new QueryWrapper<>();
            // 构造删除条件，这里的true表示删除表中所有记录
            wrapper.eq("1", "1");
            colorMomentsClusterMapper.delete(wrapper);
            for (Cluster cluster : clusters) {
                String clusterId = cluster.getId();
                int pointCount = cluster.getMembers().size();
                //获取中心点坐标，保留两位小数
                List<Double> coordinates = cluster.getCenter().getCoordinate();
                ColorMomentsCluster colorMomentsCluster = new ColorMomentsCluster(clusterId, pointCount, SerializationUtil.serialize(coordinates));
                colorMomentsClusterMapper.insert(colorMomentsCluster);
            }

            System.out.println("更新结束");

        }
        isUpdating = false;

    }

    public List<Cluster> getClusterList(){
        if(isUpdating || isAdding){
            return null;
        }
        return isoData.getClusterList();
    }





}

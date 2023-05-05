package com.example.imageRetrieval.image;

import com.example.imageRetrieval.colorMoments.ColorMoments;
import com.example.imageRetrieval.isoData.Point;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: ShiJi
 * @time: 2023/3/9 16:16
 */

public class ColorMomentsLoader implements Runnable {
    //待加载的图片的队列
    private final BlockingQueue<File> imageQueue;
    //给scheduler返回信息的队列
    private final BlockingQueue<Pair<File, Point>> colorMomentsPointsQueue;
    public ColorMomentsLoader(BlockingQueue<File> imageQueue, BlockingQueue<Pair<File, Point>> colorMomentsPointsQueue) {
        this.imageQueue = imageQueue;
        this.colorMomentsPointsQueue = colorMomentsPointsQueue;
    }

    @Override
    public void run() {
        boolean isRunning = true;
        while(isRunning){
            try {
                //获取图片
                File image = imageQueue.take();
                //计算颜色矩
                double[] colorMomentsArray = ColorMoments.calculateColorMoments(image.getPath());
                if(colorMomentsArray == null){
                    throw new RuntimeException("ERROR: ColorMomentsArray is null!");
                }
                List<Double> colorMoments = Arrays.stream(colorMomentsArray).boxed().collect(Collectors.toList());
                Point point = new Point(9, colorMoments);
                colorMomentsPointsQueue.add(Pair.of(image,point));


            } catch (InterruptedException e) {
                e.printStackTrace();
                isRunning = false;
            }
        }
    }
}

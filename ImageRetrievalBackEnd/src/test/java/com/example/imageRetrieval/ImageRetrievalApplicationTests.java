package com.example.imageRetrieval;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.imageRetrieval.image.ImgScheduler;
import com.example.imageRetrieval.isoData.Cluster;
import com.example.imageRetrieval.isoData.ISOData;
import com.example.imageRetrieval.isoData.Point;


import com.example.imageRetrieval.mapper.ColorMomentsTableMapper;
import com.example.imageRetrieval.pojo.ColorMomentsTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ImageRetrievalApplicationTests {
//
//	@Test
//	void testPoint(){
//		Point point1 = new Point(2, 2.1, 2.2);
//		Point point2 = new Point(2, 3.0, 4.0);
//		List<Point> points = new ArrayList<>();
//		points.add(point1);
//		points.add(point2);
//		Cluster cluster = new Cluster(point1);
//		cluster.setMembers(points);
//
//		System.out.println("距离为"+ point1.distance(point2));
//		System.out.println(point1);
//		System.out.println(cluster);
//	}
//
//	@Test
//	void testISOData(){
//		//生成随机数的区间
//		double MAX = 20.0;
//		double MIN = 3.0;
//		//生成随机数并根据其创建点
//		int dimension = 3;//点的维度
//		int pointCount = 30;//点的个数
//
//		Random rand = new Random();
//		List<Point> initPoints = new ArrayList<>();
//		for (int i = 0; i < pointCount; i++) {
//			//生成随机数坐标数组
//			ArrayList<Double> coords = new ArrayList<>();
//			for (int j = 0; j < dimension; j++) {
//				coords.add(MIN + (rand.nextDouble() * (MAX - MIN)));
//			}
//			//生成新的点，并加入集合
//			Point point = new Point(dimension, coords);
//			initPoints.add(point);
//		}
//
//		ISOData isoData = new ISOData(initPoints);
//		isoData.init();
//	}
//
//	@Autowired
//	private ColorMomentsTableMapper colorMomentsTableMapper;
//
//	@Test
//	void testDataBase() throws InterruptedException {
////		ColorMomentsTable testEntity = new ColorMomentsTable(1, "Cluster-1", "aaaaaaaaaaaaaaaaa", "http:aaaaaaaaaaaa");
////		colorMomentsTableMapper.insert(testEntity);
////		TimeUnit.SECONDS.sleep(2);
////		ColorMomentsTable colorMomentsTable = colorMomentsTableMapper.selectById(1);
////		System.out.println(colorMomentsTable);
//
//	}
//
//	@Autowired
//	private ImgScheduler imgScheduler;
//
//	@Test
//	void testImage() throws Exception {
//		imgScheduler.init();
//		TimeUnit.SECONDS.sleep(5);
//		QueryWrapper<ColorMomentsTable> colorMomentsTableQueryWrapper = new QueryWrapper<>();
//		colorMomentsTableQueryWrapper.lt("imageId",100000);
//		List<ColorMomentsTable> colorMomentsTables = colorMomentsTableMapper.selectList(colorMomentsTableQueryWrapper);
//		for (ColorMomentsTable colorMomentsTable : colorMomentsTables) {
//			System.out.println(colorMomentsTable);
//		}
//	}
//
//
//
//	@Test
//	void testColorMoments(){
//
//	}

}

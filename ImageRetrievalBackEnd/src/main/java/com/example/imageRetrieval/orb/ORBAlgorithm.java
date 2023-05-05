package com.example.imageRetrieval.orb;

import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

public class ORBAlgorithm {
    static {
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);   // 加载OpenCV库
        nu.pattern.OpenCV.loadShared();
    }



    // 匹配器参数
    private static final int MATCHER_NORM_TYPE = Core.NORM_HAMMING;// 归一化类型
    private static final double threshold = 75;// 相似度计算方法一的距离阈值    结果不佳时可调参
    private static final double MATCHER_MIN_DIST_RATIO = 0.9;// 相似度计算方法二的距离比率阈值

    // ORB 实例
    private static final ORB orb = ORB.create();
    // （如果最后计算的相似度不符合常理可以直接调ORB参数）
    //这是调参数举的一个例子
    //ORB orb = ORB.create(1000, 1.1f, 11, 50, 0, 2, ORB.HARRIS_SCORE, 31, 20);

    /**
     *从给定路径的图像中提取ORB描述符。
     *
     * @param imagePath 要处理的图像的路径
     * @return 图像的ORB描述符
     */
    public static Mat extractDescriptors(String imagePath) {
        Mat image = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);
        //将图片调成同一尺寸（如果最后计算的相似度不佳可以尝试去除）
        Size scaleSize = new Size(256,256);
        Imgproc.resize(image,image,scaleSize,0,0);
        // 直方图均衡化（如果最后计算的相似度不佳可以尝试去除）
        Imgproc.equalizeHist(image, image);
        // 高斯模糊处理（如果最后计算的相似度不佳可以尝试去除）
        Mat blurredImg1 = new Mat();
        Imgproc.GaussianBlur(image, blurredImg1, new Size(7, 7), 0);

        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        Mat descriptors = new Mat();
        ///////////
        // 提取des矩阵这一块有2种方法，要根据数据集的具体情况进行选择
        //////////////////////////////

        ////方法1 ORB结合Fast方法        适合于我目前找到的caltech-101数据库
        FastFeatureDetector detector = FastFeatureDetector.create();
        detector.detect(image, keypoints);
        orb.compute(image, keypoints, descriptors);

        ////方法2   orb直接计算
        //orb.detectAndCompute(image, new Mat(), keypoints, descriptors);
        return descriptors;
    }

    /**
     *计算两组ORB描述符之间的相似度。
     *
     * @param descriptors1 第一幅图像的描述符
     * @param descriptors2 第二幅图像的描述符
     * @return 两幅图像之间的相似度得分
     */
    public static double calculateSimilarity(Mat descriptors1, Mat descriptors2) {
        /////////////////////////////////
        // 特征提取这一块有不同的特征提取器，要根据具体数据集进行选择
        ///////////////////////
        ////方法1  matcher
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(descriptors1, descriptors2, matches);

        ////方法2  BFMatcher
        //DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        // 进行knnMatch匹配
        // List<MatOfDMatch> knnMatches = new ArrayList<>();
        //matcher.knnMatch(des1, des2, knnMatches, 2);


        ////////////////////////////
        // 相似度计算有三种方法
        //////////////////////////

        ////方法1  阈值计算  （这个方法按照道理应该是最不精准的，但是在我的数据测试上表现的最优）
        List<DMatch> goodMatches = new ArrayList<>();
        for (int i = 0; i < matches.rows(); i++) {
            DMatch match = matches.toList().get(i);
            if (match.distance <= threshold) {// 设置特征点的距离，小于阈值的才认为有效  阈值此处用我的测试集50是比较合适的，阈值需要根据具体情况调整
                goodMatches.add(match);
            }
        }
        // 计算相似度
        double similarity = (double) goodMatches.size() / (double) matches.rows();
        //System.out.println("两张图片相似度为:" + similarity);

        ////方法2  根据距离比值筛选匹配点 （此处给出的代码是基于特征提取选择了方法2 BFMather的情况）
//        int normType = NORM_HAMMING;
//        List<DMatch> goodMatches = new ArrayList<>();
//        System.out.println(knnMatches.size());
//        for (int i = 0; i < knnMatches.size(); i++) {
//            DMatch[] matches = knnMatches.get(i).toArray();
//            if (matches.length >= 2) {
//                DMatch m = matches[0];
//                DMatch n = matches[1];
//                if (m.distance <  MATCHER_MIN_DIST_RATIO * n.distance) {   //这个距离的阈值可调
//                    goodMatches.add(m);
//                }
//            }
//        }
        //计算相似度
//        double similarity = (double) goodMatches.size() / (double) knnMatches.size();
//      //  System.out.println("两张图片相似度为:" + similarity);

        ////方法3  使用RANSAC算法
//        List<Point> img1Points = new ArrayList<Point>();
//        List<Point> img2Points = new ArrayList<Point>();
//        for (int i = 0; i < matches.size().height; i++) {
//            DMatch match = matches.toArray()[i];
//            img1Points.add(kp1.toList().get(match.queryIdx).pt);
//            img2Points.add(kp2.toList().get(match.trainIdx).pt);
//        }
//        MatOfPoint2f img1PointsMat = new MatOfPoint2f();
//        img1PointsMat.fromList(img1Points);
//        MatOfPoint2f img2PointsMat = new MatOfPoint2f();
//        img2PointsMat.fromList(img2Points);
//        Mat mask = new Mat();
//        Mat homography = Calib3d.findHomography(img1PointsMat, img2PointsMat, Calib3d.RANSAC, 5, mask);
//        List<DMatch> goodMatches = new ArrayList<DMatch>();
//        for (int i = 0; i < mask.rows(); i++) {
//            if (mask.get(i, 0)[0] != 0) {
//                goodMatches.add(matches.toList().get(i));
//            }
//        }
//        // 计算相似度
//        double similarity = (double) goodMatches.size() / (double) matches.size().height;
//      //  System.out.println(similarity);

        return similarity;
    }
//////////////////
// 调用测试
 ///////////////
//    public static void main(String[] args) {
//        Mat des1= extractDescriptors("D:\\picture\\pagoda\\image_0001.jpg");
//        Mat des2=extractDescriptors("D:\\picture\\pagoda\\image_0002.jpg");
//        double result=calculateSimilarity(des1,des2);
//        System.out.println(result);
//    }
}
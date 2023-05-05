package com.example.imageRetrieval.colorMoments;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class ColorMoments {
	static {
		nu.pattern.OpenCV.loadShared();
	}

	private static final double W1 = 0.8;
	private static final double W2 = 0.2;
	private static final double W3 = 0.2;

	/**
	 * 计算颜色矩
	 * @param path 图片路径
	 * @return double[] 9个颜色矩
	 */
	public static double[] calculateColorMoments(String path) {
		//将图片读入到Matrix中
		Mat src = Imgcodecs.imread(path);
		//默认图像为3通道RGB格式
		if (src.channels()!=3) {
			System.out.println("Channels!=3");
			return null;
		}
		//RGB --> HSV
		Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2HSV);
		//统一尺寸
		Size scaleSize = new Size(256,256);
		Imgproc.resize(src,src,scaleSize,0,0);
		//将3通道矩阵分开
		ArrayList<Mat> channels = new ArrayList<>(3);
		Core.split(src, channels);
		Mat H = channels.get(0);
		Mat S = channels.get(1);
		Mat V = channels.get(2);

		//Color Moments
		//Mean - First order moments
		//Standard Deviation - Second order moments
		//Skewness - Third order moments
		MatOfDouble H_mean = new MatOfDouble(), H_stddev = new MatOfDouble();
		MatOfDouble S_mean = new MatOfDouble(), S_stddev = new MatOfDouble();
		MatOfDouble V_mean = new MatOfDouble(), V_stddev = new MatOfDouble();

		//Calculate Mean & Standard Deviation
		Core.meanStdDev(H ,H_mean,H_stddev);
		Core.meanStdDev(S ,S_mean,S_stddev);
		Core.meanStdDev(V ,V_mean,V_stddev);
		//Calculate Skewness
		double H_skewness = calculateSkewness(H,H_mean);
		double S_skewness = calculateSkewness(S,S_mean);
		double V_skewness = calculateSkewness(V,V_mean);

		//Mat type --> double type
		double[] H_mean_double = H_mean.get(0, 0);
		double[] S_mean_double = S_mean.get(0, 0);
		double[] V_mean_double = V_mean.get(0, 0);
		double[] H_stddev_double = H_stddev.get(0, 0);
		double[] S_stddev_double = S_stddev.get(0, 0);
		double[] V_stddev_double = V_stddev.get(0, 0);

		//destructor
		H.release();
		S.release();
		V.release();
		H_mean.release();
		S_mean.release();
		V_mean.release();
		H_stddev.release();
		S_stddev.release();
		V_stddev.release();
		return new double[]{H_mean_double[0],S_mean_double[0],V_mean_double[0],H_stddev_double[0],S_stddev_double[0],V_stddev_double[0],H_skewness,S_skewness,V_skewness};
	}
	
	private static double calculateSkewness(Mat src, Mat mean){
		MatOfDouble tempA= new MatOfDouble(CvType.CV_64F);
		MatOfDouble tempB= new MatOfDouble(CvType.CV_64F);
		Core.subtract(src,mean,tempA);
		Core.pow(tempA,3,tempB);
		Scalar tempD = Core.mean(tempB);
		return Math.cbrt(tempD.val[0]);
	}

	/**
	 * 计算相似度
	 * @param momentsA A图颜色矩
	 * @param momentsB B图颜色矩
	 * @return double 相似度
	 */
	public static double calculateSimilarityScore(double[] momentsA, double[] momentsB) {
		double result = 0.0;
		for(int i = 0; i < 9; i++){
			if(i < 3){
				result += Math.abs(momentsA[i] - momentsB[i]) * W1;
			}
			else if(i < 6){
				result += Math.abs(momentsA[i] - momentsB[i]) * W2;
			}
			else {
				result += Math.abs(momentsA[i] - momentsB[i]) * W3;
			}
		}
		return result;
	}
}


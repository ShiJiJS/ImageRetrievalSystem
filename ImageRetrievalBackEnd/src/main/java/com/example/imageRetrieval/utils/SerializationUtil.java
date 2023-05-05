package com.example.imageRetrieval.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.imageRetrieval.pojo.ColorMomentsCluster;
import com.example.imageRetrieval.pojo.ColorMomentsTable;
import com.example.imageRetrieval.pojo.ORBTable;
import org.nustaq.serialization.FSTConfiguration;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.Serializable;
import java.util.List;
import java.util.StringJoiner;

/**
 * @description:
 * @author: ShiJi
 * @time: 2023/3/17 19:45
 */
public class SerializationUtil {

    static FSTConfiguration configuration = FSTConfiguration
            .createDefaultConfiguration();
            //.createStructConfiguration();

    public static byte[] serialize(Object obj) {
        return configuration.asByteArray(obj);
    }

    public static byte[] serializeMat(Mat mat){
        DoubleMatrix doubleMatrix = new DoubleMatrix(mat.rows(), mat.cols());

        for (int i = 0; i < mat.rows(); i++) {
            for (int j = 0; j < mat.cols(); j++) {
                doubleMatrix.set(i,j,mat.get(i,j)[0]);
            }
        }
        return serialize(doubleMatrix);
    }

    public static Object unSerialize(byte[] sec) {
        return configuration.asObject(sec);
    }
    

    public static List<Double> unSerializedAsDoubleList(byte[] sec){
        Object object = configuration.asObject(sec);
        if(!(object instanceof List<?>)){ //如果类型不匹配
            throw new RuntimeException("ERROR: 反序列化的类型不匹配");
        }
        return (List<Double>)object;
    }

    public static DoubleMatrix unSerializeDoubleMatrix(byte[] sec){
        Object object = configuration.asObject(sec);
        if(!(object instanceof DoubleMatrix)){
            throw new RuntimeException("ERROR: 反序列化的类型不匹配");
        }
        return (DoubleMatrix) object;
    }

    public static Mat unSerializeMat(byte[] sec){
        DoubleMatrix doubleMatrix = unSerializeDoubleMatrix(sec);
        Mat mat = new Mat(doubleMatrix.getRows(),doubleMatrix.getCols(), CvType.CV_8U);
        for (int i = 0; i < doubleMatrix.getRows(); i++) {
            for (int j = 0; j < doubleMatrix.getCols(); j++) {
                mat.put(i,j,doubleMatrix.get(i,j));
            }
        }
        return mat;
    }

    public static JSONObject unSerializeColorMomentsTable(ColorMomentsTable colorMomentsTable){
        JSONObject returnObject = new JSONObject();
        returnObject.put("imageId",colorMomentsTable.getImageId());
        returnObject.put("clusterId",colorMomentsTable.getClusterId());
        //保留两位小数
        List<Double> doubleList = SerializationUtil.unSerializedAsDoubleList(colorMomentsTable.getCmVector());
        ListFormatter.formatDoubleList(doubleList);
        returnObject.put("cmVector",doubleList);

        returnObject.put("url",colorMomentsTable.getUrl());
        return returnObject;
    }

    public static JSONObject unSerializeColorMomentsCluster(ColorMomentsCluster colorMomentsCluster){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("clusterId",colorMomentsCluster.getClusterId());
        jsonObject.put("pointCount",colorMomentsCluster.getPointCount());
        List<Double> doubleList = SerializationUtil.unSerializedAsDoubleList(colorMomentsCluster.getCenterCoordinate());
        ListFormatter.formatDoubleList(doubleList);
        jsonObject.put("centerCoordinate",doubleList);
        return jsonObject;
    }

    public static JSONObject unSerializeORBTable(ORBTable orbTable){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("imageId",orbTable.getImageId());
        //获取mat
        DoubleMatrix doubleMatrix = SerializationUtil.unSerializeDoubleMatrix(orbTable.getMat());
        //特征描述子的数量
        jsonObject.put("fdNums",doubleMatrix.getRows());

        jsonObject.put("url",orbTable.getUrl());
        return jsonObject;
    }




}

package com.example.imageRetrieval.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * @description:
 * @author: ShiJi
 * @time: 2023/3/9 8:45
 */
@Data
@TableName("color_moments_table")
@AllArgsConstructor
public class ColorMomentsTable {

    // 图片的id
    @TableId("imageId")
    private Integer imageId;

    //所属的聚类的id
    @TableField("clusterId")
    private  String  clusterId;

    //其所对应的颜色矩向量（经过序列化）
    @TableField("cmVector")
    private byte[] cmVector;

    //图片所对应的url
    @TableField("url")
    private String url;

    public ColorMomentsTable() {
    }
}

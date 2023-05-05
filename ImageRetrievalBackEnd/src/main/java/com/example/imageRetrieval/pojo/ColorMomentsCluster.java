package com.example.imageRetrieval.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description:
 * @author: ShiJi
 * @time: 2023/3/10 19:21
 */

@Data
@TableName("color_moments_clusters")
@AllArgsConstructor
public class ColorMomentsCluster {
    @TableId("clusterId")
    private String clusterId;
    @TableField("pointCount")
    private Integer pointCount;
    @TableField("centerCoordinate")
    private byte[] centerCoordinate;
}

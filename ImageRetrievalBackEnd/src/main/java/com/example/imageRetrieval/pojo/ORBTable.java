package com.example.imageRetrieval.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description:
 * @author: ShiJi
 * @time: 2023/3/17 20:41
 */

@Data
@TableName("orb_table")
@AllArgsConstructor
public class ORBTable {
    //图片的id
    @TableId("imageId")
    private Integer imageId;
    //其所对应的ORB Mat（序列化）
    @TableField("mat")
    private byte[] mat;
    //图片所对应的url
    @TableField("url")
    private String url;

    public ORBTable() {
    }
}

package com.example.imageRetrieval.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ShiJi
 * @create 2023/4/27 14:20
 */
@Data
@TableName("image_anno_table")
@AllArgsConstructor
@NoArgsConstructor
public class ImageAnnoTable {
    @TableId(value = "pair_id", type = IdType.AUTO)
    private Integer pairId;
    @TableField("image_location_1")
    private String imageLocation1;
    @TableField("image_location_2")
    private String imageLocation2;
    @TableField("image_url_1")
    private String imageURL1;
    @TableField("image_url_2")
    private String imageURL2;
    @TableField("human_anno")
    private Boolean humanAnno;
    @TableField("color_moments_anno")
    private Boolean colorMomentsAnno;
    @TableField("orb_anno")
    private Boolean orbAnno;


}

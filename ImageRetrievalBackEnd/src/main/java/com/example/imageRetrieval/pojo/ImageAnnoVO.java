package com.example.imageRetrieval.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ShiJi
 * @create 2023/4/27 15:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageAnnoVO {
    private Integer pairId;
    private String imageURL1;
    private String imageURL2;
}

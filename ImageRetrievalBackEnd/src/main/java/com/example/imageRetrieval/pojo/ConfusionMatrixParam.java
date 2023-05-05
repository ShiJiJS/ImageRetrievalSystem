package com.example.imageRetrieval.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author ShiJi
 * @create 2023/4/27 16:51
 */
@NoArgsConstructor
public class ConfusionMatrixParam {
    public Integer TP = 0;
    public Integer FP = 0;
    public Integer FN = 0;
    public Integer TN = 0;

}

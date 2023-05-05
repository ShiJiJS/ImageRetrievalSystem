package com.example.imageRetrieval.pojo;

import lombok.Data;

/**
 * @description:
 * @author: ShiJi
 * @time: 2023/3/18 10:27
 */
@Data
public class SearchResult implements Comparable<SearchResult>{

    private Integer imageId;
    private String url;
    private Double similarityScore;


    @Override
    public int compareTo(SearchResult o) {
        return Double.compare(o.getSimilarityScore(), this.similarityScore);
    }
}

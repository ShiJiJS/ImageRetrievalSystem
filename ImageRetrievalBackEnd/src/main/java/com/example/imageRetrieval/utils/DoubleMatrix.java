package com.example.imageRetrieval.utils;

import java.io.Serializable;

/**
 * @description:
 * @author: ShiJi
 * @time: 2023/3/18 8:48
 */
public class DoubleMatrix implements Serializable {
    private final int rows;
    private final int cols;
    private final double[][] data;

    public DoubleMatrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
    }

    public DoubleMatrix(double[][] data) {
        this.rows = data.length;
        this.cols = data[0].length;
        this.data = data.clone();
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public double get(int row, int col) {
        return data[row][col];
    }

    public void set(int row, int col, double value) {
        data[row][col] = value;
    }

    public DoubleMatrix transpose() {
        DoubleMatrix result = new DoubleMatrix(cols, rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.set(j, i, get(i, j));
            }
        }
        return result;
    }

    public DoubleMatrix multiply(DoubleMatrix other) {
        if (cols != other.rows) {
            throw new IllegalArgumentException("Invalid matrix dimensions");
        }
        DoubleMatrix result = new DoubleMatrix(rows, other.cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                double sum = 0;
                for (int k = 0; k < cols; k++) {
                    sum += get(i, k) * other.get(k, j);
                }
                result.set(i, j, sum);
            }
        }
        return result;
    }

    public DoubleMatrix add(DoubleMatrix other) {
        if (rows != other.rows || cols != other.cols) {
            throw new IllegalArgumentException("Invalid matrix dimensions");
        }
        DoubleMatrix result = new DoubleMatrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.set(i, j, get(i, j) + other.get(i, j));
            }
        }
        return result;
    }

    public DoubleMatrix subtract(DoubleMatrix other) {
        if (rows != other.rows || cols != other.cols) {
            throw new IllegalArgumentException("Invalid matrix dimensions");
        }
        DoubleMatrix result = new DoubleMatrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.set(i, j, get(i, j) - other.get(i, j));
            }
        }
        return result;
    }

    public static DoubleMatrix identity(int n) {
        DoubleMatrix result = new DoubleMatrix(n, n);
        for (int i = 0; i < n; i++) {
            result.set(i, i, 1);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                builder.append(String.format("%8.2f", get(i, j)));
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}

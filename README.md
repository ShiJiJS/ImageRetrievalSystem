# 图像检索系统

## 技术栈

前端：Vue3 + PicoCSS

后端：SpringBoot + Mybatis Plus 

数据库：H2 Memory Database

## 算法 Algorithm

图像匹配：ORB和Color Moments

聚类：ISODATA

## 概要 Abstract

系统主要功能为图像匹配，即输入一张图片，从数据库中找到类似的图片。按照相似度排序显示结果。

系统有三个主要过程，初始化，聚类热更新，和图片搜索

The main function of the system is image matching, that is, input a picture and find similar pictures from the database. Display the results in order of similarity.

The system has three main processes, initialization, clustering hot update, and image search

## 初始化 Initialization

![](https://shijivk-blog.oss-cn-beijing.aliyuncs.com/img/%E5%88%9D%E5%A7%8B%E5%8C%96.jpg)

初始化的主要过程为初始化器采用多线程的方式读取本地目录下指定文件夹内的图片。分别用颜色矩算法和ORB算法计算出它的特征向量。因为颜色矩算法得出的特征向量为定长，为了提高检索速度。对颜色矩算法所产生的特征向量进行聚类处理。聚类采用ISODATA算法，将两种算法的特征向量和对颜色矩特征向量的聚类结果存入数据库，以便后续查找使用。

The main process of initialization is that the initializer reads the images in the specified folder in the local directory in a multi-threaded way. Color moment algorithm and ORB algorithm were used to calculate its feature vectors. Because the feature vector obtained by the color moment algorithm is of fixed length, in order to improve the retrieval speed. The feature vectors generated by the color moment algorithm are processed by clustering. ISODATA algorithm was used for clustering, and the feature vectors of the two algorithms and the clustering results of the color moment feature vectors were stored in the database for the convenience of subsequent search and use.

## 聚类热更新 Clustering hot update

### ISODATA聚类算法

对于颜色矩算法生成的特征向量。我们使用的是ISODATA算法进行聚类处理。

For the feature vector generated by the color moment algorithm. We use ISODATA algorithm for clustering processing.

#### 介绍

ISODATA（Iterative Self-Organizing Data Analysis Technique）是一种迭代自组织数据分析技术，它是一种无监督的聚类算法，主要用于对数据集进行分类。

ISODATA算法的优点是能够自动调整聚类数目和聚类中心，适应不同形状和大小的类别。然而，该算法对初始聚类中心的选择敏感，不同的初始聚类中心可能导致不同的聚类结果。另外，ISODATA算法需要设定多个参数，如距离阈值和最大迭代次数，这些参数的选择可能影响算法的性能。

ISODATA (Iterative Self-Organizing Data Analysis Technique) is an iterative self-organizing data analysis technique. It is an unsupervised clustering algorithm that is used to classify data sets.

The advantage of ISODATA algorithm is that it can automatically adjust the number of cluster and cluster center to adapt to different shapes and sizes of categories. However, the algorithm is sensitive to the selection of initial clustering centers, and different initial clustering centers may lead to different clustering results. In addition, ISODATA algorithm needs to set several parameters, such as distance threshold and maximum number of iterations, and the selection of these parameters may affect the performance of the algorithm.

#### 实现 Implication

主要由三个类构成，Point，Cluster定义了点和聚类。其中点的维度等参数可以自行设定。

ISOData为聚类的主要控制器，负责初始化，点的添加，聚类的热更新之类的操作。

It is mainly composed of three classes, Point and Cluster, which define point and cluster. The dimensions and other parameters of the points can be set by themselves.

ISOData is the main controller of clustering, responsible for initialization, point addition, cluster hot update and other operations.

### 热更新

![](https://shijivk-blog.oss-cn-beijing.aliyuncs.com/img/%E7%83%AD%E6%9B%B4%E6%96%B0.jpg)

我们在java中自行实现了可以支持热更新的ISODATA算法。其热更新的主要流程如上图。

当监听器监听到文件目录下的文件添加时，监听器会通过消息队列通知调度器，调度器会定时处理消息。每隔一段时间，调度器都会从消息队列中查看是否有监听器传来的新的文件添加消息，如果有，则将这些图片中提取出的特征向量添加到聚类中，并将聚类信息添加到数据库中保存。

调度器也会定期检查距离上次更新为止已经添加的文件数量。如果达到一定的数量，调度器就会阻塞来自监听器的消息。并开始对聚类进行更新操作，此时会重新计算聚类中心，并对点的数量过多或过少的聚类进行分裂和合并的处理。当更新完后则会重新开始接受来自监听器的添加操作。

We have implemented ISODATA algorithm in java which can support hot update. The main process of its hot update is shown in the figure above.

When the listener listens for the addition of a file in the file directory, the listener notifies the scheduler through the message queue, and the scheduler periodically processes the message. Every once in a while, the scheduler will check whether there is a new file addition message from the message queue from the listener. If there is, the feature vector extracted from these pictures will be added to the cluster, and the cluster information will be added to the database for saving.

The scheduler also periodically checks the number of files that have been added since the last update. If a certain amount is reached, the scheduler blocks messages from the listener. At this time, the clustering center will be recalculated, and the clustering with too many or too few points will be split and merged. When the update is complete, it will restart accepting additions from the listener.

## 图片检索

![](https://shijivk-blog.oss-cn-beijing.aliyuncs.com/img/%E5%9B%BE%E7%89%87%E6%A3%80%E7%B4%A2.jpg)

图像检索过程较为简单。前端Web页面通过Axios请求将图片等请求信息传递给后端的SpringBoot程序。后端首先调用颜色矩和ORB算法，计算出传入图片在两种算法下的特征向量。

对于颜色矩的特征向量，先找到其距离最近的一个聚类中心，然后再从数据库中遍历查找对应聚类的图片，分别计算相似度，按照排序结果返回。

对于ORB算法的特征向量，直接在数据库中遍历查找计算匹配度，按照匹配度由高到低的顺序返回。

The image retrieval process is relatively simple. The front end Web page passes the request information, such as images, to the SpringBoot program on the back end through Axios requests. The back end first calls the color moment and ORB algorithm to calculate the feature vector of the incoming image under the two algorithms.

For the feature vector of the color moment, the nearest clustering center is first found, and then the image of the corresponding clustering is searched through the database. The similarity is calculated respectively, and the sorting results are returned.

For the feature vector of ORB algorithm, the matching degree is directly searched and calculated through traversal in the database, and returned in order of the matching degree from high to low.

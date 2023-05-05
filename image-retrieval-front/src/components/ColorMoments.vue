<template>
  <div>
    <h5>颜色矩的聚类信息</h5>

    <table>
      <thead>
        <tr>
          <th scope="col">聚类id</th>
          <th scope="col">点的数量</th>
          <th scope="col">中心坐标 (此处只保留两位小数)</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="clusterInfo in clusterInfos" :key="clusterInfo.clusterId">
          <td>{{ clusterInfo.clusterId }}</td>
          <td>{{ clusterInfo.pointCount }}</td>
          <td>{{ clusterInfo.centerCoordinate }}</td>
        </tr>
      </tbody>
    </table>

    <h5>每个聚类所包含的点</h5>
    <select @change="handleSelectedClusterChange" v-model="selectedCluster">
      <option value="" disabled selected>选择聚类</option>
      <option v-for="clusterInfo in clusterInfos" :key="clusterInfo.clusterId" :value="clusterInfo.clusterId">{{ clusterInfo.clusterId }}</option>
    </select>

    <table>
      <thead>
        <tr>
          <th scope="col">图片id</th>
          <th scope="col">所属聚类的id</th>
          <th scope="col">图片对应的颜色矩向量</th>
          <th scope="col">图片展示</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="imageInfo in imageInfos" :key="imageInfo.imageId">
          <td>{{ imageInfo.imageId }}</td>
          <td>{{ imageInfo.clusterId }}</td>
          <td>{{ imageInfo.cmVector }}</td>
          <td><img :src="imageInfo.url" /></td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import request from "../utils/request";
export default {
  mounted() {
    this.getClusterInfos();
  },

  methods: {
    getClusterInfos() {
      request({
        url: "/colorMoments/getClusterInfos/5/1",
        method: "get",
      }).then((response) => {
        this.clusterInfos = response.data.data;
      });
    },

    handleSelectedClusterChange() {
      var data = { clusterId: this.selectedCluster };
      request({
        url: "/colorMoments/getPointsByClusterId/" + this.clusterPageSize + "/" + this.clusterCurrentPage,
        method: "post",
        headers: {
          "Content-Type": "application/json",
        },
        data: JSON.stringify(data),
      }).then((response) => {
        this.imageInfos = response.data.data;
      });
    },
  },

  data() {
    return {
      clusterInfos: [],
      imageInfos: [],
      selectedCluster: "",
      clusterPageSize: 10,
      clusterCurrentPage: 1,
      pointPageSize: 20,
      pointCurrentPage: 1,
    };
  },
};
</script>

<style></style>

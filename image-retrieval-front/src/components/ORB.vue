<template>
  <div>
    <h5>ORB的信息</h5>

    <table>
      <thead>
        <tr>
          <th scope="col">图片id</th>
          <th scope="col">特征描述子的数量</th>
          <th scope="col">图片展示</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="orbInfo in orbInfos" :key="orbInfo.imageId">
          <td>{{ orbInfo.imageId }}</td>
          <td>{{ orbInfo.fdNums }}</td>
          <td><img :src="orbInfo.url" /></td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import request from "../utils/request";
export default {
  mounted() {
    this.getORBInfos();
  },
  methods: {
    getORBInfos() {
      request({
        url: "/orb/getOrbInfos/10/1",
        method: "get",
      }).then((response) => {
        this.orbInfos = response.data.data;
      });
    },
  },

  data() {
    return {
      orbInfos: [],
    };
  },
};
</script>

<style></style>

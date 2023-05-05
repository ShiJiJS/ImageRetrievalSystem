<template>
  <div>
    <h1>以图搜图</h1>
    <input type="file" ref="fileInput" @change="onFileChange" />
    <button type="button" @click="searchImage">Upload</button>
    <div>{{ message }}</div>

    <h1>颜色矩搜索结果</h1>
    <table>
      <thead>
        <tr>
          <th scope="col">图片id</th>
          <th scope="col">匹配值（越低相似度越高）</th>
          <th scope="col">图片展示</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="cmSearchResult in searchInfos.cmSearchResults" :key="cmSearchResult.imageId">
          <td>{{ cmSearchResult.imageId }}</td>
          <td>{{ cmSearchResult.similarityScore }}</td>
          <td><img :src="cmSearchResult.url" /></td>
        </tr>
      </tbody>
    </table>

    <h1>ORB搜索结果</h1>

    <table>
      <thead>
        <tr>
          <th scope="col">图片id</th>
          <th scope="col">相似度</th>
          <th scope="col">图片展示</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="orbSearchResult in searchInfos.orbSearchResults" :key="orbSearchResult.imageId">
          <td>{{ orbSearchResult.imageId }}</td>
          <td>{{ orbSearchResult.similarityScore }}</td>
          <td><img :src="orbSearchResult.url" /></td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from "axios";
import requestConst from "@/const/requestConst";
export default {
  data() {
    return {
      file: null,
      message: "",
      searchInfos: {},
    };
  },

  methods: {
    onFileChange(e) {
      this.file = e.target.files[0];
    },
    searchImage() {
      let formData = new FormData();
      formData.append("name", this.file.name);
      formData.append("file", this.file);
      try {
        var _this = this;
        axios.post(requestConst.baseURL + "/searchImage", formData).then((response) => {
          _this.searchInfos = response.data.data;
        });
        this.message = "File uploaded successfully";
      } catch (err) {
        this.message = "Failed to upload file";
      }
    },
  },
};
</script>

<style></style>

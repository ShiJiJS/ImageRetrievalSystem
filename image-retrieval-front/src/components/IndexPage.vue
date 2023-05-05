<template>
  <div class="container">
    <!-- 导航栏 -->
    <nav>
      <ul>
        <li><strong>图片检索系统</strong></li>
      </ul>
      <ul>
        <li><a @click="componentName = 'ColorMoments'">颜色矩</a></li>
        <li><a @click="componentName = 'ORB'">ORB</a></li>
        <li><button @click="componentName = 'SearchImage'">搜索图片</button></li>
        <li><button @click="componentName = 'ImageAnnotation'">图片相似标注</button></li>
      </ul>
    </nav>

    <component :is="componentName"></component>
  </div>
</template>

<script>
import ColorMoments from "./ColorMoments.vue";
import ORB from "./ORB.vue";
import SearchImage from "./SearchImage.vue";
import ImageAnnotation from "./ImageAnnotation.vue"
export default {
  components: {
    ColorMoments,
    ORB,
    SearchImage,
    ImageAnnotation
  },

  data() {
    return {
      componentName: "SearchImage",
    };
  },
};
</script>

<style></style>

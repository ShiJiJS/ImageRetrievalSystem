<template>
  <div id="app">
    <IndexPage></IndexPage>
  </div>
</template>

<script>
import IndexPage from "./components/IndexPage.vue";

export default {
  name: "App",
  components: {
    IndexPage,
  },
};
</script>

<style>
body {
  margin: 0;
  padding: 0;
}
</style>

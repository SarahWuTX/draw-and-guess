<template>
  <div class="gif">
    <img :src="url" style="width: 15rem; height: 15rem;">
  </div>
</template>

<script>
export default {
  data() {
    return {
      url: null
    };
  },
  props: {
    src: ""
  },
  mounted() {
    this.url = this.src;
    setTimeout(() => {
      var gif = document.getElementsByTagName("img")[0];
      gif.style = "display: none";
      gif.src = null;
    }, 1500);
  }
};
</script>

<style scoped>
.gif {
  position: absolute;
  z-index: 10;
  display: inline;
}
</style>

<template>
  <div class="gif">
    <img :src="url" style="width: 18rem; height: 18rem;" class="gifImg">
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
    src: String
  },
  mounted() {
    this.url = this.src;
    setTimeout(() => {
      var gif = document.getElementsByClassName("gifImg")[0];
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

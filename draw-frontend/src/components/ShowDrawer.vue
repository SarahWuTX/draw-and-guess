<template>
  <div class="showDrawer">
    <h1 class="ml12">本轮由"{{name}}"作画</h1>
  </div>
</template>

<script>
import anime from "animejs";

export default {
  props: {
    name: String
  },
  mounted() {
    var letterEle = document.getElementsByClassName("ml12")[0];
    var letters = letterEle.textContent.split("");
    letterEle.textContent = "";
    for (var i in letters) {
      var ele = document.createElement("span");
      ele.textContent = letters[i];
      ele.style = "margin: 0.5rem; font-size: 1.5rem;";
      ele.className = "letter";
      letterEle.appendChild(ele);
    }
    anime
      .timeline({ loop: false })
      .add({
        targets: ".letter",
        translateX: [100, 0],
        opacity: [0, 1],
        easing: "easeOutExpo",
        duration: 1500,
        delay: function(el, i) {
          return 500 + 30 * i;
        }
      })
      .add({
        targets: ".letter",
        translateX: [0, -100],
        opacity: [1, 0],
        easing: "easeInExpo",
        duration: 1500,
        delay: function(el, i) {
          return 20 * i;
        }
      });
  }
};
</script>

<style scoped>
.ml12 {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  position: absolute;
  font-weight: 10;
  letter-spacing: 0.5em;
  left: 0;
  right: 0;
  top: 10rem;
  z-index: 10;
  text-align: center;
}
</style>

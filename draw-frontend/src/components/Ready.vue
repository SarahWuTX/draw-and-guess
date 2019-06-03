<template>
  <div class="ready">
    <div class="ml4" style="text-align: center;">
      <div class="letters letters-1">Ready</div>
      <div class="letters letters-2">Go!</div>
    </div>
  </div>
</template>

<script>
import anime from "animejs";

export default {
  mounted() {
    var ml4 = {};
    ml4.opacityIn = [0, 1];
    ml4.scaleIn = [0, 1];
    ml4.scaleOut = 3;
    ml4.durationIn = 1000;
    ml4.durationOut = 600;
    ml4.delay = 500;

    anime
      .timeline({ loop: false })
      .add({
        targets: ".ml4 .letters-1",
        opacity: ml4.opacityIn,
        scale: ml4.scaleIn,
        duration: ml4.durationIn
      })
      .add({
        targets: ".ml4 .letters-1",
        opacity: 0,
        scale: ml4.scaleOut,
        duration: ml4.durationOut,
        easing: "easeInExpo",
        delay: ml4.delay
      })
      .add({
        targets: ".ml4 .letters-2",
        opacity: ml4.opacityIn,
        scale: ml4.scaleIn,
        duration: ml4.durationIn
      })
      .add({
        targets: ".ml4 .letters-2",
        opacity: 0,
        scale: ml4.scaleOut,
        duration: ml4.durationOut,
        easing: "easeInExpo",
        delay: ml4.delay
      })
      .add({
        targets: ".ml4",
        opacity: 0,
        duration: 500,
        delay: 500
      });
  }
};
</script>

<style scoped>
.ml4 {
  position: absolute;
  font-weight: 700;
  font-size: 4em;
  left: 0;
  right: 0;
  top: 5rem;
  z-index: 10;
}
.ml4 .letters {
  font-family: "Baloo Tamma", cursive;
  color: #409eff;
  background-color: white;
  border: 0.75rem solid #409eff;
  border-radius: 5rem;
  position: absolute;
  margin: 0 28rem;
  padding: 0;
  left: 0;
  top: 0.8em;
  right: 0;
  opacity: 0;
}
</style>

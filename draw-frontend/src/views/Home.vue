<template>
  <div class="home">
    <h1 class="ml1">
      <span class="text-wrapper">
        <span class="line line1"></span>
        <span class="letters">Draw & Guess</span>
        <span class="line line2"></span>
      </span>
    </h1>
    <el-button type="primary" plain @click="enter">进入游戏</el-button>
  </div>
</template>

<script>
import anime from "animejs";

export default {
  name: "home",
  components: {
    // HelloWorld
  },
  mounted() {
    this.startup_anime();
  },
  methods: {
    enter() {
      this.$router.push({ name: "room" });
    },
    startup_anime() {
      var letterEle = document.getElementsByClassName("letters")[0];
      var letters = letterEle.textContent.split("");
      letterEle.textContent = "";
      // console.log(letters);
      for (var i in letters) {
        var ele = document.createElement("span");
        ele.textContent = letters[i];
        ele.className = "letter";
        letterEle.appendChild(ele);
      }
      // console.log(letters);
      anime
        .timeline({ loop: true })
        .add({
          targets: ".ml1 .letter",
          scale: [0.3, 1],
          opacity: [0, 1],
          translateZ: 0,
          easing: "easeOutExpo",
          duration: 600,
          delay: function(el, i) {
            return 70 * (i + 1);
          }
        })
        .add({
          targets: ".ml1 .line",
          scaleX: [0, 1],
          opacity: [0.5, 1],
          easing: "easeOutExpo",
          duration: 700,
          offset: "-=875",
          delay: function(el, i, l) {
            return 80 * (l - i);
          }
        })
        .add({
          targets: ".ml1",
          opacity: 0,
          duration: 1000,
          easing: "easeOutExpo",
          delay: 1000
        });
    }
  }
};
</script>

<style>
.home {
  text-align: center;
  color: white;
}

.ml1 {
  font-weight: 900;
  font-size: 3.5em;
}

.ml1 .letter {
  display: inline-block;
  line-height: 1em;
}

.ml1 .text-wrapper {
  position: relative;
  display: inline-block;
  padding-top: 0.1em;
  padding-right: 0.05em;
  padding-bottom: 0.15em;
}

.ml1 .line {
  opacity: 0;
  position: absolute;
  left: 0;
  height: 3px;
  width: 100%;
  background-color: #fff;
  transform-origin: 0 0;
}

.ml1 .line1 {
  top: 0;
}
.ml1 .line2 {
  bottom: 0;
}
</style>

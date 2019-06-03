<template>
  <div class="drawingBoard">
    <div class="horizontal">
      <el-progress :percentage="time/0.6" :color="timeColor" :show-text="false">00:29</el-progress>
      <p class="time">00:{{time}}</p>
    </div>
    <div id="indicator" :style="{width: indicatorEdge, height: indicatorEdge}"></div>
    <canvas
      id="canvas"
      width="800rem"
      height="350rem"
      @mousedown="penDown($event)"
      @mousemove="draw($event)"
      @mouseup="penUp($event)"
    >Your browser does not support the canvas element.你的浏览器居然不支持Canvas？！赶快换一个吧！</canvas>
    <div class="horizontal">
      <p style="margin-left: 5rem; color: gray; margin-top: 0.75rem;">颜色</p>
      <el-tooltip effect="dark" content="选择颜色" placement="top">
        <div class="tool" @mouseover="resizePalette(true)" @mouseleave="resizePalette(false)">
          <el-color-picker v-model="penColor" size="small" style="z-index: 3; opacity: 0;"></el-color-picker>
          <img
            id="palette"
            :src="require('../../static/tools/palette.png')"
            class="imgButton"
            style="border: 0; margin-right: 0.5rem; position: absolute"
          >
        </div>
      </el-tooltip>
      <div class="imgButtonBox tool">
        <el-tooltip effect="dark" content="选择画笔" placement="top">
          <img
            :src="require('../../static/tools/pencil.png')"
            @click="isPen=true"
            class="imgButton"
          >
        </el-tooltip>
      </div>
      <div class="tool">
        <p>画笔大小</p>
        <el-slider v-model="penWidth" :show-tooltip="true" style="width: 100px;"></el-slider>
      </div>

      <div class="imgButtonBox tool">
        <el-tooltip effect="dark" content="选择橡皮" placement="top">
          <img
            :src="require('../../static/tools/eraser.png')"
            @click="isPen=false"
            class="imgButton"
          >
        </el-tooltip>
      </div>

      <!-- <el-button-group>
          <el-button @click="isPen=true">画笔</el-button>
          <el-button @click="isPen=false">橡皮</el-button>
      </el-button-group>-->

      <div class="tool">
        <p>橡皮大小</p>
        <el-slider v-model="eraserWidth" :show-tooltip="true" style="width: 100px;"></el-slider>
      </div>
      <div class="tool" style="margin-left: 2rem;">
        <el-tooltip effect="dark" content="清空画板" placement="top">
          <el-button type="info" icon="el-icon-delete" circle @click="cleanCanvas"></el-button>
        </el-tooltip>
      </div>
      <!-- <div class="imgButtonBox tool">
        <img :src="require('../../static/tools/bin.png')" @click="cleanCanvas" class="imgButton">
      </div>-->
    </div>
  </div>
</template>

<script>
export default {
  name: "DrawingBoard",
  data() {
    return {
      penColor: "#000000",
      penWidth: 50,
      eraserWidth: 100,
      screenWidth: document.body.clientWidth,
      ctx: "",
      lastPos: {
        X: 0,
        Y: 0
      },
      isMouseDown: false,
      isPen: true
    };
  },
  mounted() {
    const self = this;
    this.ctx = document.getElementById("canvas").getContext("2d");
    window.onresize = () => {
      return (() => {
        window.screenWidth = document.body.clientWidth;
        self.screenWidth = window.screenWidth;
      })();
    };
  },
  computed: {
    canvasPos() {
      var o = document.getElementById("canvas");
      var x = 0;
      var y = 0;
      do {
        x += o.offsetLeft;
        y += o.offsetTop;
      } while ((o = o.offsetParent));
      return { X: x, Y: y };
    },
    timeColor() {
      return this.time >= 15 ? "#409EFF" : "#F56C6C";
    },
    indicatorEdge() {
      if (this.isPen) {
        return this.penWidth + 1;
      } else {
        return this.eraserWidth + 1;
      }
    },
    getCanvas() {
      return this.$store.state.canvas;
    },
    time() {
      return this.$store.state.time;
    },
    current() {
      return this.$store.state.current;
    },
    isDrawer() {
      return this.$store.state.isDrawer;
      consoel.log("draw?", this.$store.state.isDrawer);
    }
  },
  watch: {
    getCanvas(val) {
      if (val.type == "clean") {
        this.ctx.fillStyle = "white";
        this.ctx.fillRect(0, 0, 800, 400);
        return;
      }
      this.ctx.beginPath();
      this.ctx.moveTo(val.fromX, val.fromY);
      this.ctx.lineTo(val.toX, val.toY);
      this.ctx.lineJoin = "round";
      this.ctx.lineCap = "round";
      this.ctx.strokeStyle = val.color;
      this.ctx.lineWidth = val.width;
      this.ctx.stroke();
    },
    isDrawer(val) {
      if (val != null && val == true) {
        this.isPen = true;
      }
    }
  },
  methods: {
    penDown(e) {
      if (!this.isDrawer) {
        return;
      }
      var pos = this.canvasPos;
      this.lastPos.X = e.pageX - pos.X;
      this.lastPos.Y = e.pageY - pos.Y;
      this.isMouseDown = true;
    },
    draw(e) {
      if (!this.isDrawer) {
        return;
      }
      if (this.isMouseDown) {
        var pos = this.canvasPos;
        var canvasToSend = {
          type: "draw",
          fromX: this.lastPos.X,
          fromY: this.lastPos.Y,
          toX: e.pageX - pos.X,
          toY: e.pageY - pos.Y,
          color: "",
          width: 1
        };
        if (this.isPen) {
          canvasToSend.color = this.penColor;
          if (this.penWidth == 0) {
            canvasToSend.width = 1;
          } else {
            canvasToSend.width = this.penWidth / 10;
          }
        } else {
          canvasToSend.color = "white";
          if (this.eraserWidth == 0) {
            canvasToSend.width = 1;
          } else {
            canvasToSend.width = this.eraserWidth / 2.5;
          }
        }
        this.wsSendCanvas(canvasToSend);

        this.lastPos.X = e.pageX - pos.X;
        this.lastPos.Y = e.pageY - pos.Y;
      }
    },
    penUp(e) {
      if (!this.isDrawer) {
        return;
      }
      this.isMouseDown = false;
      var pos = this.canvasPos;
      this.lastPos.X = e.pageX - pos.X;
      this.lastPos.Y = e.pageY - pos.Y;
    },
    cleanCanvas() {
      if (!this.isDrawer) {
        return;
      }
      this.wsSendCanvas({
        type: "clean"
      });
    },
    showIndicator(e) {
      var indicator = document.getElementById("indicator");
      var x = e.pageX - indicator.clientWidth / 2;
      var y = e.pageY - indicator.clientWidth / 2;
      indicator.setAttribute(
        "style",
        "left: " + x + "px;" + "top: " + y + "px;"
      );
      console.log(indicator.style.left, indicator.style.top);
    },
    wsSendCanvas(canvas) {
      this.$store.state.ws.send(
        "/app/canvas/" + this.$store.state.room.id,
        {},
        JSON.stringify(canvas)
      );
    },
    resizePalette(isLarger) {
      var p = document.getElementById("palette");
      if (isLarger) {
        p.style =
          "transform: rotate(60deg) scale(1.5, 1.5); transition: 0.2s cubic-bezier(0.55, 0.055, 0.675, 0.19);";
      } else {
        p.style = "";
      }
    }
  }
};
</script>

<style>
.drawingBoard {
  margin-left: 1%;
  border-radius: 2rem;
  /* box-shadow: 1.5rem -1.5rem 3rem rgba(255, 255, 255, 1); */
  background-color: rgba(245, 245, 245, 1);
}

canvas {
  border-radius: 2rem;
  background-color: white;
  cursor: default;
  margin: 0.75rem;
  margin-bottom: 0.5rem;
}

.tool {
  display: flex;
  margin: 0.5rem;
  margin-top: 0;
  color: gray;
}

.tool p {
  padding: 0.25rem 0;
  margin: 0.5rem;
}

.el-progress {
  margin: 1rem 2rem 0 2rem;
  width: 80%;
  color: #409eff;
}

.time {
  margin: 1rem 0.25rem 0 0.25rem;
  width: 3rem;
}

#indicator {
  width: 0px;
  height: 0px;
  background-color: rgba(128, 128, 128, 0.5);
  border-radius: 50%;
  position: absolute;
}

.colorButton {
  border-radius: 5rem;
  margin: 0 1rem;
}

.imgButton {
  width: 2.5rem;
  height: 2.5rem;
  border: 0;
  position: absolute;
}

.imgButton:hover {
  /* width: 3rem;
  height: 3rem; */
  cursor: pointer;
  transform: rotate(60deg) scale(1.4, 1.4);
  transition: 0.2s cubic-bezier(0.55, 0.055, 0.675, 0.19);
}

.imgButtonBox {
  width: 1.5rem;
  margin: 0 0.5rem;
}
</style>

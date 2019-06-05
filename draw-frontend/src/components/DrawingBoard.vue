<template>
  <div class="drawingBoard">
    <div class="horizontal">
      <el-progress :percentage="time/0.6" :color="timeColor" :show-text="false">00:29</el-progress>
      <p class="time">00:{{time}}</p>
    </div>
    <div id="indicator" :style="{width: indicatorEdge, height: indicatorEdge}"></div>
    <div class="horizontal">
      <div
        id="svg"
        @mousedown="cursorDown($event)"
        @mousemove="dragShape($event)"
        @mouseup="cursorUp($event)"
      ></div>
      <canvas
        id="canvas"
        width="750rem"
        height="350rem"
        @mousedown="penDown($event)"
        @mousemove="handDraw($event)"
        @mouseup="penUp($event)"
      >Your browser does not support the canvas element.你的浏览器居然不支持Canvas？！赶快换一个吧！</canvas>
      <div class="shapes">
        <el-checkbox v-model="fillShape" style="margin: 0; padding: 0;">填充</el-checkbox>
        <div v-for="(s, index) in shapeUrl" :key="index">
          <button class="shape" @click="handleShapeClick($event, index)">
            <img :src="s">
          </button>
        </div>
      </div>
    </div>
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
            @click="handleChoosePen(true)"
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
            @click="handleChoosePen(false)"
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
// import d3 from "d3";
import SVG from "svg.js";
import "@svgdotjs/svg.draggable.js";

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
      fillShape: true,
      isMouseDown: false,
      isPen: true,
      shape: -1,
      shapes: {
        circle: 0,
        line: 1,
        path: 2,
        triangle: 3,
        rectangle: 4
      },
      shapeUrl: [
        require("../../static/shapes/line.png"),
        require("../../static/shapes/circle.png"),
        require("../../static/shapes/ellipse.png"),
        require("../../static/shapes/triangle.png"),
        require("../../static/shapes/rectangle.png"),
        require("../../static/shapes/star.png")
      ],
      currentShape: null,
      draw: null
    };
  },
  mounted() {
    const self = this;
    this.ctx = document.getElementById("canvas").getContext("2d");
    this.draw = SVG("svg").size(750, 350);
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
    }
  },
  watch: {
    getCanvas(val) {
      if (val.type == "clean") {
        this.ctx.fillStyle = "white";
        this.ctx.fillRect(0, 0, 800, 400);
        return;
      }
      if (val.type == "draw") {
        this.ctx.beginPath();
        this.ctx.moveTo(val.fromX, val.fromY);
        this.ctx.lineTo(val.toX, val.toY);
        this.ctx.lineJoin = "round";
        this.ctx.lineCap = "round";
        this.ctx.strokeStyle = val.color;
        this.ctx.lineWidth = val.width;
        this.ctx.stroke();
      }
      if (val.type == "shape") {
        this.ctx.beginPath();
        this.ctx.strokeStyle = val.color;
        this.ctx.fillStyle = val.color;
        this.ctx.lineWidth = 1;
        this.ctx.globalAlpha = 1;
        switch (val.shape) {
          case 0:
            this.ctx.lineWidth = val.width;
            this.ctx.moveTo(val.fromX, val.fromY);
            this.ctx.lineTo(val.toX, val.toY);
            this.ctx.stroke();
            break;
          case 1:
            this.ctx.arc(val.cx, val.cy, val.r, 0, Math.PI * 2, true);
            this.ctx.stroke();
            if (val.fillShape) {
              this.ctx.fill();
            }
            break;
          case 2:
            this.ctx.scale(1, val.ry / val.rx);
            this.ctx.arc(
              val.cx,
              (val.cy * val.rx) / val.ry,
              val.rx,
              0,
              Math.PI * 2,
              true
            );
            this.ctx.stroke();
            if (val.fillShape) {
              this.ctx.fill();
            }
            this.ctx.scale(1, val.rx / val.ry);
            break;
          case 3:
            this.ctx.moveTo(val.arr[0][0], val.arr[0][1]);
            this.ctx.lineTo(val.arr[1][0], val.arr[1][1]);
            this.ctx.lineTo(val.arr[2][0], val.arr[2][1]);
            this.ctx.closePath();
            this.ctx.stroke();
            if (val.fillShape) {
              this.ctx.fill();
            }
            break;
          case 4:
            this.ctx.rect(val.x, val.y, val.w, val.h);
            this.ctx.stroke();
            if (val.fillShape) {
              this.ctx.fill();
            }
            break;
          case 5:
            this.ctx.moveTo(val.arr[0][0], val.arr[0][1]);
            for (var i in val.arr) {
              this.ctx.lineTo(val.arr[i][0], val.arr[i][1]);
            }
            this.ctx.closePath();
            this.ctx.stroke();
            if (val.fillShape) {
              this.ctx.fill();
            }
            break;
          default:
            break;
        }
      }
    },
    isDrawer(val) {
      if (val != null && val == true) {
        this.isPen = true;
      }
    }
  },
  methods: {
    test() {},
    clearShapeButtons() {
      var shapeButtons = document.getElementsByClassName("shape");
      for (var i = 0; i < shapeButtons.length; i++) {
        shapeButtons[i].setAttribute(
          "style",
          "background-color: rgba(255, 255, 255, 0.5); border-color: rgba(255, 255, 255, 0.5);"
        );
      }
    },
    handleChoosePen(isPen) {
      this.isPen = isPen;
      this.shape = -1;
      this.clearShapeButtons();
      var svgBoard = document.getElementById("svg");
      svgBoard.setAttribute("style", "z-index: -1");
    },
    handleShapeClick(event, index) {
      this.clearShapeButtons();
      var element = document.getElementsByClassName("shape")[index];
      element.setAttribute(
        "style",
        "background-color: rgba(224, 245, 252); border-color: rgb(174, 233, 253);"
      );
      this.shape = index;
      var svgBoard = document.getElementById("svg");
      svgBoard.setAttribute("style", "z-index: 10");
    },
    cursorDown(e) {
      var pos = this.canvasPos;
      this.lastPos.X = e.pageX - pos.X;
      this.lastPos.Y = e.pageY - pos.Y;
      switch (this.shape) {
        case 0:
          var w = this.penWidth / 10;
          if (w == 0) {
            w = 1;
          }
          var line = this.draw
            .line(
              this.lastPos.X,
              this.lastPos.Y,
              this.lastPos.X,
              this.lastPos.Y
            )
            .attr({
              stroke: this.penColor,
              "stroke-opacity": 1,
              "stroke-width": w
            });
          this.currentShape = line;
          break;
        case 1:
          var circle = this.draw.circle(0).attr({
            stroke: this.penColor,
            fill: this.penColor,
            "stroke-opacity": 1,
            "stroke-width": 2
          });
          this.currentShape = circle;
          break;
        case 2:
          var ellipse = this.draw.ellipse(0, 0).attr({
            stroke: this.penColor,
            fill: this.penColor,
            "stroke-opacity": 1,
            "stroke-width": 2
          });
          this.currentShape = ellipse;
          break;
        case 3:
          var polygon = this.draw.polygon("0,0 0,0 0,0").attr({
            stroke: this.penColor,
            fill: this.penColor,
            "stroke-opacity": 1,
            "stroke-width": 2
          });
          this.currentShape = polygon;
          break;
        case 4:
          var rect = this.draw.rect(0, 0).attr({
            stroke: this.penColor,
            fill: this.penColor,
            "stroke-opacity": 1,
            "stroke-width": 2,
            x: this.lastPos.X,
            y: this.lastPos.Y
          });
          this.currentShape = rect;
          break;
        case 5:
          var polygon = this.draw.polygon("0,0 0,0 0,0").attr({
            stroke: this.penColor,
            fill: this.penColor,
            "stroke-opacity": 1,
            "stroke-width": 2
          });
          this.currentShape = polygon;
          break;
        default:
          break;
      }
      if (!this.fillShape) {
        this.currentShape.attr("fill-opacity", 0);
      }
      this.isMouseDown = true;
    },
    dragShape(e) {
      if (!this.isMouseDown) {
        return;
      }
      var pos = this.canvasPos;
      var x = e.pageX - pos.X;
      var y = e.pageY - pos.Y;
      var cx = 0.5 * (x + this.lastPos.X);
      var cy = 0.5 * (y + this.lastPos.Y);
      var dx = Math.abs(x - this.lastPos.X);
      var dy = Math.abs(y - this.lastPos.Y);

      switch (this.shape) {
        case 0:
          this.currentShape.plot(this.lastPos.X, this.lastPos.Y, x, y);
          break;
        case 1:
          var r = (Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)) * 0.5) / 1.414;
          this.currentShape
            .cx(cx)
            .cy(cy)
            .radius(r);
          break;
        case 2:
          this.currentShape
            .cx(cx)
            .cy(cy)
            .radius(dx * 0.5, dy * 0.5);
          break;
        case 3:
          this.currentShape.plot([
            [cx, this.lastPos.Y],
            [x, y],
            [this.lastPos.X, y]
          ]);
          break;
        case 4:
          this.currentShape
            .size(dx, dy)
            .cx(cx)
            .cy(cy);
          break;
        case 5:
          var r = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)) * 0.5;
          this.currentShape.plot([
            [
              cx + r * Math.cos((2 * Math.PI * 2) / 5 + 1.1 * Math.PI),
              cy + r * Math.sin((2 * Math.PI * 2) / 5 + 1.1 * Math.PI)
            ],
            [
              cx + r * Math.cos((2 * Math.PI * 0) / 5 + 1.1 * Math.PI),
              cy + r * Math.sin((2 * Math.PI * 0) / 5 + 1.1 * Math.PI)
            ],
            [
              cx + r * Math.cos((2 * Math.PI * 3) / 5 + 1.1 * Math.PI),
              cy + r * Math.sin((2 * Math.PI * 3) / 5 + 1.1 * Math.PI)
            ],
            [
              cx + r * Math.cos((2 * Math.PI * 1) / 5 + 1.1 * Math.PI),
              cy + r * Math.sin((2 * Math.PI * 1) / 5 + 1.1 * Math.PI)
            ],
            [
              cx + r * Math.cos((2 * Math.PI * 4) / 5 + 1.1 * Math.PI),
              cy + r * Math.sin((2 * Math.PI * 4) / 5 + 1.1 * Math.PI)
            ]
          ]);
          break;
        default:
          break;
      }
    },
    cursorUp(e) {
      this.isMouseDown = false;
      var shapeToSend = null;
      switch (this.shape) {
        case 0:
          var arr = this.currentShape.array().value;
          shapeToSend = {
            type: "shape",
            shape: this.shape,
            fromX: arr[0][0],
            fromY: arr[0][1],
            toX: arr[1][0],
            toY: arr[1][1],
            color: this.penColor,
            fillShape: this.fillShape,
            width: this.currentShape.attr("stroke-width")
          };
          break;
        case 1:
          console.log(this.currentShape);
          shapeToSend = {
            type: "shape",
            shape: this.shape,
            cx: this.currentShape.cx(),
            cy: this.currentShape.cy(),
            r: this.currentShape.attr("r"),
            color: this.penColor,
            fillShape: this.fillShape
          };
          break;
        case 2:
          shapeToSend = {
            type: "shape",
            shape: this.shape,
            cx: this.currentShape.cx(),
            cy: this.currentShape.cy(),
            rx: this.currentShape.rx(),
            ry: this.currentShape.ry(),
            color: this.penColor,
            fillShape: this.fillShape
          };
          break;
        case 3:
          var arr = this.currentShape.array().value;
          shapeToSend = {
            type: "shape",
            shape: this.shape,
            arr: arr,
            color: this.penColor,
            fillShape: this.fillShape
          };
          break;
        case 4:
          shapeToSend = {
            type: "shape",
            shape: this.shape,
            x: this.currentShape.x(),
            y: this.currentShape.y(),
            w: this.currentShape.width(),
            h: this.currentShape.height(),
            color: this.penColor,
            fillShape: this.fillShape
          };
          break;
        case 5:
          var arr = this.currentShape.array().value;
          shapeToSend = {
            type: "shape",
            shape: this.shape,
            arr: arr,
            color: this.penColor,
            fillShape: this.fillShape
          };
          break;
        default:
          break;
      }
      this.wsSendCanvas(shapeToSend);
      this.currentShape.remove();
      this.currentShape = null;
    },
    penDown(e) {
      // if (!this.isDrawer) {
      //   return;
      // }
      var pos = this.canvasPos;
      this.lastPos.X = e.pageX - pos.X;
      this.lastPos.Y = e.pageY - pos.Y;
      this.isMouseDown = true;
    },
    handDraw(e) {
      // if (!this.isDrawer) {
      //   return;
      // }
      if (!this.isMouseDown) {
        return;
      }
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
      if (this.shape != -1) {
      } else if (this.isPen) {
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
    },
    penUp(e) {
      // if (!this.isDrawer) {
      //   return;
      // }
      this.isMouseDown = false;
      var pos = this.canvasPos;
      this.lastPos.X = e.pageX - pos.X;
      this.lastPos.Y = e.pageY - pos.Y;
    },
    cleanCanvas() {
      // if (!this.isDrawer) {
      //   return;
      // }
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
  padding: 0 0.5rem;
}

#canvas {
  border-radius: 2rem;
  background-color: white;
  cursor: default;
  margin: 0.5rem;
  margin-bottom: 0.5rem;
}

#svg {
  position: absolute;
  border-radius: 2rem;
  background-color: rgba(0, 0, 0, 0);
  margin: 0.5rem;
  margin-bottom: 0.5rem;
  z-index: -1;
}

.el-checkbox span {
  padding: 0;
  margin: 0;
  word-wrap: break-word;
  word-break: break-all;
}

.shapes {
  margin: 2rem 0rem;
  width: auto;
  /* background-color: white; */
}

.shape {
  height: 2rem;
  width: 2rem;
  margin: 0.5rem 0;
  padding: 0rem;
  border-radius: 0.5rem;
  outline: none;
  border-width: 1px;
  border-style: solid solid solid solid;
  background-color: rgba(255, 255, 255, 0.5);
  border-color: rgba(255, 255, 255, 0.5);
}

.shape img {
  height: 1.5rem;
  width: 1.5rem;
}

.shape:hover,
.shape:focus {
  background-color: rgba(224, 245, 252, 1);
  border-color: rgb(174, 233, 253);
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

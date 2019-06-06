<template>
  <div class="room horizontal" style="width: 100%">
    <el-dialog title :visible.sync="rankVisible" width="30%" center>
      <h2 style="text-align: center; margin-top: 0;">排行榜</h2>
      <div v-for="m in displayRank" :key="m.email" class="ranking" style="text-align: center;">
        第
        <span v-if="m.rank == 1" class="rank rank1">{{m.rank}}</span>
        <span v-else-if="m.rank == 2" class="rank rank2">{{m.rank}}</span>
        <span v-else-if="m.rank == 3" class="rank rank3">{{m.rank}}</span>
        <span v-else class="rank">{{m.rank}}</span>
        名&emsp;&emsp;
        <span>{{m.name}} :</span>
        <span>&emsp;{{m.mark}} 分</span>
      </div>
    </el-dialog>
    <div v-for="(gif, i) in gifs" :key="i">
      <gif :src="gif.src" :style="'top: '+gif.top+'rem ; left:' + gif.left + 'rem'"></gif>
    </div>
    <div style="width: 65%; float:left">
      <div class="headBar">
        <el-button plain @click="handleBack" style="padding: 0.75rem 0rem;" class="rotate">返回大厅</el-button>
        <el-button
          plain
          @click="handleClickStart"
          :disabled="startButtonDisabled"
          style="padding: 0.75rem 0rem;"
          class="rotate"
        >开始游戏</el-button>
        <div class="info">
          <el-tooltip effect="dark" content="更改房间名" placement="bottom">
            <span @mousedown="handleChange" id="roomName">房间: {{room.name}}</span>
          </el-tooltip>
          <transition name="fade">
            <span v-if="showPuzzle">第 {{current.round}}/2 回合</span>
          </transition>
          <transition name="fade" v-if="showPuzzle">
            <span v-if="!isDrawer" class="puzzle">{{current.puzzle.tip}}，{{current.puzzle.length}}个字</span>
            <span v-else class="puzzle">{{current.puzzle.answer}}</span>
          </transition>
        </div>
        <!-- <el-button @click="onStop" style="margin: 0; padding: 0">try</el-button> -->
      </div>
      <ready v-if="isShowReady"></ready>
      <show-drawer v-if="showDrawer" :name="drawerName"></show-drawer>
      <drawing-board></drawing-board>
      <users></users>
    </div>
    <div style="width: 35%">
      <chat></chat>
    </div>
  </div>
</template>

<script>
import DrawingBoard from "../components/DrawingBoard.vue";
import Chat from "../components/Chat.vue";
import Users from "../components/Users.vue";
import Ready from "../components/Ready.vue";
import ShowDrawer from "../components/ShowDrawer.vue";
import gif from "../components/GIF.vue";

import SockJS from "sockjs-client";
import Stomp from "stompjs";
import { setTimeout } from "timers";
// import anime from "animejs";

export default {
  name: "Room",
  components: {
    DrawingBoard,
    Chat,
    Users,
    Ready,
    ShowDrawer,
    gif
  },
  data() {
    return {
      roomId: "",
      startButtonDisabled: false,
      isShowReady: false,
      showDrawer: false,
      rankVisible: false,
      drawerName: "",
      allGif: [],
      rank: []
    };
  },
  computed: {
    displayRank() {
      return this.rank;
    },
    gifs() {
      return this.allGif;
    },
    showPuzzle() {
      return this.$store.state.current != null;
    },
    user() {
      return this.$store.state.user;
    },
    room() {
      return this.$store.state.room;
    },
    current() {
      return this.$store.state.current;
    },
    isDrawer() {
      return this.$store.state.isDrawer;
    }
  },
  watch: {
    room(val) {
      for (var i in val.users) {
        if (val.users[i].email == this.user.email) {
          this.startButtonDisabled = false;
          return;
        }
      }
      this.startButtonDisabled = true;
    }
  },
  beforeRouteEnter(to, from, next) {
    if (sessionStorage.getItem("user") == null) {
      next(false);
    } else {
      next();
    }
  },
  beforeRouteLeave(to, from, next) {
    this.$store.commit("cleanChat");
    this.wsClose();
    next();
  },
  mounted() {
    this.roomId = this.$route.params.id;
    this.getUserInfo();
    this.getRoomInfo();
    try {
      if (!this.$store.state.ws.connected) {
        this.initWebSocket();
      }
    } catch (e) {
      this.initWebSocket();
    }
  },
  methods: {
    /* 关于 websocket */
    initWebSocket() {
      const wsuri = this.serverUrl + "/websocket";
      var websocket = new SockJS(wsuri);
      var stompClient = Stomp.over(websocket);
      this.$store.commit("setWs", stompClient);
      var self = this;

      stompClient.connect(
        {
          username: sessionStorage.getItem("user")
        },
        frame => {
          // 注册消息推送
          stompClient.subscribe("/topic/chat/" + this.roomId, response => {
            self.$store.commit("addChat", JSON.parse(response.body));
          });

          stompClient.subscribe("/topic/canvas/" + this.roomId, response => {
            self.$store.commit("setCanvas", JSON.parse(response.body));
          });

          stompClient.subscribe("/topic/room/" + this.roomId, response => {
            self.$store.commit("setRoom", JSON.parse(response.body));
          });

          stompClient.subscribe("/topic/current/" + this.roomId, response => {
            self.$store.commit("setCurrent", JSON.parse(response.body));
          });

          stompClient.subscribe("/topic/order/" + this.roomId, response => {
            self.onReceiveOrder(JSON.parse(response.body));
          });
        }
      );
    },
    wsClose() {
      if (this.$store.state.ws) {
        this.$store.state.ws.disconnect();
      }
    },
    /* 关于向后端发出请求 */
    getRoomInfo() {
      var self = this;
      this.$axios
        .get(self.serverUrl + "/room", {
          params: { id: self.roomId }
        })
        .then(response => {
          self.$store.commit("setCurrent", response.data.current);
          delete response.data.current;
          self.$store.commit("setRoom", response.data);
          for (var i in response.data.users) {
            if (
              response.data.users[i].email == sessionStorage.getItem("user")
            ) {
              this.startButtonDisabled = false;
            }
          }
        })
        .catch(error => {
          self.$message.error("加载房间数据失败\n", error);
        });
    },
    getUserInfo() {
      var self = this;
      this.$axios
        .get(self.serverUrl + "/user", {
          params: { email: sessionStorage.getItem("user") }
        })
        .then(response => {
          self.$store.commit("setUser", response.data);
        })
        .catch(error => {
          self.$message.error("加载用户数据失败\n", error);
          self.$router.push({ name: "enter" });
        });
    },
    changeName(newName) {
      if (newName == this.room.name) {
        return;
      }
      var self = this;
      this.$axios
        .put(
          self.serverUrl +
            "/room/changeName?roomId=" +
            self.room.id +
            "&newName=" +
            newName
        )
        .then(response => {
          self.$store.commit("setRoom", response.data);
        })
        .catch(error => {
          self.$message.error("更名失败\n", error);
        });
    },
    /* 关于处理按钮事件 */
    handleClickStart() {
      var inRoom = false;
      for (var i in this.room.users) {
        if (this.room.users[i].email == this.user.email) {
          inRoom = true;
          break;
        }
      }
      if (!inRoom) {
        this.startButtonDisabled = true;
        return;
      }
      this.$store.state.ws.send(
        "/app/order/" + this.roomId + "/start",
        {},
        JSON.stringify({ order: "start" })
      );
    },
    handleBack() {
      this.$router.push({ name: "square" });
    },
    handleChange() {
      if (this.startButtonDisabled) {
        this.$message.warning("无权更改");
        return;
      }
      this.$prompt("请输入新房间名", "更改房间名", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        inputValidator: value => {
          if (value == null || value == "") {
            return "不能为空";
          }
        }
      })
        .then(({ value }) => {
          this.changeName(value);
          this.$message({
            type: "success",
            message: "修改成功！"
          });
        })
        .catch(() => {});
    },
    /* 关于处理后端的指令 */
    onReceiveOrder(order) {
      switch (order.order) {
        case "start":
          this.onStart();
          break;
        case "timer":
          this.$store.commit("setTime", order.timer);
          this.startButtonDisabled = true;
          break;
        case "stop":
          this.onStop();
          break;
        case "timesUp":
          this.onTimesUp();
          break;
        case "ready":
          this.onReady();
          break;
        case "drawer":
          this.onDrawer();
          break;
        case "comment":
          this.onComment(order.isEgg);
          break;
        default:
          this.$message.error("收到无效的命令");
      }
    },
    onStart() {
      this.$message.warning("游戏开始！");
      this.startButtonDisabled = true;
    },
    onStop() {
      this.$message.warning("游戏结束！");
      this.rank = [];
      for (var i in this.current.mark) {
        this.rank.push(JSON.parse(JSON.stringify(this.current.mark[i])));
      }
      this.startButtonDisabled = false;
      this.rankVisible = true;
      setTimeout(() => {
        this.$store.commit("setCurrent", null);
      }, 5000);
    },
    onTimesUp() {
      if (this.isDrawer) {
        return;
      }
      this.$confirm("共有" + this.current.count.length + "人答对", "本轮结束", {
        confirmButtonText: "献花🌹",
        cancelButtonText: "扔鸡蛋🥚",
        center: true,
        distinguishCancelAndClose: true
      })
        .then(() => {
          this.$store.state.ws.send(
            "/app/order/" + this.roomId + "/comment",
            {},
            JSON.stringify({ message: false, sender: this.user.name })
          );
        })
        .catch(action => {
          if (action === "cancel") {
            this.$store.state.ws.send(
              "/app/order/" + this.roomId + "/comment",
              {},
              JSON.stringify({ message: true, sender: this.user.name })
            );
          }
        });
    },
    onReady() {
      this.isShowReady = true;
      setTimeout(() => {
        this.isShowReady = false;
      }, 4500);
    },
    onDrawer() {
      var name = "";
      var users = this.$store.state.room.users;
      for (var i in users) {
        if (users[i].email == this.$store.state.current.drawer) {
          name = users[i].name;
          break;
        }
      }
      this.drawerName = name;
      this.showDrawer = true;
      setTimeout(() => {
        this.showDrawer = false;
      }, 3500);
    },
    onComment(isEgg) {
      var gifSrc;
      if (isEgg) {
        gifSrc = require("../../static/gif/egg_" + this.allGif.length + ".gif");
      } else {
        gifSrc = require("../../static/gif/flower_" +
          this.allGif.length +
          ".gif");
      }
      this.allGif.push({
        src: gifSrc,
        left: Math.floor(Math.random() * 10) * 4 + 10,
        top: Math.floor(Math.random() * 10) * 1.5 + 5
      });
      setTimeout(() => {
        this.allGif.shift();
      }, 1500);
    },
    /* 其他 */
    test() {
      this.$store.state.ws.send(
        "/app/order/" + this.roomId + "/comment",
        {},
        JSON.stringify({ message: "true", sender: this.user.name })
      );
    }
  }
};
</script>


<style>
@import url("https://fonts.googleapis.com/css?family=Baloo+Tamma&display=swap");

.room {
  width: 100%;
  height: 100%;
}

.users {
  margin-top: 1rem;
}

.puzzle {
  font-size: 1.25rem;
  color: #409eff;
}

.headBar {
  margin: 0.5rem 0;
  padding: 0 2rem;
  /* background-color: white; */
  border-radius: 5rem;
  display: flex;
}

.headBar div.info {
  background-color: rgba(255, 255, 255, 0.7);
  border-radius: 5rem;
  padding: 0.75rem 0.5rem;
  margin: 0rem 1rem;
  transition: all 1s ease;
}

.headBar span {
  border-radius: 5rem;
  padding: 0rem 0.5rem;
  /* margin: 0rem 0.5rem; */
}

.el-button span {
  margin: 0 0.1rem;
}

#roomName:hover {
  cursor: pointer;
}

.fade-enter-active,
.fade-leave-active {
  transition: all 2s ease;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}

.rotate:hover {
  transform: rotateX(360deg) scale(1.1, 1.1);
  transition: 0.3s cubic-bezier(0.55, 0.055, 0.675, 0.19);
}

.ranking {
  margin: 1rem;
  font-size: 1.2rem;
}

.rank {
  background-color: lightgrey;
  border-radius: 1.25rem;
  padding: 0.5rem 0.8rem;
}

.rank1 {
  background-color: #f2d96a;
}
.rank2 {
  background-color: #d8d8d6;
}
.rank3 {
  background-color: #d28241;
}
</style>

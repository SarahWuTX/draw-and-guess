<template>
  <div class="square">
    <div>
      <el-input
        placeholder="用房间号或房间名进行搜索"
        v-model="search"
        clearable
        @keyup.enter="handleSearchRoom"
      >
        <el-button slot="append" icon="el-icon-search" @click="handleSearchRoom"></el-button>
      </el-input>
      <!-- <span style="float: right">你好，{{sessionStorage.getItem("name")}}</span> -->
    </div>

    <div>
      <el-card class="box-card" shadow="hover" style="width: 32rem;">
        <span style="margin: 0 2rem">还可以创建一个新房间和朋友一起玩哦!</span>
        <el-button type="primary" plain @click="handleCreateRoom">创建房间</el-button>
      </el-card>
    </div>
    <div class="rooms horizontal">
      <div v-if="rooms.length == 0" style="margin-left: 2rem;">
        <p>搜索不到诶，看看其他的？</p>
        <el-button type="primary" plain @click="getRoomList">查看所有房间</el-button>
      </div>
      <transition-group name="list" tag="div" class="rooms horizontal">
        <div v-for="room in rooms" :key="room.roomId">
          <el-card class="box-card" shadow="hover">
            <p>ID: {{room.roomId}}</p>
            <p>
              房间名:
              <span class="room-name">{{room.name}}</span>
            </p>
            <p>人数: {{room.userCount}}</p>
            <el-button type="primary" plain @click="handleEnterRoom">
              进入房间
              <span style="display: none;">{{room.roomId}}</span>
            </el-button>
          </el-card>
        </div>
      </transition-group>
    </div>
  </div>
</template>

<script>
export default {
  name: "square",
  data() {
    return {
      rooms: [],
      search: ""
    };
  },
  beforeRouteEnter(to, from, next) {
    if (sessionStorage.getItem("user") == null) {
      next(false);
    } else {
      next();
    }
  },
  mounted() {
    this.getRoomList();
  },
  methods: {
    getRoomList() {
      this.$axios
        .get(this.serverUrl + "/room/all")
        .then(response => {
          this.rooms = response.data;
        })
        .catch(error => {
          this.$message.error("加载房间列表失败\n", error);
        });
    },
    handleSearchRoom() {
      if (this.search == "") {
        this.getRoomList();
        return;
      } else {
        this.$axios
          .get(this.serverUrl + "/room/search", {
            params: { keyword: this.search }
          })
          .then(response => {
            this.rooms = response.data;
          })
          .catch(error => {
            this.$message.error("加载房间列表失败\n", error);
          });
      }
    },
    handleEnterRoom(event) {
      var element = event.toElement.firstElementChild;
      var roomId = element.textContent
        .replace(/进入房间/, "")
        .replace(/\s+/g, "");
      var self = this;
      this.$axios
        .get(self.serverUrl + "/room", { params: { id: roomId } })
        .then(response => {
          var room = response.data;
          if (room.users.length >= 6) {
            self.$message.warning("没挤进去，房间已经满了\n");
          } else {
            self.$router.push({
              name: "room",
              params: { id: room.id }
            });
          }
        })
        .catch(error => {
          self.$message.error("查询房间信息出错\n", error);
        });
    },
    handleCreateRoom() {
      var self = this;
      this.$axios
        .post(this.serverUrl + "/room")
        .then(response => {
          self.$message.success("创建成功！房间名为：" + response.data);
          self.$router.push({ name: "room", params: { id: response.data } });
        })
        .catch(error => {
          self.$message.error("创建房间失败\n" + error);
        });
    }
  }
};
</script>

<style scoped>
.square {
  padding: 0 5.5rem;
}

.el-input {
  width: 20rem;
  margin: 1rem 1rem;
}

.el-button:hover {
  transform: rotate(360deg) scale(1.1, 1.1);
  transition: 0.3s cubic-bezier(0.55, 0.055, 0.675, 0.19);
}

.box-card {
  width: 15rem;
  margin: 1rem 1rem;
  opacity: 0.8;
}

.box-card:hover {
  opacity: 1;
  transform: scale(1.1, 1.1);
}

.room-name {
  background-color: rgb(200, 240, 253);
  padding: 0.2rem 0.3rem;
  border-radius: 0.5rem;
}

.list-enter-active,
.list-leave-active {
  transition: all 1s;
}
.list-enter,
.list-leave-to {
  opacity: 0;
  transform: translateY(30px);
}
.list-move {
  transition: transform 1s;
}

@keyframes shake {
  0%,
  100% {
    -webkit-transform: translateX(0);
  }
  10%,
  30%,
  50%,
  70%,
  90% {
    -webkit-transform: translateX(-10px);
  }
  20%,
  40%,
  60%,
  80% {
    -webkit-transform: translateX(10px);
  }
}
@-o-keyframes shake {
  0%,
  100% {
    -webkit-transform: translateX(0);
  }
  10%,
  30%,
  50%,
  70%,
  90% {
    -webkit-transform: translateX(-10px);
  }
  20%,
  40%,
  60%,
  80% {
    -webkit-transform: translateX(10px);
  }
}
@-webkit-keyframes shake {
  0%,
  100% {
    -webkit-transform: translateX(0);
  }
  10%,
  30%,
  50%,
  70%,
  90% {
    -webkit-transform: translateX(-10px);
  }
  20%,
  40%,
  60%,
  80% {
    -webkit-transform: translateX(10px);
  }
}
@-moz-keyframes shake {
  0%,
  100% {
    -moz-transform: translateX(0);
  }
  10%,
  30%,
  50%,
  70%,
  90% {
    -moz-transform: translateX(-10px);
  }
  20%,
  40%,
  60%,
  80% {
    -moz-transform: translateX(10px);
  }
}

.shake {
  animation-name: shake;
  -o-animation-name: shake;
  -moz--name: shake;
  -webkit-animation-name: shake;
  animation-duration: 1s;
  -o-animation-duration: 1s;
  -webkit-animation-duration: 1s;
  -moz-animation-duration: 1s;
}
</style>

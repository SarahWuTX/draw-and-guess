<template>
  <div class="users horizontal">
    <transition-group name="list" class="horizontal" tag="div">
      <div class="user-card" v-for="user in displayUsers" :key="user.email">
        <el-card shadow="hover" :body-style="{ padding: '0.5rem' }">
          <embed :src="user.avatar" type="image/svg+xml">
          <div
            v-if="current != null && user.email == current.drawer"
            id="currentPlayer"
          >{{user.name}}✍️</div>
          <!-- <div v-if="room.users.indexOf(user) == 0" id="currentPlayer">{{user.name}}✍️</div> -->
          <div v-else>{{user.name}}</div>
        </el-card>
      </div>
    </transition-group>
    <div style="margin-left: 2rem;">
      <el-tooltip class="item" effect="dark" :content="joinButton.tip[isJoin]" placement="top">
        <el-button
          type="primary"
          :icon="joinButton.icon[isJoin]"
          circle
          @click="handleJoinButtonClick"
          :disabled="buttonDisabled"
        ></el-button>
      </el-tooltip>
    </div>
    <!-- <el-button @click="changePlayer" style="height: 3rem">换新玩家</el-button> -->
  </div>
</template>

<script scoped>
export default {
  name: "users",
  data() {
    return {
      joinButton: {
        tip: ["加入房间", "退出房间"],
        icon: ["el-icon-plus", "el-icon-minus"]
      },
      users: []
    };
  },
  computed: {
    displayUsers() {
      return this.users;
    },
    buttonDisabled() {
      if (this.current != null) {
        return true;
      }
      return false;
    },
    room() {
      return this.$store.state.room;
    },
    userEmail() {
      return sessionStorage.getItem("user");
    },
    isJoin() {
      for (var i in this.room.users) {
        if (this.room.users[i].email == this.userEmail) {
          return 1;
        }
      }
      return 0;
    },
    current() {
      return this.$store.state.current;
    }
  },
  watch: {
    room(val) {
      this.users = val.users;
    },
    current(val) {
      if (val == null) {
        var temp = this.users.shift();
        this.users.push(temp);
        return;
      }
      while (this.users[0].email != val.drawer) {
        this.users.push(this.users.shift());
      }
    }
  },
  mounted() {
    this.users = this.room.users;
  },
  methods: {
    handleJoinButtonClick() {
      if (this.isJoin == 0) {
        this.join();
      } else {
        this.cancelJoin();
      }
    },
    join() {
      var self = this;
      this.$axios
        .put(
          self.serverUrl +
            "/room/ready?userId=" +
            self.userEmail +
            "&roomId=" +
            self.room.id
        )
        .then(response => {
          self.$store.state.ws.send(
            "/app/room/" + self.room.id,
            {},
            JSON.stringify(response.data)
          );
          self.$store.commit("setRoom", response.data);
        })
        .catch(error => {
          self.$message.error("加入失败: " + error.response.data);
        });
    },
    cancelJoin() {
      var self = this;
      this.$axios
        .put(
          self.serverUrl +
            "/room/unready?userId=" +
            self.userEmail +
            "&roomId=" +
            self.room.id
        )
        .then(response => {
          self.$store.state.ws.send(
            "/app/room/" + self.room.id,
            {},
            JSON.stringify(response.data)
          );
          self.$store.commit("setRoom", response.data);
        })
        .catch(error => {
          self.$message.error("取消失败: " + error.response.data);
        });
    }
  }
};
</script>

<style scoped>
.user-card {
  margin: 0 0.5rem;
  text-align: center;
  width: 7.5rem;
}

#currentPlayer {
  color: #409eff;
  font-weight: bold;
}

.user-card.list-enter-active,
.user-card.list-leave-active {
  transition: all 1.5s ease-in-out;
}
.user-card.list-enter {
  opacity: 0;
  transform: translateX(-30px);
}
.user-card.list-enter-to {
  transition: all 1.5s ease-in-out;
}

.user-card.list-leave-to {
  opacity: 0;
  transform: translateY(-30px);
  transition: all 1.5s ease-in-out;
}
.user-card.list-move {
  transition: transform 2s;
}
</style>

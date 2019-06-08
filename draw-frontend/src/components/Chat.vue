<template>
  <div id="chat">
    <div id="box" ref="box">
      <div id="hello">你好，{{$store.state.user.name}}</div>
      <div style="flex: 1"></div>
      <!-- <div
        class="bubble left"
        style="opacity: 1; width: 7.40625rem; height: 1rem; margin-top: 0px; margin-left: 0px; transform: scale(1);"
      >
        <span class="loading" style="transform: translateX(0rem) scale(1);">
          <b style="opacity: 0; transform: scale(0);">•</b>
          <b style="opacity: 0; transform: scale(0);">•</b>
          <b style="opacity: 0; transform: scale(0);">•</b>
        </span>
        <span class="message" style="width: 5.71875rem; height: 1rem; opacity: 1;">Hey there 👋</span>
      </div>-->
      <!-- <transition-group name="my" tag="div"> -->
      <div v-for="(msg, index) in msgs" class="chatItem" :key="index">
        <div v-if="msg.sender.hasOwnProperty('avatar')">
          <div v-if="isMe(msg.sender.email)" class="right horizontal">
            <div>
              <div class="bubble right message">{{ msg.message }}</div>
            </div>
            <span class="avatar">
              <embed :src="msg.sender.avatar" type="image/svg+xml">
            </span>
          </div>
          <div v-else class="horizontal">
            <span class="avatar">
              <embed :src="msg.sender.avatar" type="image/svg+xml">
            </span>
            <div>
              <div class="name">{{msg.sender.name}}</div>
              <div class="bubble left message">{{ msg.message }}</div>
            </div>
          </div>
        </div>
        <div v-else style="text-align: center">
          <span>[ 系统 ]&ensp;&ensp;</span>
          <span>{{ msg.message }}</span>
        </div>
      </div>
      <!-- </transition-group> -->
    </div>
    <div>
      <input placeholder="请输入内容" v-model="input" @keyup.enter="wsSendMessage" :disabled="isDrawer">
      <el-button plain type="primary" @click="wsSendMessage" :disabled="isDrawer">发送</el-button>
    </div>
    <div>
      <div
        v-if="mark != null"
        style="margin: 0.5rem; font-size: 1.25rem; color: #E6A23C; text-align: center;"
      >~~ ~~ 玩家排行 ~~ ~~</div>
      <div v-if="mark != null" class="horizontal">
        <div v-for="m in mark" :key="m.email" class="ranking">
          <span v-if="m.rank == 1" class="rank rank1">{{m.rank}}</span>
          <span v-else-if="m.rank == 2" class="rank rank2">{{m.rank}}</span>
          <span v-else-if="m.rank == 3" class="rank rank3">{{m.rank}}</span>
          <span v-else class="rank">{{m.rank}}</span>
          <span>{{m.name}} :</span>
          <span>{{m.mark}} 分</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "chat",
  data() {
    return {
      input: "",
      displayMark: {}
    };
  },
  computed: {
    msgs() {
      return this.$store.state.chat;
    },
    isDrawer() {
      return this.$store.state.isDrawer;
    },
    mark() {
      try {
        return this.$store.state.current.mark;
      } catch (e) {
        return null;
      }
    }
  },
  watch: {
    msgs() {
      this.$nextTick(() => {
        this.$refs.box.scrollTop = this.$refs.box.scrollHeight;
      });
    }
  },
  destroyed() {
    this.websocketclose();
  },
  methods: {
    wsSendMessage() {
      if (this.input == "") {
        return;
      }
      //数据发送
      var data = {
        sender: this.$store.state.user,
        message: this.input
      };
      this.$store.state.ws.send(
        "/app/chat/" + this.$store.state.room.id,
        {},
        JSON.stringify(data)
      );
      this.input = "";
    },
    websocketclose() {
      //关闭
      if (this.stompClient) {
        this.$store.state.ws.disconnect();
      }
    },
    isMe(email) {
      return email == this.$store.state.user.email;
    }
  }
};
</script>

<style scoped>
#chat {
  min-height: 41.5rem;
  /* background-color: rgba(255, 255, 255, 0.8); */
  background-image: linear-gradient(180deg, #fdfbfb00 0%, #ffffffc5 30%);
  padding: 1rem 1rem 0.5rem 2rem;
  word-wrap: break-word;
  word-break: break-all;
}

#hello {
  position: absolute;
  right: 0;
  padding: 0.5rem;
  background-color: whitesmoke;
  border-radius: 5rem;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

#box {
  overflow-y: auto;
  overflow-x: hidden;
  height: 31rem;
  display: flex;
  flex-direction: column;
}

.chatItem {
  margin: 0.5rem 0.5rem;
}

.chatItem .right {
  float: right;
}

.chatItem .avatar {
  border-radius: 5rem;
  width: auto;
}

.chatItem .avatar embed {
  height: 3rem;
  width: 3rem;
}

.chatItem .name {
  font-size: 1rem;
  background-color: white;
  padding: 0.1rem 0.2rem;
  border-radius: 0.5rem;
  margin: 0;
  margin-bottom: 0.25rem;
  width: fit-content;
  width: -webkit-fit-content;
  width: -moz-fit-content;
}

.chatItem .message {
  max-width: 15rem;
  padding-right: 1rem;
  width: fit-content;
  width: -webkit-fit-content;
  width: -moz-fit-content;
  opacity: 1;
  transition: width 3s ease;
  width: auto;
  min-height: 1.5rem;
  word-wrap: break-word;
  word-break: break-all;
  line-height: 1.25rem;
  letter-spacing: 0.1rem;
}

.message:before {
  opacity: 0;
}

.bubble {
  overflow: hidden;
  margin: 0;
  padding: 0.25rem 0.85rem;
  border-radius: 1.25rem;
  transform-origin: 0 100%;
  /* vertical-align: middle; */
  transition: border-radius 0.25s ease-out 0.075s;
  background: rgba(206, 206, 206, 0.5);
}

.bubble.left {
  border-top-left-radius: 0.25rem;
}

.bubble.right {
  border-top-right-radius: 0.25rem;
  margin-top: 1.4rem;
}

.my-enter-active {
  transition: all 1.5s ease-in-out;
}
.my-enter {
  opacity: 0;
  transform: translateX(-30px);
}
.my-enter-to {
  transition: all 1.5s ease-in-out;
}

/* .bubble a {
  color: #0076ff;
}
.bubble .loading {
  position: absolute;
  width: 2.25rem;
  font-size: 2rem;
  line-height: 1rem;
}
.bubble .loading b {
  display: inline-block;
  color: rgba(0, 0, 0, 0.5);
} */

input {
  border-radius: 0.25rem;
  height: 1.4rem;
  width: 70%;
  padding: 0.5rem 0.8rem;
  border-width: 1px;
  border-style: solid solid solid solid;
  border-color: rgb(220, 223, 230);
  outline: none;
  font-size: 14px;
  color: rgb(96, 98, 102);
  background-color: white;
}
input:hover {
  border-color: rgb(192, 196, 203);
}
input:focus {
  border-color: #409eff;
}
input::placeholder,
input::-webkit-input-placeholder,
input:-ms-input-placeholder,
input:-moz-placeholder,
input::-moz-placeholder {
  color: rgb(192, 196, 203);
}

.ranking {
  margin: 0.5rem;
}

.rank {
  background-color: lightgrey;
  border-radius: 1.25rem;
  padding: 0.2rem 0.6rem;
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

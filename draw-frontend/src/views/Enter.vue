<template>
  <div class="enter">
    <h1 class="ml1">
      <span class="text-wrapper">
        <span class="line line1"></span>
        <span class="letters">Draw & Guess</span>
        <span class="line line2"></span>
      </span>
    </h1>
    <br>
    <br>
    <el-button type="primary" plain @click="enter">进入游戏</el-button>
    <!-- 登录对话框 -->
    <!-- <el-dialog title="登录" :visible.sync="dialogFormVisible" width="40%">
      <el-form :model="form" :label-width="formLabelWidth" :rules="rules">
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" clearable></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" show-password clearable></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.sex" placeholder="选择">
            <el-option label="女" value="woman"></el-option>
            <el-option label="男" value="man"></el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <div style="float: left; color: gray; margin-top: 1rem; font-size: 0.5rem;">
          <span>
            还没注册？
            <router-link to="register" class="link">点击注册</router-link>
          </span>
          或
          <span class="link" @click="handleTouristLogin">以游客身份登录</span>
        </div>
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="enter">确 定</el-button>
      </div>
    </el-dialog>-->
    <el-dialog title="设置昵称" :visible.sync="dialogNameVisible" width="40%">
      <input
        placeholder="输入昵称"
        v-model="username"
        @keyup.enter="dialogNameVisible = false; dialogAvatarVisible = true;"
      >
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogNameVisible = false">取 消</el-button>
        <el-button
          type="primary"
          @click="dialogNameVisible = false; dialogAvatarVisible = true;"
        >下 一 步</el-button>
      </div>
    </el-dialog>
    <el-dialog title="选择头像" :visible.sync="dialogAvatarVisible" width="40%">
      <el-radio v-model="sex" label="1">女</el-radio>
      <el-radio v-model="sex" label="2">男</el-radio>
      <div v-if="sex=='1'">
        <el-carousel
          :autoplay="false"
          type="card"
          height="10rem"
          indicator-position="none"
          @change="changeAvartar"
        >
          <el-carousel-item v-for="avatar in avatars.woman" :key="avatar">
            <embed :src="avatar" type="image/svg+xml" class="ava">
          </el-carousel-item>
        </el-carousel>
      </div>
      <div v-else>
        <el-carousel :autoplay="false" type="card" height="10rem" indicator-position="none">
          <el-carousel-item v-for="avatar in avatars.man" :key="avatar">
            <embed :src="avatar" type="image/svg+xml">
          </el-carousel-item>
        </el-carousel>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogAvatarVisible = false">取 消</el-button>
        <el-button type="primary" @click="sureToCreateUser">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import anime from "animejs";

export default {
  name: "enter",
  components: {},
  data() {
    return {
      dialogNameVisible: false,
      dialogAvatarVisible: false,
      isRegister: false,
      sex: "1",
      form: {
        email: "",
        password: "",
        name: ""
      },
      formLabelWidth: "5rem",
      rules: {
        email: [{ required: true, message: "请输入邮箱", trigger: "blur" }],
        password: [{ required: true, message: "请输入密码", trigger: "blur" }]
      },
      avatars: {
        man: [],
        woman: []
      },
      currentAvatarIndex: 0,
      username: ""
    };
  },
  computed: {},
  watch: {},
  mounted() {
    this.startup_anime();
    this.initAvatars();
  },

  methods: {
    enter() {
      var uid = sessionStorage.getItem("user");
      if (uid == null) {
        this.dialogNameVisible = true;
      } else {
        this.getUserInfo();
        this.$router.push({ name: "square" });
      }
    },
    getUserInfo() {
      var self = this;
      this.$axios
        .get(self.serverUrl + "/user", {
          params: { email: sessionStorage.getItem("user") }
        })
        .then(response => {
          // sessionStorage.setItem("name", response.data.username);
          self.$store.commit("setUser", response.data);
        })
        .catch(error => {
          self.$message.error("出错啦\n", error);
        });
    },
    sureToCreateUser() {
      this.dialogAvatarVisible = false;
      var id = Math.random()
        .toString(36)
        .substr(7);
      var avatar =
        this.sex == "1"
          ? this.avatars.woman[this.currentAvatarIndex]
          : this.avatars.man[this.currentAvatarIndex];
      var user = {
        email: id,
        password: id,
        name: this.username,
        avatar: avatar
      };
      this.$axios
        .post(this.serverUrl + "/user", user)
        .then(() => {
          sessionStorage.setItem("user", id);
          this.enter();
        })
        .catch(error => {
          this.$message.error("出错啦\n", error);
        });
    },
    startup_anime() {
      var letterEle = document.getElementsByClassName("letters")[0];
      var letters = letterEle.textContent.split("");
      letterEle.textContent = "";
      for (var i in letters) {
        var ele = document.createElement("span");
        ele.textContent = letters[i];
        ele.style = "margin: 0.5rem; font-size: 5rem;";
        ele.className = "letter";
        letterEle.appendChild(ele);
      }
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
    },
    initAvatars() {
      for (var i = 0; i < 15; i++) {
        this.avatars.woman.push(
          require("../../static/avatar/lady_" + i + ".svg")
        );
        this.avatars.man.push(require("../../static/avatar/man_" + i + ".svg"));
      }
    },
    handleTouristLogin() {
      this.$router.push({ name: "square" });
    },
    changeAvartar(newIndex, oldIndex) {
      this.currentAvatarIndex = newIndex;
    }
  }
};
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css?family=Baloo+Tamma&display=swap");
.enter {
  margin: 5rem 0;
  padding: 5rem 0;
  background-color: rgba(138, 212, 236, 0.9);
  text-align: center;
  color: white;
  /* font-family: "Avenir", Helvetica, Arial, sans-serif; */
  font-family: "Baloo Tamma", cursive;
}

.el-form-item {
  text-align: left;
}

.el-input {
  width: 20rem;
}

.link {
  text-decoration: underline;
  color: inherit;
}

.link:hover {
  color: #409eff;
  cursor: pointer;
}

embed {
  margin-top: 0.5rem;
  width: 9rem;
  height: 9rem;
}

embed:hover {
  box-shadow: 0rem 0rem 1rem gainsboro;
}

/* iframe {
  width: 5rem;
  height: 5rem;
  border-radius: 5rem;
} */

.ml1 {
  font-weight: 800;
  font-size: 3.5em;
}

.ml1 .letter {
  display: inline-block;
  line-height: 2em;
}

.ml1 .text-wrapper {
  position: relative;
  display: inline-block;
  padding-top: 0.2em;
  padding-right: 0.05em;
  padding-bottom: 0.05em;
}

.ml1 .line {
  opacity: 0;
  position: absolute;
  left: 0;
  height: 10px;
  width: 100%;
  background-color: #fff;
  transform-origin: 0 0;
  border-radius: 5rem;
}

.ml1 .line1 {
  top: 0;
}
.ml1 .line2 {
  bottom: 0;
}

input {
  border-radius: 0.25rem;
  height: 1.4rem;
  width: 75%;
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
input::-webkit-input-placeholder {
  color: rgb(192, 196, 203);
}
input:-ms-input-placeholder {
  color: rgb(192, 196, 203);
}
input:-moz-placeholder {
  color: rgb(192, 196, 203);
}
input::-moz-placeholder {
  color: rgb(192, 196, 203);
}
</style>

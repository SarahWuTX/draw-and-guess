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
    <el-dialog title="登录" :visible.sync="loginFormVisible" width="40%">
      <el-form :model="loginForm" :label-width="formLabelWidth" :rules="loginRules" ref="loginForm">
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="loginForm.email" clearable></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" show-password clearable></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <div style="float: left; color: gray; margin-top: 1rem; font-size: 0.5rem;">
          还没注册？
          <span class="link" @click="handleRegister">点击注册</span>
          或
          <span class="link" @click="handleTouristLogin">以游客身份登录</span>
        </div>
        <el-button @click="loginFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="login">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 注册对话框 -->
    <el-dialog title="注册" :visible.sync="registerFormVisible" width="40%">
      <el-form :model="form" :label-width="formLabelWidth" :rules="rules" ref="form">
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" clearable></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" show-password clearable></el-input>
        </el-form-item>
        <el-form-item label="重复密码" prop="password2">
          <el-input v-model="form.password2" show-password clearable></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="name">
          <el-input v-model="form.name" clearable></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.sex" placeholder="选择">
            <el-option label="女" value="1"></el-option>
            <el-option label="男" value="2"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <div style="float: left; color: gray; margin-top: 1rem; font-size: 0.5rem;">
          已有账号？
          <span class="link" @click="handleLogin">直接登录</span>
        </div>
        <el-button @click="registerFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="registerNext">下 一 步</el-button>
      </div>
    </el-dialog>
    <!-- 昵称对话框 -->
    <el-dialog title="设置昵称" :visible.sync="dialogNameVisible" width="40%">
      <input
        placeholder="输入昵称"
        v-model="username"
        @keyup.enter="dialogNameVisible = false; dialogAvatarVisible = true;"
      >
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogNameVisible = false">取 消</el-button>
        <el-button type="primary" @click="touristNext">下 一 步</el-button>
      </div>
    </el-dialog>
    <!-- 头像对话框 -->
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
        <el-carousel
          :autoplay="false"
          type="card"
          height="10rem"
          indicator-position="none"
          @change="changeAvartar"
        >
          <el-carousel-item v-for="avatar in avatars.man" :key="avatar">
            <embed :src="avatar" type="image/svg+xml">
          </el-carousel-item>
        </el-carousel>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleLastStep">上 一 步</el-button>
        <el-button type="primary" @click="sureToCreateUser">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import anime from "animejs";
import { callbackify } from "util";

export default {
  name: "enter",
  components: {},
  data() {
    var validatePass2 = (rule, value, callback) => {
      if (value !== this.form.password) {
        callback(new Error("两次输入密码不一致!"));
      } else {
        callback();
      }
    };
    return {
      loginFormVisible: false,
      registerFormVisible: false,
      dialogNameVisible: false,
      dialogAvatarVisible: false,
      currentStatus: 0,
      status: {
        login: 0,
        register: 1,
        tourist: 2
      },
      sex: "1",
      loginForm: {
        email: "",
        password: ""
      },
      form: {
        email: "",
        password: "",
        password2: "",
        name: "",
        sex: "1"
      },
      formLabelWidth: "5rem",
      loginRules: {
        email: [
          {
            required: true,
            message: "请输入邮箱",
            trigger: ["blur", "change"]
          },
          {
            type: "email",
            message: "请输入正确的邮箱地址",
            trigger: ["blur", "change"]
          }
        ],
        password: [
          {
            required: true,
            message: "请输入密码",
            trigger: ["blur", "change"]
          }
        ]
      },
      rules: {
        email: [
          {
            required: true,
            message: "请输入邮箱",
            trigger: ["blur", "change"]
          },
          {
            type: "email",
            message: "请输入正确的邮箱地址",
            trigger: ["blur", "change"]
          }
        ],
        password: [
          { required: true, message: "请输入密码", trigger: ["blur", "change"] }
        ],
        password2: [
          {
            required: true,
            message: "请再次输入密码",
            trigger: ["blur", "change"]
          },
          { validator: validatePass2, trigger: "blur" }
        ]
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
        this.loginFormVisible = true;
      } else {
        this.getUserInfo();
      }
    },
    getUserInfo() {
      var self = this;
      this.$axios
        .get(self.serverUrl + "/user", {
          params: { email: sessionStorage.getItem("user") }
        })
        .then(response => {
          sessionStorage.setItem("name", response.data.username);
          self.$store.commit("setUser", response.data);
          this.$router.push({ name: "square" });
        })
        .catch(error => {
          var msg = error.request.responseText;
          if (msg == null || msg == "") {
            msg = "出错啦";
          }
          self.$message.error(msg);
          sessionStorage.removeItem("user");
          this.enter();
        });
    },
    login() {
      if (!this.submitForm("loginForm")) {
        return;
      }
      var self = this;
      this.$axios
        .get(self.serverUrl + "/user", {
          params: { email: self.loginForm.email }
        })
        .then(response => {
          if (response.data.password == self.loginForm.password) {
            sessionStorage.setItem("user", self.loginForm.email);
            sessionStorage.setItem("name", response.data.username);
            self.$store.commit("setUser", response.data);
            self.$router.push({ name: "square" });
          } else {
            self.$message.error("用户名或密码错误");
          }
        })
        .catch(error => {
          self.$message.error("用户名或密码错误");
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
      if (this.currentStatus == this.status.tourist) {
        var user = {
          email: id,
          password: id,
          name: this.username,
          avatar: avatar
        };
      } else {
        var username = this.form.name;
        if (username == "") {
          username = id;
        }
        var user = {
          email: this.form.email,
          password: this.form.password,
          name: username,
          avatar: avatar
        };
      }
      this.$axios
        .post(this.serverUrl + "/user", user)
        .then(() => {
          sessionStorage.setItem("user", user.email);
          this.enter();
        })
        .catch(error => {
          this.$message.error("出错啦");
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
    handleRegister() {
      this.loginFormVisible = false;
      this.registerFormVisible = true;
    },
    handleLogin() {
      this.registerFormVisible = false;
      this.loginFormVisible = true;
    },
    handleTouristLogin() {
      this.loginFormVisible = false;
      this.dialogNameVisible = true;
      this.currentStatus = this.status.tourist;
    },
    changeAvartar(newIndex, oldIndex) {
      this.currentAvatarIndex = newIndex;
    },
    touristNext() {
      if (this.username != "") {
        this.dialogNameVisible = false;
        this.dialogAvatarVisible = true;
      }
    },
    registerNext() {
      if (!this.submitForm("form")) {
        return;
      }
      this.$axios
        .get(this.serverUrl + "/user", {
          params: { email: this.form.email }
        })
        .then(response => {
          this.$message.error("邮箱已被使用");
        })
        .catch(error => {
          this.registerFormVisible = false;
          this.dialogAvatarVisible = true;
          this.sex = this.form.sex;
          this.currentStatus = this.status.register;
        });
    },
    submitForm(formName) {
      var result;
      this.$refs[formName].validate(valid => {
        if (!valid) {
          result = false;
          return false;
        } else {
          result = true;
          return true;
        }
      });
      return result;
    },
    handleLastStep() {
      this.dialogAvatarVisible = false;
      if (this.currentStatus == this.status.tourist) {
        this.dialogNameVisible = true;
      } else if (this.currentStatus == this.status.register) {
        this.registerFormVisible = true;
      }
    }
  }
};
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css?family=Baloo+Tamma&display=swap");
@import url("https://fonts.googleapis.com/css?family=ZCOOL+KuaiLe");

.letters,
.letter {
  font-family: "Baloo Tamma", cursive;
}

.enter {
  margin: 5rem 0;
  padding: 5rem 0;
  background-color: rgba(138, 212, 236, 0.9);
  text-align: center;
  color: white;
  /* font-family: "Avenir", Helvetica, Arial, sans-serif; */
  font-family: "ZCOOL KuaiLe", cursive;
}

.el-select-dropdown__item,
.dialog-footer,
.el-form-item,
.el-radio__label {
  font-family: "ZCOOL KuaiLe", cursive;
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

<template>
  <div id="app">
    <!-- <div id="nav">
      <router-link to="/">Home</router-link>|
      <router-link to="/room">Room</router-link>
    </div>-->
    <transition name="slide-fade" mode="out-in">
      <router-view/>
    </transition>
  </div>
</template>

<script>
export default {
  mounted() {
    window.onbeforeunload = function(e) {
      e = e || window.event;
      console.log(sessionStorage.getItem("user").email);
      if (e) {
        this.$confirm("退出后将清除游戏数据, 是否继续?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
          center: true
        })
          .then(() => {
            this.$message({
              type: "success",
              message: "成功!"
            });
          })
          .catch(() => {
            this.$message({
              type: "info",
              message: "已取消"
            });
          });
      }
      if (sessionStorage.getItem("user").email.indexOf("@") != -1) {
        return;
      }
      this.$axios
        .delete(this.serverUrl + "/user", {
          params: { email: sessionStorage.getItem("user").email }
        })
        .then(() => {
          console.log("deleted");
        });
    };
  }
};
</script>

<style>
@import url("https://fonts.googleapis.com/css?family=ZCOOL+KuaiLe");

body {
  background-image: url("../static/background.png");
  background-size: cover;
  margin: 0;
}

#app {
  /* font-family: "Avenir", Helvetica, Arial, sans-serif; */
  font-family: "ZCOOL KuaiLe", cursive;
  font-size: 1.1rem;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #404040;
}
#nav {
  /* padding: 30px; */
}

.el-button span,
span.el-dialog__title,
.el-message-box {
  font-family: "ZCOOL KuaiLe", cursive;
  font-size: 1.1rem;
}

input {
  font-family: "ZCOOL KuaiLe", cursive;
  font-size: 1.5rem;
}

#nav a {
  font-weight: bold;
  color: #404040;
}

#nav a.router-link-exact-active {
  color: #ffffff;
  text-shadow: 1px 0 0 #404040, -1px 0 0 #404040, 0 1px 0 #404040,
    0 -1px 0 #404040;
  text-decoration: none;
}

.horizontal {
  display: flex;
  flex-wrap: wrap;
}

.center {
  margin: 0px auto;
}

.slide-fade-enter-active {
  transition: all 0.8s ease-out;
}
.slide-fade-leave-active {
  transition: all 0.5s ease-out;
}
.slide-fade-enter {
  transform: translateX(-20rem);
  opacity: 0;
}
.slide-fade-leave-to {
  transform: translateX(-10rem);
  opacity: 0;
}
</style>

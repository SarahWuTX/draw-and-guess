<template>
  <div class="register">
    <el-steps :active="active" finish-status="success">
      <el-step></el-step>
      <el-step></el-step>
      <el-step></el-step>
    </el-steps>
    <router-link to="/register/registerForm"></router-link>
    <router-view></router-view>
    <div style="height: 20rem; width: 100%">
      <router-view :name="route"></router-view>
    </div>
  </div>
</template>

<script>
export default {
  name: "register",
  data() {
    return {
      active: 0,
      route: "registerForm",
      form: {
        name: "",
        email: "",
        passworld: "",
        sex: ""
      },
      avatar: {
        man: [],
        woman: []
      }
    };
  },
  mounted() {},
  methods: {
    initAvatar() {
      for (var i in 16) {
      }
    }
  }
};
</script>

<style scoped>
.el-steps {
  background-color: rgba(255, 255, 255);
  padding: 0.75rem 2rem;
  border-radius: 5rem;
}
</style>


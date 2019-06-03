import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import VueAnime from "vue-animejs";
import axios from "axios";
import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css";

Vue.config.productionTip = false;
Vue.use(ElementUI);
Vue.use(VueAnime);
Vue.prototype.$axios = axios;
Vue.prototype.serverUrl = "http://localhost:8887";
// Vue.prototype.serverUrl = "https://www.sarahw.cn";

// 注册一个全局自定义指令
Vue.directive("scroll", {
  // 当被绑定的元素插入到 DOM 中时……
  inserted: function(el, binding) {
    let f = function(evt) {
      var top = el.scrollTop;
      // console.log(top);
      el.setAttribute("style", binding.value.style);
    };
    window.addEventListener("scroll", f);
  }
});

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");

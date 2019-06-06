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
// Vue.prototype.serverUrl = "http://localhost:8887";
Vue.prototype.serverUrl = "http://www.sarahw.cn:8887";

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");

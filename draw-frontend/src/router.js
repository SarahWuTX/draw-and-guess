import Vue from "vue";
import Router from "vue-router";

Vue.use(Router);

export default new Router({
  // mode: "history",
  base: process.env.BASE_URL,
  routes: [
    {
      path: "/",
      name: "enter",
      component: () => import("./views/Enter.vue")
    },
    {
      path: "/room/:id",
      name: "room",
      component: () =>
        import(/* webpackChunkName: "about" */ "./views/Room.vue")
    },
    {
      path: "/square",
      name: "square",
      component: () => import("./views/Square.vue")
    }
  ]
});

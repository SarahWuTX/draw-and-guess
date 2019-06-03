import Vue from "vue";
import Router from "vue-router";
import Home from "./views/Home.vue";
import Form from "./components/RegisterForm.vue";

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
    },
    {
      path: "/register",
      name: "register",
      component: () => import("./views/Register.vue"),
      children: [
        {
          name: "registerForm",
          path: "registerForm",
          component: Form
        },
        {
          name: "avatars",
          path: "avatars",
          component: () => import("./components/Avatars.vue")
        },
        {
          name: "enterSquare",
          path: "enterSquare",
          component: () => import("./components/EnterSquare.vue")
        }
      ]
    }
  ]
});

import Vue from "vue";
import Vuex from "vuex";
// import { stat } from "fs";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    user: {},
    ws: {},
    canvas: {},
    room: {},
    current: null,
    chat: [],
    time: 0,
    isDrawer: false
  },
  mutations: {
    setUser(state, user) {
      state.user = user;
    },
    setWs(state, ws) {
      state.ws = ws;
    },
    setRoom(state, room) {
      state.room = room;
    },
    setCurrent(state, current) {
      state.current = current;
      if (current != null && current.drawer == state.user.email) {
        state.isDrawer = true;
      } else {
        state.isDrawer = false;
      }
    },
    setCanvas(state, canvas) {
      state.canvas = canvas;
    },
    addChat(state, msg) {
      state.chat.push(msg);
    },
    cleanChat(state) {
      state.chat = [];
    },
    setTime(state, time) {
      state.time = time;
    },
    setIsDrawer(state, isDrawer) {
      state.isDrawer = isDrawer;
    }
  }
  //   actions: {
  //     addMsg_action({ commit }, obj) {
  //       commit("addMsg", obj);
  //     }
  //   }
});

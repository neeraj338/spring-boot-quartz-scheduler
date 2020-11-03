import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import vuetify from "./plugins/vuetify";
import axios from "axios";
import moment from "moment";
import Constants from "@/plugins/Constants.ts";
import Notifications from "vue-notification";

Vue.prototype.$http = axios;
Vue.config.productionTip = false;

Vue.use(Notifications);
Vue.use(Constants);
Vue.filter("formatDate", function(value: any) {
  if (value) {
    return moment(String(value)).format("yyyy-MM-dd hh:mm");
  }
});

new Vue({
  router,
  store,
  vuetify,
  render: h => h(App)
}).$mount("#app");

axios.interceptors.request.use(
  config => {
    config.headers.common["Content-Type"] = "application/json;charset=UTF-8"
    config.headers.common["Access-Control-Allow-Origin"] = "*"

    config.headers.post["Content-Type"] = "application/json;charset=UTF-8"
    config.headers.post["Access-Control-Allow-Origin"] = "*"

    config.headers.put["Content-Type"] = "application/json;charset=UTF-8"
    config.headers.put["Access-Control-Allow-Origin"] = "*"

    config.headers.patch["Content-Type"] = "application/json;charset=UTF-8"
    config.headers.patch["Access-Control-Allow-Origin"] = "*"

    const token = localStorage.getItem("access_token");
    if (token) {
      config.headers.common["Authorization"] = token;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);
axios.interceptors.response.use(
  response => {
    if (response.status === 200 || response.status === 201) {
      return Promise.resolve(response);
    } else {
      return Promise.reject(response);
    }
  },
error => {
    if (error.response.status) {
      
      let reason = "Processing failed due to some error."
      if(error.response.data && error.response.data.data && error.response.data.data.message){
        reason = error.response.data.data.message;
      }
      Vue.notify({
        type: "error",
        title: `<h2>${error.response.status} ❌</h2>`,
        text: `<h3> ${reason} !</h3>`,
      });
      return Promise.reject(error.response);
    }
  }
);


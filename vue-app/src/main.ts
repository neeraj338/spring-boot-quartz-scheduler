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

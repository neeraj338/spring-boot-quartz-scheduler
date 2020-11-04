import Vue from "vue";

export default {
  install(Vue, options) {
    Vue.prototype.$constants = function() {
      return {
        POST_SCHEDULE: `${process.env.VUE_APP_BASE_URL}/scheduler/schedule`,
        GET_JOB_NAME: `${process.env.VUE_APP_BASE_URL}/scheduler/checkJobName`,
        GET_JOBS: `${process.env.VUE_APP_BASE_URL}/scheduler/jobs`,
        RUN_NOW: `${process.env.VUE_APP_BASE_URL}/scheduler/start`,
        STOP_JOB: `${process.env.VUE_APP_BASE_URL}/scheduler/stop`,
        PAUSE_JOB: `${process.env.VUE_APP_BASE_URL}/scheduler/pause`,
        RESUME_JOB: `${process.env.VUE_APP_BASE_URL}/scheduler/resume`,
        DELETE_JOB: `${process.env.VUE_APP_BASE_URL}/scheduler/delete`,
        EDIT_JOB: `${process.env.VUE_APP_BASE_URL}/scheduler/update`,
      };
    };
  }
};

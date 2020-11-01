<template>
  <SchedulerApp msg="Welcome to Your Vue.js + TypeScript App" />
</template>

<script lang="ts">
import Vue from "vue";
import SchedulerApp from "@/components/SchedulerApp.vue"; // @ is an alias to /src

export default Vue.extend({
  name: "Home",
  components: {
    SchedulerApp
  }
});
</script>

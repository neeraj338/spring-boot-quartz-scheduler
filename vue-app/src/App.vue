<template>
  <v-app>
    <app-navigation />
    <notifications position="bottom right" />
    <v-main transition="slide-x-transition">
      <router-view></router-view>
    </v-main>
  </v-app>
</template>

<script lang="ts">
import Vue from "vue";
import AppNavigation from "@/components/AppNavigation.vue";

export default Vue.extend({
  name: "App",
  components: {
    AppNavigation
  },

  data: () => ({
    //
  }),

  methods: {},
  computed: {}
});
</script>

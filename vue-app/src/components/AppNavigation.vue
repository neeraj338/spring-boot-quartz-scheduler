<template>
  <v-app-bar app color="primary" dark>
    <div class="d-flex align-center">
      <v-img
        alt="Vuetify Logo"
        class="shrink mr-2"
        contain
        :src="require('@/assets/vuetify-logo-dark.png')"
        transition="scale-transition"
        width="40"
      />

      <v-img
        alt="Vuetify Name"
        class="shrink mt-1 hidden-sm-and-down"
        contain
        min-width="100"
        :src="require('@/assets/vuetify-name-dark.png')"
        width="100"
      />
    </div>

    <v-spacer></v-spacer>

    <v-btn href="https://github.com/neeraj338" target="_blank" text>
      <span class="mr-2">@neeraj338</span>
      <v-icon>mdi-github</v-icon>
    </v-btn>
  </v-app-bar>
</template>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({
  name: "AppNavigation"
});
</script>

<style></style>

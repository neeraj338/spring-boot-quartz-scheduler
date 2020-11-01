<template>
  <v-form>
    <v-container>
      <v-row>
        <v-col cols="12" sm="6" md="3">
          <v-text-field
            :value="filledDetails.jobName"
            label="JobName"
            outlined
            readonly
          ></v-text-field>
          <v-text-field
            :value="
              filledDetails.cron
                ? filledDetails.cron
                : filledDetails.time && filledDetails.date
                ? filledDetails.date + ' ' + filledDetails.time
                : filledDetails.defaultCron && filledDetails.defaultCron.name
                ? filledDetails.defaultCron.name
                : ''
            "
            label="Schedule"
            outlined
            readonly
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6" md="3">
          <v-text-field
            :value="filledDetails.httpMethod"
            label="HttpMethod"
            outlined
            readonly
          ></v-text-field>
          <v-text-field
            :value="filledDetails.url"
            label="Url"
            :hint="filledDetails.url"
            outlined
            readonly
          ></v-text-field>
        </v-col>
        <v-col v-if="filledDetails.httpMethod != 'GET'" cols="12" sm="6" md="6">
          <v-textarea
            :value="filledDetails.jsonstr"
            no-resize
            label="Json"
            outlined
            readonly
          ></v-textarea>
        </v-col>
      </v-row>
    </v-container>
  </v-form>
</template>

<script>
import Vue from "vue";

export default {
  name: "JobConfirmStep",
  props: ["filledDetails"],
  methods: {}
};
</script>

<style></style>

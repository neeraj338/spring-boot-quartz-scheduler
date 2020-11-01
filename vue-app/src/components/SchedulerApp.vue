<template>
  <v-container>
    <job-stepper-form></job-stepper-form>
    <jobs-data-table></jobs-data-table>
  </v-container>
</template>

<script lang="ts">
import Vue from "vue";
import JobStepper from "@/components/job/JobStepper.vue";
import JobsDataTable from "@/components/job/JobsDataTable.vue";
import JobStepperForm from "@/components/job/JobStepperForm.vue";

export default Vue.extend({
  name: "SchedulerApp",
  props: ["msg"],
  components: {
    JobStepperForm,
    JobsDataTable,
  },

  data: () => ({}),

  methods: {},
  computed: {},
});
</script>

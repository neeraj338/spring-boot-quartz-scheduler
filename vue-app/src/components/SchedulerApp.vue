<template>
  <v-container>
    <job-stepper-form @reFetchJobs="reFetchJobs"></job-stepper-form>
    <jobs-data-table :isReFetchJobs="rand"></jobs-data-table>
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

  data: () => ({
    rand: 0,
  }),

  methods: {
    reFetchJobs() {
      this.rand = Math.random() * 10000000000;
    },
  },
  computed: {},
});
</script>

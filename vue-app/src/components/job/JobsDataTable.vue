<template>
  <div>
    <modal
      :show="showModal"
      :editItem="editJobItem"
      @close="showModal = false"
    />
    <v-data-table
      class="elevation-1"
      :item-class="rowClass"
      :headers="headers"
      :items="data"
      :items-per-page="3"
      :search="search"
      :custom-filter="filterOnlyCapsText"
      :footer-props="{
        'items-per-page-options': [3],
      }"
    >
      <template v-slot:item.controls="props">
        <v-btn
          v-if="props.item.jobStatus != 'RUNNING'"
          class="mx-1"
          color="green"
          @click="runNow(props.item)"
          fab
          dark
          x-small
        >
          <v-icon dark x-small>mdi-arrow-right-bold-circle</v-icon>
        </v-btn>
        <v-btn
          v-if="props.item.jobStatus == 'RUNNING'"
          class="mx-1"
          color="red"
          fab
          dark
          x-small
          @click="stopJob(props.item)"
        >
          <v-icon dark x-small>mdi-octagon</v-icon>
        </v-btn>
        <v-btn
          v-if="props.item.jobStatus == 'SCHEDULED'"
          class="mx-1"
          color="amber"
          fab
          dark
          x-small
          @click="pause(props.item)"
        >
          <v-icon dark x-small>mdi-pause-circle-outline</v-icon>
        </v-btn>
        <v-btn
          v-if="props.item.jobStatus == 'PAUSED'"
          class="mx-1"
          color="green"
          fab
          dark
          x-small
          @click="resume(props.item)"
        >
          <v-icon dark x-small>mdi-arrow-right-drop-circle-outline</v-icon>
        </v-btn>
        <v-btn
          v-if="props.item.jobStatus != 'RUNNING'"
          class="mx-1"
          color="red"
          fab
          dark
          x-small
          @click="deleteJob(props.item)"
        >
          <v-icon dark x-small>mdi-delete</v-icon>
        </v-btn>
        <v-btn
          v-if="props.item.jobStatus != 'RUNNING'"
          class="mx-1"
          color="blue"
          fab
          dark
          x-small
          @click="showJobEditModel(props.item)"
        >
          <v-icon dark x-small>mdi-pencil</v-icon>
        </v-btn>
      </template>
      <template v-slot:top>
        <v-text-field
          v-model="search"
          label="Search"
          class="mx-4"
        ></v-text-field>
      </template>
      <template v-slot:body.append>
        <tr>
          <td></td>
          <td>
            <v-text-field
              v-model="calories"
              type="number"
              label="Less than"
            ></v-text-field>
          </td>
          <td colspan="4"></td>
        </tr>
      </template>
    </v-data-table>
  </div>
</template>

<script>
import Vue from "vue";
import axios from "axios";
import moment from "moment";
import modal from "@/components/job/JobEditModal";

export default Vue.extend({
  name: "JobsDataTable",
  components: {
    modal,
  },
  data() {
    return {
      search: "",
      calories: "",
      interval: "",
      showModal: false,
      editJobItem: "",
      data: [],
    };
  },
  mounted: function() {
    this.fetchJobs();
    /*
    this.interval = setInterval(
      function() {
        this.fetchJobs();
      }.bind(this),
      30000
    );
    */
  },
  computed: {
    headers() {
      return [
        {
          text: "Job Name",
          align: "start",
          sortable: false,
          value: "jobName",
        },
        {
          text: "Last Run",
          value: "lastFiredTime",
          filter: (value) => {
            if (!this.calories) return true;

            return value < parseInt(this.calories);
          },
        },
        { text: "Next Run", value: "nextFireTime" },
        { text: "Job Status", value: "jobStatus" },
        { text: "", value: "controls", sortable: false },
      ];
    },
  },

  methods: {
    filterOnlyCapsText(value, search, item) {
      return (
        value != null &&
        search != null &&
        typeof value === "string" &&
        (value
          .toString()
          .toLocaleUpperCase()
          .indexOf(search) !== -1 ||
          value
            .toString()
            .toLocaleLowerCase()
            .indexOf(search) !== -1)
      );
    },
    showJobEditModel(item) {
      this.showModal = true;
      this.editJobItem = item;
    },
    fetchJobs: function() {
      //"https://jsonplaceholder.typicode.com/users";
      this.$http.get(this.$constants().GET_JOBS).then(
        (result) => {
          console.log(result.data);
          this.data = result.data.data;
          this.data.map((x) => {
            x.lastFiredTime = moment(x.lastFiredTime).format(
              "YYYY-MM-DD HH:mm"
            );
            const nextRun = x.nextFireTime ? x.nextFireTime : x.scheduleTime;
            x.nextFireTime = moment(nextRun).format("YYYY-MM-DD HH:mm");
          });
        },
        (error) => {
          console.log(error);
        }
      );
    },
    runNow(item) {
      console.log(`Run Now triggered :: ${item}`);
      this.$http
        .patch(
          this.$constants().RUN_NOW,
          {},
          {
            params: { jobName: item.jobName },
          }
        )
        .then(
          (result) => {
            console.log(result.data);
          },
          (error) => {
            console.log(error);
          }
        );
    },
    stopJob(item) {
      console.log(`Stoping job :: ${item}`);
      this.$http
        .patch(
          this.$constants().STOP_JOB,
          {},
          {
            params: { jobName: item.jobName },
          }
        )
        .then(
          (result) => {
            console.log(result.data);
          },
          (error) => {
            console.log(error);
          }
        );
    },
    pause(item) {
      console.log(`Pause job :: ${item}`);
      this.$http
        .patch(
          this.$constants().PAUSE_JOB,
          {},
          {
            params: { jobName: item.jobName },
          }
        )
        .then(
          (result) => {
            console.log(result.data);
          },
          (error) => {
            console.log(error);
          }
        );
    },
    resume(item) {
      console.log(`Resume job :: ${item}`);
      this.$http
        .patch(
          this.$constants().RESUME_JOB,
          {},
          {
            params: { jobName: item.jobName },
          }
        )
        .then(
          (result) => {
            console.log(result.data);
          },
          (error) => {
            console.log(error);
          }
        );
    },
    deleteJob(item) {
      console.log(`Delete job :: ${item}`);
      this.$http
        .delete(this.$constants().DELETE_JOB, {
          params: { jobName: item.jobName },
        })
        .then(
          (result) => {
            console.log(result.data);
          },
          (error) => {
            console.log(error);
          }
        );
    },
    rowClass(item) {
      console.log(item);
      return "ma-xs-2";
    },
  },
});
</script>

<style></style>

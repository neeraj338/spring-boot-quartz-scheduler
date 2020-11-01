<template>
  <v-stepper v-model="curr" color="green">
    <v-stepper-header class="overflow-x-auto">
      <v-stepper-step
        v-for="(step, n) in steps"
        :key="n"
        :complete="stepComplete(n + 1)"
        :step="n + 1"
        :rules="[value => !!step.valid]"
        :color="stepStatus(n + 1)"
      >
        {{ step.name }}
      </v-stepper-step>
    </v-stepper-header>
    <v-stepper-content v-for="(step, n) in steps" :step="n + 1" :key="n">
      {{ step.name }}
      <v-card color="grey lighten-1" class="mb-12" elevation="8" height="200px">
        <v-card-text>
          <v-form :ref="'stepForm'" v-model="step.valid" lazy-validation>
            <template v-if="n == 0">
              <v-container>
                <v-row>
                  <v-col cols="12" md="4">
                    <v-text-field
                      v-model="formField.jobName"
                      :rules="step.rules"
                      :counter="10"
                      label="Job name"
                      outlined
                      required
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" md="3">
                    <v-btn
                      large
                      elevation="6"
                      color="primary"
                      @click="validateJobName"
                    >
                      check
                    </v-btn>
                  </v-col>
                </v-row>
              </v-container>
            </template>
            <!-- choose cron expression and date time -->
            <template v-if="n == 1">
              <v-container>
                <v-row>
                  <v-col cols="12" sm="3" md="3">
                    <v-menu
                      v-model="formField.menu2"
                      :close-on-content-click="false"
                      :nudge-right="40"
                      transition="scale-transition"
                      offset-y
                      min-width="290px"
                    >
                      <template v-slot:activator="{ on, attrs }">
                        <v-text-field
                          v-model="formField.date"
                          label="Pick date"
                          prepend-icon="mdi-calendar"
                          readonly
                          v-bind="attrs"
                          v-on="on"
                        ></v-text-field>
                      </template>
                      <v-date-picker
                        v-model="formField.date"
                        @input="formField.menu2 = false"
                      ></v-date-picker>
                    </v-menu>
                  </v-col>
                  <v-col cols="12" sm="3">
                    <v-dialog
                      ref="dialog"
                      v-model="formField.timeModel"
                      :return-value.sync="formField.time"
                      persistent
                      width="290px"
                    >
                      <template v-slot:activator="{ on, attrs }">
                        <v-text-field
                          v-model="formField.time"
                          label="Pick time"
                          prepend-icon="mdi-clock-time-four-outline"
                          readonly
                          v-bind="attrs"
                          v-on="on"
                        ></v-text-field>
                      </template>
                      <v-time-picker
                        v-if="formField.timeModel"
                        v-model="formField.time"
                        full-width
                      >
                        <v-spacer></v-spacer>
                        <v-btn
                          text
                          color="primary"
                          @click="formField.timeModel = false"
                        >
                          Cancel
                        </v-btn>
                        <v-btn
                          text
                          color="primary"
                          @click="setTime(formField.time, n)"
                        >
                          OK
                        </v-btn>
                      </v-time-picker>
                    </v-dialog>
                  </v-col>
                  <v-col cols="12" md="3" sm="3">
                    <v-text-field
                      v-model="formField.cron"
                      :counter="30"
                      label="Cron"
                      outlined
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" md="3" sm="3">
                    <v-select
                      item-text="name"
                      item-value="last"
                      :items="cronItems"
                      v-model="formField.defaultCron"
                      label="Select Schedule"
                      @change="assignCronScheduleOnChange"
                      outlined
                    ></v-select>
                  </v-col>
                </v-row>
              </v-container>
            </template>
            <!-- job data  -->
            <template v-if="n == 2">
              <v-container>
                <v-row>
                  <v-col cols="6" md="2" sm="2">
                    <v-select
                      item-text="name"
                      item-value="last"
                      :items="httpMethods"
                      v-model="formField.httpMethod"
                      label="Http Method"
                      @change="assignHttpMethodOnChange"
                      outlined
                    ></v-select>
                  </v-col>
                  <v-col cols="12" md="6" sm="6">
                    <v-text-field
                      v-model="formField.url"
                      label="Url"
                      :rules="step.rules"
                      outlined
                      required
                    ></v-text-field
                  ></v-col>
                  <div v-if="formField.httpMethod !== 'GET'">
                    <div
                      class="static"
                      v-bind:class="{ active: true, 'text-danger': true }"
                      v-if="formField.jsonstr && jsonerror"
                    >
                      ❌ {{ jsonerror }}
                    </div>
                    <div
                      v-bind:class="{ 'text-success': true }"
                      v-if="!jsonerror"
                    >
                      Valid JSON ✔
                    </div>
                    <v-textarea
                      background-color="amber lighten-4"
                      color="orange orange-darken-4"
                      no-resize
                      rows="4"
                      label="JSON"
                      v-model="formField.jsonstr"
                      ref="jsonText"
                      placeholder="paste or type JSON here..."
                      @change="prettyFormat(n)"
                    ></v-textarea>
                  </div>
                </v-row>
              </v-container>
            </template>
            <!--display confirm -->
            <template v-if="n == 3">
              <job-confirm-step :filledDetails="formField"></job-confirm-step>
            </template>
          </v-form>
        </v-card-text>
      </v-card>
      <v-btn
        v-if="n + 1 < lastStep"
        color="primary"
        @click="validate(n)"
        :disabled="!step.valid"
        >Continue</v-btn
      >
      <v-btn v-else color="success" @click="done()">Done</v-btn>

      <v-btn color="orange darken-4" dark text @click="curr = n == 0 ? 1 : n"
        >Back</v-btn
      >
    </v-stepper-content>
  </v-stepper>
</template>

<script>
import Vue from "vue";
import JobConfirmStep from "@/components/job/JobConfirmStep.vue";

export default {
  name: "JobStepperForm",
  components: { JobConfirmStep },
  data: () => ({
    curr: 1,
    lastStep: 4,
    steps: [
      {
        name: "Choose Job Name",
        rules: [v => !!v || "Required"],
        valid: true
      },
      {
        name: "Select Corn Schedule",
        rules: [v => !!v || "Required"],
        valid: true
      },
      {
        name: "Job Data ",
        rules: [v => (v && v.length >= 4) || "Enter at least 4 characters."],
        valid: true
      },
      { name: "Confirm" }
    ],
    valid: false,
    formField: {
      jobName: "testing",
      menu2: false,
      date: new Date().toISOString().substr(0, 10),
      time: "00:00",
      timeModel: false,
      cron: "",
      defaultCron: { name: "Now", last: "" },
      httpMethod: "GET",
      url: "http://google.com",
      jsonstr: "{}"
    },
    jsonerror: "",
    stepForm: []
  }),
  methods: {
    stepComplete(step) {
      return this.curr > step;
    },
    stepStatus(step) {
      return this.curr > step ? "green" : "blue";
    },
    validate(n) {
      this.steps[n].valid = false;
      const v = this.$refs.stepForm[n].validate();
      if (v) {
        this.steps[n].valid = true;
        // continue to next
        this.curr = n + 2;
      }
    },
    done() {
      this.curr = 1;
      this.createJob();
      this.formField = {
        jobName: "New Job",
        menu2: false,
        date: new Date().toISOString().substr(0, 10),
        time: "00:00",
        timeModel: false,
        cron: "0 0/1 * 1/1 * ? *",
        defaultCron: { name: "Now", last: "" },
        httpMethod: "GET",
        url: "http://google.com",
        jsonstr: "{}"
      };
      console.log(this.formField.jobName);
    },
    createJob() {
      const axiosConfig = {
        headers: {
          "Content-Type": "application/json;charset=UTF-8",
          "Access-Control-Allow-Origin": "*"
        }
      };
      this.$http
        .post(this.$constants().POST_SCHEDULE, this.formField.jsonstr, {
          params: {
            jobName: this.formField.jobName,
            jobScheduleTime: this.formField.date + " " + this.formField.time,
            cronExpression: this.formField.cron
          },
          headers: {
            "Content-Type": "application/json;charset=UTF-8",
            "Access-Control-Allow-Origin": "*"
          }
        })
        .then(
          result => {
            console.log(result.data);
            this.$notify({
              type: "success",
              title: "<h2>Success!</h2>",
              text: "<h3>Done ✅</h3>"
            });
          },
          error => {
            console.log(error);
            this.$notify({
              type: "error",
              title: "<h2>Error!</h2>",
              text: "</h3>Process Failed ❌</h3>"
            });
          }
        );
    },
    validateJobName() {
      this.$http
        .get(this.$constants().GET_JOB_NAME, {
          params: { jobName: this.formField.jobName }
        })
        .then(
          result => {
            console.log(result.data);
            if (result.data.data == true) {
              this.$notify({
                type: "warn",
                title: "<h2>Already Exists ❌</h2>",
                message: "Job Name already exists!"
              });
              console.log(process.env.BASE_URL);
              this.formField.jobName =
                this.formField.jobName + Math.ceil(Math.random() * 10000000000);
            } else {
              this.$notify({
                type: "important",
                title: "<h2>Good to choose ✅ </h2>",
                text: ""
              });
            }
          },
          error => {
            console.log(error);
          }
        );
    },
    setTime(time, index) {
      console.log(time);
      this.$refs.dialog[index - 1].save(time);
    },
    assignCronScheduleOnChange(value) {
      console.log(`Selected value is ${value}`);
      this.formField.cron = value;
    },
    assignHttpMethodOnChange(httpMethod) {
      console.log(`Selected Http Method is  ${httpMethod}`);
      this.formField.httpMethod = httpMethod;
    },
    prettyFormat: function(index) {
      // reset error
      this.jsonerror = "";
      let jsonValue = "";
      try {
        // try to parse
        jsonValue = JSON.parse(this.formField.jsonstr);
        this.steps[index].valid = false;
        const v = this.$refs.stepForm[2].validate();
        if (v) {
          this.steps[index].valid = true;
        }
        this.valid = true;
      } catch (e) {
        this.steps[index].valid = false;
        this.valid = false;
        this.jsonerror = JSON.stringify(e.message);
        const textarea = this.$refs.jsonText;
        if (this.jsonerror.indexOf("position") > -1) {
          // highlight error position
          const positionStr = this.jsonerror.lastIndexOf("position") + 8;
          const posi = parseInt(
            this.jsonerror.substr(positionStr, this.jsonerror.lastIndexOf('"'))
          );
          this.jsonerror = "Err:@ pos:20";
          if (posi >= 0) {
            if (textarea.createTextRange) {
              const range = textarea.createTextRange();
              range.collapse(true);
              range.moveEnd("character", posi);
              range.moveStart("character", posi + 1);
              range.select();
            } else if (textarea.setSelectionRange) {
              textarea.setSelectionRange(posi, posi + 1);
            }
          }
        }
        return "";
      }
      return JSON.stringify(jsonValue, null, 2);
    }
  },
  computed: {
    cronItems() {
      return [
        {
          name: "Now",
          last: ""
        },
        {
          name: "Every 1 minutes",
          last: "0 0/1 * 1/1 * ? *"
        },
        {
          name: "Every day at 10 AM",
          last: "0 0 10 1/1 * ? *"
        },
        {
          name: "Every hour starting 10 AM",
          last: "0 0 0/1 1/1 * ? *"
        },
        {
          name: "Every week Tue and Thur at 10 AM",
          last: "0 0 10 ? * TUE,THU *"
        }
      ];
    },
    httpMethods() {
      return [
        {
          name: "GET",
          last: "GET"
        },
        {
          name: "PUT",
          last: "PUT"
        },
        {
          name: "PATCH",
          last: "PATCH"
        },
        {
          name: "POST",
          last: "POST"
        },
        {
          name: "DELETE",
          last: "DELETE"
        }
      ];
    }
  }
};
</script>

<style></style>

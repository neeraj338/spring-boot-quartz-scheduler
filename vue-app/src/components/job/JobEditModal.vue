<template>
  <div class="modal-mask" v-show="show">
    <div class="modal-container">
      <div class="modal-header">
        <h3>Editing Job... {{ editItem.jobName }}</h3>
      </div>
      <div class="modal-header text-right"></div>
      <div class="modal-body">
        <v-text-field
          v-model="cron"
          :counter="10"
          label="New Cron"
          outlined
        ></v-text-field>
        <div>
          <!-- date time -->
          <v-menu
            v-model="dateMenu"
            :close-on-content-click="false"
            :nudge-right="40"
            transition="scale-transition"
            offset-y
            min-width="290px"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="date"
                label="Pick date"
                prepend-icon="mdi-calendar"
                readonly
                v-bind="attrs"
                v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker
              v-model="date"
              @input="dateMenu = false"
            ></v-date-picker>
          </v-menu>

          <v-dialog
            ref="dialog"
            v-model="timeModel"
            :return-value.sync="time"
            persistent
            width="290px"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="time"
                label="Pick time"
                prepend-icon="mdi-clock-time-four-outline"
                readonly
                v-bind="attrs"
                v-on="on"
              ></v-text-field>
            </template>
            <v-time-picker v-if="timeModel" v-model="time" full-width>
              <v-spacer></v-spacer>
              <v-btn text color="primary" @click="timeModel = false">
                Cancel
              </v-btn>
              <v-btn text color="primary" @click="setTime(time)">
                OK
              </v-btn>
            </v-time-picker>
          </v-dialog>
        </div>
      </div>
      <div class="modal-footer text-right">
        <v-btn color="red" fab dark x-small @click="close">
          <v-icon dark small>mdi-close-circle-outline</v-icon>
        </v-btn>
        <v-btn small elevation="6" color="primary" @click="saveSchedule">
          Save ✅
        </v-btn>
      </div>
    </div>
  </div>
</template>

<script>
import Vue from "vue";
export default Vue.extend({
  name: "JobEditModal",
  props: ["show", "editItem"],
  data: () => ({
    cron: "",
    dateMenu: false,
    date: new Date().toISOString().substr(0, 10),
    time: "00:00",
    timeModel: false,
  }),
  methods: {
    saveSchedule: function() {
      // Some save logic goes here...

      this.$http
        .put(
          this.$constants().EDIT_JOB,
          {},
          {
            params: {
              jobName: this.editItem.jobName,
              jobScheduleTime: this.date + " " + this.time,
              cronExpression: this.cron,
            },
          }
        )
        .then(
          (result) => {
            console.log(result.data);
            this.close();
            this.$notify({
              type: "success",
              title: "<h2>Done ✅</h2>",
              message: "Updated Successfully !",
            });
          },
          (error) => {
            console.log(error);
          }
        );
    },
    close: function() {
      this.$emit("close");
    },
    setTime(time) {
      console.log(time);
      this.$refs.dialog.save(time);
    },
  },
  mounted: function() {
    document.addEventListener("keydown", (e) => {
      if (this.show && e.keyCode == 27) {
        this.close();
      }
    });
  },
});
</script>

<style lang="css" scoped>
 {
  box-sizing: border-box;
}

.modal-mask {
  position: fixed;
  z-index: 9998;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  transition: opacity 0.3s ease;
}

.modal-container {
  width: 500px;
  margin: 40px auto 0;
  padding: 20px 30px;
  background-color: #fff;
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
  transition: all 0.3s ease;
  font-family: Helvetica, Arial, sans-serif;
}

.modal-header h3 {
  margin-top: 0;
  color: #42b983;
}

.modal-body {
  margin: 20px 0;
}

.text-right {
  text-align: right;
}

.form-label {
  display: block;
  margin-bottom: 1em;
}

.form-label > .form-control {
  margin-top: 0.5em;
}

.form-control {
  display: block;
  width: 100%;
  padding: 0.5em 1em;
  line-height: 1.5;
  border: 1px solid #ddd;
}

/*
 * The following styles are auto-applied to elements with
 * transition="modal" when their visibility is toggled
 * by Vue.js.
 *
 * You can easily play with the modal transition by editing
 * these styles.
 */

.modal-enter {
  opacity: 0;
}

.modal-leave-active {
  opacity: 0;
}

.modal-enter .modal-container,
.modal-leave-active .modal-container {
  -webkit-transform: scale(1.1);
  transform: scale(1.1);
}
</style>

<template>
  <div class="imp-bg" v-on:click="close">
  </div>
  <div class="imp-box">
    <div class="imp-title">
      Importer une image:
      <button class="imp-close" v-on:click="close">X</button>
    </div>
    <div class="imp-field">
      <label>File
        <input id="file" ref="file" type="file" v-on:change="handleFileUpload"/>
      </label>
      <div class="imp-btn">
        <button v-on:click="uploadImge">Importer</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "importImg",
  data() {
    return {
      file: '',
      filename: '',
    }
  },
  methods: {
    close() {
      this.$emit('update', false);
    },
    handleFileUpload() {
      this.file = this.$refs.file.files[0];
    },
    uploadImge() {
      if (this.file !== '') {
        this.$parent.httpApi.postImage(this.file);
      }
      this.$parent.update('impImg', false);
    },
  },
}
</script>

<style scoped>
.imp-bg {
  background-color: rgba(0, 0, 0, 0.7);
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  width: 100vw;
}

.imp-bg:hover {
  cursor: pointer;
}

.imp-box {
  position: fixed;
  left: 33%;
  cursor: auto;
  background-color: white;
  width: 33%;
  border-radius: 10px;
  margin-top: 30vh;
}

.imp-title {
  font-size: 20px;
  font-weight: bold;
  height: 30px;
  border-bottom: solid 1px;
  border-color: lightgray;
}

.imp-close {
  cursor: pointer;
  position: absolute;
  top: 2px;
  right: 10px;
  border: none;
  border-radius: 25px;
  height: 25px;
  width: 25px;
  background-color: rgb(189, 61, 61);
  opacity: 90%;
  color: white;
}

.imp-btn {
}

.imp-field {
  height: 75px;
  padding-top: 10px;
  padding-bottom: 0;
  padding-left: 10px;
  text-align: left;
}
</style>
<template>
  <div class="hello">
    <select v-model="selected" multiple>
      <option v-for="image in response" :key="image.id" :value="getImage(image.id)">
        {{ image.name }}
      </option>
    </select>
    <img alt="" v-bind:src="selected[0]">
    <div class="container">
      <div class="large-12 medium-12 small-12 cell">
        <label>File
          <input id="file" ref="file" type="file" v-on:change="handleFileUpload()"/>
        </label>
        <button v-on:click="submitFile()">Submit</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "HelloWorld",
  props: {
    msg: String,
  },
  data() {
    return {
      file: '',
      selected: [],
      response: [],
      errors: [],
    };
  },
  mounted: async function () {
    await new Promise(() =>
        axios.get(`images`)
            .then((response) => {
              // JSON responses are automatically parsed.
              this.response = response.data;
            }));
    throw new Error('Oops');
  },
  methods: {
    getImage(id) {
      return "http://localhost:8080/images/" + id;
    },
    handleFileUpload() {
      this.file = this.$refs.file.files[0];
    },
    submitFile() {
      let formData = new FormData();
      formData.append('file', this.file);

      axios.post('/images', formData, {headers: {'Content-Type': 'multipart/form-data'}})
          .then(value => {
            console.log(value.status);
          })
          .catch(error => {
            console.log(error.status);
          });
    },
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}
</style>

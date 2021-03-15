<template>
  <div class="edt-bg">
    <img ref="img" alt="" v-bind:src="$parent.imageUrl" v-on:change="handleFileUpload">
    <div style="position: fixed; bottom: 0; width: 100%;">
      <div class="edt-navbar">
        <span>
          <button v-if="display" v-on:click="deleteImage">Delete</button>
          <button v-on:click="uploadImge">Save</button>
          <button v-on:click="close">Exit</button>
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "EditImg",
  data() {
    return {
      display: true,
      file: '',
    }
  },
  methods: {
    close() {
      this.$parent.update('edtImg', this.$parent.imageId);
    },
    handleFileUpload() {
      this.file = this.$refs.file.files[0];
    },
    uploadImge() {
      if (this.file !== '') {
        this.$parent.httpApi.postImage(this.file);
      }
    },
    deleteImage() {
      this.$parent.httpApi.deleteImage(this.$parent.imageId);
      this.$parent.imageId = this.$parent.httpApi.response[0].id;
      this.$parent.imageUrl = this.$parent.httpApi.getImage(this.$parent.imageId);
      this.close();
    },
    print() {
      console.log(this.$parent.imageId);
    },
  },
}
</script>

<style scoped>
img {
  position: absolute;
  margin: auto;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  max-height: 100vh;
  max-width: 100vw;
}

.edt-bg {
  background-color: rgb(44, 62, 80);
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  width: 100vw;
  cursor: pointer;
}

.edt-navbar {
  /*opacity: 0%;*/
  background-color: white;
  border-top-left-radius: 15px;
  border-top-right-radius: 15px;
  height: 50px;
  width: 500px;
  margin: 0 auto;
}

.edt-navbar:hover {
  opacity: unset;
}
</style>
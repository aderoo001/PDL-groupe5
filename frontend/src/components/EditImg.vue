<template>
  <div class="edt-bg">
    <img ref="img" alt="" v-bind:src="imageUrl" v-on:change="handleFileUpload">
    <div style="position: fixed; bottom: 0; width: 100%;">
      <div class="edt-navbar">
        <div class="edt-navbar-in">
          <div class="edt-filter">
            <label>
              <select v-model="algorithm" v-on:click="processImage">
                <option value="" >Filtre</option>
                <option value="luminosity">Luminosité</option>
                <option value="egalize">Égalisation</option>
                <option value="colorize">Colorisation</option>
                <option value="blur">Flou</option>
                <option value="sobel">Contour</option>
              </select>
            </label>
          </div>
          <div class="edt-range">
            <label>
              <input ref="range"
                     max="100"
                     min="0"
                     type="range"
                     value="0"
                     v-on:mousemove="processImage">
            </label>
          </div>
          <div class="edt-btn-grp">
            <button class="edt-btn edt-btn-left"
                    v-on:click="deleteImage">Del
            </button>
            <button class="edt-btn"
                    v-on:click="saveImage">Save
            </button>
            <button class="edt-btn edt-btn-right edt-btn-danger"
                    v-on:click="close">Exit
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "EditImg",
  data() {
    return {
      httpApi: this.$parent.httpApi,
      imageUrl: this.$parent.imageUrl,
      imageId: this.$parent.imageId,
      algorithm: '',
      opt: '0',
    }
  },
  methods: {
    close() {
      this.$parent.update('edtImg', this.imageId);
    },
    processImage() {
      this.opt = this.$refs.range.value;
      this.imageUrl =this.imageUrl.split("?")[0];
      this.imageUrl =
          this.imageUrl
          + "?algorithm="
          + this.algorithm
          + "&opt1="
          + this.opt;
      console.log(this.imageUrl);
    },
    saveImage() {
      File.download(this.imageUrl)
          .then((link) => { link.click() })
    },
    deleteImage() {
      let tmp = 0;
      while (
          tmp < this.httpApi.response.length
          && this.imageId === this.httpApi.response[tmp].id ) {
        tmp++;
      }
      this.httpApi.deleteImage(this.imageId);
      try {
        this.imageId = this.httpApi.getId(tmp);
      }catch (e) {
        this.imageId = 0;
      }
      this.close();
    },
    print() {
      console.log(this.imageId);
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

input {
  cursor: pointer;
}

select {
  cursor: pointer;
}

.edt-bg {
  background-color: rgb(44, 62, 80);
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  width: 100vw;
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

.edt-navbar-in {
  position: absolute;
  margin: auto;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  height: 30px;
  width: 500px;
  padding: 0;
}

.edt-range {
  position: absolute;
  display: inline;
  bottom: 0;
  left: 150px;
  width: fit-content;
}

.edt-filter {
  position: absolute;
  display: inline;
  left: 25px;
}

.edt-btn-grp {
  position: absolute;
  display: inline;
  right: 25px;
}

.edt-btn-danger {
  background-color: #f6d5d9 !important;
  color: #711c25 !important;
  border: solid 1px #711c25 !important;
}

.edt-btn-danger:hover {
  opacity: 50%;
}

.edt-btn {
  margin: 0;
  padding: 0;
  height: 30px;
  width: 40px;
  background-color: white;
  border: solid 1px grey;
  cursor: pointer;
}

.edt-btn:hover {
  opacity: 50%;
}

.edt-btn-left {
  border-top-left-radius: 5px;
  border-bottom-left-radius: 5px;
}

.edt-btn-right {
  border-top-right-radius: 5px;
  border-bottom-right-radius: 5px;
}
</style>
<template>
  <div class="edt-bg">
    <img ref="img" alt="" v-bind:src="imageUrl">
    <div class="edt-metadata">
      <h4>Metadata :</h4>
      <p v-for="(data, name) in $parent.image" :key="data">{{ name }} : {{ data }} </p>
    </div>
    <div style="position: fixed; bottom: 0; width: 100%;">
      <div class="edt-navbar">
        <div class="edt-navbar-in">
          <div class="edt-filter">
            <label>
              <select v-model="algorithm">
                <option value="" v-on:click="processImage">-----</option>
                <option value="increaseLuminosity">Luminosité</option>
                <option value="histogram">Égalisation</option>
                <option value="color">Colorisation</option>
                <option value="blur">Flou</option>
                <option value="outline" v-on:click="processImage">Contour</option>
              </select>
            </label>
          </div>
          <div class="edt-opt">
            <div v-if="algorithm === 'increaseLuminosity'" class="edt-range">
              <div>
                <span style="position: absolute; left: 0">-256</span>
                <span>0</span>
                <span style="position: absolute; right: 0">255</span>
              </div>
              <div>
                <label>
                  <input ref="increaseLuminosity"
                         max="255"
                         min="-256"
                         type="range"
                         value="0"
                         v-on:mouseup="processImage">
                </label>
              </div>
            </div>

            <div v-if="algorithm === 'histogram'">
              <label>
                <select ref="histogram" v-on:change="processImage">
                  <option value="saturation">Saturation</option>
                  <option value="value">Valeur</option>
                </select>
              </label>
            </div>


            <div v-if="algorithm === 'color'" class="edt-range">
              <div>
                <span style="position: absolute; left: 0">0</span>
                <span>180</span>
                <span style="position: absolute; right: 0">360</span>
              </div>
              <div>
                <label>
                  <input ref="color"
                         max="359"
                         min="0"
                         type="range"
                         value="0"
                         v-on:mouseup="processImage">
                </label>
              </div>
            </div>

            <div v-if="algorithm === 'blur'">
              <label>
                <select ref="blur_1"
                        style="margin-right: 5px;">
                  <option value="M">Moyen</option>
                  <option value="G">Gaussien</option>
                </select>
              </label>
              <label>
                <input ref="blur_2"
                       min="0"
                       type="number"
                       value="0"
                       class="edt-number-input"
                       v-on:change="processImage">
              </label>
            </div>
          </div>
          <div class="edt-btn-grp">
            <div class="edt-btn edt-btn-left"
                 v-on:click="deleteImage">
              Del
            </div>

            <div class="edt-btn"
                 v-on:click="saveImage">
              Save
            </div>

            <div class="edt-btn edt-btn-right edt-btn-danger"
                 v-on:click="close">
              Exit
            </div>
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
      imageUrl: this.$parent.image.url,
      imageId: this.$parent.image.id,
      algorithm: 'none',
      img64b: '',
    }
  },
  methods: {
    sleep(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },
    close() {
      this.$emit('update', false);
    },
    makeUrl(algorithm = '', opt1 = '', opt2 = '') {
      let url = this.imageUrl.split("?")[0];
      if (algorithm !== '') {
        url += "?algorithm="
            + algorithm;
      }
      if (opt1 !== '' && algorithm !== '') {
        url += "&opt1="
            + opt1;
      }
      if (opt2 !== '' && algorithm !== '') {
        url += "&opt2="
            + opt2;
      }
      return url;
    },
    processImage() {
      switch (this.algorithm) {
        case "":
          this.imageUrl = this.makeUrl();
          break;
        case "increaseLuminosity":
          this.imageUrl =
              this.makeUrl(
                  this.algorithm,
                  this.$refs.increaseLuminosity.value
              );
          break;
        case "histogram":
          this.imageUrl =
              this.makeUrl(
                  this.algorithm,
                  this.$refs.histogram.value
              );
          break;
        case "color":
          this.imageUrl =
              this.makeUrl(
                  this.algorithm,
                  this.$refs.color.value
              );
          break;
        case "blur":
          this.imageUrl =
              this.makeUrl(
                  this.algorithm,
                  this.$refs.blur_1.value,
                  this.$refs.blur_2.value
              );
          break;
        case "outline":
          this.imageUrl =
              this.makeUrl(
                  this.algorithm
              );
          break;
      }
    },
    async saveImage() {
      const img = await this.$parent.httpApi.getImage(this.imageUrl);
      let link = document.createElement('a')
      link.href = img;
      link.download = this.$parent.image.name;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    deleteImage() {
      this.$parent.httpApi.response.forEach(
          value => {
            if (value.id !== this.imageId) this.$parent.image = value;
          });
      this.httpApi.deleteImage(this.imageId);
      this.$parent.update('editImg', false);
    },
    print() {
      console.log(this.$parent.image);
    },
  },
}
</script>

<style scoped>
h4 {
  margin: 0;
}

p {
  margin: 0;
}

a {
  color: inherit;
  text-decoration: inherit;
}

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

img:hover + .edt-metadata {
  opacity: unset !important;
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
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
  height: 60px;
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

.edt-opt {
  position: absolute;
  left: 155px;
  width: fit-content;
  height: 30px;
  margin-right: 5px;
  margin-left: 5px;
}

.edt-opt .edt-range {
  position: absolute;
  top: -3px;
}

.edt-opt .edt-number-input {
  position: absolute;
  top: -2px;
  display: inline-flex;
  width: 50px;
}

.edt-filter {
  position: absolute;
  display: inline;
  left: 25px;
}

.edt-btn-grp {
  position: absolute;
  display: inline-flex;
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
  height: 27px;
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

.edt-metadata {
  opacity: 0;
  padding: 10px;
  position: absolute;
  top: 0;
  right: 0;
  background-color: white;
  border-bottom-left-radius: 10px;
  text-align: justify;
}

.edt-metadata:hover {
  opacity: unset !important;
}

.edt-range {
  font-size: 10px;
  max-width: 200px;
}
</style>
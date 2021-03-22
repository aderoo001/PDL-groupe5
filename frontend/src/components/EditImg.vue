<template>
  <div class="edt-bg">
    <img ref="img" alt="" v-bind:src="imageUrl">
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
            <div v-if="algorithm === 'increaseLuminosity'">
              <label>
                <input type="range"
                       min="-256"
                       max="255"
                       value="0"
                       ref="increaseLuminosity"
                       v-on:mouseup="processImage">
              </label>
            </div>

            <div v-if="algorithm === 'histogram'">
              <label>
                <select ref="histogram" v-on:change="processImage">
                  <option value="saturation">Saturation</option>
                  <option value="value">Valeur</option>
                </select>
              </label>
            </div>

            <div v-if="algorithm === 'color'">
              <label>
                <input type="range"
                       min="0"
                       max="359"
                       value="0"
                       ref="color"
                       v-on:mouseup="processImage">
              </label>
            </div>

            <div v-if="algorithm === 'blur'">
              <label>
                <select ref="blur_1"
                        style="margin-right: 5px; margin-left: 5px;">
                  <option value="M">Moyen</option>
                  <option value="G">Gaussien</option>
                </select>
              </label>
              <label>
                <input type="number"
                       ref="blur_2"
                       min="0"
                       value="0"
                       style="width: 50px; margin-right: 5px; margin-left: 5px;"
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
      imageUrl: this.$parent.imageUrl,
      imageId: this.$parent.imageId,
      algorithm: 'none',
      img : '',
    }
  },
  methods: {
    sleep(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },
    close() {
      this.$parent.update('edtImg', this.imageId);
    },
    makeUrl(algorithm = '', opt1 = '', opt2 = '') {
      let url = this.imageUrl.split("?")[0];
      if (algorithm !== ''){
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
      let filename = 'image.jpeg';
      this.$parent.httpApi.response.forEach(value => {
        if (value.id === this.imageId) {
          filename = value.name;
        }
      });
      link.download = filename;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    deleteImage() {
      let tmp = 0;
      while (tmp < this.httpApi.response.length
      && this.imageId === this.httpApi.getId(tmp)) {
        tmp++;
      }
      this.httpApi.deleteImage(this.imageId);
      try {
        this.imageId = this.httpApi.getId(tmp);
      } catch (e) {
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

.edt-opt {
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
</style>
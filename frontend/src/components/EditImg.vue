<template>

  <div class="edt-bg">
    <div v-if="!thumbNav"
         v-on:click="thumbNav = true;"
         class="edt-btn edt-btn-left edt-btn-right">
      Show
    </div>
    <div class="edt-thumbnail-nav"
         v-if="thumbNav">
      <div class="edt-metadata">
        <div v-on:click="thumbNav = false;"
           class="edt-btn edt-btn-left edt-btn-right"
           style="margin-left: 15vw">
          Hide
        </div>
        <h4 style="color: white;">Metadata :</h4>
        <p v-for="(data, name) in image" :key="data"
           style="color: white;">
          {{ name }} : {{ data }}
        </p>
      </div>
      <div class="edt-thumbnail-list">
        <div class="edt-thumbnail" v-for="img in $parent.httpApi.response" :key="img">
          <img v-bind:src="img.url"
               v-on:click="image = img; imageUrl = img.url;"
               style="max-width: 15vw; max-height: 20vh; z-index: 15" alt="">
        </div>
      </div>
    </div>
    <img class="edt-img" ref="img" alt="" v-bind:src="imageUrl" style="z-index: 10;">
    <div style="position: fixed; bottom: 0; width: 100%;">
      <div class="edt-navbar">
        <div class="edt-navbar-in">
          <div class="edt-filter">
            <label>
              <select v-model="algorithm">
                <option value="" v-on:click="processImage" selected>-----</option>
                <option value="increaseLuminosity">Luminosité</option>
                <option value="histogram">Égalisation</option>
                <option value="color">Colorisation</option>
                <option value="blur">Flou</option>
                <option value="outline" v-on:click="processImage">Contour</option>
                <option value="grayLevel" v-on:click="processImage">Lv Gris</option>
              </select>
            </label>
          </div>
          <div class="edt-opt">
            <div v-if="algorithm === 'increaseLuminosity'" class="edt-range">
<!--              <div style=-->
<!--                       "position: fixed;-->
<!--                       z-index: -10;-->
<!--                       top: 0;-->
<!--                       left: 0;-->
<!--                       width: 100vw;-->
<!--                       height: 100vh;-->
<!--                       border: solid;"-->
<!--                   v-on:mouseover.once="processImage"></div>-->
              <div>
                <span style="position: absolute; left: 0">-255</span>
                <span>0</span>
                <span style="position: absolute; right: 0">255</span>
              </div>
              <div>
                <label>
                  <input ref="increaseLuminosity"
                         max="255"
                         min="-255"
                         type="range"
                         value="0"
                         v-on:mouseup="processImage">
                </label>
              </div>
            </div>

            <div v-if="algorithm === 'histogram'">
<!--              <div style=-->
<!--                       "position: fixed;-->
<!--                       z-index: -10;-->
<!--                       top: 0;-->
<!--                       left: 0;-->
<!--                       width: 100vw;-->
<!--                       height: 100vh;-->
<!--                       border: solid;"-->
<!--                   v-on:mouseover.once="processImage"></div>-->
              <label>
                <select ref="histogram" v-on:change="processImage">
                  <option value="saturation">Saturation</option>
                  <option value="value">Valeur</option>
                </select>
              </label>
            </div>


            <div v-if="algorithm === 'color'" class="edt-range">
<!--              <div style=-->
<!--                       "position: fixed;-->
<!--                       z-index: -10;-->
<!--                       top: 0;-->
<!--                       left: 0;-->
<!--                       width: 100vw;-->
<!--                       height: 100vh;-->
<!--                       border: solid;"-->
<!--                   v-on:mouseover.once="processImage"></div>-->
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
<!--              <div style=-->
<!--                       "position: fixed;-->
<!--                       z-index: -10;-->
<!--                       top: 0;-->
<!--                       left: 0;-->
<!--                       width: 100vw;-->
<!--                       height: 100vh;-->
<!--                       border: solid;"-->
<!--                   v-on:mouseover.once="processImage"></div>-->
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
                       max="15"
                       type="number"
                       value="0"
                       class="edt-number-input"
                       v-on:change="processImage">
              </label>
            </div>
          </div>
          <div class="edt-btn-grp">
            <div class="edt-btn edt-btn-left"
                 v-on:click="$parent.impImg = true; print();">
              Add
            </div>

            <div class="edt-btn"
                 v-on:click="saveImage">
              Save
            </div>

            <div class="edt-btn edt-btn-right edt-btn-danger"
                 v-on:click="deleteImage">
              Del
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
      thumbNav: true,
      httpApi: this.$parent.httpApi,
      image: this.$parent.image,
      imageUrl: this.$parent.image.url,
      algorithm: 'none',
      img64b: '',
    }
  },
  methods: {
    close() {
      this.$emit('update', false);
    },
    addUrlParameter(url = this.imageUrl, name, value) {
      if (value === 'none') return url;
      if (name === '' || value === '') return url;
      if (url.includes('?')) {
        url += '&' + name + '=' + value;
      } else {
        url += '?' + name + '=' + value;
      }
      return url;
    },
    processImage() {
      this.imageUrl = this.imageUrl.split("?")[0];
      this.imageUrl = this.addUrlParameter(undefined,'algorithm', this.algorithm );
      switch (this.algorithm) {
        case "increaseLuminosity":
          this.imageUrl = this.addUrlParameter(undefined, 'incLumDelta', this.$refs.increaseLuminosity.value);
          break;
        case "histogram":
          this.imageUrl = this.addUrlParameter(undefined,'histAlgoType', this.$refs.histogram.value );
          break;
        case "color":
          this.imageUrl = this.addUrlParameter(undefined,'colorDelta', this.$refs.color.value );
          break;
        case "blur":
          this.imageUrl = this.addUrlParameter(undefined,'blurAlgoType', this.$refs.blur_1.value );
          this.imageUrl = this.addUrlParameter(undefined,'blurIntensity', this.$refs.blur_2.value*2+1 );
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
            if (value.id !== this.image.id) this.$parent.image = value;
          });
      this.httpApi.deleteImage(this.image.id);
      this.$parent.update();
    },
    print() {
      console.log(this.$parent.impImg);
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

.edt-img {
  z-index: 0;
  position: absolute;
  margin: auto;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  max-height: 80vh;
  max-width: 80vw;
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
  background-color: white;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
  height: 60px;
  width: 500px;
  margin: 0 auto;
}

.edt-navbar-in {
  position: absolute;
  z-index: 20;
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
  padding: 10px;
  text-align: left;
  border-bottom: solid 1px #141d26;
  height: 25vh;
  overflow: hidden;
}

.edt-range {
  font-size: 10px;
  max-width: 200px;
}

.edt-thumbnail-nav {
  position: fixed;
  z-index: 20;
  background-color: rgba(0,0,0,0.40);
  height: 100vh;
  width: 19vw;
}

.edt-thumbnail-list {
  z-index: 15;
  width: 19vw;
  max-height: 75vh;
  overflow: scroll;
}

.edt-thumbnail {
  padding-inline: 1vw;
  padding-block: 2vh;
  display: block;
  width: 15vw;
  height: 20vh;
}

.edt-thumbnail:hover {
  opacity: 70%;
  cursor: pointer;
}
</style>
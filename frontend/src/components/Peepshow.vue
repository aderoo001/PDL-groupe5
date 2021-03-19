<template>
  <div class="peep-box">
    <div class="peep-img-box">
      <img alt=""
           class="peep-img"
           v-bind:src="imageUrl"
           v-on:click="$parent.update('edtImg', imageId); print();">
    </div>
    <div class="peep-selector">
      <div v-for="image in httpApi.response"
           :key="image.id" class="peep-selector-btn-box">
        <button v-if="imageId === image.id"
                class="peep-selector-btn-selected"
                v-on:click="getImageUrl(image.id)">
        </button>
        <button v-else
                class="peep-selector-btn"
                v-on:click="getImageUrl(image.id)">
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Peepshow",
  data() {
    return {
      httpApi: this.$parent.httpApi,
      imageUrl: this.$parent.imageUrl,
      imageId: this.$parent.imageId,
    }
  },
  methods: {
    getImageUrl(id) {
      this.imageId = id;
      this.imageUrl = this.httpApi.getImage(id);
    },
    print() {
      console.log(this.imageId);
    },
  }
}
</script>

<style scoped>
h3 {
  margin: 40px 0 0;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}

.peep-box {
  height: 75vh;
  width: 100%;
  background-color: #2c3e50;
}

.peep-img-box {
  height: 65vh;
}

.peep-img {
  margin: 5vh;
  height: 55vh;
  cursor: pointer;
}

.peep-selector {
  height: 10vh;
}

.peep-selector-btn-box {
  display: inline;
  margin-top: 5vh;
  margin-inline: 1vh;
  width: 10px;
  height: 5vh
}

.peep-selector-btn {
  height: 17px;
  width: 10px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
}

.peep-selector-btn:hover {
  background-color: darkslategrey;
}

.peep-selector-btn-selected {
  height: 17px;
  width: 10px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  background-color: grey;
}
</style>
<template>
  <div class="peep-box">
    <div class="peep-img-box">
      <img alt=""
           class="peep-img"
           v-bind:src="$parent.imageUrl"
           v-on:click="$parent.update('edtImg', $parent.imageId)">
    </div>
    <div class="peep-selector">
      <div v-for="image in $parent.httpApi.response"
           :key="image.id" class="peep-selector-btn-box">
        <button v-if="$parent.imageId === image.id"
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
  methods: {
    getImageUrl(id) {
      this.$parent.imageId = id;
      this.$parent.imageUrl = this.$parent.httpApi.getImage(id);
    },
    print() {
      console.log(this.$parent.imageId);
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
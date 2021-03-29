<template>
  <div class="peep-box">
    <div class="peep-img-box">
      <img alt=""
           class="peep-img"
           v-bind:src="$parent.image.url"
           v-on:click="$emit('update', true);">
    </div>
    <div class="peep-selector">
      <div class="btn-grp">
        <div class="btn" v-on:click="$parent.getPreviousImage">Prev</div>
        <div class="display-nav">
          {{ $parent.httpApi.getImageIndex($parent.image) + 1 }}/{{ $parent.httpApi.response.length }}
        </div>
        <div class="btn" v-on:click="$parent.getNextImage">Next</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Peepshow"
}
</script>

<style scoped>
img:hover {
  opacity: 75%;
}

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

.display-nav {
  color: white;
  margin-left: 10px;
  margin-right: 10px;
}
</style>
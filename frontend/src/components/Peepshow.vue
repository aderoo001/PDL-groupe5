<template>
  <div class="peepshow">
    <h3>Galerie :</h3>
    <div id="inline" v-for="image in response" :key="image.id">
      <img v-bind:src="getImage(image.id)" v-bind:alt="image.name">
    </div>
  </div>
</template>

<script type="text/javascript" src="@/components/http-api.js"></script>

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
  img {
    max-height: 100px;
  }
  #inline {
    display: inline;
  }
</style>
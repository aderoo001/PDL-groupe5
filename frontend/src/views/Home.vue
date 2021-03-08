<template>
  <div class="home">
    <button v-on:click="swapView">Swap</button>
    {{ swap }}
    <div v-if="swap">
      <HelloWorld msg="Welcome to Your Vue.js App"/>
    </div>
    <div v-else>
      <Peepshow/>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import HelloWorld from '@/components/HelloWorld.vue'
import Peepshow from '@/components/Peepshow.vue'

export default {
  name: 'Home',
  components: {
    HelloWorld,
    Peepshow
  },
  data() {
    return {
      swap: false
    }
  },
  methods: {
    swapView() {
      this.swap = !this.swap;
    }
  }
}
</script>
<template>
  <div class="home">
    <div v-if="impImg">
      <ImportImg ref="importImg"
                 v-on:update="impImg = false;"/>
    </div>
    <EditImg ref="editImg"
             :imageId="image.id"
             :imageUrl="image.url"
             v-on:update="editImg = false"/>
  </div>
</template>

<script>
// @ is an alias to /src
import ImportImg from "@/components/importImg";
import EditImg from "@/components/EditImg";
import HttpApi from "@/components/http-api";

export default {
  name: 'Home',
  components: {
    EditImg,
    ImportImg,
  },
  data() {
    return {
      httpApi: new HttpApi(),
      image: {},
      impImg: false,
    }
  },
  async mounted() {
    const list = await this.httpApi.init();
    if (list.length > 0) {
      this.image = list[0];
    }
  },
  methods: {
    sleep(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },
    async update() {
      location.reload();
    },
    getPreviousImage() {
      let tmp = this.httpApi.getImageIndex(this.image);
      if (tmp > 0) this.image = this.httpApi.response[tmp - 1];
    },
    getNextImage() {
      let tmp = this.httpApi.getImageIndex(this.image);
      if (tmp < this.httpApi.response.length - 1)
        this.image = this.httpApi.response[tmp + 1];
    },
  }
}
</script>
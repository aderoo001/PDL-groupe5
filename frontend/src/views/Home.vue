<template>
  <div class="home">
    <div v-if="impImg">
      <ImportImg ref="importImg"
                 v-on:update="impImg = false;"/>
    </div>
    <div v-if="editImg">
      <EditImg ref="editImg"
               :imageId="image.id"
               :imageUrl="image.url"
               v-on:update="editImg = false"/>
    </div>
    <Peepshow ref="peepshow"
              v-on:update="editImg = true;"/>
    <button v-on:click="impImg = true;">Importer</button>
  </div>
</template>

<script>
// @ is an alias to /src
import Peepshow from '@/components/Peepshow.vue'
import ImportImg from "@/components/importImg";
import EditImg from "@/components/EditImg";
import HttpApi from "@/components/http-api";

export default {
  name: 'Home',
  components: {
    EditImg,
    ImportImg,
    Peepshow
  },
  data() {
    return {
      httpApi: new HttpApi(),
      image: {},
      impImg: false,
      editImg: false,
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
    async update(cpt, event) {
      switch (cpt) {
        case "impImg" :
          this.impImg = event;
          break;
        case "editImg":
          this.editImg = event;
          break;
      }
      const index = this.httpApi.getImageIndex(this.image);
      const list = await this.httpApi.init();
      if (list.length > 0) {
        this.image = list[index];
      }
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

<style>
.btn-grp {
  display: inline-flex;

}

.btn {
  border-radius: 5px;
  background-color: ghostwhite;
  border: solid 1px grey;
  width: 40px;
  height: 27px;
  cursor: pointer;
}

.btn:hover {
  opacity: 75%;
}
</style>
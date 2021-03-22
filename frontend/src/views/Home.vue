<template>
  <div class="home">
    <div v-if="impImg">
      <ImportImg ref="importImg"
                 :imageId="imageId"
                 :imageUrl="imageUrl"
                 v-on:update="impImg = $event; this.httpApi.getImagesList()"/>
    </div>
    <div v-if="editImg">
      <EditImg ref="editImg"
               :imageId="imageId"
               :imageUrl="imageUrl"/>
    </div>
    <Peepshow ref="peepshow"
              :imageId="imageId"
              :imageUrl="imageUrl"/>
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
      imageUrl: "http://localhost:8080/images/0",
      imageId: 0,
      impImg: false,
      editImg: false,
      updateImg: false,
    }
  },
  async mounted() {
    const list = await this.httpApi.init();
    if (list.length > 0) {
      this.imageId = list[0].id;
      this.imageUrl = list[0].url;
    }
  },
  methods: {
    update(comp, id) {
      this.httpApi.getImagesList();
      this.imageId = id;
      this.imageUrl = this.httpApi.getImageUrl(id);
      switch (comp) {
        case "impImg":
          this.impImg = !this.impImg;
          break;
        case "edtImg":
          this.editImg = !this.editImg;
          break;
      }
    }
  }
}
</script>
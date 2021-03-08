import axios from "axios";

export default {
  name: "httpApi",
  data() {
    return {
      file: '',
      selected: [],
      response: [],
      errors: [],
    };
  },
  mounted: async function() {
    await new Promise(() =>
      axios.get(`images`)
      .then((response) => {
        // JSON responses are automatically parsed.
        this.response = response.data;
      }));
    throw new Error('Oops');
  },
  methods: {
    getImage(id) {
      return "http://localhost:8080/images/" + id;
    },
    handleFileUpload(){
      this.file = this.$refs.file.files[0];
    },
    submitFile(){
      let formData = new FormData();
      formData.append('file', this.file);

      axios.post( '/images', formData, { headers: {'Content-Type': 'multipart/form-data'} })
          .then( value => {
            console.log(value.status);
          })
          .catch( error => {
            console.log(error.status);
          });
    },
  }
}

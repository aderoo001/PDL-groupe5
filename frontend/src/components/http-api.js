import axios from "axios";

export default class HttpApi {
  message = "";
  response = [];
  error = [];

  constructor(message) {
    this.message = message;
  }

  async init() {
    await new Promise(() =>
        axios.get(`images`)
            .then((response) => {
              // JSON responses are automatically parsed.
              this.response = response.data;
            }));
    throw new Error('Oops');
  }

  getImage(id) {
    return "http://localhost:8080/images/" + id;
  }

  postImage(file) {
    let formData = new FormData();
    formData.append('file', file);

    axios.post('/images', formData, {headers: {'Content-Type': 'multipart/form-data'}})
        .then(value => {
          console.log(value.status);
        })
        .catch(error => {
          console.log(error.status);
        });
  }

  async deleteImage(id) {
    await new Promise(() =>
        axios.delete(`images/` + id)
            .then((response) => {
              // JSON responses are automatically parsed.
              this.response = response.data;
            })
            .catch((error) => {
              this.error = error;
            }));
    throw new Error('Oops');
  }

  updateImage(file, id) {
    console.log(id);
  }
}

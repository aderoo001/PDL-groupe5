import axios from "axios";

export default class HttpApi {
    response = [];
    error = [];

    constructor() {
    }

    getImageIndex(image) {
        return this.response.indexOf(image);
    }

    init() {
        return axios.get(`images`)
            .then((response) => {
                this.response = response.data;
                return this.response;
            }).catch(error => {
                this.error = error.status;
            });
    }

    async getImagesList() {
        await axios.get(`images`)
            .then((response) => {
                this.response = response.data;
                return this.response;
            }).catch(error => {
                this.error = error.status;
            });
    }

    getImage(url) {
        return axios.get(
            url.split("http://localhost:8080/")[1],
            {
                responseType: 'arraybuffer'
            }).then((response) => {
            const base64 = btoa(new Uint8Array(response.data)
                .reduce(
                    (data, byte) => data + String.fromCharCode(byte), ''
                ));
            return "data:image/jpeg;base64," + base64;
        }).catch(error => {
            this.error = error.status;
        });
    }

    async postImage(file) {
        let formData = new FormData();
        formData.append('file', file);

        await axios.post('/images', formData, {headers: {'Content-Type': 'multipart/form-data'}}
        ).catch(error => {
            this.error = error.status;
        });
    }

    async deleteImage(id) {
        await axios.delete(`images/` + id)
            .then((response) => {
                this.response = response.data;
            }).catch((error) => {
                this.error = error.status;
            });
    }
}

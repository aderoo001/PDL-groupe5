import axios from "axios";

export default class HttpApi {
    response = [];
    error = [];

    constructor() {
    }

    getId(pos) {
        if ( this.response.length > pos) {
            return this.response[pos].id;
        }
        throw new Error("Out of range => response");
    }

    getImageUrl(id) {
        return "http://localhost:8080/images/" + id;
    }

    init() {
        return axios.get(`images`)
                .then((response) => {
                    // JSON responses are automatically parsed.
                    this.response = response.data;
                    return this.response;
                }).catch(error => {
                    this.error = error.status;
                });
    }

    async getImagesList() {
        await axios.get(`images`)
                .then((response) => {
                    // JSON responses are automatically parsed.
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
                const base64 =  btoa(new Uint8Array(response.data)
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
                    // JSON responses are automatically parsed.
                    this.response = response.data;
                }).catch((error) => {
                    this.error = error.status;
                });
    }
}

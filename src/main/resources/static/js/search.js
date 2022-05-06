// search_page
let search_page = new Vue({
    el: '#search_foot',
    data: {
        keyword: '',
        results: [],
        status: true,
    },
    methods: {
        search5: function () {
            let keyword = this.keyword;
            axios.get('/search/' + keyword).then((response) => {
                this.results = response.data;
                this.status = false
                // 5.5改动    searcherror->searchError
                if (response.data === "searchError") {
                    this.status = false
                    console.log(this.status)
                }
                // console.log(this.results);
            });
        },
        search6: function () {
            let keyword = this.keyword;
            axios.get('/search', {
                params: {
                    q: keyword
                }
            }).then((response) => {
                this.results = response.data;
                this.status = false
                // 5.5改动    searcherror->searchError
                if (response.data === "searchError") {
                    this.status = false
                    console.log(this.status)
                }
                // console.log(this.results);
            });
        }

    }
});
//http://localhost:8086/search_page?q=springboot
window.search_page = search_page;


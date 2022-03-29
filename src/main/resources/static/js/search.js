// search_page
import axios from "axios";

let search_page=new Vue({
    el:'#search_foot',
    data:{
        keyword:'',
        results: [],
        status: true
    },
    methods:{
        search5: function (){
            let keyword=this.keyword;
            console.log(keyword);
            axios.get('/search/'+keyword).then((response)=>{
                console.log(response.data);
                this.results=response.data;
                this.status=false
                if (response.data=="searcherror"){
                    this.status=false
                    console.log(this.status)
                }
                console.log(this.results);
            });
        }
    }
});
//http://localhost:8086/search_page?q=springboot
window.search_page=search_page;


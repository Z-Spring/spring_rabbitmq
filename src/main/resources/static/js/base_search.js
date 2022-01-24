'use strict';

//base Page
let base=new Vue({
    el:'#base_navigate',
    data:{
        keyword2:'',
        results: []
    },
    methods:{
        search:function (){
            let keyword2=this.keyword2;
            axios.post('/search_page',{
                keyword2: keyword2
            }).then(resp=>{

                console.log(resp.data);
            });
        }
    }
});
window.base=base;
'use strict';

/*function search() {
    let keyword=$('#test2').val();
    $.ajax('/search/'+keyword,{
        dataType:'html',
        result:''
    }).done(function (data){
        $('#book_info').html(data);
        console.log('success'+data);
    }).fail(function (xhr, status) {
        console.log('失败: ' + xhr.status + ', 原因: ' + status);
    })
}*/
new Vue({
    el:'#search_foot',
    data:{
        keyword:'',
        results: []
    },
    methods:{
        search2: function (){
            let keyword=this.keyword;
            console.log(keyword);
            axios.get('/search/'+keyword).then((response)=>{
                console.log(response.data);
                this.results=response.data;
                console.log(this.results);
            })
        }
    }
})
new Vue({
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
            })
        }
    }
})

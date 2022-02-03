// this .js page is not be used
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
// search_page
let search_page=new Vue({
    el:'#search_foot',
    data:{
        keyword:'',
        results: []
    },
    methods:{
        search5: function (){
            let keyword=this.keyword;
            console.log(keyword);
            axios.get('/search/'+keyword).then((response)=>{
                console.log(response.data);
                this.results=response.data;
                console.log(this.results);
            });
        }
    }
});
//http://localhost:8086/search_page?q=springboot
window.search_page=search_page;
/*
    (function f() {
    let url=window.location.href;
    console.log(url);
    let local=url.split('?')[1];
    console.log(local);
    let result=local.split('=')[1];
    console.log(result);
    let s=new Vue({
    el:'#search_foot',
    data:{
    keyword:''
}
});
    window.s=s;
    window.s.keyword=result;
    window.search_page=result;
    axios.get('/search/'+result).then((response)=>{
    console.log(response.data);
});
})();*/
/*    function jump() {
    let searchValue=$('#test2').val();
    window.location.href='/search_page?keyword='+searchValue;
}*/


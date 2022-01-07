function checkForm() {
    /*          let pwd1=document.getElementById('pwd1').value;
                let pwd2=document.getElementById('pwd2').value;
                let md5_pwd=document.getElementById('md5_pwd');
                md5_pwd.value=md5(pwd2);*/
    let
        email = $('input:text[name=email]').val(),
        name = $('input:text[name=name]').val(),
        pwd1 = $('#pwd1').val(),
        pwd2 = $('#pwd2').val(),
        md5_pwd = document.getElementById('md5_pwd'),//TODO:这里好奇怪，用jQuery表示value为啥不行？
        message = $('#message'),
        re = /^[\w+\.]+\@\w+\.(com|org)$/

    md5_pwd.value = md5(pwd1);
    // md5_pwd.val()=md5(pwd1);   不知道为啥这里这样不行
    //邮箱和用户名空值判断
    if (email === '' || name === '') {
        console.log('???');
        message.text('输入值不能为空！').css('color', 'red');
        // alert('输入值不能为空！');
        return false;
    }
    //邮箱格式判断
    if (re.test(email) === false) {
        message.text('邮箱格式不正确！').css('color', 'red');
        return false;
    }
    // 判断前后密码相同
    if (pwd1 !== pwd2) {
        message.text('前后密码不一致！').css('color', 'red');
        // alert('前后密码不一致！');
        return false;
    }
    return true;
};
/*function login() {
    let
        name=$('#name').val(),
        password=$('#password').val()
    $.post('/login').done(function (data){

        console.log('bbbb');
        if (data==='failed'){
            $('span').text('failure').css('color','#ff0000');
        }else if (data==='success'){
            console.log('aa')
            location.href='/base';
        }
    }).fail(function (status){
        console.log(status);
    })
}*/



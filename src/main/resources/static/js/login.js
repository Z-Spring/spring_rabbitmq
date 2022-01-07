$(function () {
    console.log('zhang');
    $('#login-form').validate({
        rules: {
            name: 'required',
            email: {
                required: true,
                email: true//验证email合规性
            },
            password: 'required'
        },
        errorPlacement: function (error, element) {
            // Append error within linked label
            $(element)
                .closest("form")
                .find("label[for='" + element.attr("id") + "']")
                .append(error);
        },

        errorElement: "span",
        messages: {
            name: '(必填字段)',
            email: {
                required: '(必填字段)',
                email: '(请输入正确格式)',
            },
            password: '(必填字段)'
        }
        /*errorPlacement:function(error,element){
            error.appendTo(element.parent().parent());
        }*/
    })
})




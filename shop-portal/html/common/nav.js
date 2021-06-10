
$(function () {

    $.ajaxSetup({
        beforeSend: function(xhr) {
            //var auth = window.localStorage.getItem("x-auth");
            var token = $.cookie("token");
            xhr.setRequestHeader("token", token);
        }
    })

})




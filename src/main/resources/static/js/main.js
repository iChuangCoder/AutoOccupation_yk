function sub() {
    $.ajax({
        url: "/addtesk",
        method: "POST",
        data: $("#form").serialize(),
        success: (res) => {
            alert(res.msg)
            return false;
        }
    })
    return false;
}


function login() {
    $.ajax({
        url: "/login",
        method: "POST",
        data: $("#form").serialize(),
        success: (res) => {
            alert(res.msg)
            return false;
        }
    })
    return false;
}




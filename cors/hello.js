$(document).ready(function() {
    $.ajax({
        url: "http:\\localhost:8080/users/1"
    }).then(function(data, status, jqxhr) {
       $('.greeting-id').append(data.id);
       $('.greeting-content').append(data.username);
       $('.greeting-email').append(data.email);
       console.log(jqxhr);
    });
});
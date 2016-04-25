$(document).ready(function() {
  $("#loginBtn").click(function() {
    var ref = new Firebase("https://moviespotlight.firebaseio.com/");

    loginEmail = $('#email').val();
    loginPass = $('#password').val();

    ref.authWithPassword({
      email    : loginEmail,
      password : loginPass
    }, function(error, authData) {
      if (error) {
        console.log("Login Failed!", error);
        message(error, false);
      } else {
        console.log("Authenticated successfully with payload:", authData);
        message("Success!", true);
        $('form').hide();
      }
    });
  });
});

function message(text, type) {
  if (type) {
    $('#message').text(text).css('color', 'green');
  } else {
    $('#message').text(text).css('color', 'red');
  }
}
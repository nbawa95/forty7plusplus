function login(event) {
	event.preventDefault();
	var ref = new Firebase("https://moviespotlight.firebaseio.com/");

	loginEmail = $('#email').val();
	loginPass = $('#password').val();

	ref.authWithPassword({
	  email    : loginEmail,
	  password : loginPass
	}, function(error, authData) {
	  if (error) {
	    console.log("Login Failed!", error);
	  	$('#message').text(error).css('color', 'red');
	  } else {
	    console.log("Authenticated successfully with payload:", authData);
	    window.location.href = 'index.html';
	  }
	});
}
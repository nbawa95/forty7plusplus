majors = ["Architecture", "Industrial Design", "Computational Media", "Computer Science", "Aerospace Engineering", "Biomedical Engineering", "Chemical and Biomolecular Engineering", "Civil Engineering", "Computer Engineering", "Electrical Engineering", "Environmental Engineering", "Industrial Engineering", "Materials Science and Engineering", "Mechanical Engineering", "Nuclear and Radiological Engineering", "Applied Mathematics", "Applied Physics", "Biochemistry", "Biology", "Chemistry", "Discrete Mathematics", "Earth and Atmospheric Sciences", "Physics", "Psychology", "Applied Languages and Intercultural Studies", "Computational Media", "Economics", "Economics and International Affairs", "Global Economics and Modern Languages", "History, Technology, and Society", "International Affairs", "International Affairs and Modern Language", "Literature, Media, and Communication", "Public Policy", "Business Administration"];

function message(text, type) {
	if (type) {
		$('#message').text(text).css('color', 'green');
	} else {
		$('#message').text(text).css('color', 'red');
	}
}

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
	    message(error, false);
	  } else {
	    console.log("Authenticated successfully with payload:", authData);
	    window.location.href = 'index.html';
	  }
	});
}

function register(event) {
	event.preventDefault();
	var ref = new Firebase("https://moviespotlight.firebaseio.com/");

	email = $('#email').val();
	pass = $('#password').val();
	name = $('#name').val();
	major = majors[Number($('#major').val())];

	ref.createUser({
	  email: email,
	  password: pass
	}, function(error, userData) {
	  if (error) {
	    switch (error.code) {
	      case "EMAIL_TAKEN":
	        console.log("The new user account cannot be created because the email is already in use.");
	        message("The new user account cannot be created because the email is already in use.", false);
	        break;
	      case "INVALID_EMAIL":
	        console.log("The specified email is not a valid email.");
	        message("The specified email is not a valid email.", false);
	        break;
	      default:
	        console.log("Error creating user:", error);
	        message("Error creating user", false);
	    }
	  } else {
	  	message("Successfully created account!", true);
	    console.log("Successfully created user account with uid:", userData.uid);
	    ref.child("users/" + userData.uid).set({
	    	"name": name,
	    	"major": major,
	    	"admin": false,
	    	"locked": false,
	    	"blocked": false,
	    	"numLoginAttempts": 0
	    });
	    userOb = {};
	    userOb[email.replace('.', '*')] = userData.uid;
	    ref.child("contact/").update(userOb);
	    ref.authWithPassword({
	      email    : email,
	      password : pass
	    }, function(error, authData) {
	      if (error) {
	        console.log("Login Failed!", error);
	        message(error, false);
	      } else {
	        console.log("Authenticated successfully with payload:", authData);
	        window.location.href = 'index.html';
	      }
	    });
	  }
	});

}

function resetPass(event) {
	event.preventDefault();
	var ref = new Firebase("https://moviespotlight.firebaseio.com/");

	resetEmail = $('#email').val();

	ref.resetPassword({
	  email: resetEmail
	}, function(error) {
	  if (error) {
	    switch (error.code) {
	      case "INVALID_USER":
	        console.log("The specified user account does not exist.");
	        message("The specified user account does not exist.", false);
	        break;
	      default:
	        console.log("Error resetting password:", error);
	        message("There was an error resetting your password.", false);
	    }
	  } else {
	    console.log("Password reset email sent successfully!");
	    message("Password reset email sent successfully!", true);
	  }
	});
}

function addMajors() {
	for (major in majors) {
		$('.login #major').append('<option value="' + major + '">' + majors[major] + '</option>');
	}
}
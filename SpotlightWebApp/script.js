var ref = new Firebase("https://moviespotlight.firebaseio.com");
majors = ["Architecture", "Industrial Design", "Computational Media", "Computer Science", "Aerospace Engineering", "Biomedical Engineering", "Chemical and Biomolecular Engineering", "Civil Engineering", "Computer Engineering", "Electrical Engineering", "Environmental Engineering", "Industrial Engineering", "Materials Science and Engineering", "Mechanical Engineering", "Nuclear and Radiological Engineering", "Applied Mathematics", "Applied Physics", "Biochemistry", "Biology", "Chemistry", "Discrete Mathematics", "Earth and Atmospheric Sciences", "Physics", "Psychology", "Applied Languages and Intercultural Studies", "Computational Media", "Economics", "Economics and International Affairs", "Global Economics and Modern Languages", "History, Technology, and Society", "International Affairs", "International Affairs and Modern Language", "Literature, Media, and Communication", "Public Policy", "Business Administration"];

$(document).ready(function() {
	var authData = ref.getAuth();
	ref.onAuth(function(authData) {
	  if (! authData) {
	  	console.log("Client unauthenticated.")
	  	window.location.href = 'login.html';
	  }
	});
	var userRef = ref.child("users").child(authData.uid);
	userRef.once("value", function(data) {
		// Add the user's name to the search bar
		$('#search').attr("placeholder", "Search for a movie, " + data.val().name.split(' ')[0]);
		$('#name').val(data.val().name);
		$('#major').val(majors.indexOf(data.val().major));
		$('#email').val(authData.password.email);
		console.log(authData);
  	}, function (errorObject) {
  		console.log("The read failed: " + errorObject.code);
	});

	$('#name').change(function() {
		ref.child("users/" + authData.uid).update({"name": $('#name').val()});
		message("Updated your name!", true);
	});

	$('#major').change(function() {
		ref.child("users/" + authData.uid).update({"major": majors[Number($('#major').val())]});
		message("Updated your major!", true);
	});

	var searchQuery = getUrlVars()["query"];

	if (searchQuery) {
		console.log(searchQuery);
		$('#search').val(searchQuery).addClass('active');
		search(searchQuery);
	}

	ref.child("ratings").on("value", function(snapshot) {
		console.log(snapshot.val());
		snapshot.forEach(function(data) {
			if (Number(data.val().overallRating) > 4) {
				if (data.val().review[ref.getAuth().uid] == undefined) {
					addRecommendation(data.key());
				}
			}
		});
	});

	// $("#search").keyup(function() {
	// 	search($('#search').val());
	// });
});

function updateEmail() {
	event.preventDefault();
	ref.changeEmail({
	  oldEmail: ref.getAuth().password.email,
	  newEmail: $('#email').val(),
	  password: $('#password').val()
	}, function(error) {
	  if (error) {
	    switch (error.code) {
	      case "INVALID_PASSWORD":
	        console.log("The specified user account password is incorrect.");
	        message("Seems like you entered your password incorrectly", false);
	        break;
	      case "INVALID_USER":
	        console.log("The specified user account does not exist.");
	        message("The specified user account does not exist.", false);
	        break;
	      default:
	        console.log("Error creating user:", error);
	        message("Error creating user.", false);
	    }
	  } else {
	    console.log("User email changed successfully!");
	    message("User email changed successfully!", true);
	  }
	});
}

function updatePass() {
	event.preventDefault();
	ref.changePassword({
	  email: ref.getAuth().password.email,
	  oldPassword: $('#oldPassword').val(),
	  newPassword: $('#newPassword').val()
	}, function(error) {
	  if (error) {
	    switch (error.code) {
	      case "INVALID_PASSWORD":
	        console.log("The specified user account password is incorrect.");
	        message("Seems like you entered your password incorrectly", false);
	        break;
	      case "INVALID_USER":
	        console.log("The specified user account does not exist.");
	        message("The specified user account does not exist.", false);
	        break;
	      default:
	        console.log("Error changing password:", error);
	        message("Error changing password", false);
	    }
	  } else {
	    console.log("User password changed successfully!");
	    message("Password changed successfully!", true);
	  }
	});
}

function message(text, type) {
	if (type) {
		$('#message').text(text).css('color', 'green');
	} else {
		$('#message').text(text).css('color', 'red');
	}
}

function showProfile() {
	
}

function addRecommendation(movieID) {
	var movieRecEle = '<a href="movie.html?id=' + movieID + '"><div class="movie" data-movieid="' + movieID + '"><img></div></a>';
	$('.recommendations').append(movieRecEle);
	setMoviePoster(movieID);
}

function setMoviePoster(movieID) {
	var search = $.get("http://www.omdbapi.com/?i=" + movieID + "&plot=short", function(data) {
		$('.movie[data-movieid=' + movieID + '] img').attr('src', data.Poster);		
	});
	search.fail(function() {
    	console.log("error " + movieID);
  	});
}

function search(searchQuery) {
	$('.results').show().empty();
	console.log('loading results');
	var search = $.get("http://www.omdbapi.com/?s=" + searchQuery, function(data) {
		console.log(data)
		if (data.Search) {
			$('.recommendations').hide();
			for (var resultNum = 0; resultNum < data.Search.length; resultNum++) {
				$('.results').append(movieSearchResultEle(data.Search[resultNum].imdbID, data.Search[resultNum].Title, data.Search[resultNum].Poster))
			}			
		} else {
			$('.recommendations').show();
			if ($('#search').val() != "") {
				console.log("No results");
				$('.results').append('<div class="noresults">Sorry! There are no results that match your query.</div>');
			}
		}
	});
	search.fail(function() {
    	console.log("error");
  	});
}

function logout() {
	ref.unauth();
}

// Read a page's GET URL variables and return them as an associative array.
function getUrlVars()
{
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}

var movieSearchResultEle = function(movieID, movieTitle, moviePoster) {
	if (moviePoster == "N/A") {
		return '<a href="movie.html?id=' + movieID + '"><div class="movie" data-movieid="' + movieID + '"><span>' + movieTitle + '</span></div></a>';
	} else {
		return '<a href="movie.html?id=' + movieID + '"><div class="movie" data-movieid="' + movieID + '"><img src="' + moviePoster + '"><span>' + movieTitle + '</span></div></a>';
	}
}
var ref = new Firebase("https://moviespotlight.firebaseio.com");
var ratings;
var userHasRated = null;
var major;

$(document).ready(function() {
	var authData = ref.getAuth();
	ref.onAuth(function(authData) {
	  if (! authData) {
	  	console.log("Client unauthenticated.")
	  	window.location.href = 'login.html';
	  }
	});

	var movieID = getUrlVars()["id"];

	if (movieID) {
		console.log(movieID);
		ref.child("users").child(authData.uid).once("value", function(data) {
			major = data.val().major;
			$('#major-initials').text(data.val().major.replace(/[^A-Z]/g, ''));
			setMovieRatingInfo(movieID, data.val().major);
	  	}, function (errorObject) {
	  		console.log("The read failed: " + errorObject.code);
		});
		setMovieInfo(movieID);
		addFBInte(movieID);
	}

	$('#movie-spotlight-yourRating i').click(function(event) {
		theStar = event.target;
		var rating = 5 - $('#movie-spotlight-yourRating i').index(theStar);
		$('#movie-spotlight-yourRating').attr('data-rating', rating);
		var userRatingChange;
		var newTotal = 0;
		if (ratings) {
			if (ratings.NumtotalRatings) {
				newTotal = Number(ratings.NumtotalRatings);
			}
			var majorTotal = 0;
			if (ratings["Num" + major]) {
				majorTotal = Number(ratings["Num" + major]);
			}
			if (userHasRated == null) {
				userRatingChange = rating;
				newTotal++;
				majorTotal++;
			} else {
				userRatingChange = rating - Number(userHasRated);
			}
			ref.child("ratings/" + movieID).update({
				"overallRating": (Number(ratings.overallRating) * Number(ratings.NumtotalRatings) + userRatingChange) / newTotal,
				"NumtotalRatings": newTotal
			});
			if (ratings[major]) {
				var temp = {};
				temp[major] = (Number(ratings[major]) * Number(ratings["Num" + major]) + userRatingChange) / majorTotal;
				temp["Num" + major] = majorTotal;
				ref.child("ratings/" + movieID).update(temp);
			} else {
				var temp = {};
				temp[major] = rating;
				temp["Num" + major] = majorTotal;
				ref.child("ratings/" + movieID).update(temp);				
			}
		} else {
			var temp = {
				"overallRating": rating,
				"NumtotalRatings": 1,
			};
			temp[major] = rating;
			temp["Num" + major] = 1;
			ref.child("ratings/" + movieID).update(temp);
		}
		ref.child("ratings/" + movieID + "/review/" + ref.getAuth().uid).update({
			"rating": rating
		});
	});
});

function addFBInte(movieID) {
	theEle = '<iframe src="https://www.facebook.com/plugins/like.php?href=https%3A%2F%2Fmoviespotlight.firebaseapp.com%2Fmovie.html%2Fid=' + movieID + '&width=180&layout=button_count&action=recommend&show_faces=true&share=true&height=46&appId" width="180" height="46" style="border:none;overflow:hidden" scrolling="no" frameborder="0" allowTransparency="true"></iframe>';
	$('.fb').append(theEle);
}

function setMovieInfo(movieID) {
	console.log('loading results');
	var search = $.get("http://www.omdbapi.com/?i=" + movieID + "&plot=full", function(data) {
		document.title = data.Title + ' | Spotlight';
		$('#movie-title').text(data.Title);
		$('#movie-plot').text(data.Plot);
		$('#movie-imdbRating').text(data.imdbRating);
		$('#movie-poster').attr('src', data.Poster);		
		console.log(data)
	});
	search.fail(function() {
    	console.log("error");
  	});
}

// Read a page's GET URL variables and return them as an associative array.
function getUrlVars() {
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

function setMovieRatingInfo(movieID, major) {
	console.log('loading results ' + major);
	ref.child("ratings/" + movieID).on("value", function(snapshot) {
		if (snapshot.val() == null) {
			$('.rating').hide();
			$($('.rating')[2]).show();
		} else {
			$('#movie-spotlight-rating').text(Number(snapshot.val().overallRating).toFixed(1));
			if (snapshot.val().review[ref.getAuth().uid]) {
				var yourRating = snapshot.val().review[ref.getAuth().uid].rating;
				userHasRated = yourRating;
				$('#movie-spotlight-yourRating').attr('data-rating', yourRating);				
			}
			if (snapshot.val()[major]) {
				$('#movie-spotlight-majorRating').text(Number(snapshot.val()[major]).toFixed(1));				
			} else {
				$($('.rating')[1]).hide();
			}
			ratings = snapshot.val();
			console.log(snapshot.val());			
		}
	});
}
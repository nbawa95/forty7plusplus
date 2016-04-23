var ref = new Firebase("https://moviespotlight.firebaseio.com");

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
  	}, function (errorObject) {
  		console.log("The read failed: " + errorObject.code);
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
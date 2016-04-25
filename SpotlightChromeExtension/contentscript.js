(function(window, $, undefined){ 

	var ref = new Firebase("https://moviespotlight.firebaseio.com");
	var authData = ref.getAuth();
	var movieID = window.location.href.split("/")[4];
	var spotlightLink = "http://ashaysheth.com/projects/spotlight/movie.html?id=" + movieID;
	$("#spotlight-link").attr("href", spotlightLink);
	ref.child("ratings/" + movieID).on("value", function(snapshot) {
		if (snapshot.val() == null) {
			$('.spotlight-rating').hide();
		} else {
			$('#movie-spotlight-rating').text(snapshot.val().overallRating);
		}
	});
	$('<div style="text-align: center;"><a id="spotlight-link" href="' + spotlightLink + '"><div class="spotlight-rating" title="The Spotlight Rating" style="display: inline-block; background: #444; margin-top: 5px; padding: 3px 5px 0; border-radius: 20px; font-size: 15px; vertical-align: top; cursor: pointer;"><img src="https://moviespotlight.firebaseapp.com/ratingCircle.png" style="display: inline-block; height: 30px; width: 30px;"><span id="movie-spotlight-rating" style="vertical-align: top; line-height: 30px; margin-right: 5px; margin-left: 5px;"></span><span style="vertical-align: top; line-height: 30px; margin-right: 5px; font-size: 15px;">&#9733</span></div></a></div>').insertAfter(".imdbRating");
	console.log(authData);
	// if (authData) {
	// }


})(window, jQuery);
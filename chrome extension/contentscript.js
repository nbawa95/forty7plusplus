

(function(window, $, undefined){ 

  //$('.actions').append('<br><br><span class="btnWrap mltBtn mltBtn-s60"><a id="watch-trailer" href="javascript:void(0);" style="margin-top:-5px" class="svf-button svfb-silver addlk evo-btn svf-button-inq save2add"><span class="inr">Watch Trailer</span></a></span>');
  //$('.star-rating-widget').append('<br><br><div class="rating"><img src="icon.png"><span id="movie-spotlight-rating"></span><i class="fa fa-star"></i></div>');


	var ref = new Firebase("https://moviespotlight.firebaseio.com");
	//var movieID = "tt1837576";
	var url = window.location.href;
	var movieIDS = url.split("/");
	var movieID = movieIDS[4];
	var spotlightLink = "https://moviespotlight.firebaseapp.com/movie.html?id=" + movieIDS[4];
	$("a").attr("href", spotlightLink);
	ref.child("ratings/" + movieID).on("value", function(snapshot) {
		if (snapshot.val() == null) {
			$('.spotlight-rating').hide();
		} else {
			$('#movie-spotlight-rating').text(snapshot.val().overallRating);
		}
	});



	//$('.star-rating-widget').append('<div class="spotlight-rating" style="display: inline-block; background: #333; padding: 5px 10px 5px 5px; border-radius: 20px; font-size: 15px; vertical-align: top; cursor: pointer;"><img src="chrome.extension.getURL(\'icon.png\')"><span id="movie-spotlight-rating"></span><span>&#9733;</span></div>');
	$('<div class="spotlight-rating" title="Click to view this movie on Spotlight." style="display: inline-block; background: #737373; margin-left: 43px; margin-top: 5px; padding: 5px 10px 5px 5px; border-radius: 20px; font-size: 15px; vertical-align: top; cursor: pointer;"><img src="https://moviespotlight.firebaseapp.com/ratingCircle.png" style="display: inline-block; height: 30px; width: 30px;"><span id="movie-spotlight-rating" style="vertical-align: top; line-height: 30px; margin-right: 5px; margin-left: 5px;"></span><span style="vertical-align: top; line-height: 30px; margin-right: 5px; font-size: 15px;">&#9733</span><a id="link"></a></div>').insertAfter(".imdbRating");
	$(".spotlight-rating").click(function() {
		var url = window.location.href;
		var movieIDS = url.split("/");
		var movieID = movieIDS[4];
		var spotlightLink = "https://moviespotlight.firebaseapp.com/movie.html?id=" + movieIDS[4];
		$("a").attr("href", spotlightLink);
		window.location = $(this).find("a").attr("href"); 
	 	return false;
	});

})(window, jQuery);


//<a href="http://pro.imdb.com/title/tt0286788?rf=cons_tt_contact&amp;ref_=cons_tt_contact" class="quicklink">IMDbPro</a>
//<button class = "sptolight-rating"> <span class 

//<div class="rating"><img src="icon.png"><span id="movie-spotlight-rating"></span><i class="fa fa-star"></i></div>
// $('.star-rating-widget').append('<br><br><div class="rating" style="display: inline-block; height: 30px;
//     width: 30px;
//     line-height: 30px;
//     text-align: center;
//     margin-right: 5px;
//     background: #FFC100;
//     color: #181818;
//     border-radius: 50%;
//     font-size: 12px;
//     font-weight: bold;
//     vertical-align: bottom;"><img src="icon.png"><span id="movie-spotlight-rating"></span><i class="fa fa-star"></i></div>');



// .rating {
//     display: inline-block;
//     background: #333;
//     padding: 5px 10px 5px 5px;
//     border-radius: 20px;
//     font-size: 15px;
//     vertical-align: top;
//     cursor: pointer;
// }

// .rating img, .rating div {
//     display: inline-block;
//     height: 30px;
//     width: 30px;
//     line-height: 30px;
//     text-align: center;
//     margin-right: 5px;
//     background: #FFC100;
//     color: #181818;
//     border-radius: 50%;
//     font-size: 12px;
//     font-weight: bold;
//     vertical-align: bottom;
// }

// .rating span {
    // line-height: 30px;
    // margin-right: 5px;
// }
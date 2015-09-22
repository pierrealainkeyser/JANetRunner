({
	baseUrl : "static/js/",
	name : "main",
	paths : { //
		jquery : 'https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.4/jquery.js',//
		underscore : 'https://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.8.3/underscore.js',//
		tweenlite : 'https://cdnjs.cloudflare.com/ajax/libs/gsap/1.18.0/TweenLite.min.js',//
		mousetrap : 'https://cdnjs.cloudflare.com/ajax/libs/mousetrap/1.4.6/mousetrap.min.js',//
		interact : 'https://cdnjs.cloudflare.com/ajax/libs/interact.js/1.2.4/interact.min.js',//	
		conf : "./anr/conf"
	},//
	shim : {//
		tweenlite : {
			exports : 'TweenLite'
		}, //
		mousetrap : {
			exports : 'Mousetrap'
		}
	//
	}
})
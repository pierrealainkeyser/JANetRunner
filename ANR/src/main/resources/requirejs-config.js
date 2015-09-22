({
    baseUrl: "/js/",
    conf : "./anr/conf" },//
    include :["*.js"]
    shim : {//
    	tweenlite : { exports : 'TweenLite' }, //
    	mousetrap : { exports : 'Mousetrap' }, //
		} //
	},
    out: "main-built.js"
})
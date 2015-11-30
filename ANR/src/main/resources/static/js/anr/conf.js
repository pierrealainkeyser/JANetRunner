define([ "geometry/size", "layout/impl/anchorlayout", "layout/impl/flowlayout", "layout/impl/translatelayout", "layout/impl/gridlayout",
		"layout/impl/hostlayout" ], //
function(Size, AnchorLayout, FlowLayout, TranslateLayout, GridLayout, HostLayout) {

	function Conf() {
		var w = 80;
		var h = 111;

		// configuration pour les cartes
		this.card = { normal : new Size(w, h),//
		mini : new Size(w / 3.2, h / 3.2),//
		zoom : new Size(w * 2, h * 2),//
		selected : new Size(w * 1.75, h * 1.75),//		
		layouts : { tokens : new GridLayout({ maxCols : 3, padding : 2 }),//
		wrapper : new HostLayout({}), //
		access : new GridLayout({  maxCols : 6, padding : 2 }) //
		} // 
		};

		// la duree d'animation
		this.animation = { normal : 0.3, fast : 0.15 }

		// la taille de l'écran
		this.screen = new Size(1300, 800);

		// la position des layouts sur l'écran
		this.corp = { layouts : {// 
		translate : new TranslateLayout({ y : -1 }),//
		servers : new FlowLayout({ direction : FlowLayout.Direction.RIGHT, align : FlowLayout.Align.LAST, spacing : 2, padding : 0 }) } // 
		}

		// configuration pour les serveurs
		this.server = { layouts : {//
		main : new FlowLayout({ direction : FlowLayout.Direction.TOP, align : FlowLayout.Align.MIDDLE, padding : 3 }),//
		stacked : new AnchorLayout({}),//
		upgrades : new FlowLayout({ padding : 2, direction : FlowLayout.Direction.RIGHT, spacing : -this.card.normal.width / 1.5 }),//
		minSize : new AnchorLayout({ padding : 3, minSize : this.card.normal }),//
		ices : new FlowLayout({ direction : FlowLayout.Direction.TOP, padding : 3, spacing : 4 }) //
		} };

		// configuration pour le runner
		this.runner = { layouts : {//
		stacked : new AnchorLayout({}),//
		column : new FlowLayout({ direction : FlowLayout.Direction.BOTTOM, align : FlowLayout.Align.LAST, padding : 0, spacing : 6 }),//
		row : new FlowLayout({ direction : FlowLayout.Direction.LEFT, align : FlowLayout.Align.FIRST, spacing : 2, padding : 0 }),//
		resources : new FlowLayout({ direction : FlowLayout.Direction.LEFT, align : FlowLayout.Align.FIRST, spacing : 7, padding : 8 }),//
		programsHardwares : new FlowLayout({ direction : FlowLayout.Direction.LEFT, align : FlowLayout.Align.FIRST, spacing : 7, padding : 0 }),//
		translate : new TranslateLayout({ x : -1 }) //
		} }

		// ombrage des cartes
		this.shadow = {//
		front : { horizontal : "2.5px -2.5px 4px 0px rgba(60, 60, 60, 0.8)", vertical : "2.5px 2.5px 4px 0px rgba(60, 60, 60, 0.8)" },//
		back : { horizontal : "-2.5px -2.5px 4px 0px rgba(60, 60, 60, 0.8)", vertical : "-2.5px 2.5px 4px 0px rgba(60, 60, 60, 0.8)" },//
		action : "0px 0px 5px 2px rgb(240, 173, 78)",//
		marked : "0px 0px 5px 2px #FF3300"//
		};

		// les niveau de positions en couches
		this.zindex = {//
		chat : 100000, //
		card : 1000,//
		zoom : 2000,//
		status : 10,//
		run : 5 //
		};

		// le nombre d'élément dans le chat
		this.chat = { limit : 10,//
		xOffset : -200 //
		};
	}
	var conf = new Conf();
	return conf;
});
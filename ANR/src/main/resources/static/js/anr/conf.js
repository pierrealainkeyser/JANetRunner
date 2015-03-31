define([ "geometry/size", "layout/impl/anchorlayout", "layout/impl/flowlayout", "layout/impl/translatelayout" ], //
function(Size, AnchorLayout, FlowLayout, TranslateLayout) {

	function Conf() {
		var w = 80;
		var h = 111;

		this.card = { normal : new Size(w, h), mini : new Size(w / 3, h / 3), zoom : new Size(w * 2, h * 2) };

		// la duree d'animation
		this.animation = { normal : 0.3, fast : 0.15 }

		// la taille de l'écran
		this.screen = new Size(1300, 800);

		// la position des layouts sur l'écran
		this.corp = { layouts : { translate : new TranslateLayout({ y : -1 }),
			servers : new FlowLayout({ direction : FlowLayout.Direction.RIGHT, align : FlowLayout.Align.LAST, spacing : 3, padding : 0 }) } }

		// configuration pour le server
		this.server = { layouts : { main : new FlowLayout({ direction : FlowLayout.Direction.TOP, align : FlowLayout.Align.MIDDLE, padding : 3 }),
			stacked : new AnchorLayout({}),
			upgrades : new FlowLayout({ padding : 0, direction : FlowLayout.Direction.RIGHT, spacing : -this.card.normal.width / 2 }),
			minSize : new AnchorLayout({ padding : 3, minSize : this.card.normal }),
			ices : new FlowLayout({ direction : FlowLayout.Direction.TOP, padding : 3 }) } };
	}
	var conf = new Conf();
	return conf;
});
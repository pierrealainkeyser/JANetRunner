define([ "geometry/size", "layout/impl/anchorlayout", "layout/impl/flowlayout" ], function(Size, AnchorLayout, FlowLayout) {

	function Conf() {
		var w = 80;
		var h = 111;

		this.card = {
			normal : new Size(w, h),
			mini : new Size(w / 3, h / 3),
			zoom : new Size(w * 2, h * 2)
		};

		//configuration pour le server
		this.server = {
			layouts : {
				main : new FlowLayout({
					direction : FlowLayout.Direction.TOP,
					align : FlowLayout.Align.MIDDLE,
					padding : 3
				}),
				stacked : new AnchorLayout({}),
				upgrades : new FlowLayout({
					padding : 0,
					direction : FlowLayout.Direction.RIGHT,
					spacing : -this.card.normal.width / 2
				}),
				minSize : new AnchorLayout({
					padding : 3,
					minSize : this.card.normal
				}),
				ices : new FlowLayout({
					direction : FlowLayout.Direction.TOP,
					padding : 3
				})
			}
		};
	}

	return new Conf();
});
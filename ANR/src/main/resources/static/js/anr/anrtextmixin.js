define([ "mix" ], function(mix) {
	function AnrTextMixin() {

	}

	mix(AnrTextMixin, function() {

		/**
		 * Interpolation des caracteres ANR
		 * 
		 * @param string
		 * @returns
		 */
		this.interpolateString = function(string) {

			if (string) {
				var str = string.replace(/\{(\d+):(\w+)\}/g, function() {
					var nb = arguments[1];
					var str = arguments[2];
					if ('trace' === str)
						return "<strong>Trace<sup>" + nb + "</sup></strong> -";
					else if ('credit' === str)
						return nb + "<span class='icon icon-credit'></span>";
					else if ('sub' === str)
						return "<span class='icon icon-subroutine'></span>";
					else if ('click' === str) {
						var a = [];
						for (var i = 0; i < nb; i++)
							a.push("<span class='icon icon-click'/>");
						return a.join(",");
					}

					return "";
				});

				return str.replace(/\|([A-Za-z&\d\s\:]+)\|/g, function() {
					var str = arguments[1];
					return "<em>" + str + "</em>";
				});
			} else
				return "";
		}
	});

	return AnrTextMixin;
})
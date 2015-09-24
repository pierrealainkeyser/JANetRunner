define([ "mix", "jquery", "underscore", "ui/jqueryboxsize", "geometry/size", "geometry/rectangle",// 
"layout/impl/flowlayout", "layout/abstractboxcontainer", "anr/anrtextmixin" ],// 
function(mix, $, _, JQueryBoxSize, Size, Rectangle,//
FlowLayout, AbstractBoxContainer, AnrTextMixin) {

	// ---------------------------------------------------

	/**
	 * Permet d'afficher les messages
	 */
	function BoxChat(layoutManager, text) {
		JQueryBoxSize.call(this, layoutManager, $("<span class='chat'>" + text + "</span>"), {
			zIndex : true,
			rotation : false,
			autoAlpha : true,
			size : true
		});

		// l'offset de début
		this.deltaLeft = 0;
	}

	mix(BoxChat, JQueryBoxSize);
	mix(BoxChat, function() {

		/**
		 * Permet de faire un effet d'apparence en scrollant
		 */
		this.onFirstSyncScreen = function(css) {
			var newCss = _.clone(css);
			newCss.left -= this.deltaLeft;

			this.tweenElement(this.element, newCss, true);
			return false;
		}
	});

	// ---------------------------------------------------
	function ChatTracker(layoutManager, limit) {
		AbstractBoxContainer.call(this, layoutManager, {
			addZIndex : true
		}, new FlowLayout({
			align : FlowLayout.Align.LAST,
			direction : FlowLayout.Direction.BOTTOM,
			spacing : 2,
			padding : 1
		}));

		this.limit = limit || 20;
		this.setZIndex(config.zindex.chat);
	}
	mix(ChatTracker, AbstractBoxContainer);
	mix(ChatTracker, AnrTextMixin);
	mix(ChatTracker, function() {

		/**
		 * Permet d'afficher une liste de message
		 */
		this.addChats = function(chats) {
			var newsBcs = [];
			for ( var c in chats) {
				var bc = new BoxChat(this.layoutManager, this.interpolateString(chats[c]));
				this.addChild(bc, 0);
				newsBcs.push(bc);
			}

			// suppression des elements en trop
			var size = 0;
			while ((size = this.size()) > this.limit) {
				var box = this.childs[size - 1];
				box.remove();
				this.removeChild(box);
			}
		}
	})

	return ChatTracker;
});
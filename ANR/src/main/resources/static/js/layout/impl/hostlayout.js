define([ "mix", "underscore", "geometry/package" ], function(mix, _, geom) {
	function HostLayout(options) {
		options = options || {};
		this.padding = options.padding || -20;
	}

	function CardNode(layout, card) {
		this.layout = layout;
		this.id = card.id();
		this.card = card;
		this.hostedNodes = [];
		this.bounds = new geom.Rectangle({ size : card.local.size });
	}

	mix(CardNode, function() {
		this.append = function(node) {
			this.hostedNodes.push(node);
		}

		this.indexInHost = function() {
			var host = this.card.host;
			if (host)
				return host.index;
			else
				return -1;
		}

		/**
		 * Renvoi l'ID de la carte hote
		 */
		this.hostId = function() {
			var host = this.card.host;

			if (host && host.card)
				return host.card.id();
			else
				return null;
		}

		/**
		 * Calcul le layout, mais tous les enfants en premier
		 */
		this.doLayout = function() {
			_.each(this.hostedNodes, function(hn) {
				hn.doLayout();
			});

			// le rectangle d'ensemble
			var rectangle = this.bounds;
			var x = 0;

			var sorted = _.sortBy(this.hostedNodes, function(cn) {
				return cn.indexInHost()
			});
			var padding = this.layout.padding;
			_.each(sorted, function(hn) {
				var bounds = hn.bounds;
				var h = hn.card.local.size.height * 2 / 3;
				bounds.moveTo({ x : x, y : h });

				// décallage sur la droite
				x += bounds.size.width + padding;

				rectangle = rectangle.merge(bounds);
			});
			this.bounds = rectangle;
		}

		/**
		 * Propage les coordonnées locales
		 */
		this.mergeLocal = function(offset) {

			var ori = this.bounds.clonePoint();
			var point = this.bounds.clonePoint();

			// centre la carte sur le centre
			if (!_.isEmpty(this.hostedNodes)) {
				var deltaX = this.bounds.size.width / 2 - this.card.local.size.width / 2;
				point.add({ x : deltaX, y : 0 });
			}

			// on décalle la position
			point.add(offset);
			ori.add(offset);
			this.card.local.moveTo(point);

			// réalise le merge sur les élements suivants
			_.each(this.hostedNodes, function(hn) {
				hn.mergeLocal(ori);
			});
		}

		/**
		 * Permet de réaliser un parcours par niveau
		 */
		this.collect = function() {
			var collected = [];
			var iterates = [ this ];
			while (!_.isEmpty(iterates)) {
				var newIterates = [];
				_.each(iterates, function(cn) {
					collected.push(cn.card);

					// on rajoute tous les noeuds hotes
					newIterates = newIterates.concat(cn.hostedNodes);
				});
				iterates = newIterates;
			}
			return collected;
		}
	});

	HostLayout.prototype.doLayout = function(boxcontainer, childs) {

		var me = this;
		var allHosts = {};
		// création de la liste des CardNode
		_.each(childs, function(c) {
			var cid = c.id();
			var cn = allHosts[cid];
			if (!cn)
				allHosts[cid] = cn = new CardNode(me, c);
		});

		// le noeud racine
		var rootNode = null;

		// création de l'arborescence
		_.each(allHosts, function(cn) {
			var cid = cn.hostId();
			if (cid !== null) {
				var host = allHosts[cid];
				host.append(cn);
			} else
				rootNode = cn; // si pas de parent c'est le noeud racine
		});

		// réalise le layout en 2 phases à partir de la racine
		rootNode.doLayout();
		rootNode.mergeLocal(new geom.Point());

		// Affectation du rank par niveau, ce qui permet de calcul le
		// zIndex par apres
		_.each(rootNode.collect(), function(card, index) {
			card.setRank(index);
		});

		// recherche de la zone enblogante
		var bounds = new geom.Rectangle();
		_.each(allHosts, function(cn) {
			bounds = bounds.merge(cn.card.local);
		});

		// transmission de la taille au container
		boxcontainer.local.resizeTo(bounds.size);
	};

	return HostLayout;
});

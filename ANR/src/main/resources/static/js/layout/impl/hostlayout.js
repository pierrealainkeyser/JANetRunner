define([ "mix", "underscore", "geometry/package" ], function(mix, _, geom) {
	function HostLayout() {
	}

	function CardNode(card) {
		this.id = card.id();
		this.card = card;
		this.hostedNodes = [];
		this.bounds = new geom.Rectangle({size : card.local.size});
	}

	mix(CardNode, function() {
		this.append = function(node) {
			this.hostedNodes.push(node);
		}

		this.hostId = function() {
			var host = this.card.host;
			if (host)
				return host.card.id();
			else
				return null;
		}

		this.doLayout = function() {
			// réalise le layout de tous les enfants
			_.each(this.hostedNodes, function(hn) {
				hn.doLayout();
			});

			// le rectangle d'ensemble
			var rectangle = this.bounds;
			var x = 0;
			var deltaY = 150;
			_.each(this.hostedNodes, function(hn) {
				var bounds = hn.bounds;
				bounds.moveTo({x : x,y : deltaY});

				// décallage sur la droite
				x += bounds.size.width;

				rectangle = rectangle.merge(bounds);
			});
			this.bounds = rectangle;
		}

		this.mergeLocal = function(offset) {

			var ori = this.bounds.point;
			var point = this.bounds.clonePoint();

			// centre la carte sur le centre
			if (!_.isEmtpy(this.hostedNode)) {
				var deltaX = this.bounds.width / 2 + this.card.local.size.width / 2;
				point.add({
					x : deltaX,
					y : 0
				});
			}

			// on décalle la position
			point.add(offset);
			ori.add(offset);
			card.local.moveTo(point);

			// réalise le merge sur les élements suivants
			_.each(this.hostedNode, function(hn) {
				hn.mergeLocal(ori);
			});
		}
	});

	HostLayout.prototype.doLayout = function(boxcontainer, childs) {

		var allHosts = {};
		// création de la liste des CardNode
		_.each(childs, function(c) {
			var cid = c.id();
			var cn = allHosts[cid];
			if (!cn)
				allHosts[cid] = cn = new CardNode(c);
		});

		// le noeud racine
		var rootNode = null;

		// création de l'arborescence
		_.each(allHosts, function(cn) {
			var hid = cn.hostId();
			if (hid !== null) {
				var host = allHosts[cid];
				host.append(cn);
			} else
				rootNode = cn; // si pas de parent c'est le noeud racine
		});

		// réalise le layout en 2 phases à partir de la racine
		rootNode.doLayout();
		rootNode.mergeLocal(new geom.Point());

		// TODO quid du z-index ?

		// recherche de la zone enblogante
		var bounds = new geom.Rectangle();
		_.each(allHosts, function(cn) {
			bounds = bounds.merge(cn.local);
		});

		// transmission de la taille au container
		boxcontainer.local.resizeTo(bounds.size);
	};

	return HostLayout;
});

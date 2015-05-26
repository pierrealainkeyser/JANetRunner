define([ "mix", "underscore", "util/observablemixin" ], function(mix, _, ObservableMixin) {
	function SubModel() {
		this.subs = {};
	}

	SubModel.REMOVED = "subsRemoved";
	SubModel.ADDED = "subsAdded";
	SubModel.SELECTED = "subSelected";
	SubModel.BROKEN = "subsBroken";

	mix(SubModel, ObservableMixin);
	mix(SubModel, function() {

		this.fireEvts = function(subs, doConcat) {
			var broken = [];
			var added = [];
			_.each(subs, function(s) {
				var id = s.id;
				if (this.subs[id]) {
					if (s.broken)
						broken.push(s);
				} else
					added.push(s);
			}.bind(this));

			if (!_.isEmpty(added))
				this.performChange(SubModel.ADDED, function() {
					var me = this;
					if (doConcat)
						_.each(subs, function(s) {
							me.subs[s.id] = s;
						})

					return { newSubs : subs, size : this.subs.length };
				}.bind(this));
			else if (!_.isEmpty(broken))
				this.performChange(SubModel.BROKEN, function() {
					return { broken : broken, size : this.subs.length }
				}.bind(this))
		}

		/**
		 * Compte les sous-routine selectionne
		 */
		this.getSelecteds = function() {
			var selecteds = [];
			this.eachSubs(function(s) {
				if (s.selected)
					selecteds.push(s);
			});
			return selecteds;
		}

		/**
		 * Changement de sélection d'un sub
		 */
		this.select = function(sub, selected) {
			this.performChange(SubModel.SELECTED, function() {
				sub.selected = selected;
				return { selected : selected }
			}.bind(this))

		}

		/**
		 * Permet de rajouter le tableau de subs
		 */
		this.update = function(subs) {

			// création du tableau si nécessaire
			if (!_.isArray(subs))
				subs = [ subs ];

			this.fireEvts(subs, true);
		}

		/**
		 * Supprime toutes les subs du model
		 */
		this.removeAll = function() {
			if (this.hasSubs()) {
				this.performChange(SubModel.REMOVED, function() {
					var removed = this.subs;
					this.subs = {};
					return { removedSubs : removed, size : this.subs.length };
				}.bind(this));
			}
		}

		this.eachSubs = function(c) {
			_.each(this.subs, c);
		}

		/**
		 * Renvoi vrai s'il y a au moins une sub dans le model
		 */
		this.hasSubs = function() {
			return !_.isEmpty(this.subs);
		}

	});
	return SubModel;
})

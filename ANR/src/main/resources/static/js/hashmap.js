function Hashmap() {
	this.hashes = {};

	this.put = function(key, value) {
		this.hashes[JSON.stringify(key)] = value;
	}

	this.get = function(key) {
		return this.hashes[JSON.stringify(key)];
	}

	this.values = function() {
		return this.hashes;
	}
}
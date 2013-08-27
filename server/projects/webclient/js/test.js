(function() {

	'use strict';

	$(document).ready(function() {
		initPosition(testOffer);
	});

	function initPosition(callback) {
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(function(p) {
				if (p.coords) {
					callback(p.coords);
				}
			}, function() {
			}, {
				enableHighAccuracy : true,
				maximumAge : 600000
			});
		}
	}

	function testOffer(local) {
		if (!local) {
			return;
		}
		
		var location = {};
		location['longitude'] = local.longitude;
		location['latitude'] = local.latitude;
		var data = {};
		data['userLocation'] = location;
		var req = {};
		req['GetUserOffersRequest'] = data;
		var str = JSON.stringify(req);
		$.ajax({
			dataType : 'json',
			type : 'POST',
			contentType : 'application/json',
			data : str,
			url : 'kikbak/user/hasoffer',
			success : function(json) {
				alert(json.hasUserOffersResponse.hasOffer);
			}
		});
	}

})();
var config = {
}

function getHeight() {
  xHeight = null;
  if(window.screen != null)
    xHeight = window.screen.availHeight;
  if(window.innerHeight != null)
    xHeight =   window.innerHeight;
  if(document.body != null)
    xHeight = document.body.clientHeight;
  return xHeight;
}

function showError() {
  alert("Service is unavailable. Please try again later.");
}

function showGpsError(userId) {
  alert("Cannot locate user");
  getOffersByLocation(userId, null);
}

function fbInit() {
  FB.init({
    appId: '135124669870013',
    channelUrl: '/m/channel.html',
    status: true,
    cookie: true,
    xfbml: true
  });       
  FB.Event.subscribe('auth.authResponseChange', function(response) {
    if (response.status === 'connected') {
      connectFb();
    } else if (response.status === 'not_authorized') {
      FB.login();
    } else {
      FB.login();
    }
  });
}

function connectFb() {
  if (typeof Storage != 'undefined') {
    var userId = localStorage.userId;
    if (typeof userId != 'undefined' && userId != 'undefined' && 
            userId != null && userId != 'null' && userId != '') {
      initPage();
      return;
    }
  }
  FB.api('/me', function(response) {
    var data = {};
    data['user'] = response;
    var req = {};
    req['RegisterUserRequest'] = data;
    var str = JSON.stringify(req);

    $.ajax({
      dataType: 'json',
      type: 'POST',
      contentType: 'application/json',
      data: str,
      url: 'kikbak/user/register/fb/',
      success: function(json) {
        updateFbFriends(json.registerUserResponse.userId.userId);
      },
      error: showError
    });
  });
}

function updateFbFriends(userId) {
  FB.api('/me/friends', {fields: 'name,id,first_name,last_name,username'}, function(friend_response) {
    var data = {};
    data['friends'] = friend_response.data;
    var req = {};
    req['UpdateFriendsRequest'] = data;
    var str = JSON.stringify(req);

    $.ajax({
      dataType: 'json',
      type: 'POST',
      contentType: 'application/json',
      data: str,
      url: 'kikbak/user/friends/fb/' + userId,
      success: function(json) {
        if (typeof Storage != 'undefined') {
          localStorage.userId = userId;
        }
        initPage();
      },
      error: showError
    });
  });
}

function initPage() {
  var suffix = 'index.html';
  if (window.location.href.indexOf(suffix, window.location.href.length - suffix.length) !== -1) {
    window.location.href = 'offer.html';
  }
  suffix = 'offer.html';
  if (window.location.href.indexOf(suffix, window.location.href.length - suffix.length) !== -1) {
     getOffers();
  }
  suffix = 'redeem.html';
  if (window.location.href.indexOf(suffix, window.location.href.length - suffix.length) !== -1) {
     getRedeems();
  }
}

function getOffers() {
  if (typeof Storage != 'undefined') {
    var userId = localStorage.userId;

    if (typeof userId != 'undefined' && userId != 'undefined' &&
            userId != 'null' && userId != null && userId != '') {
      var location = {};
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(p) {
          getOffersByLocation(userId, p);
        }, function() {
          showGpsError(userId);
        },
        { enableHighAccuracy:true,maximumAge:600000 });
      } else {
        getOffersByLocation(userId, null);
      }
    }
  }
}

function renderOffer(offer) {
  var html = '<li>';
  
  var json = escape(JSON.stringify(offer));
  html += '<div class="offer-div"><a href="#" data-object="' + json + '" class="offer-details-btn clearfix">';
  html += '<img id="offer-background" src="img/offer.png" />';
  // html += '<img src="' + json.merchantImageUrl + '" />';
  html += '<img id="offer-label1" src="img/label1_price.png" />';
  html += '<div id="offer-label1-text">';
  var gift = "<p>GIVE</p><p>";
  gift += offer.giftType == 'percentage' ? offer.giftValue + "%</p><p>OFF</p>" :
          "$" + offer.giftValue + "</p>";
  html += gift + '</div>';
  html += '<img id="offer-label2" src="img/label2_price.png" />';
  html += '<div id="offer-label2-text">';
  gift = "<p>GET</p><p>";
  gift += "$" + offer.kikbakValue + "</p>";
  html += gift + '</div>';
  html += '</a></div>';
  html += '</li>';
  $('#offer-list').append(html);
}

function getOffersByLocation(userId, position) {
  var location = {};
  if (position != null) {
    location['longitude'] = position.coords.longitude; 
    location['latitude'] = position.coords.latitude; 
  }
  var data = {};
  data['userLocation'] = location;
  var req = {};
  req['GetUserOffersRequest'] = data;      
  var str = JSON.stringify(req);
  $.ajax({
    dataType: 'json',
    type: 'POST',
    contentType: 'application/json',
    data: str,
    url: 'kikbak/user/offer/' + userId,
    success: function(json) {
      var offers = json.getUserOffersResponse.offers;
      $.each(offers, function(i, offer) {
        renderOffer(offer);
      });
    },
    error: showError
  });
}

function getRedeems() {
  if (typeof Storage != 'undefined') {
    var userId = localStorage.userId;

    if (typeof userId != 'undefined' && userId != 'undefined' &&
            userId != 'null' && userId != null && userId != '') {
      $.ajax({
      });
    }
  }
}


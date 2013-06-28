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
  if (typeof Storage != 'undefined') {
    var pageType = localStorage.pageType;
    if (pageType === 'offer') {
      getOffers();
    } else {
      getOffers();
      // getRedeems();
    }
  } else {
     getOffers();
  }
}

function getOffers() {
  if (typeof Storage != 'undefined') {
    localStorage.pageType = 'offer';
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
  var gift = "GIVE<br>";
  gift += offer.giftType == 'percentage' ? offer.giftValue + "%<br>OFF" :
          "$" + offer.giftValue;
  html += gift + '</div>';
  html += '<img id="offer-label2" src="img/label2_price.png" />';
  html += '<div id="offer-label2-text">';
  gift = "GET<br>";
  gift += "$" + offer.kikbakValue;
  html += gift + '</div>';
  html += '</a></div>';
  html += '</li>';
  $('#offer-list').append(html);
}

function getOffersByLocation(userId, position) {
  setWrapperSize();
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
      setTimeout(function(){
        reload();
      }, 500);
    },
    error: showError
  });
}

function getRedeems() {
  if (typeof Storage != 'undefined') {
    setWrapperSize();
    var userId = localStorage.userId;

    if (typeof userId != 'undefined' && userId != 'undefined' &&
            userId != 'null' && userId != null && userId != '') {
      $.ajax({
      });
    }
  }
}

function reload() {
  if ( typeof getOffersByLocation.ov == 'undefined' ) {
    getOffersByLocation.ov = new iScroll('offer-view');
  } else {
    getOffersByLocation.ov.refresh();
  }

  if (getBrowserName() == 'Safari') {
    $('#footer').css('position', 'static');
  } else {
    $('#footer').css('position', 'fixed');
  }
  window.scrollTo(0, 1);
}

function getBrowserName() {
  var nVer = navigator.appVersion;
  var nAgt = navigator.userAgent;

  if(navigator.platform == 'iPhone' || navigator.platform == 'iPod'){
    if ((verOffset=nAgt.indexOf('Safari'))!=-1) {
      return 'Safari';
    }
  }
  return 'other';
}

function setWrapperSize() {
  var wrapperSize;
  if (getBrowserName() == 'Safari') {
    wrapperSize = getHeight() - 45 - 50 + 60;
  } else {
    wrapperSize = getHeight() - 45 - 50;
  };
    
   $('.wrapper').css('min-height', wrapperSize + 'px');
   $('#footer').css('margin-top', wrapperSize + 50 + 'px');
}


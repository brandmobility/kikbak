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
      connectFb(response.authResponse.accessToken);
    } else if (response.status === 'not_authorized') {
      FB.login();
    } else {
      FB.login();
    }
  });
}

function connectFb(accessToken) {
  if (typeof Storage != 'undefined') {
    localStorage.accessToken = accessToken;
    var userId = localStorage.userId;
    if (typeof userId != 'undefined' && userId != 'undefined' && 
            userId != null && userId != 'null' && userId != '') {
      initPosition(initPage);
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

function initPosition(callback) {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(function(p) {
      initPage.p = p.coords;
      callback();
    }, function() {
    },
    { enableHighAccuracy:true,maximumAge:600000 });
  }
}

function initPage() {
  var suffix = 'index.html';
  if (window.location.href.indexOf(suffix, window.location.href.length - suffix.length) !== -1) {
    window.location.href = 'offer.html';
  }
  $('#offer-view').hide();
  $('#offer-details-view').hide();
  $('#redeem-view').hide();
  $('#redeem-details-view').hide();
  $('#back-btn-div').hide();
  $('#suggest-btn-div').hide();
  $('#footer').hide();
  $('#suggest-view').hide();
  window.scrollTo(0, 1);
  if (typeof Storage != 'undefined') {
    var pageType = localStorage.pageType;
    if (pageType === 'redeem') {
      $('#redeem-view').show('');
      $('#footer').show('');
      getRedeems();
    } else if (pageType === 'offer-detail') {
      $('#offer-details-view').show('');
      $('#back-btn-div').show('');
      getOfferDetail();
    } else if (pageType === 'redeem-detail') {
      $('#redeem-details-view').show('');
      $('#back-btn-div').show('');
      getRedeemDetail();
    } else if (pageType === 'suggest') {
      $('#suggest-view').show('');
      $('#back-btn-div').show('');
      $('#back-btn').click(function(e){
        e.preventDefault();
        localStorage.pageType = 'offer';
      });
    }else {
      // offer page is the default page
      $('#offer-view').show('');
      $('#suggest-btn-div').show('');
      $('#footer').show('');
      getOffers();
    }
  }
  setWrapperSize();
  setTimeout(function(){
    reload();
  }, 500);
}

function getOffers() {
  if (typeof Storage != 'undefined') {
    localStorage.pageType = 'offer';
    var userId = localStorage.userId;

    if (typeof userId != 'undefined' && userId != 'undefined' &&
            userId != 'null' && userId != null && userId != '') {
      if (typeof initPage.p !== 'undefined') {
        getOffersByLocation(userId, initPage.p);
      } else {
        getOffersByLocation(userId, null);
      }
    }
  }
}

function renderOffer(offer) {
  var html = '<li>';
  
  var json = escape(JSON.stringify(offer));
  html += '<div class="offer-div">';
  html += '<a href="#" data-object="' + json + '" class="offer-details-btn clearfix">';
  html += '<img class="offer-background" src="img/offer.png" />';
  // TODO
  // html += '<img class="offer-background" src="' + json.merchantImageUrl + '" />';
  html += '<div class="offer-background-grad" />';
  html += '</a>';
  html += '<img class="offer-label1" src="img/label1_price.png" />';
  html += '<div class="offer-label1-text">';
  var gift = "Give<br>";
  gift += offer.giftType == 'percentage' ? offer.giftValue + "%<br>OFF" :
          "$" + offer.giftValue;
  html += gift + '</div>';
  html += '<img class="offer-label2" src="img/label2_price.png" />';
  html += '<div class="offer-label2-text">';
  gift = "Get<br>";
  gift += "$" + offer.kikbakValue;
  html += gift + '</div>';
  html += '<div class="brand">';
  html += offer.merchantName;
  html += '</div><div class="website">';
  html += '<a href="' + offer.merchantUrl + '"><img class="website-img" src="img/ic_web.png" /></a>';
  html += '</div>';

  local = getDisplayLocation(offer.locations);
  if (local != 'undefined') {
    html += '<div class="phone">';
    html += '<a href="tel:' + local.phoneNumber + '"><img class="website-img" src="img/ic_phone.png" /></a>';
    html += '</div><div class="map">';
    html += '<a href="' + generateMapUrl(offer.merchantName, local) + '"><img class="website-img" src="img/ic_map.png" />' + '&nbsp;' + computeDistance(local) + '</a>';
    html += '</div>';
  }
  html += '</div>';
  html += '</li>';
  $('#offer-list').append(html);
}

function generateMapUrl(name, local) {
  var escapedName = encodeURIComponent(name);
  var url = 'https://maps.google.com/maps?q=' + escapedName + '%40' + local.latitude + ',' + local.longitude;
  return url;
}

function computeDistance(local) {
  if (typeof(Number.prototype.toRad) === "undefined") {
    Number.prototype.toRad = function() {
      return this * Math.PI / 180;
    }
  }

  if (typeof initPage.p === 'undefined') {
    return 'Unknown';
  }

  var lat1 = parseFloat(local.latitude);
  var lat2 = parseFloat(initPage.p.latitude);
  var lon1 = parseFloat(local.longitude);
  var lon2 = parseFloat(initPage.p.longitude);

  var R = 3958.756; // mile
  var dLat = (lat2-lat1).toRad();
  var dLon = (lon2-lon1).toRad();
  var lat1 = lat1.toRad();
  var lat2 = lat2.toRad();
  var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
              Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
  var d = parseInt(R * c * 10);
  return parseInt(d / 10) + '.' + (d % 10) + ' mile';
}

function getDisplayLocation(locations) {
  return locations[0];
}

function getOffersByLocation(userId, position) {
  var location = {};
  if (position != null) {
    location['longitude'] = position.longitude;
    location['latitude'] = position.latitude;
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
      $('#offer-list').html('');
      $.each(offers, function(i, offer) {
        renderOffer(offer);
      });
      $('.offer-details-btn').click(function(e) {
        e.preventDefault();
        if (Storage != 'undefined') {
          localStorage.offerDetail = $(this).attr('data-object');
          localStorage.pageType = 'offer-detail';
        }
        initPage();
      });
      setTimeout(function(){
        resizeScroll();
      }, 500);
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
        // TODO
      });
    }
  }
}

function getOfferDetail() {
  if (typeof Storage != 'undefined') {
    var userId = localStorage.userId;
    if (typeof userId != 'undefined' && userId != 'undefined' &&
            userId != 'null' && userId != null && userId != '') {
      var offer = jQuery.parseJSON(unescape(localStorage.offerDetail));
      renderOfferDetail(offer);

      $('#back-btn').click(function(e) {
        e.preventDefault();
        localStorage.pageType = 'offer';
        initPage();
      });

      $('#take-picture').change(function(e) {
        // TODO
        var files = e.target.files;
        var file;
        if (files && files.length > 0) {
           file = files[0];
           try {
             var URL = window.webkitURL || window.URL;
             var imgUrl = URL.createObjectURL(file);
             $('#show-picture').load(function(e){
               URL.revokeObjectURL(this.src);
             });
             $('#show-picture').attr('src', imgUrl);
           } catch (e) {
             try {
               var fileReader = new FileReader();
               fileReader.onload = function (e) {
                 $('#show-picture').src = e.target.result;
               }
               fileReader.readAsDataUrl(file);
             } catch (e) {
               alert('Unsupported browser');
             }
           }
        }
      });

      $('#share-form input[name="comment"]').bind('keyup', function(){
        if ($('#share-form input[name="comment"]').val().replace(/^\s+|\s+$/g, '') != '') {
          $('#share-form input[name="share"]').removeAttr('disabled');
        } else {
          $('#share-form input[name="share"]').attr('disabled', 'disabled');
        }
      });

      $('#terms').click(function(e) {
        e.preventDefault();
      });
    }
  }
}

function getRedeemDetail() {
  if (typeof Storage != 'undefined') {
    var userId = localStorage.userId;
    if (typeof userId != 'undefined' && userId != 'undefined' &&
            userId != 'null' && userId != null && userId != '') {
      var redeem = jQuery.parseJSON(unescape(localStorage.redeemDetail));
      renderRedeemDetail(redeem);
      $('#back-btn').click(function(e){
        e.preventDefault();
        localStorage.pageType = 'redeem';
        initPage();
      });
    }
  }
}

function renderOfferDetail(offer) {
  var html = '<form id="share-form" type="POST" enctype="multipart/form-data" onsubmit="shareOffer(); return false;" >';
  html += '<div class="add-photo"><img src="img/offer.png" class="add-photo" alt="" id="show-picture" /></div>';
  // TODO
  // html += '<div class="add-photo"><img src="' + offer.imageUrl + '" class="add-photo" alt="" id="show-picture"></div>';
  html += '<div class="add-photo-btn">';
  html += '<input name="source" type="file" id="take-picture" accept="image/*" />';
  html += '</div>';
  html += '<div class="brand">';
  html += offer.merchantName;
  html += '</div><div class="website">';
  html += '<a href="' + offer.merchantUrl + '"><img class="website-img" src="img/ic_web.png" /></a>';
  html += '</div>';
  local = getDisplayLocation(offer.locations);
  if (local != 'undefined') {
    html += '<div class="phone">';
    html += '<a href="tel:' + local.phoneNumber + '"><img class="website-img" src="img/ic_phone.png" /></a>';
    html += '</div><div class="map">';
    html += '<a href="' + generateMapUrl(offer.merchantName, local) + '"><img class="website-img" src="img/ic_map.png" />' + '&nbsp;' + computeDistance(local) + '</a>';
    html += '</div>';
  }
  html += '</div>';
  html += '<div id="share-div" class="form-view">';
  html += '<input name="comment" type="textarea" placeholder="Add Comment..." />';
  html += '<input name="message" type="hidden" value="Visit getkikbak.com for an exclusive offer shared by your friend" />';
  html += '<div id="share-detail-div">';
  html += '<div id="give-detail">';
  html += '<div id="give-detail-summary">';
  html += '<img src="img/ic_gift.png" />';
  html += '<p>' + offer.giftDesc + '</p>';
  html += '</div>';
  html += '<div id="give-detail-detail">';
  html += '<p>' + offer.giftDescOptional + '</p>'; 
  html += '</div>';
  html += '<a href="#" id="terms">Terms and Conditions</a>'
  html += '<input name="share" type="submit" class="btn" value="Give To Friends" disabled />';
  html += '</div>';
  html += '</form>';
  html += '</div>';
  $('#offer-details-view').html(html);
}

function shareOffer() {
  if (typeof Storage != 'undefined') {
    var userId = localStorage.userId;
    $('#share-form input[name="share"]').attr('disabled', 'disabled');
    if (typeof userId != 'undefined' && userId != 'undefined' &&
          userId != 'null' && userId != null && userId != '') {
      var message = $('#share-form input[name="comment"]').val(); 
      var msg = 'Visit getkikbak.com for an exclusive offer shared by your friend';
      if (!$('#take-picture')[0].files || $('#take-picture')[0].files.length == 0) {
        // TODO 
        var req = {};
        req['url'] = 'http://54.244.124.116/m/img/offer.png';
        req['message'] = msg;
        FB.api('/photos', 'post', req, onShareResponse);
      } else {
        var req = new FormData();
        var file =  $('#take-picture')[0].files[0];
        req.append('source', file);
        req.append('message', msg);
        var url='https://graph.facebook.com/photos?access_token=' + localStorage.accessToken + "&message=" + msg;
        $.ajax({
          url: url,
          data: req,
          cache: false,
          contentType: false,
          processData: false,
          type: 'POST',
          success: onShareResponse,
          error: showError
        });
      }
    }
  }
}

function onShareResponse(response) {
  if (response && response.post_id) {
    var offer = jQuery.parseJSON(unescape(localStorage.offerDetail));
    var exp = {};
    local = getDisplayLocation(offer.locations);
    exp['locationId'] = local.locationId;
    exp['merchantId'] = offer.merchantId;
    exp['offerId'] = offer.id;
    // TODO
    exp['fbImageId'] = response.id;
    var message = $('#share-form input[name="comment"]').val(); 
    exp['caption'] = message;
    var data = {};
    data['experience'] = exp;
    var req = {};
    req['ShareExperienceRequest'] = data;
    var str = JSON.stringify(req);
    $.ajax({
      dataType: 'json',
      type: 'POST',
      contentType: 'application/json',
      data: str,
      url: 'kikbak/ShareExperience/' + userId,
      success: function(json) {
        showShareSuccessful();
      },
      error: showError
    });
  } else {
    $('#share-form input[name="share"]').removeAttr('disabled');
    alert(response.error.message);
  }
}

function showShareSuccessful() {
}

function renderRedeemDetail(redeem) {
  // TODO
}

function resizeScroll() {
  if ( typeof getOffersByLocation.ov == 'undefined' ) {
    getOffersByLocation.ov = new iScroll('offer-view');
  } else {
    getOffersByLocation.ov.refresh();
  }
}

function reload() {
  adjustAddPhoto();
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
    wrapperSize = getHeight() - 45 - 45 + 60;
  } else {
    wrapperSize = getHeight() - 45 - 45;
  };
    
  $('.add-photo').css('height', wrapperSize - 200 + 45 + 'px');
  $('#show-picture').css('max-height', wrapperSize - 200 + 45 + 'px');
  $('.wrapper').css('min-height', wrapperSize + 'px');
  $('#footer').css('margin-top', wrapperSize + 45 + 'px');
}

function adjustSuggest() {
  if ($('#suggest-form input[name="name"]').val().replace(/^\s+|\s+$/g, '') != '' &&
      $('#suggest-form input[name="reason"]').val().replace(/^\s+|\s+$/g, '') != '') {
    $('#suggest-form input[name="suggest"]').removeAttr('disabled');
  } else {
    $('#suggest-form input[name="suggest"]').attr('disabled', 'disabled');
  }
}

function iOSversion() {
  if (/iP(hone|od|ad)/.test(navigator.platform)) {
    // supports iOS 2.0 and later: <http://bit.ly/TJjs1V>
    var v = (navigator.appVersion).match(/OS (\d+)_(\d+)_?(\d+)?/);
    return [parseInt(v[1], 10), parseInt(v[2], 10), parseInt(v[3] || 0, 10)];
  }
}

function adjustAddPhoto() {
  var ver = iOSversion();
  if (ver && ver[0] < 6) {
    $('.add-photo-btn').hide(); 
  }
}

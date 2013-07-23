var config = {
  backend: ''
}

$(document).ready(function() {  
  $('body').scrollTop($(document).height());
  $.ajaxSetup({ cache: true });
  $.getScript('//connect.facebook.net/en_US/all.js', function(){
    window.fbAsyncInit = fbInit;
  });
  $('.offer-btn').click(function(){
    if (typeof Storage != 'undefined') {
      localStorage.pageType = 'offer';
      initPage();
    }
  });
  $('.redeem-btn').click(function(){
    if (typeof Storage != 'undefined') {
      localStorage.pageType = 'redeem';
      initPage();
    }
  });
  $('.suggest-btn').click(function(){
    if (typeof Storage != 'undefined') {
      localStorage.pageType = 'suggest';
      initPage();
    }
  });
  setWrapperSize();

  $('#suggest-form input[name="name"]').bind('keyup', adjustSuggest);
  $('#suggest-form input[name="reason"]').bind('keyup', adjustSuggest);

  $('.take-picture').change(function(e) {
    var files = e.target.files;
    var file;
    if (files && files.length > 0) {
      file = files[0];
      try {
        var URL = window.webkitURL || window.URL;
        var imgUrl = URL.createObjectURL(file);
        $('.show-picture').load(function(e){
          URL.revokeObjectURL(this.src);
        });
        $('.show-picture').attr('src', imgUrl);
      } catch (e) {
        try {
          var fileReader = new FileReader();
          fileReader.onload = function (e) {
            $('.show-picture').src = e.target.result;
          }
          fileReader.readAsDataUrl(file);
        } catch (e) {
          alert('Unsupported browser');
        }
      }
    }
    adjustSuggest();
  });

  function updateOrientation() {
    window.scrollTo(0, 1);
  }
  window.addEventListener('orientationchange', updateOrientation);

});

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
    appId: '493383324061333',
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
    if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
      initPosition(initPage);
      return;
    }
  }
  FB.api('/me', function(response) {
    var user = {};
    user['id'] = response.id;
    user['first_name'] = response.first_name;
    user['last_name'] = response.last_name;
    user['name'] = response.name;
    user['link'] = response.link;
    user['gender'] = response.gender;
    user['locale'] = response.locale;
    user['verified'] = response.verified;
    user['timezone'] = response.timezone;
    user['updated_time'] = response.updated_time;
    user['email'] = response.email
    var data = {};
    data['user'] = user;
    var req = {};
    req['RegisterUserRequest'] = data;
    var str = JSON.stringify(req);

    if (typeof Storage != 'undefined') {
      localStorage.userName = response.first_name + ' ' + response.last_name;
    }

    $.ajax({
      dataType: 'json',
      type: 'POST',
      contentType: 'application/json',
      data: str,
      url: config.backend + 'kikbak/user/register/fb/',
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
      url: config.backend + 'kikbak/user/friends/fb/' + userId,
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
  $('#suggest-view').hide();
  $('#offer-btn-div').css('background', 'url("img/btn_normal.png")');
  $('#redeem-btn-div').css('background', 'url("img/btn_normal.png")');
  window.scrollTo(0, 1);
  if (typeof Storage != 'undefined') {
    var code = localStorage.code;
    if (typeof code !== 'undefined' && code !== 'null' && code !== '') {
      claimGift(code);
      return;
    }
    
    var pageType = localStorage.pageType;
    if (pageType === 'redeem') {
      $('#redeem-view').show('');
      $('#redeem-btn-div').css('background', 'url("img/btn_highlighted.png")');
      getRedeems();
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
        initPage();
      });
    } else if (pageType === 'offer') {
      $('#offer-view').show('');
      $('#suggest-btn-div').show('');
      $('#offer-btn-div').css('background', 'url("img/btn_highlighted.png")');
      getOffers();
    } else { //if (pageType === 'offer-detail') {
      $('#offer-details-view').show('');
      $('#back-btn-div').hide('');
      // TODO
      //$('#back-btn-div').show('');
      getOfferDetail();
    }
  }
  setWrapperSize();
  setTimeout(function(){
    reload();
  }, 500);
}

function claimGift(code) {
  var userId = localStorage.userId,
      url = config.backend + 'kikbak/rewards/claim/' + userId + '/' + code;
  $.ajax({
    dataType: 'json',
    type: 'GET',
    contentType: 'application/json',
    url: url,
    success: function(json) {
      localStorage.pageType = 'redeem';
      localStorage.code = null;
      initPage();
    },
    error: showError
  });
}

function getOffers() {
  if (typeof Storage != 'undefined') {
    localStorage.pageType = 'offer';
    var userId = localStorage.userId;

    if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
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
    return '';
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
    url: config.backend + 'kikbak/user/offer/' + userId,
    success: function(json) {
      var offers = json.getUserOffersResponse.offers;
      $('#offer-list').html('');
      $.each(offers, function(i, offer) {
        renderOffer(offer);
      });
      if (offers.length == 0) {
        $("#no-offer-list").show();
      } else {
        $("#no-offer-list").hide();
      }
      $('.offer-details-btn').click(function(e) {
        e.preventDefault();
        if (Storage != 'undefined') {
          localStorage.offerDetail = $(this).attr('data-object');
          localStorage.pageType = 'offer-detail';
        }
        initPage();
      });
    },
    error: showError
  });
}

function getRedeems() {
  if (typeof Storage != 'undefined') {
    var userId = localStorage.userId;

    if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
      var data = {};
      var req = {};
      req['RewardsRequest'] = data;      
      var str = JSON.stringify(req);

      $.ajax({
        dataType: 'json',
        type: 'POST',
        contentType: 'application/json',
        data: str,
        url: config.backend + 'kikbak/rewards/request/' + userId,
        success: function(json) {
          var gifts = json.rewardsResponse.gifts;
          $('#redeem-list').html('');
          $.each(gifts, function(i, gift) {
            renderRedeem(gift);
          });
          $('.redeem-details-btn').click(function(e) {
            e.preventDefault();
            if (Storage != 'undefined') {
              localStorage.redeemDetail = $(this).attr('data-object');
              localStorage.pageType = 'redeem-detail';
            }
            initPage();
          });
        },
        error: showError
      });
    }
  }
}

function renderRedeem(redeem) {
  var html = '<li>';
  
  var json = escape(JSON.stringify(redeem));
  html += '<div class="offer-div">';
  html += '<a href="#" data-object="' + json + '" class="redeem-details-btn clearfix">';
  html += '<img class="offer-background" src="img/offer.png" />';
  // TODO
  // html += '<img class="redeem-background" src="' + json.merchantImageUrl + '" />';
  html += '<div class="offer-background-grad" />';
  html += '</a>';
  html += '<div class="brand">';
  html += redeem.merchant.name;
  html += '</div><div class="website">';
  html += '<a href="' + redeem.merchant.url + '"><img class="website-img" src="img/ic_web.png" /></a>';
  html += '</div>';

  local = getDisplayLocation(redeem.merchant.locations);
  if (local != 'undefined') {
    html += '<div class="phone">';
    html += '<a href="tel:' + local.phoneNumber + '"><img class="website-img" src="img/ic_phone.png" /></a>';
    html += '</div><div class="map">';
    html += '<a href="' + generateMapUrl(redeem.merchant.name, local) + '"><img class="website-img" src="img/ic_map.png" />' + '&nbsp;' + computeDistance(local) + '</a>';
    html += '</div>';
  }
  html += '<div class="seperator"></div>';
  html += '<div class="redeem-desc-div">' + redeem.desc + '</div>';
  html += '<div class="redeem-desc-detail-div">' + redeem.descOptional + '</div>';
  html += '</div>';
  html += '</li>';
  $('#redeem-list').append(html);

}

function getOfferDetail() {
  if (typeof Storage != 'undefined') {
    var userId = localStorage.userId;
    if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
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
    if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
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
  html += '<div class="image-add"><img src="img/offer.png" class="addimg add-photo show-picture" id="show-picture" />';
  // TODO
  // html += '<div class="image-add"><img src="' + offer.imageUrl + '" class="addimg add-photo show-picture" id="show-picture">>';
  html += '<span class="imgshado"></span>';
  html += '<div class="add-photo-btn">';
  html += '<h2>Add your own photo</h2>';
  html += '<div class="camicon"><img src="images/camicon.png">';
  html += '<input name="source" type="file" id="take-picture" class="camicon take-picture" style="height:100px;margin-left:40%;width:20%;opacity:0;" accept="image/*" /></div>';
  html += '</div>';
  html += '<h3>' + offer.merchantName + '</h3>';
  html += '<div class="opt-icon">';
  local = getDisplayLocation(offer.locations);
  if (local != 'undefined') {
    html += '<a href="' + generateMapUrl(offer.merchantName, local) + '"><img class="website-img" src="images/ic_map@2x.png" />' + '&nbsp;' + computeDistance(local) + '</a>';
    html += '<a href="' + offer.merchantUrl + '"><img class="website-img" src="images/ic_web@2x.png" /></a>';
    html += '<a href="tel:' + local.phoneNumber + '"><img class="website-img" src="images/ic_phone@2x.png" /></a>';
  } else {
    html += '<a href="' + offer.merchantUrl + '"><img class="website-img" src="images/ic_web@2x.png" /></a>';
  }
  html += '</div>';
  html += '<input name="comment" type="text" placeholder="Add Comment..." class="addcmt" />';
  html += '<input name="message" type="hidden" value="Visit getkikbak.com for an exclusive offer shared by your friend" />';
  html += '</div>';
  html += '<div class="img-botm-patrn"></div>';
  html += '<div class="gv">';
  html += '<h2><img src="images/ic_gift@2x.png" />';
  html += offer.giftDesc + '</h2>';
  html += '<h4>' + offer.giftDetailedDesc + '</h4>';
  html += '</div>';
  html += '<div class="crt">';
  html += '<h2>' + offer.kikbakDesc + '</h2>';
  html += '<h4>' + offer.kikbakDetailedDesc + '</h4>';
  html += '</div>';
  html += '<div class="crt">';
  html += '<a href="#" id="terms" class="trm" >Terms and Conditions</a>';
  html += '<a href="#" id="learns" class="lrn-mor" >Learn more</a>';
  html += '</div>';
  html += '<input name="share" type="submit" class="btn grd3" value="Give To Friends" disabled />';
  html += '</form>';
  $('#offer-details-view').html(html);
}

function doSuggest() {
  if (typeof Storage != 'undefined') {
    var userId = localStorage.userId;
    $('#share-form input[name="share"]').attr('disabled', 'disabled');
    if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
      var req = new FormData();
      var file =  $('#take-picture-suggest')[0].files[0];
      req.append('source', file);
      var url='https://graph.facebook.com/photos?access_token=' + localStorage.accessToken;
      $.ajax({
        url: url,
        data: req,
        cache: false,
        contentType: false,
        dataType: 'json',
        type: 'POST',
        success: onSuggestResponse,
        error: showError
      });
    }
  }
}

function onSuggestResponse() {
  if (response && response.post_id) {
    // TODO
  } else {
    $('#share-form input[name="suggest"]').removeAttr('disabled');
    alert(response.error.message);
  }
}

function shareOffer() {
  if (typeof Storage != 'undefined') {
    var userId = localStorage.userId;
    $('#share-form input[name="share"]').attr('disabled', 'disabled');
    if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
      var message = $('#share-form input[name="comment"]').val(); 
      var msg = 'Visit getkikbak.com for an exclusive offer shared by your friend';
      if (!$('#take-picture')[0].files || $('#take-picture')[0].files.length == 0) {
        // TODO 
        onShareResponse('http://54.244.124.116/m/img/offer.png');
      } else {
        var req = new FormData();
        var file =  $('#take-picture')[0].files[0];
        req.append('file', file);
        req.append('userId', userId);
        $.ajax({
          url: config.backend + '/s/upload.php',
          data: req,
          cache: false,
          contentType: false,
          dataType: 'json',
          type: 'POST',
          success: function(response) {
            if (response && response.url) {
              onShareResponse(response.url);
            }
          },
          error: showError
        });
      }
    }
  }
}

function onShareResponse(url) {
  var offer = jQuery.parseJSON(unescape(localStorage.offerDetail)),
    exp = {},
    message = $('#share-form input[name="comment"]').val(),
    data = {},
    req = {},
    str;
  local = getDisplayLocation(offer.locations);
  exp['locationId'] = local.locationId;
  exp['merchantId'] = offer.merchantId;
  exp['offerId'] = offer.id;
  exp['fbImageId'] = 0;
  exp['imageUrl'] = url; 
  exp['caption'] = message;
  exp['type'] = 'email';
  data['experience'] = exp;
  req['ShareExperienceRequest'] = data;
  str = JSON.stringify(req);
  $.ajax({
    dataType: 'json',
    type: 'POST',
    contentType: 'application/json',
    data: str,
    url: config.backend + 'kikbak/ShareExperience/' + localStorage.userId,
    success: function(json) {
      if (json && json.shareExperienceResponse && json.shareExperienceResponse.referrerCode) {
        showShareSuccessful(json.shareExperienceResponse.referrerCode, message, url);
      } else {
        showError();
      }
    },
    error: showError
  });
}

function showShareSuccessful(code, msg, url) {
  var data = {
    'name': localStorage.userName,
    'code': code,
    'desc': msg,
    'url': url
  };
  if (getBrowserName() == 'Safari') {
    $.ajax({
      type: 'GET',
      data: data,
      dataType: 'json',
      url: '/s/email.php',
      success: function(json) {
        window.location.href = 'mailto:?content-type=text/html&subject=' + json.title + '&body=' + json.body;
      },
      error: showError
    });
  } else {
    $.ajax({
      type: 'GET',
      data: data,
      dataType: 'json',
      url: '/s/sms.php',
      success: function(json) {
        window.location.href = 'sms://?&body=' + json.body;
      },
      error: showError
    });
  }
}

function renderRedeemDetail(redeem) {
  var html = '';
  html += '<div class="add-photo"><img src="img/offer.png" class="add-photo" alt="" id="show-picture" /></div>';
  // TODO
  // html += '<div class="add-photo"><img src="' + redeem.imageUrl + '" class="add-photo" alt="" id="show-picture"></div>';
  html += '<div class="brand">';
  html += redeem.merchant.name;
  html += '</div><div class="website">';
  html += '<a href="' + redeem.merchant.url + '"><img class="website-img" src="img/ic_web.png" /></a>';
  html += '</div>';
  local = getDisplayLocation(redeem.merchant.locations);
  if (local != 'undefined') {
    html += '<div class="phone">';
    html += '<a href="tel:' + local.phoneNumber + '"><img class="website-img" src="img/ic_phone.png" /></a>';
    html += '</div><div class="map">';
    html += '<a href="' + generateMapUrl(redeem.merchant.name, local) + '"><img class="website-img" src="img/ic_map.png" />' + '&nbsp;' + computeDistance(local) + '</a>';
    html += '</div>';
  }
  html += '</div>';
  html += '<div class="form-view">';
  html += '<div id="share-detail-div">';
  html += '<div id="friend-name">' + redeem.friendName + '</div>';
  html += '<div id="friend-image">';
  html += '<img src="https://graph.facebook.com/' + redeem.fbFriendId + '/picture" />';
  html += '</div>';
  html += '<div id="friend-comment">';
  html += redeem.caption;
  html += '</div>';
  html += '<div id="give-detail">';
  html += '<div id="give-detail-summary">';
  html += '<p>' + redeem.desc + '</p>';
  html += '</div>';
  html += '<div id="give-detail-detail">';
  html += '<p>' + redeem.descOptional + '</p>'; 
  html += '</div>';
  html += '<a href="#" id="terms">Terms and Conditions</a>';
  html += '<a href="#" id="redeem-btn" class="btn btn-large btn-success">Redeem</a>';
  html += '</div>';
  html += '</div>';
  $('#redeem-details-view').html(html);
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
}

function adjustSuggest() {
  if ($('#suggest-form input[name="name"]').val().replace(/^\s+|\s+$/g, '') != '' &&
      $('#suggest-form input[name="reason"]').val().replace(/^\s+|\s+$/g, '') != '' &&
      $('#take-picture-suggest')[0].files && $('#take-picture-suggest')[0].files.length > 0) {
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

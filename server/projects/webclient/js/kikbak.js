var config = {
  backend: '',
  appId: 493383324061333,
  landing: 'http://test.kikbak.me/m/kikbak/landing.html?code=',
  latitude: 37.42082770,
  longitude: -122.13043270
}

var s = (Storage) ? localStorage : {};
var req = 0;

$(document).ready(function() {
  if (s.pageType == 'offer-detail') {
    s.pageType = 'offer';
  }
  $(document).ajaxStart(function (){
    req++;
    $('#spinner').show();
  });
  $(document).ajaxComplete(function (){
    if (--req == 0) {
      $('#spinner').hide();
    }
  });
  $('body').scrollTop($(document).height());
  $.ajaxSetup({ cache: true });
  $.getScript('//connect.facebook.net/en_US/all.js', function(){
    window.fbAsyncInit = fbInit;
  });
  $('.offer-btn').click(function(){
    s.pageType = 'offer';
    initPage();
  });
  $('.redeem-btn').click(function(){
    s.pageType = 'redeem';
    initPage();
  });
  $('.suggest-btn').click(function(){
    s.pageType = 'suggest';
    initPage();
  });
  $('.offer-list-btn').click(function(){
    s.pageType = 'offer';
    initPage();
  });
  setWrapperSize();

  $('#suggest-form input[name="name"]').bind('keyup', adjustSuggest);
  $('#suggest-form textarea[name="reason"]').bind('keyup', adjustSuggest);
  
  $('#share-facebook').click(shareViaFacebook);
  $('#share-email').click(shareViaEmail);
  $('#share-sms').click(shareViaSms);

  $(".popup-close-btn").click(function() {
    $('.popup').hide();
  });

  $('#suggest-form input[name="source"]').change(function(e) {
    var files = e.target.files;
    var file;
    
    if (files && files.length > 0) {
      file = files[0];
      try {
        var URL = window.webkitURL || window.URL;
        var imgUrl = URL.createObjectURL(file);
        $('#show-picture-suggest').load(function(e){
          URL.revokeObjectURL(this.src);
        });
        $('#show-picture-suggest').attr('src', imgUrl);
        $('#add-photo-suggest').hide();
      } catch (e) {
        try {
          var fileReader = new FileReader();
          fileReader.onload = function (e) {
            $('#show-picture-suggest').src = e.target.result;
          }
          fileReader.readAsDataUrl(file);
        } catch (e) {
          alert('Unsupported browser');
        }
      }
    }
    adjustSuggest();
  });
  
  
  $('#claim-credit-form input').bind('keyup', function() {
    var form = $('#claim-credit-form');
    var valid = true;
    $.each(form.serializeArray(), function() { 
      if (this.value.replace(/^\s+|\s+$/g, '') == '') {
        valid = false;
      }
    });
    if (valid) {
      $('#claim-credit-btn').removeAttr('disabled');
    } else {
      $('#claim-credit-btn').attr('disabled', 'disabled');
    }
  });
  
  $('#claim-credit-btn').click(function() {
    var o = {};
    var form = $('#claim-credit-form');
    $.each(form.serializeArray(), function() { 
      o[this.name] = this.value;
    });
    var claim = {};
    claim['claim'] = o;
    var req = {};
    req['ClaimCreditRequest'] = claim;
    var str = JSON.stringify(req);
    $.ajax({
      dataType: 'json',
      type: 'POST',
      contentType: 'application/json',
      data: str,
      url: config.backend + 'kikbak/rewards/claim/' + s.userId + '/',
      success: function(json) {
        $('#success-popup h3').html('Your reward claim has been submitted');
        $('#success-popup p').html('');
        $('#success-popup').show();
      },
      error: showError
    });
    return false;
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
  $('#spinner h2').html('Waiting');
  alert("Service is unavailable. Please try again later.");
}

function fbInit() {
  FB.init({
    appId: '493383324061333',
    channelUrl: '/m/channel.html',
    status: true,
    cookie: true,
    xfbml: true
  });
  initPosition(initPage);
}

function connectFb(accessToken) {
  s.accessToken = accessToken;
  var userId = s.userId;
  if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
    initPosition(initPage);
    return;
  }
  var user = {};
  user['access_token'] = accessToken;
  var data = {};
  data['user'] = user;
  var req = {};
  req['RegisterUserRequest'] = data;
  var str = JSON.stringify(req);

  $.ajax({
    dataType: 'json',
    type: 'POST',
    contentType: 'application/json',
    data: str,
    url: config.backend + 'kikbak/user/register/fb/',
    success: function(json) {
      updateFbFriends(json.registerUserResponse.userId.userId, initPage);
    },
    error: showError
  });
}

function updateFbFriends(userId, cb) {
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
        s.userId = userId;
        cb();
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
  $('#redeem-gift-success').hide();
  $('#offer-btn-div').css('background', 'url("img/btn_normal.png")');
  $('#redeem-btn-div').css('background', 'url("img/btn_normal.png")');
  window.scrollTo(0, 1);
  
  var code = s.code;
  if (typeof code !== 'undefined' && code !== 'null' && code !== '') {
    loginFb();
    claimGift(code);
    return;
  }

  var pageType = s.pageType;
  if (pageType === 'redeem') {
    loginFb();
    $('#redeem-view').show('');
    $('#redeem-btn-div').css('background', 'url("img/btn_highlighted.png")');
    $('#heading').html('Kikbak');
    getRedeems();
  } else if (pageType === 'redeem-gift-detail') {
    loginFb();
    $('#redeem-details-view').show('');
    $('#back-btn-div').show('');
    $('#heading').html('Redeem');
    getRedeemGiftDetail();
  } else if (pageType === 'redeem-credit-detail') {
    loginFb();
    $('#redeem-details-view').show('');
    $('#back-btn-div').show('');
    $('#heading').html('Redeem');
    getRedeemCreditDetail();
  } else if (pageType === 'suggest') {
    loginFb();
    $('#suggest-view').show('');
    $('#back-btn-div').show('');
    $('#heading').html('Gift');
    $('#show-picture-suggest').removeAttr('src');
    $('#add-photo-suggest').show();
    $('#back-btn').unbind();
    $('#back-btn').click(function(e) {
      e.preventDefault();
      s.pageType = 'offer';
      initPage();
    });
  } else if (pageType === 'offer-detail') {
    $('#offer-details-view').show('');
    $('#back-btn-div').hide('');
    $('#heading').html('Gift');
    $('#back-btn-div').show('');
    getOfferDetail();
  } else {// Default
    $('#offer-view').show('');
    $('#suggest-btn-div').show('');
    $('#heading').html('Gift');
    $('#offer-btn-div').css('background', 'url("img/btn_highlighted.png")');
    getOffers();
  }

  setWrapperSize();
  setTimeout(function(){
    reload();
  }, 500);
}

function claimGift(code) {
  var userId = s.userId,
      url = config.backend + 'kikbak/rewards/claim/' + userId + '/' + code;
  s.code = null;
  $.ajax({
    dataType: 'json',
    type: 'GET',
    contentType: 'application/json',
    url: url,
    success: function(json) {
      s.pageType = 'redeem';
      initPage();
    },
    error: function() {
      initPage();
    }
  });
}

function getOffers() {
  s.pageType = 'offer';
  var userId = 0;

  if ( typeof userId !== 'undefined' && userId !== null && userId !== '') {
    if ( typeof initPage.p !== 'undefined') {
      getOffersByLocation(userId, initPage.p);
    } else {
      getOffersByLocation(userId, null);
    }
  }
}

function renderOffer(offer) {
  var html = '';
  
  var json = escape(JSON.stringify(offer));
  html += '<div class="imglist"><a href="#" data-object="' + json + '" class="offer-details-btn clearfix">';
  html += '<span class="imgshado"></span><div class="lstimg">';
  html += '<img src="' + offer.offerImageUrl + '" />';
  html += '</a></div>';
  html += '<h3>' + offer.merchantName + '</h3>';
  
  var local = getDisplayLocation(offer.locations);
  if (local != 'undefined') {
    html += '<div class="opt-icon"><a href="' + generateMapUrl(offer.merchantName, local) + '"><img src="images/ic_map@2x.png">' + '&nbsp;' + computeDistance(local) + '</a>';
    html += '<a href="' + offer.merchantUrl + '"><img src="images/ic_web@2x.png" /></a>';
    html += '<a href="tel:' + local.phoneNumber + '"><img src="images/ic_phone@2x.png" /></a>';
  } else {
    html += '<a href="' + offer.merchantUrl + '"><img src="images/ic_web@2x.png" /></a>';
  }
  html += '</div>';
  html += '<div class="ribn"><img src="images/ribncorn.png" class="crn">';
  var gift = "<span>GIVE</span>";
  gift += offer.giftType == 'percentage' ? offer.giftValue + "% off" :
          "$" + offer.giftValue;
  html += '<div class="giv grd3">' + gift + '</div>';
  html += '<img src="images/ribnaro.png">';
  html += '<div class="get"><span>GET</span>$' + offer.kikbakValue + '</div>';
  html += '</div></div>';
  
  $('#offer-list').append(html);
}

function generateMapUrl(name, local) {
  var escapedName = encodeURIComponent(name);
  var url = 'https://maps.google.com/maps?q=' + escapedName + '%40' + local.latitude + ',' + local.longitude;
  return url;
}

function computeDistanceDigit(local) {
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
  return parseInt(d / 10) + '.' + (d % 10);
}

function computeDistance(local) {
  return computeDistanceDigit(local) + ' mile';
}

function getDisplayLocation(locations) {
  return locations[0];
}

function getOffersByLocation(userId, position) {
  var location = {};
  if (position != null) {
    location['longitude'] = config.longitude ? config.longitude : position.longitude;
    location['latitude'] = config.latitude ? config.latitude : position.latitude;
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
      s.offerCount = offers.length;
      
      if (offers.length == 1) {
        s.offerDetail = escape(JSON.stringify(offers[0]));
        s.pageType = 'offer-detail';
        initPage();
        $('back-btn').hide();
      }
      
      var availCount = 0;
      var availOffer = null;
      $.each(offers, function(i, offer) {
        var local = getDisplayLocation(offer.locations);
        if (computeDistanceDigit(local) < 0.5) {
          ++availCount;
          availOffer = offer;
        }
      });
      
      if (availCount == 1) {
        s.offerDetail = escape(JSON.stringify(availOffer));
        s.pageType = 'offer-detail';
        initPage();
      }
      
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
        s.offerDetail = $(this).attr('data-object');
        s.pageType = 'offer-detail';
        initPage();
      });
    },
    error: showError
  });
}

function getRedeems() {
  var userId = s.userId;

  if ( typeof userId !== 'undefined' && userId !== null && userId !== '') {
    var data = {};
    var req = {};
    req['RewardsRequest'] = data;
    var str = JSON.stringify(req);

    $.ajax({
      dataType : 'json',
      type : 'POST',
      contentType : 'application/json',
      data : str,
      url : config.backend + 'kikbak/rewards/request/' + userId,
      success : function(json) {
        var gifts = json.rewardsResponse.gifts;
        var credits = json.rewardsResponse.credits;
        var giftCollection = {};
        var creditCollection = {};
        $('#redeem-list').html('');
        $.each(gifts, function(i, gift) {
          var mid = gift.merchant.id;
          if (giftCollection[mid]) {
            giftCollection[mid].push(gift);
          } else {
            giftCollection[mid] = [gift];
          }
        });
        $.each(credits, function(i, credit) {
          var mid = credit.merchant.id;
          if (creditCollection[mid]) {
            creditCollection[mid].push(credit);
          } else {
            creditCollection[mid] = [credit];
          }
        });

        var mark = {};
        for (var mid in giftCollection) {
          renderRedeem(giftCollection[mid], creditCollection[mid]);
          mark[mid] = 1;
        }
        for (var mid in creditCollection) {
          if (!mark[mid]) {
            renderRedeem(giftCollection[mid], creditCollection[mid]);
          }
        }

        $('.redeem-gift-btn').click(function(e) {
          e.preventDefault();
          var gifts = jQuery.parseJSON(unescape($(this).attr('data-object')));
          $('#friend-popup h1').html('');
          var list = $('#friend-list');
          list.html(gifts.desc);
          if (gifts && gifts.shareInfo.length > 1) {
            for (var shareInfo in gifts.shareInfo.length) {
              var li = '<li class="frd-bx" >';
              li += '<img src="https://graph.facebook.com/' + shareInfo.fbFriendId + '/picture?type=square">';
              li += '<h2>' + shareInfo.friendName + '</h2>';
              var data = {
                'gift': gifts,
                'shareInfo': shareInfo
              };
              var j = escape(JSON.stringify(data));
              li += '<a href="#" class="nxtt select-gift-btn" data-object="' + j + '"><img src="images/nxt-aro.png"></a></li>';
              list.append(li);
            }

            $('#friend-popup').show();
            $('.select-gift-btn').click(function() {
              $('#friend-popup').hide();
              s.giftDetail = $(this).attr('data-object');
              s.pageType = 'redeem-gift-detail';
              initPage();
            });

          } else if (gifts) {
            var data = {
              'gift': gifts,
              'shareInfo': gifts.shareInfo[0]
            };
            var j = escape(JSON.stringify(data));
            s.giftDetail = j;
            s.pageType = 'redeem-gift-detail';
            initPage();
          }
        });
        $('.redeem-credit-btn').click(function(e) {
          e.preventDefault();
          var credits = jQuery.parseJSON(unescape($(this).attr('data-object')));
          s.creditDetail = escape(JSON.stringify(credits));
          s.pageType = 'redeem-credit-detail';
          initPage();
        });
      },
      error : showError
    });
  }
}

function renderRedeem(gifts, credits) {
  var m = credits ? credits[0].merchant : gifts[0].merchant;
  var imgUrl = credits ? credits[0].imageUrl : gifts[0].defaultGiveImageUrl;
  
  var html = '<div class="rdem-bx"><div class="redeem"><span class="imgshado"></span><div class="lstimg">';
  html += '<img class="redeem-background" src="' + imgUrl + '" />';  html += '<a href="' + m.url + '"><img class="website-img" src="img/ic_web.png" /></a>';
  html += '</div><h3>' + m.name + '</h3><div class="opt-icon">';
  local = getDisplayLocation(m.locations);
  if (local != 'undefined') {
    html += '<a href="tel:' + local.phoneNumber + '"><img src="images/ic_phone@2x.png" /></a>';
    html += '<a href="' + m.url + '"><img src="images/ic_web@2x.png" /></a>';
    html += '<a href="' + generateMapUrl(m.name, local) + '"><img src="images/ic_map@2x.png" />' + '&nbsp;' + computeDistance(local) + '</a>';
  } else {
    html += '<a href="' + m.url + '"><img src="images/ic_web@2x.png" /></a>';
  }

  var valid = false;
  html += '</div></div><div class="lst-dtl">';
  if (gifts) {
    var g = gifts[0];
    if (g.validationType === 'barcode') {
      var json = escape(JSON.stringify(g));
      var style = credits ? ' lft-bdr' : '';
      html += '<a href="#" data-object="' + json + '" class="redeem-gift-btn clearfix">';
      html += '<div class="lft-dtl' + style + '"><span>USE RECEIVED GIFT </span><h2>' + g.desc + '</h2>';
      html += '<img style="width:64px;height:64px;" src="https://graph.facebook.com/' + g.shareInfo[0].fbFriendId + '/picture?type=square">';
      if (gifts.length > 1) {
        html += '<img src="images/actor-bk.png" class="actbrd">';
      }
      html += '</div>';
      html += '</a>';
      valid = true;
    } 
  }
  if (credits) {
    var c = credits[0];
    if (c.rewardType === 'gift_card') {
      var json = escape(JSON.stringify(c));
      html += '<a href="#" data-object="' + json + '" class="redeem-credit-btn clearfix">';
      html += '<div class="rit-dtl"><span>CLAIM REWARD</span><h2>' + c.desc + '</h2></div>';
      html += '</a>';
      valid = true;
    }
  }
  html += '</div>';
  
  if (valid) {
    $('#redeem-list').append(html);
  }
}

function getOfferDetail() {
  var userId = 0;
  // s.userId;
  if ( typeof userId !== 'undefined' && userId !== null && userId !== '') {
    var offer = jQuery.parseJSON(unescape(s.offerDetail));
    renderOfferDetail(offer);

    $('#back-btn').unbind();
    $('#back-btn').click(function(e) {
      e.preventDefault();
      s.pageType = 'offer';
      initPage();
    });

    $('#take-picture').change(function(e) {
      var icon = $('.camicon');
      var files = e.target.files;
      var file;

      if (files && files.length > 0) {
        file = files[0];
        try {
          var URL = window.webkitURL || window.URL;
          var imgUrl = URL.createObjectURL(file);
          $('#show-picture').load(function(e) {
            URL.revokeObjectURL(this.src);
          });
          $('#show-picture').attr('src', imgUrl);
          icon.removeClass('camicon');
          icon.addClass('smallcamicon');
          $('#take-photo-header').hide();
        } catch (e) {
          try {
            var fileReader = new FileReader();
            fileReader.onload = function(e) {
              $('#show-picture').src = e.target.result;
            }
            fileReader.readAsDataUrl(file);
          } catch (e) {
            alert('Unsupported browser');
          }
        }
      }
    });
  }
}

function getRedeemGiftDetail() {
  var userId = s.userId;
  if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
    var gifts = jQuery.parseJSON(unescape(s.giftDetail));
    renderRedeemGiftDetail(gifts);
    $('#back-btn').unbind();
    $('#back-btn').click(function(e){
      e.preventDefault();
      s.pageType = 'redeem';
      initPage();
    });
  }
}

function getRedeemCreditDetail() {
  var userId = s.userId;
  if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
    var credit = jQuery.parseJSON(unescape(s.creditDetail));
    renderRedeemCreditDetail(credit);
    $('#back-btn').unbind();
    $('#back-btn').click(function(e){
      e.preventDefault();
      s.pageType = 'redeem';
      initPage();
    });
  }
}

function renderOfferDetail(offer) {
  var html = '<form id="share-form" type="POST" enctype="multipart/form-data" onsubmit="shareOffer(); return false;" >';
  html += '<div class="image-add"><img src="' + offer.offerImageUrl + '" class="addimg add-photo show-picture" id="show-picture">';
  html += '<span class="imgshado"></span>';
  html += '<div class="add-photo-btn">';
  html += '<h2 id="take-photo-header">Add your own photo</h2>';
  html += '<div class="camicon"><img src="images/camicon.png">';
  html += '<input name="source" type="file" id="take-picture" class="camicon take-picture" style="height:60px;width:100%;opacity:0;" accept="image/*" /></div>';
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
  html += '<input name="comment" placeholder="Add Comment..." class="addcmt" />';
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
  html += '<a href="#" class="trm" onclick="showTerms(\'' + offer.tosUrl + '\')" >Terms and Conditions</a>';
  html += '</div>';
  var userId = s.userId;
  if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
    html += '<input name="share" type="submit" class="btn grd3 botm-position" value="Give To Friends" />';
  } else {
    html += '<input name="share" type="submit" class="btn grd3" value="Connect with Facebook to share" />';
    html += '<div class="crt">';
    html += '<p><font size="2">We use Facebook to make it easy for you to store, redeem, and share gifts.  We will never post on Facebook with your permission.</font></p>';
    html += '</div>';
  }
  html += '</form>';
  $('#offer-details-view').html(html);
  
  var options = '';
  $.each(offer.locations, function(i, l) {
    var selected = '';
    if (i === 0) {
      selected = ' selected';
    }
    var addr2 = l.address2 ? l.address2 : '';
    options += '<option value="' + l.locationId + '" ' + selected + '>' + l.address1 + ' ' + addr2 + ', ' + l.city + '</option>';
  });
  $('#location-sel').html(options);
  if (offer.locations.length <= 1) {
    $('#location-sel-div').hide();
  }
}

function showTerms(url) {
  $('#terms iframe').attr('src', url);
  $('#terms').show();
}

function doSuggest() {
  var userId = s.userId;
  $('#suggest-form input[name="suggest"]').attr('disabled', 'disabled');
  if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
    var req = new FormData();
    var file =  $('#take-picture-suggest')[0].files[0];
    req.append('file', file);
    req.append('userId', userId);
    $.ajax({
      url : config.backend + '/suggest/upload.php',
      data : req,
      processData : false,
      cache : false,
      contentType : false,
      dataType : 'json',
      type : 'POST',
      success : function(response) {
        if (response && response.url) {
          onSuggestResponse(response.url);
        } else {
          showError();
        }
      },
      error : showError
    });
  }
}

function onSuggestResponse(url) {
  var userId = s.userId;
  if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
    var business = {};
    business['business_name'] = $('#suggest-form input[name="name"]').val();
    business['why'] = $('#suggest-form input[name="reason"]').val();
    business['image_url'] = url; 
    var data = {};
    data['business'] = business;
    var req = {};
    req['SuggestBusinessRequest'] = data;
    var str = JSON.stringify(req);
    $.ajax({
      url : config.backend + 'kikbak/suggest/business/' + userId,
      dataType: 'json',
      type: 'POST',
      contentType: 'application/json',
      data: str,
      success : function(response) {
        s.pageType = 'offer';
        initPage();
      },
      error : showError
    });
  }
}

function shareOffer() {
  var userId = s.userId;
  if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
    updateFbFriends(userId, function() {
      var message = $('#share-form input[name="comment"]').val();
      var msg = 'Visit getkikbak.com for an exclusive offer shared by your friend';
      if (!$('#take-picture')[0].files || $('#take-picture')[0].files.length == 0) {
        var src = $('#share-form .image-add img').attr('src');
        onShareResponse(src);
      } else {
        var req = new FormData();
        var file = $('#take-picture')[0].files[0];
        req.append('file', file);
        req.append('userId', userId);
        $.ajax({
          url : config.backend + '/s/upload.php',
          data : req,
          processData : false,
          cache : false,
          contentType : false,
          dataType : 'json',
          type : 'POST',
          success : function(response) {
            if (response && response.url) {
              onShareResponse(response.url);
            }
          },
          error : showError
        });
      }
    });
  } else {
    loginFb();
  }
}

function loginFb() {
  var userId = s.userId;
  if ( typeof userId == 'undefined' || userId == null || userId == '') {
    FB.login(function(response) {
      if (response.status === 'connected') {
        connectFb(response.authResponse.accessToken);
      } else if (response.status === 'not_authorized') {
        FB.login();
      } else {
        FB.login();
      }
    }, {
      scope : "email,read_friendlists,publish_stream,publish_actions"
    });
  }
}

function onShareResponse(url) {
  $('#share-help-form input[name="url"]').val(url);
  $('#share-popup').show();
}
  
function doShare(cb, type) {
  $('.popup').hide();
  $('#spinner h2').html('Sharing Gift');
  $('#spinner').show();
  
  var offer = jQuery.parseJSON(unescape(s.offerDetail)),
    exp = {},
    message = $('#share-form input[name="comment"]').val(),
    data = {},
    req = {},
    url = $('#share-help-form input[name="url"]').val(),
    locationId = $('#location-sel').val(),
    str;
  
  $('#share-form input[name="comment"]').val('');
  
  if (typeof locationId === 'undefined') {
    locationId = 0;
  }
    
  exp['merchantId'] = offer.merchantId;
  exp['offerId'] = offer.id;
  exp['imageUrl'] = url; 
  exp['caption'] = message;
  exp['type'] = type;
  exp['locationId'] = locationId;
  exp['platform'] = /iP(hone|od|ad)/.test(navigator.platform) ? 'ios' : 'android';
  exp['employeeId'] = $('#share-help-form input[name="associateName"]').val();
  data['experience'] = exp;
  req['ShareExperienceRequest'] = data;
  str = JSON.stringify(req);
  
  $('#share-help-form input[name="storeId"]').val('');
  
  $.ajax({
    dataType: 'json',
    type: 'POST',
    contentType: 'application/json',
    data: str,
    url: config.backend + 'kikbak/ShareExperience/' + s.userId,
    success: function(json) {
      if (json && json.shareExperienceResponse && json.shareExperienceResponse.referrerCode) {
        cb(json.shareExperienceResponse.referrerCode, message, url, json.shareExperienceResponse);
      } else {
        showError();
      }
    },
    error: showError
  });
}

function shareViaSms() {
  doShare(function(code, msg, url, resp) {
    $('#spinner h2').html('Waiting');
    if (--req == 0) {
      $('#spinner').hide();
    }
    setTimeout(function(){
      $('#success-popup h3').html('You have shared a gift');
      $('#success-popup p').html('We will notify you when a friend uses your gift and you earn a reward');
      $('#success-popup').show();
    }, 200);
    window.location.href = 'sms://?body=' + encodeURIComponent(resp.template.body);
  }, 'sms');
}

function shareViaEmail() {
  $('#spinner h2').html('Waiting');
  doShare(function(code, msg, url, resp) {
    if (--req == 0) {
      $('#spinner').hide();
    }
    setTimeout(function(){
      $('#success-popup h3').html('You have shared a gift');
      $('#success-popup p').html('We will notify you when a friend uses your gift and you earn a reward');
      $('#success-popup').show();
    }, 200);
    window.location.href = 'mailto:?content-type=text/html&subject=' + encodeURIComponent(resp.template.subject) 
        + '&body=' + encodeURIComponent(resp.template.body);
  }, 'email');
}

function shareViaFacebook() {
  $('#spinner h2').html('Sharing gift');
  doShare(function(code, msg, imageUrl, resp) {
    var fbUrl = config.landing + code;
    var o = {
      'app_id' : config.appId,
      'url' : fbUrl.replace(/\//g, '\/'),
      'title': '\"' + encodeURIComponent('title') + '\"',
      'description': '\"' + encodeURIComponent('desc') + '\"',
    };
    var req = {
      'access_token' : s.accessToken,
      //'privacy': '{"value":"all_friends"}',
      'object' : JSON.stringify(o),
      'method' : 'POST',
      'format' : 'json'
    };
    var url = 'https://graph.facebook.com/me/objects/referredlabs:coupon';
    $.ajax({
      url : url,
      data : req,
      cache : false,
      contentType : false,
      dataType : 'json',
      type : 'GET',
      success : function(response) {
        if (response && response.id) {
          var req = {
            'access_token' : s.accessToken,
            'method' : 'POST',
            'coupon' : fbUrl,
            'fb:explicitly_shared' : 'true',
            'format' : 'json'
          };
          var url = 'https://graph.facebook.com/me/referredlabs:share';
          $.ajax({
            url : url,
            data : req,
            cache : false,
            contentType : false,
            dataType : 'json',
            type : 'GET',
            success : function(response) {
              $('#spinner h2').html('Waiting');
              $('#success-popup h3').html('You have shared a gift');
              $('#success-popup p').html('We will notify you when a friend uses your gift and you earn a reward');
              $('#success-popup').show();
            },
            error : function() {
              showError();
            }
          });
        } else {
          showError();
        }
      },
      error : function() {
        showError();
      }
    }); 

  }, 'fb');
}

function encodeQueryData(data) {
   var ret = [];
   for (var d in data)
    ret.push(encodeURIComponent(d) + "=" + encodeURIComponent(data[d]));
   return ret.join("&");
}

function renderRedeemGiftDetail(data) {
  var gift = data.gift;
  var share = data.shareInfo;
  var html = '';
  html += '<div class="image-add rdme"><img src="' + share.imageUrl + '" class="addimge"><span class="imgshado"></span>';
  html += '<h3>' + gift.merchant.name + '</h3>';
  html += '<div class="opt-icon">';
  local = getDisplayLocation(gift.merchant.locations);
  if (local != 'undefined') {
    html += '<a href="' + generateMapUrl(gift.merchant.name, local) + '"><img src="images/ic_map@2x.png" />' + '&nbsp;' + computeDistance(local) + '</a>';
    html += '<a href="' + gift.merchant.url + '"><img src="images/ic_web@2x.png" /></a>';
    html += '<a href="tel:' + local.phoneNumber + '"><img src="images/ic_phone@2x.png" /></a>';
  } else {
    html += '<a href="' + gift.merchant.url + '"><img src="images/ic_web@2x.png" /></a>';
  }
  html += '</div></div>';
  html += '<div class="gvrdm">';
  html += '<img src="https://graph.facebook.com/' + share.fbFriendId + '/picture?type=square">';
  html += '<div class="avtr-cmnt">';
  html += '<h2>' + share.friendName + '</h2>';
  html += '<h2>' + share.caption + '</h2>';
  html += '</div></div>';
  html += '<div class="gvrdm-botm-patrn"></div>';
  html += '<div class="crt">';
  html += '<h2>' + gift.desc + '</h2>';
  html += '<h4>' + gift.detailedDesc + '</h4>';
  html += '<div class="crt">';
  html += '<a href="#" class="trm" onclick="showTerms(\'' + gift.tosUrl + '\')" >Terms and Conditions</a>';
  html += '</div>';
  html += '<button id="redeem-gift-instore-btn" class="btn grd3 botm-position">Redeem now in store</button>';
  
  $('#redeem-details-view').html(html);
  
  $('#redeem-gift-instore-btn').click(function() {
    doRedeemGift(gift);
  });
}

function doRedeemGift(gift) {
  var imgUrl = config.backend + 'kikbak/rewards/generateBarcode/' + s.userId + '/' + gift.shareInfo[0].allocatedGiftId + '/200/300/';
  $('#redeem-gift-success .pg-hedng').html(gift.merchant.name);
  $('#redeem-gift-success .cd-br-stin h1').html(gift.desc);
  $('#redeem-gift-success .cd-br-stin h3').html(gift.detailedDesc);
  $('#redeem-gift-success img').attr('src', imgUrl);
  $('#redeem-details-view').hide();
  $('#redeem-gift-success').show();
}

function renderRedeemCreditDetail(credit) {
  var html = '';
  html += '<div class="image-add rdme"><img src="' + gift.imageUrl + '" class="addimge"><span class="imgshado"></span>';
  html += '<h3>' + credit.merchant.name + '</h3>';
  if (credit.redeemedGiftsCount) {
    html += '<div class="msgg">Your gift has been redeemed by ' + credit.redeemedGiftsCount + ' friend' + (credit.redeemedGiftsCount > 1 ? 's' : '') + '</div>';
  }
  html += '</div>';
  html += '<div class="gv">';
  html += '<p>You have a reward ready to be claimed!</p>';
  html += '</div>';
  html += '<div class="crt">';
  html += '<h1>$' + credit.value.toFixed(2) + '</h1>';
  html += '<h5>' + credit.rewardType + '</h5>';
  html += '<a href="#" class="trm" onclick="showTerms(\'' + credit.tosUrl + '\')">Terms and Conditions</a>';
  html += '</div>';
  html += '<button id="claim-credit-form-btn" class="btn grd3 botm-position">Claim reward now</button>';
  
  $('#redeem-details-view').html(html);
  
  $("#claim-credit-form-btn").click(function() {
    claimCreditForm(credit);
  });
}

function claimCreditForm(credit) {
  $('#redeem-details-view').hide();
  
  $('#claim-credit-form .pg-hedng').html(credit.merchant.name);
  $('#claim-credit-form .hedng h1').html('$' + credit.value.toFixed(2));
  $('#claim-credit-form .hedng h3').html(credit.rewardType);
  $('#claim-credit-form input[name="creditId"]').val(credit.id);
  $('#claim-credit-btn').attr('disabled', 'disabled');
  
  $('#claim-credit-div').show();
  
  $('#back-btn').unbind();
  $('#back-btn').click(function(e) {
    e.preventDefault();
    $('#claim-credit-div').hide();
    s.pageType = 'redeem-credit-detail';
    initPage();
  })
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
    $('#share-sms-div').hide();
  } else {
    wrapperSize = getHeight() - 45 - 45;
  };
    
  $('.wrapper').css('min-height', wrapperSize + 'px');
}

function adjustSuggest() {
  if ($('#suggest-form input[name="name"]').val().replace(/^\s+|\s+$/g, '') != '' &&
      $('#suggest-form textarea[name="reason"]').val().replace(/^\s+|\s+$/g, '') != '' &&
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

var config = {
  backend: '',
  appId: 493383324061333
}

$(document).ready(function() {
  $(document).ajaxStart(function (){
    $('#spinner').show();
  });
  $(document).ajaxComplete(function (){
    $('#spinner').hide();
  });
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
  
  $('#share-facebook').click(shareViaFacebook);
  $('#share-email').click(shareViaEmail);
  $('#share-sms').click(shareViaSms);

  $(".popup-close-btn").click(function() {
    $('.popup').hide();
  });

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
      url: config.backend + 'kikbak/rewards/claim/' + localStorage.userId + '/',
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
      $('#heading').html('Kikbak');
      getRedeems();
    } else if (pageType === 'redeem-gift-detail') {
      $('#redeem-details-view').show('');
      $('#back-btn-div').show('');
      $('#heading').html('Redeem');
      getRedeemGiftDetail();
    } else if (pageType === 'redeem-credit-detail') {
      $('#redeem-details-view').show('');
      $('#back-btn-div').show('');
      $('#heading').html('Redeem');
      getRedeemCreditDetail();
    } else if (pageType === 'suggest') {
      $('#suggest-view').show('');
      $('#back-btn-div').show('');
      $('#heading').html('Gift');
      $('#back-btn').unbind();
      $('#back-btn').click(function(e){
        e.preventDefault();
        localStorage.pageType = 'offer';
        initPage();
      });
    } else if (pageType === 'offer') {
      $('#offer-view').show('');
      $('#suggest-btn-div').show('');
      $('#heading').html('Gift');
      $('#offer-btn-div').css('background', 'url("img/btn_highlighted.png")');
      getOffers();
    } else { //if (pageType === 'offer-detail') {
      $('#offer-details-view').show('');
      $('#back-btn-div').hide('');
      $('#heading').html('Gift');
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
            if (Storage != 'undefined') {
              var gifts = jQuery.parseJSON(unescape($(this).attr('data-object')));
              localStorage.giftDetail = escape(JSON.stringify(gifts[0]));
              localStorage.pageType = 'redeem-gift-detail';
            }
            initPage();
          });
          $('.redeem-credit-btn').click(function(e) {
            e.preventDefault();
            if (Storage != 'undefined') {
              var credits = jQuery.parseJSON(unescape($(this).attr('data-object')));
              localStorage.creditDetail = escape(JSON.stringify(credits[0]));
              localStorage.pageType = 'redeem-credit-detail';
            }
            initPage();
          });
        },
        error: showError
      });
    }
  }
}

function renderRedeem(gifts, credits) {
  var m = gifts ? gifts[0].merchant : credits[0].merchant; 
  
  var html = '<div class="rdem-bx"><div class="redeem"><span class="imgshado"></span><div class="lstimg">';
  html += '<img class="offer-background" src="img/offer.png" />';
  // TODO
  // html += '<img class="redeem-background" src="' + json.merchantImageUrl + '" />';  html += '<a href="' + redeem.merchant.url + '"><img class="website-img" src="img/ic_web.png" /></a>';
  html += '</div><h3>' + m.name + '</h3><div class="opt-icon">';
  local = getDisplayLocation(m.locations);
  if (local != 'undefined') {
    html += '<a href="tel:' + local.phoneNumber + '"><img src="images/ic_phone@2x.png" /></a>';
    html += '<a href="' + m.url + '"><img src="images/ic_web@2x.png" /></a>';
    html += '<a href="' + generateMapUrl(m.name, local) + '"><img src="images/ic_map@2x.png" />' + '&nbsp;' + computeDistance(local) + '</a>';
  } else {
    html += '<a href="' + m.url + '"><img src="images/ic_web@2x.png" /></a>';
  }

  html += '</div></div><div class="lst-dtl">';
  if (gifts) {
    var g = gifts[0];
    var json = escape(JSON.stringify(gifts));
    var style = credits ? ' lft-bdr' : '';
    html += '<a href="#" data-object="' + json + '" class="redeem-gift-btn clearfix">';
    html += '<div class="lft-dtl' + style + '"><span> received GIFT </span><h2>' + g.desc + '</h2></div>';
    html += '</a>'; 
  }
  if (credits) {
    var c = credits[0];
    var json = escape(JSON.stringify(credits));
    html += '<a href="#" data-object="' + json + '" class="redeem-credit-btn clearfix">';
    html += '<div class="rit-dtl"><span> earned credit </span><h2>' + c.desc + '</h2></div>';
    html += '</a>'; 
  }
  html += '</div>';
  
  $('#redeem-list').append(html);

}

function getOfferDetail() {
  if (typeof Storage != 'undefined') {
    var userId = localStorage.userId;
    if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
      var offer = jQuery.parseJSON(unescape(localStorage.offerDetail));
      renderOfferDetail(offer);

      $('#back-btn').unbind();
      $('#back-btn').click(function(e) {
        e.preventDefault();
        localStorage.pageType = 'offer';
        initPage();
      });

      $('#take-picture').change(function(e) {
        // TODO
        var icon = $('.camicon');
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
             icon.removeClass('camicon');
             icon.addClass('smallcamicon');
             $('#take-photo-header').hide();
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

function getRedeemGiftDetail() {
  if (typeof Storage != 'undefined') {
    var userId = localStorage.userId;
    if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
      var gifts = jQuery.parseJSON(unescape(localStorage.giftDetail));
      renderRedeemGiftDetail(gifts);
      $('#back-btn').unbind();
      $('#back-btn').click(function(e){
        e.preventDefault();
        localStorage.pageType = 'redeem';
        initPage();
      });
    }
  }
}

function getRedeemCreditDetail() {
  if (typeof Storage != 'undefined') {
    var userId = localStorage.userId;
    if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
      var credit = jQuery.parseJSON(unescape(localStorage.creditDetail));
      renderRedeemCreditDetail(credit);
      $('#back-btn').unbind();
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
  html += '<h2 id="take-photo-header">Add your own photo</h2>';
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
  html += '<a href="#" class="trm" onclick="$(\'#terms\').show();" >Terms and Conditions</a>';
  html += '<a href="#" class="lrn-mor" onclick="$(\'#learn\').show();" >Learn more</a>';
  html += '</div>';
  html += '<input name="share" type="submit" class="btn grd3" value="Give To Friends" disabled />';
  html += '</form>';
  $('#offer-details-view').html(html);
  
  var options = '';
  $.each(offer.locations, function(i, l) {
    var selected = '';
    if (i === 0) {
      selected = ' selected';
    }
    options += '<option value="' + l.locationId + '" ' + selected + '>' + l.address1 + ' ' + l.address2 + ', ' + l.city + '</option>';
  });
  $('#location-sel').html(options);
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
      
      $('#share-form input[name="comment"]').val(''); 
      
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
          processData: false,
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
  $('#share-help-form input[name="url"]').val(url);
  $('#share-popup').show();
}
  
function doShare(cb, type) {
  $('.popup').hide();
  $('#spinner h2').html('Sharing Gift');
  $('#spinner').show();
  
  var offer = jQuery.parseJSON(unescape(localStorage.offerDetail)),
    exp = {},
    message = $('#share-form input[name="comment"]').val(),
    data = {},
    req = {},
    url = $('#share-help-form input[name="url"]').val(),
    locationId = $('#location-sel').val(),
    str;
  
  if (typeof locationId === 'undefined') {
    locationId = 0;
  }
    
  exp['merchantId'] = offer.merchantId;
  exp['offerId'] = offer.id;
  exp['fbImageId'] = 0;
  exp['imageUrl'] = url; 
  exp['caption'] = message;
  exp['type'] = type;
  exp['locationId'] = locationId;
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
    url: config.backend + 'kikbak/ShareExperience/' + localStorage.userId,
    success: function(json) {
      if (json && json.shareExperienceResponse && json.shareExperienceResponse.referrerCode) {
        cb(json.shareExperienceResponse.referrerCode, message, url);
      } else {
        showError();
      }
    },
    error: showError
  });
}

function shareViaSms() {
  $('#spinner h2').html('Sharing gift');
  doShare(function(code, msg, url) {
    var data = {
      'name': localStorage.userName,
      'code': code,
      'desc': msg,
      'url' : url
    };
    if (getBrowserName() !== 'Safari') {
      $.ajax({
        type: 'GET',
        data: data,
        dataType: 'json',
        url: '/s/sms.php',
        success: function(json) {
          $('#spinner h2').html('Waiting');
          window.location.href = 'sms://?&body=' + json.body;
        },
        error: function() {
          showError();
        }
      });
    }
  }, 'sms');
}

function shareViaEmail() {
  $('#spinner h2').html('Sharing gift');
  doShare(function(code, msg, url) {
    var data = {
      'name': localStorage.userName,
      'code': code,
      'desc': msg,
      'url' : url
    };
    if (getBrowserName() == 'Safari') {
      $.ajax({
        type: 'GET',
        data: data,
        dataType: 'json',
        url: '/s/email.php',
        success: function(json) {
          $('#spinner h2').html('Waiting');
          window.location.href = 'mailto:?content-type=text/html&subject=' + json.title + '&body=' + json.body;
        },
        error: function() {
          showError();
        }
      });
    } else {
      $.ajax({
        type: 'GET',
        data: data,
        dataType: 'json',
        url: '/s/sms.php',
        success: function(json) {
          $('#spinner h2').html('Waiting');
          window.location.href = 'mailto:?content-type=text/html&subject=' + json.title + '&body=' + json.body;
        },
        error: function() {
          showError();
        }
      });
    }
  }, 'email');
}

function shareViaFacebook() {
  $('#spinner h2').html('Sharing gift');
  doShare(function(code, msg, imageUrl) {
    var data = {
      'name': localStorage.userName,
      'code': code,
      'desc': msg,
      'url' : imageUrl
    };
    $.ajax({
      type: 'GET',
      data: data,
      dataType: 'json',
      url: '/s/fb.php',
      success: function(json) {
        var o = {
          'app_id': config.appId,
          'url': 'http://young-springs-3453.herokuapp.com/'.replace(/\//g, '\/'),
          //'url': json.url.replace(/\//g, '\/'),
          'image': imageUrl.replace(/\//g, '\/'),
          'title': '\"' + encodeURIComponent(json.title) + '\"',
          'description': '\"' + encodeURIComponent(json.body) + '\"',
          'location': {
            'longitude': initPage.p.longitude,
            'latitude': initPage.p.latitude,
          }
        };
        var req = {
          'access_token': localStorage.accessToken,
          //'privacy': '{"value":"all_friends"}',
          'object': JSON.stringify(o),
          'method': 'POST',
          'format': 'json'
        };
        var url = 'https://graph.facebook.com/me/objects/referredlabs:coupon';
        $.ajax({
          url : url,
          data : req,
          cache : false,
          contentType : false,
          dataType: 'json',
          type : 'GET',
          success : function(response) {
            if (response && response.id) {
              var getData = {
                'fb:app_id': config.appId,
                'og:type': 'referredlabs:coupon',
                //'og:url': json.url,
                'og:image': imageUrl,
                'og:title': json.title,
                'og:description': json.body,
              }
              var getUrl = 'http://young-springs-3453.herokuapp.com/repeater.php?' + encodeQueryData(getData);
              var req = {
                'access_token': localStorage.accessToken,
                'method': 'POST',
                'coupon': getUrl,
                'fb:explicitly_shared': 'true',
                'format': 'json'
              };
              var url = 'https://graph.facebook.com/me/referredlabs:share';
              $.ajax({
                url : url,
                data : req,
                cache : false,
                contentType : false,
                dataType: 'json',
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
      },
      error: function() {
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

function renderRedeemGiftDetail(gift) {
  var html = '';
  html += '<div class="image-add rdme"><img src="images/addedimg.png"  class="addimg"><span class="imgshado"></span>';
  // TODO
  // html += '<div class="image-add rdme"><img src="' + gift.imageUrl + '" class="addimge"><span class="imgshado"></span>';
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
  html += '<img src="images/awtraimg.png">';
  // TODO real avatar
  html += '<div class="avtr-cmnt">';
  html += '<h2>' + gift.friendName + '</h2>';
  html += '<h2>' + gift.caption + '</h2>';
  html += '</div></div>';
  html += '<div class="gvrdm-botm-patrn"></div>';
  html += '<div class="crt">';
  html += '<h2>' + gift.desc + '</h2>';
  html += '<h4>' + gift.detailedDesc + '</h4>';
  html += '<div class="crt">';
  html += '<a href="#" class="trm" onclick="$(\'#terms\').show();" >Terms and Conditions</a>';
  html += '<a href="#" class="lrn-mor" onclick="$(\'#learn\').show();" >Learn more</a>';
  html += '</div>';
  html += '<button id="redeem-gift-btn" class="btn grd3">Redeem now in store</button>';
  
  $('#redeem-details-view').html(html);
}

function renderRedeemCreditDetail(credit) {
  var html = '';
  html += '<div class="image-add rdme"><img src="images/addedimg.png"  class="addimg"><span class="imgshado"></span>';
  // TODO
  // html += '<div class="image-add rdme"><img src="' + gift.imageUrl + '" class="addimge"><span class="imgshado"></span>';
  html += '<h3>' + credit.merchant.name + '</h3>';
  html += '<div class="msgg">Your gift has been redeemed by ' + credit.redeeemedGiftsCount + ' friend' + credit.redeeemedGiftsCount > 1 ? 's' : '' + '</div>';
  html += '</div>';
  html += '<div class="gv">';
  html += '<p>You have a reward ready to be claimed!</p>';
  html += '</div>';
  html += '<div class="crt">';
  html += '<h1>$' + credit.value.toFixed(2) + '</h1>';
  html += '<h5>' + credit.rewardType + '</h5>';
  html += '<a href="#" class="trm" onclick="$(\'#terms\').show();">Terms and Conditions</a>';
  html += '</div>';
  html += '<button id="claim-credit-form-btn" class="btn grd3">Claim reward now</button>';
  
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
  $('#back-btn').click(function() {
    $('#claim-credit-div').hide();
    $('#redeem-details-view').show();
    $('#back-btn').unbind();
    $('#back-btn').click(function(e){
      e.preventDefault();
      localStorage.pageType = 'redeem';
      initPage();
    });
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

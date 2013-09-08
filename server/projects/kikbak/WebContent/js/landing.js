var config = {
  backend : '',
  mobileUrl : '/m/offer.html',	
};

$(document).ready(function() {
  if (window.mobilecheck()) {
	$('#tel-div').show();
  } else {
    $('#tel-div').hide();
  }
  window.fbAsyncInit = function() {
    FB.init({appId: '493383324061333', status: false, cookie: true, xfbml: true});
  };
  (function() {
    var e = document.createElement('script'); e.async = true;
    e.src = document.location.protocol + '//connect.facebook.net/en_US/all.js';
    document.getElementById('fb-root').appendChild(e);
  }());
  $('#loginFb').click(fbLogin);
  $('#redeemGift').click(redeemGift);
});

function showError() {
  alert("Service is unavailable. Please try again later.");
}

function fbLogin() {
  FB.login(function(response) {
    if (response.status === 'connected') {
      connectFb(response.authResponse.accessToken);
    } else if (response.status === 'not_authorized') {
      FB.login();
    } else {
      FB.login();
    }
  }, {scope:"email,read_friendlists,publish_stream,publish_actions"});
}

function connectFb(accessToken) {
  localStorage.accessToken = accessToken;
  var userId = localStorage.userId;
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
    url: config.backend + 'user/register/fb/',
    success: function(json) {
      if (json && json.registerUserResponse && json.registerUserResponse.status === 'TOO_FEW_FRIENDS') {
        alert('For security purposes, Kikbak requires that your Facebook account have a certain minimum friend count. Unfortunately your chosen account does not qualify.');
        return;
      }
      if (!json || !json.registerUserResponse || !json.registerUserResponse.userId || !json.registerUserResponse.userId.userId) {
    	showError();
    	return;
      }
      updateFbFriends(json.registerUserResponse.userId.userId);
    },
    error: showError
  });
}

function updateFbFriends(userId) {
  if (userId === localStorage.friendUserId) {
	alert('Cannot redeem the gift you shared');
	return;
  }
  localStorage.userId = userId;
  $('#facebook-div').hide();
  $('#redeem-div').show();
}

function redeemGift() {
//  if (window.mobilecheck()) {
//    window.location.href = config.mobileUrl;
//  } else {
    claimCode(localStorage.userId);
//  }
}

function claimCode(userId) {
  var url = config.backend + 'rewards/claim/' + userId + '/' + localStorage.code;
  $.ajax({
    dataType: 'json',
    type: 'GET',
    contentType: 'application/json',
    url: url,
    success: function(json) {
      if (json && json.claimGiftResponse) {
    	var resp = json.claimGiftResponse;
        if (resp.status && resp.status === 'EXPIRED') {
          alert('The gift has expired');
          return;
        } else if (resp.status && resp.status === 'LIMIT_REACH') {
          alert('The gift is not available anymore');
          return;
        } else if (resp.status && resp.status === 'LIMIT_REACH') {
          alert('The gift is not available anymore');
          return;
        } else if (resp.status !== 'OK') {
          showError();
          return;
        }
    	if (resp.agId) {
          window.location.href = 'gift/success.html?user=' + userId + '&gid=' + json.claimGiftResponse.agId + '&code=' + localStorage.code;
        }
      } else {
    	showError();
      }
    },
    error: showError
  });
}

$(document).ready(function() {
  window.fbAsyncInit = function() {
    FB.init({appId: '493383324061333', status: false, cookie: true, xfbml: true});
  };
  (function() {
    var e = document.createElement('script'); e.async = true;
    e.src = document.location.protocol + '//connect.facebook.net/en_US/all.js';
    document.getElementById('fb-root').appendChild(e);
  }());
  $('#loginFb').click(fbLogin);

  var test = document.createElement('input');
  if(!('placeholder' in test)) {
    $(document).on('focus', 'input', function () {
      if ($(this).attr('placeholder') != '' && $(this).val() == $(this).attr('placeholder')) {
        $(this).val('').removeClass('hasPlaceholder');
      }
    });
    $(document).on('blur', 'input', function () {
      if ($(this).attr('placeholder') != '' && ($(this).val() == '' || $(this).val() == $(this).attr('placeholder'))) {
        $(this).val($(this).attr('placeholder')).addClass('hasPlaceholder');
      }
    });
    $(document).on('focus', 'textarea', function () {
      if ($(this).attr('placeholder') != '' && $(this).val() == $(this).attr('placeholder')) {
        $(this).val('').removeClass('hasPlaceholder');
      }
    });
    $(document).on('blur', 'textarea', function () {
      if ($(this).attr('placeholder') != '' && ($(this).val() == '' || $(this).val() == $(this).attr('placeholder'))) {
        $(this).val($(this).attr('placeholder')).addClass('hasPlaceholder');
      }
    });
  }

  $('input').blur();
  $('textarea').blur();

});

function redeemBarcode(code) {
  console.log('redeem code ' + code);
}

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
    url: 'user/register/fb/',
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
  localStorage.userId = userId;
  registerCb(userId);
}


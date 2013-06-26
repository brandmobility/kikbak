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
      var suffix = 'index.html';
      if (window.location.href.indexOf(suffix, window.location.href.length - suffix.length) !== -1) {
        window.location.href = 'offer.html';
      }
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
        var suffix = 'index.html';
        if (window.location.href.indexOf(suffix, window.location.href.length - suffix.length) !== -1) {
          window.location.href = 'offer.html';
        }
      },
      error: showError
    });
  });
}

function getOffers() {
  if (typeof Storage != 'undefined') {
    var userId = localStorage.userId;

    if (typeof userId != 'undefined' && userId != 'undefined' &&
            userId != 'null' && userId != null && userId != '') {
      $.ajax({
      });
    }
  }
}


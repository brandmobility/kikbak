(function(){

'use strict';

(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
})(window,document,'script','//www.google-analytics.com/analytics.js','ga');
ga('create', 'UA-45251651-1');
ga('send', 'pageview');

var user = {};
var storiesResponse = {};
var currentOffer = {};

function shareOfferFromChannel(channel) {
  var story;
  for (var i in storiesResponse.stories) {
    if (storiesResponse.stories[i].type.toLowerCase() === channel) {
      story = storiesResponse.stories[i];
    }
  }

  if (channel === 'fb') {
    var landingUrl = storiesResponse.stories[0].landingUrl;
    var str = 'https://www.facebook.com/sharer/sharer.php?u=' + encodeURIComponent(landingUrl);
    window.open(str, '_blank');
  }

  if (story) {
    var landingUrl = story.landingUrl;
    var body = story.body;
    var subject = story.subject;
    if (channel === 'twitter') {
      var str = "https://twitter.com/share?";
      var params = [ 
        {name:"url", value:landingUrl},
        {name:"via", value:"kikbak"},
        {name:"count", value:"none"},
        {name:"text", value: body}
      ];  
      $.each(params, function (i, item) {
        str += encodeURIComponent(item.name) + "=" + encodeURIComponent(item.value) + "&";
      }); 
      window.open(str, '_blank');
    } else if (channel === 'email') {
      var str = 'mailto:?content-type=text/html&subject=' + encodeURIComponent(subject)
          + '&body=' + encodeURIComponent(body);
      if (getBrowserName() === 'Safari') {
        window.location.href = str;
      } else {
        window.open(str, '_blank');
      }
    } else if (channel === 'sms') {
      var str = 'sms:?body=' + encodeURIComponent(encodeURIComponent(body));
      if (getBrowserName() === 'Safari') {
        window.location.href = str;
      } else {
        window.open(str, '_blank');
      }
    }
  }

  ga('send', 'event', 'button', 'click', 'share via ' + channel);
  var requestUrl = config.backend + 'kikbak/v2/share/addsharetype?code=' + encodeURIComponent(storiesResponse.code) + '&type=' + encodeURIComponent(channel);
  $.ajax({
    type: 'GET',
    contentType: 'application/json',
    url: requestUrl,
    success: function() {},
    error: function() {}
  });
}

function registerUser(cb) {
  var invalid = false;
  var requestUrl = config.backend + 'kikbak/user/register';
  if (user.type === 'facebook') {
    requestUrl += '/fb?token=' + encodeURIComponent(user.accessToken);
    if (user.phone) {
      requestUrl += '&phone=' + encodeURIComponent(user.phone);
    }
  } else {
    var emailInput = $('#email-input');
    var email = emailInput.val();
    user.email = email;
    user.name = $('#username-input').val();
    requestUrl += '/web?email=' + encodeURIComponent(user.email);
    requestUrl += '&name=' + encodeURIComponent(user.name);
  }
  var phoneInput = $('#phone-input');
  if (phoneInput.hasClass('required')) {
    user.phone = phoneInput.val();
    if (validatePhone(user.phone)) {
      requestUrl += '&phone=' + encodeURIComponent(user.phone);
    } else {
      phoneInput.css('border', '2px solid red');
      invalid = true;
    }
  }
  if (invalid) return;

  $.ajax({
    type: 'GET',
    contentType: 'application/json',
    url:requestUrl,
    success: function(json) {
      if (json && json.registerUserResponse && json.registerUserResponse.status === 'TOO_FEW_FRIENDS') {
        alert('For security purposes, Kikbak requires that your Facebook account have a certain minimum friend count. Unfortunately your chosen account does not qualify.');
        return;
      }
      if (!json || !json.registerUserResponse || !json.registerUserResponse.userId || !json.registerUserResponse.userId.userId) {
        showError();
        return;
      }
      s.userId = json.registerUserResponse.userId.userId;
      user.id = s.userId;

      if (user.type === 'facebook') {
        updateFbFriends(user.id, cb);
      } else {
        cb();
      }
    },
    error: showError
  });
}

function preShareClick() {
  var offer = currentOffer;
  ga('send', 'event', 'button', 'click', 'share offer');
  registerUser(function() {
    uploadPhotoAfterLogin(function() {
      var requestUrl = config.backend + 'kikbak/v2/share/getstories?userid=' + encodeURIComponent(user.id) + '&offerid=' + encodeURIComponent(offer.id) 
        + '&platform=' + (getBrowserName() === 'Safari' ? 'ios' : 'android') + '&imageurl=' + encodeURIComponent(offer.sharedImageUrl);
      if (user.email) {
        requestUrl += '&email=' + encodeURIComponent(user.email);
      }
      if (user.phone) {
        requestUrl += '&phonenumber=' + encodeURIComponent(user.phone);
      }
      var comment = $('#comment-input').val();
      if (comment) {
        requestUrl += '&caption=' + encodeURIComponent(comment);
      }
      var employeeId = $('#employeeId-input').val();
      if (employeeId) {
        requestUrl += '&employeeid=' + encodeURIComponent(employeeId);
      }
      var locationId = $('#location-sel').val();
      if (locationId) {
    	requestUrl += "&locationid=" + encodeURIComponent(locationId);
      }
      $.ajax({
        type: 'GET',
        contentType: 'application/json',
        url: requestUrl,
        success: function(json) {
          if (json && json.storiesResponse) {
            storiesResponse = json.storiesResponse;
            if (storiesResponse.status === 'OK') {
              var landingUrl = storiesResponse.stories[0].landingUrl;
              var landingHref = $('#landing');
              landingHref.attr('href', landingUrl);
              landingHref.html(landingUrl);
              $('#pre-share-popup').hide();
              if (offer.auth === 'facebook') {
                $('#post-share-detail h3').html('Give offer to friends');
              } else {
                $('#post-share-detail h3').html('3. Share your offer');
              }
              $('#share-popup').show();
              ga('send', 'event', 'button', 'show', 'give method select');
            } else if (storiesResponse.status === 'LIMIT_REACH') {
              showErrorWithMsg('Congrats! You have earned the maximum number of rewards. Thank you for referring your friends to Verizon.<br /><br />Unfortunately you are no longer eligible to share this offer with friends.');
            } else {
              showError();
            }
          } else {
            showError();
          }
        },
        error: showError
      });
    });
  }); 
}

function onInput() {
  var valid = true;
  $('.required').each(function() {
    var thisElement = $(this);
    thisElement.css('border', '1px solid gray');
    var value = thisElement.val();
    if (thisElement.attr('type') === 'tel') {
      if (value.replace(/^\d/g, "") === '') {
        valid = false;
      }
    } else {
      if (value.replace(/^\s+|\s+$/g, '') === '') {
        valid = false;
      }
    }
  });
  if (valid) {
    $('.share-btn').removeAttr('disabled');
  } else {
    $('.share-btn').attr('disabled', 'disabled');
  }
}

function completeUserInfo() {
  $('#login-div').hide();
  onInput();
  $('#create-share-div').show();
}

function initUserInfoForm() {
  $('#login-div').show();
  $('#create-share-div').hide();
}

function recordFbLogin(resp) {
  var accessToken = resp.accessToken;
  s.accessToken = accessToken;
  user.accessToken = accessToken;
  user.type = 'facebook';
  user.fbId = resp.userID;

  $('#username-input').removeClass('required');
  $('#email-input').removeClass('required');
}

function validateEmail(email) { 
  var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(email);
}

function validatePhone(phone) {
  if (!phone) return false;
  var normalizedPhone = phone.replace(/^\d$/g, "");
  return normalizedPhone.length >= 10;
}

var authType = {
  facebook: {
    login_div: '<div id="login-div">' +
                 '<h3>Create your offer</h3>' +
    	         '<div id="location-sel-div"><select id="location-sel" class="location-sel"></select></div>' +
		         '<div class="note"><p>Optinal: Let friends konw who helped you</p></div>' +
    		     '<input id="employeeId-input" class="location-sel" type="text" name="employeeId" placeholder="Employee name [optional]" />' +
                 '<input id="share-btn-fb" name="share" type="button" class="fb-share" value="       Connect with Facebook" />' +
                 '<p>We use Facebook to personalize your offers and notify you via email when friends redeem them. <b>We will never post without your permission.</b></p>' +
               '</div>',
    handler_register: function() {
      $('#share-btn-fb').click(function () {
        user.status = 'share-login';
        var cb = function(resp) {
    	  recordFbLogin(resp);
    	  preShareClick();
    	};
        loginFb(cb);
      });
    }
  },
  phone: {
    login_div: '<div id="login-div">' +
                 '<h3>1. Register</h3>' +
                 '<input id="username-input" type="text" class="required" placeholder="Your name" name="username" />' + 
                 '<input id="email-input" type="email" class="required" placeholder="Your email" name="email" />' + 
                 '<button id="login-email" class="share-btn btn grd-btn" disabled="disabled" >Submit</button>' +
                 '<hr>' +
                 '<span> OR </span><input id="share-btn-fb" name="share" type="button" class="fb-share" style="width:88%;margin:0;height:30px;" value="       Connect with Facebook" />' +
                 '<p>We use Facebook to personalize your offers and notify you via email when friends redeem them. <b>We will never post without your permission.</b></p>' +
               '</div>' +
               '<div id="create-share-div" style="display:none">' +
               '<h3>2. Create your offer</h3>' +
               '<input type="tel" id="phone-input" placeholder="Your Verizon phone number" />' +
	           '<div id="location-sel-div"><select id="location-sel" class="location-sel"></select></div>' +
		       '<div class="note"><p>Optinal: Let friends konw who helped you</p></div>' +
		       '<input id="employeeId-input" class="location-sel" type="text" name="employeeId" placeholder="Employee ID [optional]" />' +
               '<button id="create-share-btn" class="share-btn btn grd-btn" disabled="disabled" >Create offer to share</button>' +
             '</div>',
    handler_register: function() {
      $('#share-btn-fb').click(function () {
        user.status = 'share-login';
        var cb = function(resp) {
          recordFbLogin(resp);
          $('#phone-input').addClass('required');
          completeUserInfo();
    	};
        loginFb(cb);
      });
      $('#login-email').click(function() {
    	var emailInput = $('#email-input');
    	var email = emailInput.val();
    	if (!validateEmail(email)) {
    	  emailInput.css('border', '2px solid red');
    	  return;
    	}
        $('#phone-input').addClass('required');
        completeUserInfo();
      });
      $('#create-share-btn').click(preShareClick);
    }
  },
  none: {
    login_div: '<div id="login-div">' +
                 '<h3>1. Register</h3>' +
                 '<input id="username-input" type="text" class="required" placeholder="Your name" name="username" />' + 
                 '<input id="email-input" type="email" class="required" placeholder="Your email" name="email" />' + 
                 '<button id="login-email" class="share-btn btn grd-btn" disabled="disabled" >Submit</button>' +
                 '<hr>' +
                 '<span> OR </span><input id="share-btn-fb" name="share" type="button" class="fb-share" style="width:88%;margin:0;height:30px;" value="       Connect with Facebook" />' +
                 '<p>We use Facebook to personalize your offers and notify you via email when friends redeem them. <b>We will never post without your permission.</b></p>' +
               '</div>' + 
               '<div id="create-share-div" style="display:none">' +
                 '<h3>2. Create your offer</h3>' +
	             '<div id="location-sel-div"><select id="location-sel" class="location-sel"></select></div>' +
		         '<div class="note"><p>Optinal: Let friends konw who helped you</p></div>' +
		         '<input id="employeeId-input" class="location-sel" type="text" name="employeeId" placeholder="Employee name [optional]" />' +
                 '<button id="create-share-btn" class="share-btn btn grd-btn" disabled="disabled" >Create offer to share</button>' +
               '</div>',
    handler_register: function() {
      $('#share-btn-fb').click(function () {
        user.status = 'share-login';
        var cb = function(resp) {
          recordFbLogin(resp);
          completeUserInfo();
    	};
        loginFb(cb);
      });
      $('#login-email').click(function() {
      	var emailInput = $('#email-input');
      	var email = emailInput.val();
      	if (!validateEmail(email)) {
      	  emailInput.css('border', '2px solid red');
      	  return;
      	}
        completeUserInfo();
      });
      $('#create-share-btn').click(preShareClick);
    }    
  }
};

window.mobilecheck = function() {
  var check = false;
  (function(a) {
    if (/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i
      .test(a)
      || /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i
       .test(a.substr(0, 4)))
      check = true;
  })(navigator.userAgent || navigator.vendor || window.opera);
  return check;
}

var pageType = window.location.hash;
var merchantTypePrefix = "#merchant-";
if (!window.mobilecheck()) {
  if (pageType.indexOf(merchantTypePrefix) === 0) {
    window.location.href = "/d/" + pageType;
  } else {
    alert("The offer is only available on mobile now. The desktop version is coming soon. Please visit kikbak.me for now");
    window.location.href = "https://kikbak.me";
  }
}

var config = {
  backend: '',
  appId: 493383324061333
}

var s = (Storage) ? localStorage : {};

$(document).ready(function() {
  $('body').show();
  
  var pageType = window.location.hash;
  var merchantTypePrefix = "#merchant-";

  if (pageType.indexOf(merchantTypePrefix) === 0) {
    var strArray = pageType.split('-', 2);
    var merchantTag = strArray[0] + '-' + strArray[1];
   	history.pushState({}, 'merchant', merchantTag);
  } else if (pageType === '#offer-detail') {
   	history.pushState({}, 'offer', '#offer');
  } else if (pageType === '#redeem-gift-detail') {
   	history.pushState({}, 'redeem', '#redeem');
  } else if (pageType === '#redeem-credit-detail') {
   	history.pushState({}, 'redeem', '#redeem');
  }
  $(document).ajaxStart(function (){
    $('#spinner').show();
  });
  $(document).ajaxStop(function (){
    $('#spinner').hide();
  });
  $('body').scrollTop($(document).height());
  $.ajaxSetup({ cache: true });
  $.getScript('//connect.facebook.net/en_US/all.js', function(){
    window.fbAsyncInit = fbInit;
  });
  $('.offer-btn').click(function(e){
	e.preventDefault();
   	history.pushState({}, 'offer', '#offer');
    initPage();
  });
  $('.redeem-btn').click(function(e){
	e.preventDefault();
   	history.pushState({}, 'redeem', '#redeem');
    initPage();
  });
  $('.suggest-btn').click(function(e){
	e.preventDefault();
   	history.pushState({}, 'suggest', '#suggest');
    initPage();
  });
  $('.offer-list-btn').click(function(e){
	e.preventDefault();
   	history.pushState({}, 'offer', '#offer');
    initPage();
  });

  $('#suggest-form input[name="name"]').bind('keyup', adjustSuggest);
  $('#suggest-form textarea[name="reason"]').bind('keyup', adjustSuggest);
  
  $(".popup-close-btn").click(function(e) {
	e.preventDefault();
    $('.popup').hide();
  });

  $('#suggest-form input[name="suggest"]').click(function(e) {
    e.preventDefault();
    doSuggest();
  });
  
  $('#suggest-form input[name="source"]').change(function(e) {
    var files = e.target.files;
    var file;
    
    if (files && files.length > 0) {
      file = files[0];
      try {
        var URL = window.webkitURL || window.URL;
        var imgUrl = URL.createObjectURL(file);
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
  
  $('#claim-credit-btn').click(function(e) {
    e.preventDefault();
    var o = {};
    var form = $('#claim-credit-form');
    $.each(form.serializeArray(), function() { 
      o[this.name] = this.value;
    });

    var invalid = false;
    for (var i in keys) {
      var k = keys[i];
      if (/^\d+$/.test(o[k].replace(/^\s+|\s+$/g, ''))) {
        o[k] = o[k].replace(/^\s+|\s+$/g, '');
        $('#claim-credit-form input[name="' + k + '"]').css('border', '1px solid gray');
      } else {
    	invalid = true;
    	$('#claim-credit-form input[name="' + k + '"]').css('border', '2px solid red');
      }
    }
    if (invalid) {
      $('#claim-credit-form h3').html('Sorry, the information you entered is not recognized as valid.<br />Please ensure that is accurate and try again.');
      return;
    } else {
      $('#claim-credit-form h3').html('Please provide the following to redeem:');
    }
    
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
        $('#success-popup h1').html('Success!');
        $('#success-popup h3').html('<br>Your reward claim has been submitted');
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
  var xHeight = null;
  if(window.screen != null)
    xHeight = window.screen.availHeight;
  if(window.innerHeight != null)
    xHeight =   window.innerHeight;
  if(document.body != null)
    xHeight = document.body.clientHeight;
  return xHeight;
}

function showError() {
  showErrorWithMsg("Service is unavailable. Please try again later.");
}

function showErrorWithMsg(msg) {
  $('#spinner h2').html('Waiting');
  $('#success-popup h1').html('');
  $('#success-popup h3').html('');
  $('#success-popup p').html(msg);
  $('#success-popup').show();
}

function fbInit() {
  FB.init({
    appId: '493383324061333',
    channelUrl: '/m/channel.html',
    status: true,
    cookie: true,
    xfbml: true
  });
  if (s.accessToken) {
    FB.getLoginStatus(function(response) {
      if (response.status === 'connected') {
        s.accessToken = response.authResponse.accessToken; 
      } else {
        s.accessToken = null;
      }
    });
  }
  if (user.status === 'share-login') {
    user.cb();
    user.status = null;
  } else {
    initPage();
  }
}

function connectFb(accessToken) {
  s.accessToken = accessToken;
  var userId = s.userId;
  if (userId) {
	initPage();
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
      if (json && json.registerUserResponse && json.registerUserResponse.status === 'TOO_FEW_FRIENDS') {
        alert('For security purposes, Kikbak requires that your Facebook account have a certain minimum friend count. Unfortunately your chosen account does not qualify.');
        return;
      }
      if (!json || !json.registerUserResponse || !json.registerUserResponse.userId || !json.registerUserResponse.userId.userId) {
        showError();
        return;
      }
      s.userId = json.registerUserResponse.userId.userId;
      initPage();
    },
    error: showError
  });
}

function updateFbFriends(userId, cb) {
  var data = {};
  data['access_token'] = s.accessToken;
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
}

function defaultNoLocationCallback() {
  alert('We are unable to detect your current location.\n\nIf you are inside a participating store and would like to share a Kikbak offer with your friends, please enable location services for your phone and web browser in your device settings.');
  window.location.href = "https://kikbak.me";
}

function initPosition(callback, failCallback) {
  if (navigator.geolocation) {
    $('#spinner').show();
    navigator.geolocation.getCurrentPosition(function(p) {
      initPage.p = p.coords;
      $('#spinner').hide();
      callback();
      $(window).bind('popstate', callback);
    }, function() {
      $('#spinner').hide();
      if (failCallback) {
        failCallback();
      } else {
        defaultNoLocationCallback();
      }
    },
    { enableHighAccuracy:true,maximumAge:600000,timeout:5000 });
  } else {
    if (failCallback) {
      failCallback();
    } else {
      defaultNoLocationCallback();
    }
  }
}

function clearView() {
  $('.popup').hide();
  $('#crop-image-div').hide();
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
}

function initPage() {
  clearView();

  var code = s.code;
  if (typeof code !== 'undefined' && code !== 'null' && code !== '') {
    loginFb();
    claimGift(code);
    return;
  }

  var pageType = window.location.hash;
  var merchantTypePrefix = "#merchant-";
  if (pageType.indexOf(merchantTypePrefix) === 0) {
    var strArray = pageType.split('-');
    if (strArray.length == 2) {
      getOffersByMerchant(strArray[1]);
    } else {
      getOfferDetail();
    }
  } else if (pageType === '#redeem') {
    loginFb();
    getRedeems();
  } else if (pageType === '#redeem-gift-detail') {
    loginFb();
    getRedeemGiftDetail();
  } else if (pageType === '#redeem-credit-detail') {
    loginFb();
    getRedeemCreditDetail();
  } else if (pageType === '#suggest') {
    loginFb();
    $('#suggest-view').show('');
    $('#back-btn-div').show('');
    $('#heading').html('Give');
    $('#show-picture-suggest').removeAttr('src');
    $('#add-photo-suggest').show();
    $('#back-btn').unbind();
    $('#back-btn').click(function(e) {
      window.history.back();
    });
  } else if (pageType === '#offer-detail') {
    getOfferDetail();
  } else {// Default
    getOffers(pageType !== 'offer');
  }
}

function claimGift(code) {
  var userId = s.userId,
      url = config.backend + 'kikbak/rewards/claimgift/' + userId + '/' + code;
  s.code = null;
  $.ajax({
    dataType: 'json',
    type: 'GET',
    contentType: 'application/json',
    url: url,
    success: function(json) {
   	  history.pushState({}, 'redeem', '#redeem');
      initPage();
    },
    error: function() {
      initPage();
    }
  });
}

function getOffers(force) {
  history.pushState({}, 'offer', '#offer');
  var userId = 0;

  initPosition(function() {
    clearView();
    if ( typeof initPage.p !== 'undefined') {
      getOffersByLocation(userId, initPage.p, force);
    } else {
      getOffersByLocation(userId, null, force);
    }
  });
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
  if (local) {
    html += '<div class="opt-icon"><a href="' + generateMapUrl(offer.merchantName, local) + '"><img class="website-img map-img" src="images/ic_map@2x.png">' + '&nbsp;' + offer.dist + ' mi</a>';
    html += '<a href="' + offer.merchantUrl + '"><img class="website-img" src="images/ic_web@2x.png" /></a>';
    html += '<a href="tel:' + local.phoneNumber + '"><img class="website-img" src="images/ic_phone@2x.png" /></a>';
  } else {
    html += '<a href="' + offer.merchantUrl + '"><img class="website-img" src="images/ic_web@2x.png" /></a>';
  }
  html += '</div>';
  html += '<div class="ribn"><img src="images/ribncorn.png" class="crn">';
  var gift = "";
  if (offer.kikbakValue) {
    gift = "<span>Give</span>";
  } else {
    gift = "<span style='margin-top:40px;'>Give</span>";
  }
  gift += offer.giftDesc;
  if (offer.kikbakValue) {
    html += '<div class="giv grd3">' + gift + '</div>';
    html += '<img src="images/ribnaro.png">';
    html += '<div class="get"><span>Get</span>$' + offer.kikbakValue + '</div>';
  } else {
    html += '<div class="giv grd3" style="height:118px;">' + gift + '</div>';
    html += '<img src="images/ribnaro.png">';
  }
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
  return computeDistanceDigit(local) + ' mi';
}

function getDisplayLocation(locations) {
  $.each(locations, function(i, local) {
    if (typeof local.dist === 'undefined') {
      local.dist = computeDistanceDigit(local);
    }
  });
  return locations.sort(function(a, b) {return a.dist - b.dist})[0];
}

function renderOfferList(json, tagname, tag, force) {
  var offers = json.getUserOffersResponse.offers;
  s.offerCount = offers.length;
            
  var availCount = 0;
  var availOffer = null;

  if (initPage.p) {
    $.each(offers, function(i, offer) {
      if (offer.locations.length) {
      var local = getDisplayLocation(offer.locations);
        offer.dist = local.dist;
        if (local.dist < 0.5) {
          ++availCount;
          availOffer = offer;
        }
      }
    });
  }
  
  if (offers.length == 1) {
    s.offerDetail = escape(JSON.stringify(offers[0]));
	history.pushState({}, tagname, tag);
    $('back-btn').hide();
    initPage();
    return;
  }
  
  offers = offers.sort(function(a, b) {return a.dist && b.dist ? a.dist - b.dist : -1});
  
  if (availCount == 1 /*&& !force*/) {
    s.offerDetail = escape(JSON.stringify(availOffer));
	history.pushState({}, tagname, tag);
    initPage();
    return;
  }
  
  $('#offer-list').html('');
  $.each(offers, function(i, offer) {
    renderOffer(offer);
  });
      
  $('#offer-view').show('');
  $('#suggest-btn-div').show('');
  $('#heading').html('Give');  $('#offer-btn-div').css('background', 'url("img/btn_highlighted.png")');
  
  if (offers.length == 0) {
    $("#no-offer-list").show();
  } else {
    $("#no-offer-list").hide();
  }
  $('.offer-details-btn').click(function(e) {
	history.pushState({}, tagname, tag);
    e.preventDefault();
    s.offerDetail = $(this).attr('data-object');
    initPage();
  });
}

function getOffersByMerchant(merchant) {
  initPosition(function() {
    getOffersByMerchantWithLocation(merchant);
  }, function() {
    getOffersByMerchantWithLocation(merchant);
  });
}

function getOffersByMerchantWithLocation(merchant) {
  $.ajax({
    dataType: 'json',
    type: 'GET',
    contentType: 'application/json',
    url: config.backend + 'kikbak/v2/user/offer/0/' + merchant,
    success: function(json) {
      renderOfferList(json, 'merchant-offer-detail', '#merchant-' + merchant + '-offer');
    },
    error: showError
  });
}

function getOffersByLocation(userId, position, force) {
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
  $('#spinner h2').html('Loading offer');
  $.ajax({
    dataType: 'json',
    type: 'POST',
    contentType: 'application/json',
    data: str,
    url: config.backend + 'kikbak/v2/user/offer/' + userId,
    success: function(json) {
      $('#spinner h2').html('Loading offer');
      renderOfferList(json, 'offer-detail', '#offer-detail', force);
    },
    error: showError
  });
}

function getRedeems() {
  initPosition(getRedeemsWithLocation);
}

function getRedeemsWithLocation() {
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
        clearView();
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
        var redeemData = {};
        getRedeems.counter = 0;
        for (var mid in giftCollection) {
          if (renderRedeem(giftCollection[mid], creditCollection[mid])) {
            mark[mid] = 1;
            getRedeems.counter++;
            redeemData['gift'] = giftCollection[mid][0];
            if (creditCollection[mid]) {
              redeemData['credit'] = creditCollection[mid][0];
            }
          }
        }
        for (var mid in creditCollection) {
          if (!mark[mid]) {
            if (renderRedeem(giftCollection[mid], creditCollection[mid])) {
              getRedeems.counter++;
              redeemData['credit'] = creditCollection[mid][0];
            }
          }
        }

        $('#redeem-btn-div').css('background', 'url("img/btn_highlighted.png")');
        $('#heading').html('Kikbak');
        
        $('.redeem-data-btn').click(function(e) {
          e.preventDefault();
          var redeemData = jQuery.parseJSON(unescape($(this).attr('data-object')));
          onClickRedeem(redeemData);
        });
        $('.redeem-gift-btn').click(function(e) {
          e.preventDefault();
          var gifts = jQuery.parseJSON(unescape($(this).attr('data-object')));
          onClickRedeemGift(gifts);
        });
        $('.redeem-credit-btn').click(function(e) {
          e.preventDefault();
          var credits = jQuery.parseJSON(unescape($(this).attr('data-object')));
          onClickRedeemCredit(credits);
        });
        
        if (getRedeems.counter == 0) {
          $("#no-redeem-list").show();
        } else {
          $("#no-redeem-list").hide();
        }
        if (getRedeems.counter == 1) {
          onClickRedeem(redeemData);
          return;
        } 

        $('#redeem-view').show('');
      },
      error : showError
    });
  }
}

function onClickRedeem(redeemData) {
  if (!redeemData['gift']) {
    onClickRedeemCredit(redeemData['credit']);
    return;
  }
  if (!redeemData['credit']) {
    onClickRedeemGift(redeemData['gift']);
    return;
  }
  $('#redeem-view').show('');
  var gift = redeemData['gift'];
  var credit = redeemData['credit'];
  $('#friend-popup h1').html('');
  var list = $('#friend-list');
  var li = '<li class="frd-bx" >';
  li += '<h2>' + gift.desc + '</h2>';
  var j = escape(JSON.stringify(gift));
  li += '<a href="#" class="nxtt" id="redeem-gift-shortcut" data-object="' + j + '"><img width="11" height="18" src="images/nxt-aro.png"></a></li>';
  list.append(li);
  var li = '<li class="frd-bx" >';
  li += '<h2>' + credit.desc + '</h2>';
  var j = escape(JSON.stringify(credit));
  li += '<a href="#" class="nxtt" id="redeem-credit-shortcut" data-object="' + j + '"><img width="11" height="18" src="images/nxt-aro.png"></a></li>';
  list.append(li);
  
  $('#redeem-gift-shortcut').click(function(e) {
    e.preventDefault();
    var gifts = jQuery.parseJSON(unescape($(this).attr('data-object')));
    onClickRedeemGift(gifts);
  });
  $('#redeem-credit-shortcut').click(function(e) {
    e.preventDefault();
    var credits = jQuery.parseJSON(unescape($(this).attr('data-object')));
    onClickRedeemCredit(credits);
  });
  $('#friend-popup').show();
}

function onClickRedeemCredit(credits) {
  s.creditDetail = escape(JSON.stringify(credits));
  history.pushState({}, 'redeem-credit-detail', '#redeem-credit-detail');
  initPage();
}

function onClickRedeemGift(gifts) {
    $('#friend-popup h1').html(gifts.desc);
    var list = $('#friend-list');
    list.html('');
    if (gifts && gifts.shareInfo.length > 1) {
      $('#redeem-view').show('');
      for (var i in gifts.shareInfo) {
    	var shareInfo = gifts.shareInfo[i];
        var li = '<li class="frd-bx" >';
        var data = {
          'gift' : gifts,
          'shareInfo' : shareInfo
        };
        var j = escape(JSON.stringify(data));
        li += '<a href="#" data-object="' + j + '" class="select-gift-btn">';
        if (shareInfo.fbFriendId) {
          li += '<img class="avatar" src="https://graph.facebook.com/' + shareInfo.fbFriendId + '/picture?width=80&height=80">';
        }
        li += '<h2>' + shareInfo.friendName + '</h2>';
        li += '<img width="11" height="18" class="nxtt" src="images/nxt-aro.png"></a></li>';
        list.append(li);
      }

      $('#friend-popup').show();
      $('.select-gift-btn').click(function(e) {
        e.preventDefault();
        $('#friend-popup').hide();
        s.giftDetail = $(this).attr('data-object');
        history.pushState({}, 'redeem-gift-detail', '#redeem-gift-detail');
        initPage();
      });

    } else if (gifts) {
      var data = {
        'gift' : gifts,
        'shareInfo' : gifts.shareInfo[0]
      };
      var j = escape(JSON.stringify(data));
      s.giftDetail = j;
      history.pushState({}, 'redeem-gift-detail', '#redeem-gift-detail');
      initPage();
    }
}

function renderRedeem(gifts, credits) {
  var m = credits ? credits[0].merchant : gifts[0].merchant;
  var imgUrl = credits ? credits[0].imageUrl : gifts[0].defaultGiveImageUrl;
  
  var redeemData = {};
  if (gifts) {
    redeemData['gift'] = gifts[0];
  }
  if (credits) {
    redeemData['credit'] = credits[0];
  }
  var json = escape(JSON.stringify(redeemData));
  var html = '<div class="rdem-bx"><div class="redeem">';
  html += '<a href="#" data-object="' + json + '" class="redeem-data-btn clearfix">';
  html += '<span class="imgshado"></span>';
  html += '<div class="lstimg">';
  html += '<img class="redeem-background" src="' + imgUrl + '" />';
  html += '</div>';  
  html += '</a>';
  html += '<h3>' + m.name + '</h3><div class="opt-icon">';
  var local = getDisplayLocation(m.locations);
  if (local) {
    html += '<a href="' + generateMapUrl(m.name, local) + '><img class="website-img map-img" src="images/ic_map@2x.png" />' + '&nbsp;' + computeDistance(local) + '</a>';
    html += '<a href="' + m.url + '"><img class="website-img" src="images/ic_web@2x.png" /></a>';
    html += '<a href="tel:' + local.phoneNumber + '"><img class="website-img" src="images/ic_phone@2x.png"  /></a>';
  } else {
    html += '<a href="' + m.url + '"><img class="website-img" src="images/ic_web@2x.png" /></a>';
  }

  var valid = false;
  html += '</div></div><div class="lst-dtl">';
  if (gifts) {
    var g = gifts[0];
    if (g.validationType === 'barcode') {
      var json = escape(JSON.stringify(g));
      var style = credits ? ' lft-bdr' : '';
      html += '<a href="#" data-object="' + json + '" class="redeem-gift-btn clearfix">';
      html += '<div class="lft-dtl' + style + '"><span>USE RECEIVED GIFT<h2>' + g.desc + '</h2></span>';
      html += '<img style="width:32px;height:32px;float:right;" src="https://graph.facebook.com/' + g.shareInfo[0].fbFriendId + '/picture?width=80&height=80">';
      if (g.shareInfo.length > 1) {
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
  html += '</div>';
  
  if (valid) {
    $('#redeem-list').append(html);
    return true;
  }
  
  return false;
}

function getOfferDetail() {
  var userId = 0;
  // s.userId;
  if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
    var offer = jQuery.parseJSON(unescape(s.offerDetail));
    renderOfferDetail(offer);

    $('#back-btn').unbind();
    $('#back-btn').click(function(e) {
      e.preventDefault();
      history.pushState({}, 'offer-list', '#offer-list');
      initPage();
    });
    
    $('#share-btn').click(function(e){
      e.preventDefault();
      shareOffer(offer);
    });

    $('input [name="comment"]').bind('input',function(){
      ga('send', 'event', 'button', 'click', 'add comment');
    });
    $('#take-picture').change(function(e) {
      ga('send', 'event', 'button', 'click', 'take picture');
      $("#share-btn").attr('disabled', 'disabled');
      var icon = $('.camicon');
      var files = e.target.files;
      var file;

      if (files && files.length > 0) {
        file = files[0];
        try {
          var URL = window.webkitURL || window.URL;
          var imgUrl = URL.createObjectURL(file);
          var images = $('#show-picture');
          images.unbind('load');
          images.load(function(e) {
            icon.removeClass('camicon');
            icon.addClass('smallcamicon');
            $('#take-photo-header').hide();
            $('#heading').html('Resize image');
            $('#offer-details-view').hide();
            $('#crop-image-div').show();
            var cropImage = $('#crop-image-div .tkpoto img');
            cropImage.attr('src', imgUrl);
            cropImage.one('load', function(e) {
              URL.revokeObjectURL(imgUrl);
            });

            var width = window.innerWidth;
            var jcrop_api, x, y, w, h;
            var options = {
              bgColor : 'black',
              bgOpacity : .4,
              setSelect : [width - 10, width - 10, 10, 10],
              allowResize : false,
              allowSelect : false,
              onChange : function updateCoords(c) {
                x = c.x;
                y = c.y;
                w = c.w;
                h = c.h;
              }
            };
            cropImage.Jcrop(options, function() {
              jcrop_api = this;
            });

            var goback = function() {
              jcrop_api.release();
              $('#heading').html('Give');
              $('#offer-details-view').show();
              $('#crop-image-div').hide();
            };

            $('#crop-image').unbind('click');
            $('#crop-image').click(function(e) {
              e.preventDefault();
              $("#share-btn").removeAttr('disabled');
              goback();
              $('#photo-x').val(x);
              $('#photo-y').val(y);
              $('#photo-w').val(w);
              $('#photo-h').val(h);
            });
          });
          images.attr('src', imgUrl);

        } catch (e) {
          alert('Unsupported browser');
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
      window.history.back();
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
      window.history.back();
    });
  }
}

function renderOfferDetail(offer) {
  var auth = offer.auth ? offer.auth : 'none';
  var template = authType[auth];
  $('#pre-share-detail').html(template.login_div);
  template.handler_register();
  initLocationAndRenderSharePopup(offer);
  $('.share-channel').click(function(e){
    shareOfferFromChannel($(this).attr('data-channel'));
    e.preventDefault();
  });
  $('#pre-share-popup input').keyup(onInput);
  if (getBrowserName() === 'Safari') {
    $('#share-sms-div').hide();
    $('.btn-group').css('width', '33%');
  }

  currentOffer = offer;
  var html = '';
  ga('send', 'event', 'button', 'show', 'give ' + offer.merchantName);
  var userId = s.userId;
  html += '<div id="share-after-login-div">';
  html += '<form id="share-form" type="POST" enctype="multipart/form-data" style="margin-bottom:0px;">';
  html += '<div id="share-image-add" class="image-add"><img src="' + offer.giveImageUrl + '" class="addimg add-photo show-picture" id="show-picture">';
  html += '<span class="imgshado"></span>';
  html += '<div class="add-photo-btn">';
  html += '<h2 id="take-photo-header">Add your own photo</h2>';
  html += '<div class="camicon"><img src="images/camicon.png">';
  html += '<input name="source" type="file" id="take-picture" class="camicon take-picture" style="height:60px;width:100%;opacity:0;" accept="image/*" />';
  html += '<input name="x" id="photo-x" type="hidden" />';
  html += '<input name="x" id="photo-y" type="hidden" />';
  html += '<input name="x" id="photo-w" type="hidden" />';
  html += '<input name="x" id="photo-h" type="hidden" /></div>';
  html += '</div>';
  html += '<h3>' + offer.merchantName + '</h3>';
  html += '<div class="opt-icon">';
  var local = getDisplayLocation(offer.locations);
  if (local) {
    if (offer.dist) {
      html += '<a href="' + generateMapUrl(offer.merchantName, local) + '" style="margin-right:10%;"><img class="website-img map-img" src="images/ic_map@2x.png" />' + '&nbsp;' + offer.dist + ' mi </a>';
    }
    html += '<a href="' + offer.merchantUrl + '"><img class="website-img" src="images/ic_web@2x.png" /></a>';
    html += '<a href="tel:' + local.phoneNumber + '"><img class="website-img" src="images/ic_phone@2x.png" /></a>';
  } else {
    html += '<a href="' + offer.merchantUrl + '"><img class="website-img" src="images/ic_web@2x.png" /></a>';
  }
  html += '</div>';
  html += '<input name="comment" id="comment-input" placeholder="Add comment" class="addcmt" style="width:94%;"/>';
  html += '</div>';
  html += '<div class="img-botm-patrn"></div>';
  if (offer.kikbakDesc) {
    html += '<div class="gv">';
    html += '<h2><img src="images/ic_gift@2x.png" />';
    html += 'Give ' + offer.giftDesc + '</h2>';
    html += '<h4>' + offer.giftDetailedDesc + '</h4>';
    html += '</div>';
    html += '<div class="crt">';
    html += '<h2><img width="19px" src="images/ic_give_trophy.png" /> ';
    html += 'Get ' + offer.kikbakDesc + '</h2>';
    html += '<h4>' + offer.kikbakDetailedDesc + '</h4>';
    html += '</div>';
  } else {
    html += '<div class="gv gv-only">';
    html += '<h2><img src="images/ic_gift@2x.png" />';
    html += 'Give ' + offer.giftDesc + '</h2>';
    html += '<h4>' + offer.giftDetailedDesc + '</h4>';
    html += '</div>';
  }
  html += '<div class="crt" style="margin:0 2%;">';
  html += '<a href="#" class="trm" id="term-btn" >&nbsp;Terms and Conditions</a>';
  html += '</div>';
  html += '<input id="share-btn" name="share" type="button" class="btn grd-btn botm-position" value="Give to friends" />';
  html += '</form>';
  html += '</div>';
  $('#offer-details-view').html(html);
  
  if (!offer.kikbakDesc) {
    var shareImg = $("#share-image-add");
    shareImg.height(Math.max(shareImg.height() + 25, shareImg.width()));
  }
  
  $('#term-btn').click(function(e) {
	e.preventDefault();
    showTerms(offer.tosUrl);
  });
  
  $('#offer-details-view').show('');
  $('#back-btn-div').hide('');
  $('#heading').html('Give');
  if (s.offerCount > 1) {
    $('#back-btn-div').show('');
  }

  $('.popup').css('min-height', $(document).height());
}

function initLocationAndRenderSharePopup(offer) {
  if (!initPage.p) {
    initPosition(function() {
      getDisplayLocation(offer.locations);
      renderSharePopup(offer);
    }, function() {
      renderSharePopup(offer);
    });
  } else {
    getDisplayLocation(offer.locations);
    renderSharePopup(offer);
  }
}

function renderSharePopup(offer) {
  var locations = offer.locations.sort(function(a, b) {return a.dist && b.dist ? a.dist - b.dist : -1});
  var options = '';
  $.each(offer.locations, function(i, l) {
    var selected = '';
    if (i === 0) {
      if ((l.dist && l.dist < 0.5) || offer.locations.length === 1) {
        selected = ' selected';
      } else {
    	options += '<option value="false">Pick location</options>'  
      }
    }
    var addr2 = l.address2 ? ' ' + l.address2 : '';
    options += '<option value="' + l.locationId + '" ' + selected + '>' + l.address1 + addr2 + ', ' + l.city + '</option>';
  });
  $('#location-sel').html(options);
  if (offer.locations.length === 1) {
	$('#share-locaition-div').hide('');
  } else {
	$('#share-locaition-div').show('');
  }
  
  if (offer.hasEmployeeProgram) {
	$('#share-employee-div').show();
  } else {
	$('#share-employee-div').hide();
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
        history.pushState({}, 'offer', '#offer');
        initPage();
      },
      error : showError
    });
  }
}

function shareOffer(offer) {
  ga('send', 'event', 'button', 'click', 'share offer');
  initUserInfoForm();
  $('#pre-share-popup').show();
}

function dataURItoBlob(dataURI, dataTYPE) {
  var binary = atob(dataURI.split(',')[1]), array = [];
  for(var i = 0; i < binary.length; i++) array.push(binary.charCodeAt(i));
  return new Blob([new Uint8Array(array)], {type: dataTYPE});
}

function uploadPhotoAfterLogin(cb) {
  var offer = currentOffer;
  var userId = s.userId;
  var src = $('#share-form .image-add img').attr('src');
  offer.sharedImageUrl = src;
  if (!$('#take-picture')[0].files || $('#take-picture')[0].files.length == 0) {
    cb();
  } else {
    var req = new FormData();
    var file = $('#take-picture')[0].files[0];
    var img = new Image();
    var URL = window.webkitURL || window.URL;
    var imgUrl = URL.createObjectURL(file);
    img.onload = function() {
      var max = 1024;
      var r = 0.25;//max / ((img.width > img.height) ? img.width : img.height);
      var h = img.height * r;
      var w = img.width * r;
      var canvas = document.createElement("canvas");
      canvas.setAttribute('width', w);
      canvas.setAttribute('height', h);
      var ctx = canvas.getContext("2d");
      ctx.drawImage(img, 0, 0);
      var dataurl = canvas.toDataURL("image/jpeg");

      var cropImage = $('#crop-image-div .tkpoto img');
      req.append('file', dataURItoBlob(dataurl, 'image/jpeg'));
      req.append('userId', userId);
      req.append('ios', getBrowserName() === 'Safari');
      req.append('x', $('#photo-x').val() / parseInt(cropImage.css('width')));
      req.append('y', $('#photo-y').val() / parseInt(cropImage.css('height')));
      req.append('w', $('#photo-w').val() / parseInt(cropImage.css('width')));
      req.append('h', $('#photo-h').val() / parseInt(cropImage.css('height')));
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
            offer.sharedImageUrl = response.url;
          }
          cb();
        },
        error : function() {
          cb();
        }
      });
    };
    img.src = imgUrl;
  }
}

function loginFb(cb) {
  ga('send', 'event', 'button', 'click', 'facebook auth');
  FB.login(function(response) {
    if (response.status === 'connected') {
      ga('send', 'event', 'button', 'click', 'facebook auth success');
      if (cb) {
        cb(response.authResponse);
        user.cb = cb(response.authResponse);
      } else {
        connectFb(response.authResponse.accessToken);
      }
    } else if (response.status === 'not_authorized') {
      ga('send', 'event', 'button', 'click', 'facebook auth failure');
      FB.login();
    } else {
      ga('send', 'event', 'button', 'click', 'facebook auth failure');
      FB.login();
    }
  }, {
    scope : "email,read_friendlists,publish_stream,publish_actions"
  });
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
  var local = getDisplayLocation(gift.merchant.locations);
  var dist = 0;
  if (local) {
    dist = computeDistanceDigit(local);
    html += '<a href="' + generateMapUrl(gift.merchant.name, local) + '"><img src="images/ic_map@2x.png" />' + '&nbsp;' + dist + ' mi</a>';
    html += '<a href="' + gift.merchant.url + '"><img class="website-img" src="images/ic_web@2x.png" /></a>';
    html += '<a href="tel:' + local.phoneNumber + '"><img class="website-img" src="images/ic_phone@2x.png" /></a>';
  } else {
    html += '<a href="' + gift.merchant.url + '"><img class="website-img" src="images/ic_web@2x.png" /></a>';
  }
  if (local && dist < 0.5) {
    doRedeemGift(gift);
    return;
  }
  html += '</div></div>';
  html += '<div class="gvrdm">';
  html += '<img width="40" height="40" src="https://graph.facebook.com/' + share.fbFriendId + '/picture?width=80&height=80">';
  html += '<div class="avtr-cmnt">';
  html += '<h2>' + share.friendName + '</h2>';
  html += '<h2>' + share.caption + '</h2>';
  html += '</div></div>';
  html += '<div class="gvrdm-botm-patrn"></div>';
  html += '<div class="crt">';
  html += '<h2>' + gift.desc + '</h2>';
  html += '<h4>' + gift.detailedDesc + '</h4>';
  html += '<div class="crt">';
  html += '<a href="#" class="trm" id="term-btn" >Terms and Conditions</a>';
  html += '</div>';
  html += '<button id="redeem-gift-instore-btn" class="btn grd-btn botm-position">Redeem now in store</button>';
  
  $('#redeem-details-view').html(html);
  
  $('#term-btn').click(function(e) {
	e.preventDefault();
    showTerms(gift.tosUrl);
  });
  
  $('#redeem-gift-instore-btn').click(function(e) {
    e.preventDefault();
    if (dist > 0.5) {
      alert("Oops! You have to be in the store to redeem.\n\nLooks like you might not be there yet. Try again?");
    } else {
      doRedeemGift(gift);
    }
  });
  
  $('#redeem-details-view').show('');
  if (getRedeems.counter > 1) {
    $('#back-btn-div').show('');
  }
  $('#heading').html('Redeem');
}

function doRedeemGift(gift) {
  var agId = gift.shareInfo[0].allocatedGiftId;
  $.ajax({
    dataType: 'json',
    type: 'GET',
    contentType: 'application/json',
    url: config.backend + 'kikbak/rewards/allocateBarcode/' + s.userId + '/' + agId + '/',
    success: function(json) {
      if (json && json.barcodeResponse && json.barcodeResponse.code) {
        var imgUrl = config.backend + 'kikbak/rewards/generateBarcode/' + s.userId + '/' + json.barcodeResponse.code + '/100/200/';
        $('#redeem-gift-success .pg-hedng').html(gift.merchant.name);
        $('#redeem-gift-success .cd-br-stin h1').html(gift.desc);
        $('#redeem-gift-success .cd-br-stin h3').html(gift.detailedDesc);
        $('#redeem-gift-success img').attr('src', imgUrl);
        $('#redeem-details-view').hide();
        $('#redeem-gift-success').show();
  	} else {
        alert('Sorry, we are currently running out of gift. It will be available soon. Please try later.');  
      }
    },
    error: function() {
      alert('Sorry, we are currently running out of gift. It will be available soon. Please try later.');  
    }
  });
  $('#redeem-gift-success .pg-hedng').html(gift.merchant.name);
  $('#redeem-gift-success .cd-br-stin h1').html(gift.desc);
  $('#redeem-gift-success .cd-br-stin h3').html(gift.detailedDesc);
  $('#redeem-gift-success img').attr('src', imgUrl);
  $('#redeem-details-view').hide();
  $('#redeem-gift-success').show();
}

function renderRedeemCreditDetail(credit) {
  var html = '';
  html += '<div class="image-add rdme"><img src="' + credit.imageUrl + '" class="addimge" style="height:100%;"><span class="imgshado"></span>';
  html += '<h3>' + credit.merchant.name + '</h3>';
  if (!credit.redeemedGiftsCount) {
    credit.redeemedGiftsCount = 1;
  }
  html += '<div class="msgg">Your gift has been redeemed by ' + credit.redeemedGiftsCount + ' friend' + (credit.redeemedGiftsCount > 1 ? 's' : '') + '</div>';
  html += '</div>';
  html += '<div class="gv">';
  html += '<p style="margin-bottom:0;">You have a reward ready to be claimed!</p>';
  html += '</div>';
  html += '<div class="crt">';
  html += '<h1>$' + credit.value.toFixed(2) + '</h1>';
  if (credit.rewardType == 'gift_card') {
    html += '<h5>Gift Card</h5>';
  }
  html += '<a href="#" class="trm" id="term-btn">Terms and Conditions</a>';
  html += '</div>';
  html += '<button id="claim-credit-form-btn" class="btn grd-btn botm-position">Claim reward now</button>';
  
  $('#redeem-details-view').html(html);
  
  $('#term-btn').click(function(e) {
	e.preventDefault();
    showTerms(credit.tosUrl);
  });
  
  $("#claim-credit-form-btn").click(function(e) {
	e.preventDefault();
    claimCreditForm(credit);
  });
  
  $('#redeem-details-view').show('');
  if (getRedeems.counter > 1) {
    $('#back-btn-div').show('');
  }
  $('#heading').html('Redeem');
}

function claimCreditForm(credit) {
  $('#redeem-details-view').hide();
  
  $('#claim-credit-form .pg-hedng').html(credit.merchant.name);
  $('#claim-credit-form .hedng h1').html('$' + credit.value);
  if (credit.rewardType == 'gift_card') {
    $('#claim-credit-form .hedng h3').html('Gift Card');
  }
  $('#claim-credit-form input[name="creditId"]').val(credit.id);
  $('#claim-credit-btn').attr('disabled', 'disabled');
  
  $('#claim-credit-div').show();
  
  $('#back-btn').unbind();
  $('#back-btn').click(function(e) {
    e.preventDefault();
    window.history.back();
  })
}

function getBrowserName() {
  var nVer = navigator.appVersion;
  var nAgt = navigator.userAgent;

  if(navigator.platform == 'iPhone' || navigator.platform == 'iPod'){
    if (nAgt.indexOf('Safari') != -1) {
      return 'Safari';
    }
  }
  return 'other';
  
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

})();

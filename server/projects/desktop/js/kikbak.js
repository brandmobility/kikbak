(function(){

'use strict';

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
};
var pageType = window.location.hash;
var merchantTypePrefix = "#merchant-";
if (window.mobilecheck()) {
  if (pageType.indexOf(merchantTypePrefix) === 0) {
    window.location.href = "/m/" + pageType;
  } else {
    alert("The offer is only available on mobile now. The desktop version is coming soon. Please visit kikbak.me for now");
    window.location.href = "https://kikbak.me";
  }
}

var user = {};
var storiesResponse = {};

(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
})(window,document,'script','//www.google-analytics.com/analytics.js','ga');
ga('create', 'UA-45251651-1');
ga('send', 'pageview');

var config = {
  backend: '',
  appId: 493383324061333
};

var s = (Storage) ? localStorage : {};

var merchantCustom = {
  verizon: {
    instruction: "NOTE to participants in this program you must be a current Verizon customer with your billing address in one of the following states: VA, NC, SC, GA, GL, LA, TX, KY, AL, LS, MS, AR, AS. If your billing address is not in a qualifying state you are not eligible to earn rewards.",
    program_name: "Southern States Referral Program"
  },
  all: {
    program_name: "Refer your friend"
  }
};

var placeHolder = {
  verizon: {
    "share-btn": "ID of Verizon employee that helped your [optional]"
  },
  all: {}
}

var authType = {
  facebook: {
    login_div: '<a id="loginFb" href="#">' +
                 '<img src="images/fb-btn-new.png" width="258" height="55" style="margin: 0 auto;">' + 
               '</a>',
    phone_div: ''
  },
  phone: {
    login_div: '<div style="width:50%;float:left;margin-top:5px">' +
                 '<input id="username-input" type="text" class="required" name="username" placeholder="Name">' +
                 '<input id="email-input" type="email" class="required" name="email" placeholder="Email">' +
               '</div><div style="width:50%;float:left;margin-top:10px;">' +
                 '<p>OR</p>' +
                 '<a id="loginFb" href="#">' + 
                   '<img src="images/fb-btn-new.png" width="220" height="40">' +
                 '</a>' +
               '</div>',
    phone_div: '<input id="phone-input" type="tel" class="required" name="phone" placeholder="Your Verizon phone number" />'

  },
  none: {
    login_div: '<div style="width:50%;float:left;margin-top:5px;">' +
                 '<input id="username-input" class="required" type="text" name="username" placeholder="Name">' +
                 '<input id="email-input" class="required" type="email" name="email" placeholder="Email">' +
               '</div><div style="width:50%;margin-top:5px;float:left">' +
                 '<p>OR</p>' +
                 '<a id="loginFb" href="#">' + 
                   '<img src="images/fb-btn-new.png" width="220" height="40">' +
                 '</a>' +
               '</div>',
    phone_div: ''
  }
};

$(document).ready(function() {
  $('body').show();
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
  };
  $(document).ajaxStart(function (){
    $('#spinner').show();
  });
  $(document).ajaxStop(function (){
    $('#spinner').hide();
  });
  $('.popup-close-btn').click(function(){
    $('.popup').hide();
  });
  
  var pageType = window.location.hash;
  var merchantTypePrefix = "#merchant-";
  if (pageType.indexOf(merchantTypePrefix) === 0) {
    var strArray = pageType.split('-', 2);
    var merchantTag = strArray[0] + '-' + strArray[1];
   	history.pushState({}, 'merchant', merchantTag);
  }
  $.ajaxSetup({ cache: true });
  $.getScript('//connect.facebook.net/en_US/all.js', function(){
    window.fbAsyncInit = fbInit;
  });
  
});

function onInput() {
  var valid = true;
  $('.required').each(function() {
    var thisElement = $(this);
    thisElement.css('border', '2px inset');
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
    $('#share-btn').removeAttr('disabled');
  } else {
    $('#share-btn').attr('disabled', 'disabled');
  }
}

function showError() {
  var msg = "Service is unavailable. Please try again later.";
  $('#error-popup p').html(msg);
  $('#error-popup').show()
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
  initPage();
}

function connectFb(resp) {
  var accessToken = resp.accessToken;
  s.accessToken = accessToken;
  user.accessToken = accessToken;
  user.type = 'facebook';
  user.fbId = resp.userID;

  $('#username-input').removeClass('required');
  $('#email-input').removeClass('required');
  $('#login_div').hide();

  $('#user-div img').attr('src', 'https://graph.facebook.com/' + user.fbId + '/picture?type=square');
  FB.api('/me', function(response) {
    $('#user-div h3').html(response.name);
    $('#user-div').show();
  });

  onInput();
}

function validateEmail(email) { 
  var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(email);
}

function validatePhone(phone) {
  if (!phone) return false;
  var normalizedPhone = phone.replace(/^\d/g, "");
  return normalizedPhone >= 10;
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
    if (!validateEmail(email)) {
      emailInput.css('border', '2px solid red');
      invalid = true;
    }
    user.email = email;
    user.name = $('#username-input').val();
    requestUrl += '/web?email=' + encodeURIComponent(user.email);
    requestUrl += '&name=' + encodeURIComponent(user.name);
  }
  var phoneInput = $('#phone-input');
  if (phoneInput) {
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

function cannotFindOffer() {
  window.location.href = 'https://kikbak.me';
}

function initPage() {
  var pageType = window.location.hash;
  var merchantTypePrefix = "#merchant-";
  if (pageType.indexOf(merchantTypePrefix) === 0) {
    var strArray = pageType.split('-');
    if (strArray.length == 2) {
      getOffersByMerchant(strArray[1]);
    } else {
      getOfferDetail();
    }
  } else {// Default
    cannotFindOffer();
  }
}

function getOffersByMerchant(merchant) {
  $.ajax({
    dataType: 'json',
    type: 'GET',
    contentType: 'application/json',
    url: config.backend + 'kikbak/v2/user/offer/0/' + merchant,
    success: function(json) {
  	  var offers = json.getUserOffersResponse.offers;
  	  if (!offers || offers.length <= 0) {
  		cannotFindOffer();
  	  }
      s.offerDetail = escape(JSON.stringify(offers[0]));
      var custom = merchantCustom.all;
      if (merchantCustom.hasOwnProperty(merchant.toLowerCase())) {
        custom = merchantCustom[merchant.toLowerCase()];
      }
      s.merchantCustom = escape(JSON.stringify(custom));
      var holder = placeHolder.all;
      if (placeHolder.hasOwnProperty(merchant.toLowerCase())) {
        holder = placeHolder[merchant.toLowerCase()];
      }
      s.holder = escape(JSON.stringify(holder));
  	  history.pushState({}, 'merchant-offer-detail', '#merchant-' + merchant + '-offer');
      initPage();
    },
    error: showError
  });
}

function shareOfferFromChannel(channel) {
  var story;
  for (var i in storiesResponse.stories) {
    if (storiesResponse.stories[i].type.toLowerCase() === channel) {
      story = storiesResponse.stories[i];
    }
  }

  var w = screen.width*3/4;
  var h = screen.height*3/4;
  var x = w/6;
  var y = h/6;

  if (channel === 'fb') {
    var landingUrl = storiesResponse.stories[0].landingUrl;
    var str = 'https://www.facebook.com/sharer/sharer.php?u=' + encodeURIComponent(landingUrl);
    window.open(str, '_blank', 'top='+y+',left='+x+',width='+w+',height='+h);
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
      window.open(str, '_blank', 'top='+y+',left='+x+',width='+w+',height='+h);
    } else if (channel === 'email') {
      var str = 'mailto:?content-type=text/html&subject=' + encodeURIComponent(subject)
          + '&body=' + encodeURIComponent(body);
      var win = window.open(str, '_blank', 'top='+y+',left='+x+',width='+w+',height='+h);
      setTimeout(function(){
        win.close();
      }, 100);
    } else if (channel === 'gmail') {
      var str = 'https://mail.google.com/mail/?view=cm&fs=1&tf=1&source=mailto&su=' + encodeURIComponent(subject)
          + '&body=' + encodeURIComponent(body);
      window.open(str, '_blank', 'top='+y+',left='+x+',width='+w+',height='+h);
    } else if (channel === 'yahoo') {
      var str = 'http://compose.mail.yahoo.com/?Subject=' + encodeURI3986(encodeURI3986(subject))
          + '&body=' + encodeURI3986(encodeURI3986(body));
      window.open(str, '_blank', 'top='+y+',left='+x+',width='+w+',height='+h);
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
}

function getOfferDetail() {
  var userId = 0;
  // s.userId;
  if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
    var offer = jQuery.parseJSON(unescape(s.offerDetail));
    var merchantCustom = jQuery.parseJSON(unescape(s.merchantCustom));
    if (!merchantCustom) {
      merchantCustom = {};
    }
    var holder = jQuery.parseJSON(unescape(s.holder));
    if (!holder) {
      holder = {};
    }
    renderOfferDetail(offer, merchantCustom, holder);
 
    $('#share-btn').click(function(e){
      e.preventDefault();
      getOfferStory(offer);
    });
    $('.share-channel').click(function(e){
      shareOfferFromChannel($(this).attr('data-channel'));
      e.preventDefault();
    });
    
    $('#loginFb').click(function(e){
      e.preventDefault();
      loginFb();
    });
  }
}

function renderOfferDetail(offer, custom, holder) {
  var html = '';
  ga('send', 'event', 'button', 'show', 'give ' + offer.merchantName);
  $('.brand-name').html(offer.merchantName);
  $('#container').css('background-image', 'url(' + offer.offerImageUrl + ')');
  $('#container').css('background-size', '100%');
  $('#container').css('background-position', 'initial initial');
  $('#container').css('background-repeat', 'initial initial');
  $('#header h1').css('background-image', 'url(' + offer.merchantLogoUrl + ')');
  $('#header h1').css('background-size', '100%');
  $('#header h1').css('background-position', 'initial initial');
  $('#header h1').css('background-repeat', 'no-repeat');

  var auth = offer.auth ? offer.auth : 'none';
  var template = authType[auth];

  for (var i in template) {
    $('#' + i).html(template[i]);
  }

  ga('send', 'event', 'button', 'show', 'facebook auth');
  
  $('#globe-href').attr('href', offer.merchantUrl);
  
  $('#show-picture').attr('src', offer.giveImageUrl);
  if (offer.kikbakDesc) {
    $('#ribbon-bottom').show();
    $('#ribbon-bottom h2').html(offer.kikbakDesc);
    $('#ribbon-bottom p').html(offer.kikbakDetailedDesc);
  } else {
    $('#ribbon-bottom').hide();	  
  }
  $('#ribbon h2').html(offer.giftDesc);
  $('#ribbon p').html(offer.giftDetailedDesc);

  for (var name in custom) {
    $('#' + name + ' p').html(custom[name]);
    $('#' + name).show();
  }
 
  for (var name in holder) {
    $('#' + name).attr('placeholder', custom[name]);
    $('#' + name).show();
  }
 
  $('#tos').click(function(e) {
    e.preventDefault();
    window.open(offer.tosUrl);
    return false;
  });

  $('input').keyup(onInput);
}

function getOfferStory(offer) {
  ga('send', 'event', 'button', 'click', 'share offer');
  registerUser(function() {
    var requestUrl = config.backend + 'kikbak/v2/share/getstories?userid=' + encodeURIComponent(user.id) + '&offerid=' + encodeURIComponent(offer.id) + '&platform=PC&imageurl=' + encodeURIComponent(offer.giveImageUrl);
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
    $.ajax({
      type: 'GET',
      contentType: 'application/json',
      url: requestUrl,
      success: function(json) {
        if (json && json.storiesResponse && json.storiesResponse.code) {
          storiesResponse = json.storiesResponse;
          var landingUrl = storiesResponse.stories[0].landingUrl;
          var landingHref = $('#landing');
          landingHref.attr('href', landingUrl);
          landingHref.html(landingUrl);
          $('#pre-share-div').hide();
          $('#share-div').show();
        } else {
          showError();
        }
      },
      error: showError
    });
  }); 
}

function loginFb() {
  ga('send', 'event', 'button', 'click', 'facebook auth');
  FB.login(function(response) {
    if (response.status === 'connected') {
      ga('send', 'event', 'button', 'click', 'facebook auth success');
      connectFb(response.authResponse);
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

function doShare(cb, type, url, message) {
  var offer = jQuery.parseJSON(unescape(s.offerDetail)),
    exp = {},
    data = {},
    req = {},
    str;
  
  exp['merchantId'] = offer.merchantId;
  exp['offerId'] = offer.id;
  exp['imageUrl'] = url; 
  exp['caption'] = message;
  exp['type'] = type;
  exp['platform'] = 'pc';
  data['experience'] = exp;
  req['ShareExperienceRequest'] = data;
  str = JSON.stringify(req);
  
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

function shareViaEmail(url, message) {
  ga('send', 'event', 'button', 'click', 'share via email');
  doShare(function(code, msg, url, resp) {
    window.location.href = 'mailto:?content-type=text/html&subject=' + encodeURIComponent(resp.template.subject) 
        + '&body=' + encodeURIComponent(resp.template.body);
  }, 'email', url, message);
}

function encodeURI3986(uri) {
  return encodeURIComponent(uri).replace(/!/g,'%21').replace(/\*/g,'%2A').replace(/\(/g,'%28').replace(/\)/g,'%29').replace(/'/g,'%27');
}

function shareViaYahoo(url, message) {
  ga('send', 'event', 'button', 'click', 'share via yahoo');
  doShare(function(code, msg, url, resp) {
    window.location.href = 'http://compose.mail.yahoo.com/?Subject=' + encodeURI3986(encodeURI3986(resp.template.subject))
        + '&body=' + encodeURI3986(encodeURI3986(resp.template.body));
  }, 'email', url, message);
}

function shareViaGmail(url, message) {
  ga('send', 'event', 'button', 'click', 'share via gmail');
  doShare(function(code, msg, url, resp) {
    window.location.href = 'https://mail.google.com/mail/?view=cm&fs=1&tf=1&source=mailto&su=' + encodeURIComponent(resp.template.subject) 
        + '&body=' + encodeURIComponent(resp.template.body);
  }, 'email', url, message);
}

function shareViaTwitter(url, message) {
  ga('send', 'event', 'button', 'click', 'share via twitter');
  doShare(function(code, msg, url, resp) {
	var fbUrl = resp.template.landingUrl;
	var msg = resp.template.body;
    window.twttr.ready(function() {
      var str = "https://twitter.com/share?";
      var params = [
        {name:"url", value:fbUrl},
        {name:"via", value:"kikbak"},
        {name:"count", value:"none"},                                                         
        {name:"text", value:msg}                                     
      ];
      $.each(params, function (i, item) {
        str += encodeURIComponent(item.name) + "=" + encodeURIComponent(item.value) + "&";
      });
      window.location.href = str;
    });
  }, 'twitter', url, message);
}

function shareViaFacebook(url, message) {
  ga('send', 'event', 'button', 'click', 'share via facebook');
  doShare(function(code, msg, url, resp) {
    var fbUrl = resp.template.landingUrl;
    var o = {
      'app_id' : config.appId,
      'url' : fbUrl.replace(/\//g, '\/'),
      'title': '\"' + encodeURIComponent('title') + '\"',
      'description': '\"' + encodeURIComponent('desc') + '\"'
    };
    var req = {
      'access_token' : s.accessToken,
      //'privacy': '{"value":"all_friends"}',
      'object' : JSON.stringify(o),
      'method' : 'POST',
      'format' : 'json'
    };
    var url = 'https://graph.facebook.com/me/objects/kikbakme:coupon';
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
          var url = 'https://graph.facebook.com/me/kikbakme:share';
          $.ajax({
            url : url,
            data : req,
            cache : false,
            contentType : false,
            dataType : 'json',
            type : 'GET',
            success : function(response) {
              $('#share-div').hide();
              $('#share-success').show();
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
  }, 'fb', url, message);
}

})();

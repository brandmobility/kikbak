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

var config = {
  backend: '',
  appId: 493383324061333
};

var s = (Storage) ? localStorage : {};

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
  
  window.twttr = (function (d,s,id) {
    var t, js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return; js=d.createElement(s); js.id=id;
    js.src="https://platform.twitter.com/widgets.js"; fjs.parentNode.insertBefore(js, fjs);
    return window.twttr || (t = { _e: [], ready: function(f){ t._e.push(f) } }); 
  }(document, "script", "twitter-wjs"));
});

function showError() {
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

      $('#user-div img').attr('src', 'https://graph.facebook.com/' + resp.userID + '/picture?type=square');
      FB.api('/me', function(response) {
        $('#user-div h3').html(response.name);
        $('#user-div').show();
      });
      afterUserLogin();
    },
    error: showError
  });
}

function afterUserLogin() {
  $('#facebook-div').hide();
  $('#share-div').css('opacity', '1.0');
  $('#share-div').show();
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
  	  history.pushState({}, 'merchant-offer-detail', '#merchant-' + merchant + '-offer');
      initPage();
    },
    error: showError
  });
}


function getOfferDetail() {
  var userId = 0;
  // s.userId;
  if (typeof userId !== 'undefined' && userId !== null && userId !== '') {
    var offer = jQuery.parseJSON(unescape(s.offerDetail));
    renderOfferDetail(offer);
    
    $('#share-email').click(function(e){
      e.preventDefault();
      shareOffer(offer, shareViaEmail);
    });
    $('#share-facebook').click(function(e){
       e.preventDefault();
       shareOffer(offer, shareViaFacebook);
    });
    $('#share-twitter').click(function(e){
      e.preventDefault();
      shareOffer(offer, shareViaTwitter);
    });
    $('#share-gmail').click(function(e){
      e.preventDefault();
      shareOffer(offer, shareViaGmail);
    });
    $('#share-yahoo').click(function(e){
      e.preventDefault();
      shareOffer(offer, shareViaYahoo);
    });
    
    $('#loginFb').click(function(e){
      e.preventDefault();
      loginFb();
    });

    $('input [name="comment"]').bind('input',function(){
      ga('send', 'event', 'button', 'click', 'add comment');
    });
    $('#take-picture').change(function(e) {
      ga('send', 'event', 'button', 'click', 'take picture');
      var files = e.target.files;
      var file;

      if (files && files.length > 0) {
        file = files[0];
        try {
          var URL = window.webkitURL || window.URL;
          var imgUrl = URL.createObjectURL(file);
          $('#show-picture').attr('src', imgUrl);
          $('#take-picture').hide();
        } catch (e) {
          alert('Unsupported browser');
        }
      }
    });
  }
}

function renderOfferDetail(offer) {
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
    
  $('#facebook-div').show();
  $('#share-div').css('opacity', '0.2');
  $('#share-div').show();
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
  
  $('#tos').click(function(e) {
    e.preventDefault();
    window.open(offer.tosUrl);
    return false;
  });
}

function shareOffer(offer, cb) {
  ga('send', 'event', 'button', 'click', 'share offer');
  var userId = s.userId;
  updateFbFriends(userId, function() {
	shareOfferAfterLogin(offer, cb);
  });
}

function dataURItoBlob(dataURI, dataTYPE) {
  var binary = atob(dataURI.split(',')[1]), array = [];
  for(var i = 0; i < binary.length; i++) array.push(binary.charCodeAt(i));
  return new Blob([new Uint8Array(array)], {type: dataTYPE});
}

function shareOfferAfterLogin(offer, cb) {
  var message = $('#addcomment').val();
  var userId = s.userId;
  cb(offer.giveImageUrl, message);
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

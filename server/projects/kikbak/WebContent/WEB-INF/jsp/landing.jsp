<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.net.*"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head prefix="og: http://ogp.me/ns# product: http://ogp.me/ns/product#">
        <title>Kikbak</title>
        <meta property="fb:app_id" content="493383324061333">
        <meta property="og:url" content="${url}">
        <meta property="og:type" content="kikbakme:coupon">
        <meta property="og:title" content="${title}">
        <meta property="og:image" content="${shareInfo.imageUrl}">
        <meta property="og:description" content="${body}
        <c:choose><c:when test="${not empty location}">${shareInfo.friendName} was at ${gift.merchant.name} at ${location.address1}, ${location.city}, ${location.state}</c:when></c:choose>">
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0" />
        <c:choose>
        <c:when test="${not mobile}">
        <link href="css/style.css" rel="stylesheet" type="text/css" />
        </c:when>
        <c:otherwise>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen">
        <link href="css/style-mobile.css" rel="stylesheet" type="text/css" />
        </c:otherwise>
        </c:choose>
        <link rel="shortcut icon" href="http://kikbak.me/wp-content/uploads/2013/10/fav.png">
    </head>
    <body>
        <c:choose>
        <c:when test="${not mobile}">
        <div id="container"></div>
        <div id="wrap">
            <div id="green-overlay" style="display:none;"></div>
            <div id="main" class="clearfix">
                <div id="info">
                    <div class="info-header clearfix">
                        <c:choose>
                        <c:when test="${not empty shareInfo.fbFriendId}">
                        <img src="https://graph.facebook.com/${shareInfo.fbFriendId}/picture?width=124&height=124" class="userphoto" width="62px" height="62px" />
                        <c:choose>
                        <c:when test="${not empty shareInfo.caption}">
                        <h3 style="margin-top:5px;float:none;">${shareInfo.friendName}</h3>
                        <p>${shareInfo.caption}</p>
                        </c:when>
                        <c:otherwise>
                        <h3 style="float:left;">${shareInfo.friendName}</h3>
                        </c:otherwise>
                        </c:choose>
                        </c:when>
                        <c:otherwise>
                        <c:choose>
                        <c:when test="${not empty shareInfo.caption}">
                        <h3 style="margin-top:5px;float:none;">${shareInfo.friendName}</h3>
                        <p>${shareInfo.caption}</p>
                        </c:when>
                        <c:otherwise>
                        <h3 style="float:left;margin-top:10px;">${shareInfo.friendName}</h3>
                        </c:otherwise>
                        </c:choose>
                        </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="info-content">
                        <img src="${shareInfo.imageUrl}" width="340px" height="340px" class="main-image" />
                        <div class="overlay">
                            <img class="overlay-img" src="img/blk-shad.png" width="340" height="188" />
                        </div>

                        <div class="content-detail clearfix">
                            <h3>${gift.merchant.name}</h3>
                            <a href="https://maps.google.com/maps?q=${encodeMerchantName}%40${location.latitude},${location.longitude}" target="_blank"><div class="location"><img src="img/icon-location.png" width=22 height=22></div></a>
                            <a href="${gift.merchant.url}" target="_blank"><div class="globe"><img src="img/icon-globe.png" width=22 height=22 /></div></a>
                        </div>
                    </div>
                    <c:choose>
                    <c:when test="${not empty location}">
                    <div class="info-footer">
                        <img src="img/icon-info.png" class="info-icon" />
                    <c:choose>
                    <c:when test="${not empty shareInfo.employeeId}">
                        ${shareInfo.friendName} was helped by <strong>${shareInfo.employeeId}</strong> at <strong>${gift.merchant.name}</strong>: <strong>${location.address1}, ${location.city}, ${location.state}</strong><br />
                        If you visit the same store, ${shareInfo.employeeId} can help you too.
                    </c:when>
                    <c:otherwise>
                        ${shareInfo.friendName} was at <strong>${gift.merchant.name}</strong>: <strong>${location.address1}, ${location.city}, ${location.state}</strong>
                    </c:otherwise>
                    </c:choose>
                    </div>
                    </c:when>
                    </c:choose>
                </div>
                <div id="main-right">
                    <div id="header">
                        <a href="${gift.merchant.url}" target="_blank">
                            <img class="logo" />
                        </a>
                        <c:choose>
                        <c:when test="${gift.validationType == 'barcode'}">
                        <button id="print-btn" style="display:none;" class="btn grd-btn print-btn" onclick="window.print()"><img src="img/printer.png" /><span>  Print page</span></button>
                        </c:when>
                        </c:choose>
                    </div>
                    <div id="success-msg" style="display:none;">
                        <h1>Success!</h1>
                        <h3 style="margin-bottom:25px;">Bring this offer into ${gift.merchant.name} Store to redeem your gift.</h3>
                    </div>
                    <div id="welcome-msg">
                        <h3>An exclusive offer for <strong>${gift.merchant.name}</strong></h3>
                    </div>
                    <div id="ribbon">
                    	<h2>${gift.desc}</h2>
                        <p>${gift.detailedDesc}</p>
                    </div>
                    <div id="facebook-div">
                        <c:choose>
                        <c:when test="${gift.merchant.shortname != 'Verizon'}">
                        <p class="title">Connect with Facebook to claim your gift.</p>
                        <a id="loginFb" href="#"> <img src="img/fb-btn-new.png" width="258" height="55" style="margin: 0 auto;" /></a>
                        <p class="disclaimer">We use Facebook to make it easy for you to share, redeem and share gifts.<br/> We will never post without your permission.</p>
                        </c:when>
                        <c:otherwise>
                        <div id="zipcode-div">
                            <input type="text" id="zipcode-input" placeholder="Your zip code"></input>
                        </div>
                        <div id="zipcode-qualify-div" style="display:none;">
                            <button id="anonymous-redeem-barcode-btn" class="btn grd-btn mini-btn">Generate in-store coupon</button>
                            <button id="anonymous-redeem-barcode-online-btn1" class="btn grd-btn mini-btn">Redeem online</button>
                            <p class="redeem-note">Or connect with Facebook to send the offer to the Kikbak app for iOS or Android. Use it to easily access your gift at the store.</p>
                            <a id="loginFb" href="#"> <img src="img/fb-btn-new.png" width="258" height="55" style="margin: 0 auto;" /></a>
                        </div>
                        <div id="zipcode-nonqualify-div" style="display:none;">
                            <p class="redeem-note" style="margin-top:5px;" >This offer is not eligible for stores in your area, but you can still  use it  online.</p>
                            <button id="anonymous-redeem-barcode-online-btn2" class="btn grd-btn">Redeem online now</button>
                        </div>
                        </c:otherwise>
                        </c:choose>
                    </div>
                    <c:choose>
                    <c:when test="${gift.validationType != 'barcode'}">
                    <div id="redeem-div" style="display: none;">
                        <p class="redeem-note">Awesome! Your gift is now stored to your Facebook <br/>account. To use your gift, download the Kikbak app. It’ll be<br/> waiting for whenever you’re ready to use it.</p>
                        <div>
                            <a href="https://itunes.apple.com/us/app/kikbak/id707697884?mt=8"><img src="img/app-store.png" /></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <a href="https://play.google.com/store/apps/details?id=com.referredlabs.kikbak&hl=en"><img src="img/google-play.png" /></a>
                        </div>
                    </div>
                    </c:when>
                    <c:otherwise>
                    <div id="redeem-div" style="display: none;">
                        <div id="redeem-btn-div">
                            <p class="redeem-note">
                                Print out your code now or visit this link from your phone at the store
                            </p>
                            <input type="button" id="redeem-barcode-btn" class="btn grd-btn" value="Generate offer to use in store" />
                            <p class="redeem-note">
                                You will now find your offer in the Kikbak app for iOS and Android.<br />Download the app now.
                            </p>
                            <div>
                                <a href="https://itunes.apple.com/us/app/kikbak/id707697884?mt=8"><img src="img/app-store.png" /></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                <a href="https://play.google.com/store/apps/details?id=com.referredlabs.kikbak&hl=en"><img src="img/google-play.png" /></a>
                            </div>
                        </div>
                        <p id="barcode-note"></p>
                        <img id="barcode" src="" style="display: none;" />
                        <p id="barcode-val"></p>
                        <a id="barcode-link" target="_blank" class="btn grd-btn" style="display:none;">Use online now</a>
                    </div>
                    </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        <div id="footer-wrap">
            <div id="footer" class="clearfix">
                <div class="footer-left"><a href="" onclick="window.open('${gift.tosUrl}');return false;" class="lft">Terms & Conditions</a></div>
                <div class="footer-right"><a href="https://kikbak.me" class="rit">&copy; Referred Labs, 2013</a></div>
            </div>
        </div>
        </c:when>
        <c:otherwise>
        <div id="landing-div">
            <c:choose>
            <c:when test="${not empty location}">
            <div id="jumbotron">
                <p style="margin-top: 10px; margin-left: 10px;">
                    <c:choose>
                    <c:when test="${not empty shareInfo.employeeId}">
                        ${shareInfo.friendName} was helped by <strong>${shareInfo.employeeId}</strong> at <strong>${gift.merchant.name}</strong>: <strong>${location.address1}, ${location.city}, ${location.state}</strong><br />
                        If you visit the same store, ${shareInfo.employeeId} can help you too.
                    </c:when>
                    <c:otherwise>
                        ${shareInfo.friendName} was at <strong>${gift.merchant.name}</strong>: <strong>${location.address1}, ${location.city}, ${location.state}</strong>
                    </c:otherwise>
                    </c:choose>
                </p>
            </div>
            </c:when>
            </c:choose>
            <div style="position:relative">
                <img src="${shareInfo.imageUrl}" width="100%" height="330px" />
                <img src="img/blk-shad.png" width="100%" height="200px" style="position: absolute; top: 130px;" />
            </div>
            <div class="widget">
                <div class="span4">
                    <h3 style="padding: 0px; margin: 0px;">${gift.merchant.name}</h3>
                </div>
                <div class="span4" style="margin-top:5px;">
                    <div class="pull-left">
                        <a href="https://maps.google.com/maps?q=${encodeMerchantName}%40${location.latitude},${location.longitude}" target="_blank">
                            <span class="glyphicon glyphicon-map-marker" style="color: #FFFFFF; font-size: 18px; margin-right: 20px; position: relative;"></span>
                        </a>
                    </div>
                    <div class="pull-left">
                        <a href="${gift.merchant.url}" target="_blank"><div class="globe"></div></a>
                    </div>
                    <div class="pull-left">
                        <a href="tel:${location.phoneNumber}"> <span class="glyphicon glyphicon-earphone" style="color: #FFFFFF; font-size: 18px; position: relative;"></span>
                        </a>
                    </div>
                </div>
                <div class="clearfix"></div>
            </div>
            <div class="page-header" style="padding: 0;">
                <div class="row show-grid" style="margin: 0; padding-top: 12px;">
                    <c:choose>
                    <c:when test="${not empty shareInfo.fbFriendId}">
                    <div class="col-md-4" style="padding-left: 5px">
                        <img src="https://graph.facebook.com/${shareInfo.fbFriendId}/picture?width=120&height=120" class="img-rounded" width="60px" />
                    </div>
                    <div class="col-md-8">
                        <c:choose>
                        <c:when test="${not empty shareInfo.caption}">
                        <h3 style="color: #3a3a3a; font-size: 20px; font-weight: bold;">${shareInfo.friendName}</h3>
                        <p>${shareInfo.caption}</p>
                        </c:when>
                        <c:otherwise>
                        <h3 style="color: #3a3a3a; font-size: 20px; font-weight: bold; margin-top: 36px;">${shareInfo.friendName}</h3>
                        </c:otherwise>
                        </c:choose>
                    </div>
                    </c:when>
                    <c:otherwise>
                    <div class="col-md-8" style="padding: 0 0 0 10px;">
                        <c:choose>
                        <c:when test="${not empty shareInfo.caption}">
                        <h3 style="color: #3a3a3a; font-size: 20px; font-weight: bold;">${shareInfo.friendName}</h3>
                        <p>${shareInfo.caption}</p>
                        </c:when>
                        <c:otherwise>
                        <h3 style="color: #3a3a3a; font-size: 20px; font-weight: bold;">${shareInfo.friendName}</h3>
                        </c:otherwise>
                        </c:choose>
                    </div>
                    </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="page-header">
                <div class="col-md-6">
                    <div
                        style="background-color: #767676; padding-top: 20px; padding-bottom: 4px;">
                        <h3 style="font-size: 38px; font-family: HelveticaNeue Bold; line-height: 18px;">${gift.desc}</h3>
                        <p style="font-family: HelveticaNeueLT Pro 55 Roman; font-size: 18px; margin: 0;">${gift.detailedDesc}</p>
                    </div>
                </div>
                <div class="col-md-1"></div>
            </div>
            <div class="clearfix"></div>
            <div id="facebook-div">
                <c:choose>
                <c:when test="${gift.merchant.shortname == 'Verizon'}">
                <div id="zipcode-div">
                    <input type="number" id="zipcode-input" placeholder="Your zip code"></input>
                </div>
                <div id="zipcode-qualify-div" style="text-align: center;display:none;">
                    <button id="anonymous-redeem-barcode-btn" class="btn grd-btn mini-btn">Generate in-store<br/>coupon</button>
                    <button id="anonymous-redeem-barcode-online-btn1" class="btn grd-btn mini-btn">Redeem online</button>
                    <div class="page-header" style="padding-left: 10px; padding-right: 10px; margin: 10px 0 0 0; text-align: center">
                        <p style="font-size: 15px; font-family: HelveticaNeueLT Pro 45 Lt;">Or connect with Facebook to send the offer to the Kikbak app for iOS and Android. Use it to easily access your gift at the store.</p>
                        <div style="height: 40px;margin-bottom:20px;">
                            <a id="loginFb" href="#"> <img src="img/facebook-button.png" width=240 /> </a>
                        </div>
                    </div>
                </div>
                <div id="zipcode-npnequalify-div" style="text-align: center;display:none;">
                     <p style="font-size: 15px; font-family: HelveticaNeueLT Pro 45 Lt;" >Sorry, this offer is not eligible for stores in your area. However, you can still redeem this offer online.</p>
                     <button id="anonymous-redeem-barcode-online-btn2" class="btn grd-btn">Redeem online now</button>
                </div>
                </c:when>
                <c:otherwise>
                <div class="page-header"
                    style="padding-left: 10px; padding-right: 10px; margin: 10px 0 0 0; text-align: center">
                    <p style="font-size: 15px; font-family: HelveticaNeueLT Pro 45 Lt;">Or connect with Facebook to send the offer to the Kikbak app for iOS and Android. Use it to easily access your gift at the store.</p>
                    <div style="height: 40px;margin-bottom:20px;">
                        <a id="loginFb" href="#"> <img src="img/facebook-button.png" width=240 />
                        </a>
                    </div>
                </div>
                </c:otherwise>
                </c:choose>
            </div>
            <c:choose>
            <c:when test="${gift.validationType != 'barcode'}">
            <div id="redeem-div" style="display: none;">
                <div style="text-align: center; padding-top: 20px;">
                    <p>
                    Awesome! Your gift is now stored to <br/> your Facebook
                    account. To use your gift, <br/> download the Kikbak app. It’ll
                    be waiting <br/> for whenever you’re ready to use it.
                    </p>
                </div>
                <div style="text-align: center; padding-bottom: 20px;">
                    <a href="https://itunes.apple.com/us/app/kikbak/id707697884?mt=8"><img src="img/app-store.png" width="40%" /></a>&nbsp;&nbsp;&nbsp;&nbsp;
                    <a href="https://play.google.com/store/apps/details?id=com.referredlabs.kikbak&hl=en"><img src="img/google-play.png" width="40%" /></a>
                </div>
            </div>
            </c:when>
            <c:otherwise>
            <div id="redeem-div" style="display: none;">
                <div style="text-align: center; padding-top: 20px;">
                    <input type="button" id="redeem-barcode-btn" class="btn grd-btn" value="Redeem now in store" />
                    <p>
                    You will now find your offer in the Kikbak app for iOS and Android.<br />Download the app now.
                    </p>
                </div>
                <div style="text-align: center; padding-bottom: 20px;">
                    <a href="https://itunes.apple.com/us/app/kikbak/id707697884?mt=8"><img src="img/app-store.png" width="40%" /></a>&nbsp;&nbsp;&nbsp;&nbsp;
                    <a href="https://play.google.com/store/apps/details?id=com.referredlabs.kikbak&hl=en"><img src="img/google-play.png" width="40%" /></a>
                </div>
            </div>
            </c:otherwise>
            </c:choose>
            <div class="clearfix"></div>
            <div id="footer">
                <div class="pull-left">
                    <a href="" onclick="window.open('${gift.tosUrl}');return false;"
                        class="lft" style="color: #F3F3F3;font-size:12px;font-family:arial;">Terms & Conditions</a>
                </div>
                <div class="pull-right">
                    <a href="https://kikbak.me" class="rit" style="color: #C3C3C3;font-size:12px;font-family:arial;">&copy;
                        Referred Labs, 2013</a>
                </div>
            </div>
            <div class="clearfix"></div>
        </div>
    </div>
    <div id="redeem-gift-success" style="display:none;">
        <h1 class="pg-hedng">${gift.merchant.name}</h1>
        <div class="hedng hedbk2" id="success-msg">
        <h2>Success!</h2>
            <p>Please show this screen when paying.</p>
        </div>
        <div class="hedbk1-botm-patrn"></div>
        <div class="cd-br-stin">
            <h2>OFFER</h2>
            <h1>${gift.desc}</h1>
            <h3>${gift.detailedDesc}</h3>
        </div>
        <div class="redeem-online">
            <p id="barcode-note"></p>
            <img id="barcode" src="" />
            <p id="barcode-val"></p>
            <a id="barcode-link" target="_blank" style="display:none;" class="btn grd-btn">Use online now</a>
        </div>
        <div class="crt share-offer">
            <p>Share the same offer with your friends.<br/>
            You earn for every friend that redeems.</p>
        </div>
        <div style="text-align: center; padding-top: 20px;" class="share-offer">
            <a id="share-offer-btn" href="/m/${gift.merchant.shortname}" target="_blank" class="btn grd-btn">I'm ready to give to others</a>
        </div>
    </div>
    </c:otherwise>
    </c:choose>
    <div id="fb-root"></div>
    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js"></script>
    <script>
    var agId;
    
    <c:choose>
    <c:when test="${not mobile}">
    $('#container').css('background-image', 'url(${gift.defaultGiveImageUrl})');
    $('#container').css('background-size', '100%');
    $('#container').css('background-position', 'initial initial');
    $('#container').css('background-repeat', 'initial initial');
    $('#header .logo').attr('src', '${merchantUrl}');
    </c:when>
    </c:choose>

    <c:choose>
    <c:when test="${gift.expired or hostBlock}">
    setTimeout(function() {
      alert('The offer has expired');
      window.location.href = "https://kikbak.me";
    }, 1000);
    </c:when>
    </c:choose>

    <c:choose>
    <c:when test="${gift.merchant.shortname == 'Verizon'}">
    var zipcodeInput = $('#zipcode-input');
    zipcodeInput.keyup(function() {
      var zipcode = zipcodeInput.val().replace(/^\d$/g, "");
      if (zipcode.length === 5) {
        $.ajax({
          type: 'GET',
          contentType: 'application/json',
          url: 'v2/share/validateZip?zipCode=' + zipcode + '&offerId=${gift.offerId}',
          success: function(json) {
            var valid = json.zipValidationResponse.status;
            if (valid === 'OK') {
              $('#zipcode-qualify-div').show();
              $('#zipcode-nonqualify-div').hide();
            } else {
              $('#zipcode-qualify-div').hide();
              $('#zipcode-nonqualify-div').show();
            }
          }, 
          error: showError
        });
      } else {
        $('#zipcode-qualify-div').hide();
        $('#zipcode-nonqualify-div').hide();
      }
    });    
    $('#anonymous-redeem-barcode-btn').click(function() {
      claimGift(0, false);
    });
    $('#anonymous-redeem-barcode-online-btn1').click(function() {
      claimGift(0, true);
    });
    $('#anonymous-redeem-barcode-online-btn2').click(function() {
      claimGift(0, true);
    });
    </c:when>
    </c:choose>

    function computeDistanceDigit(p, lat, lng) {
      if (typeof(Number.prototype.toRad) === "undefined") {
    	Number.prototype.toRad = function() {
          return this * Math.PI / 180;
        }
      }

      var lat1 = parseFloat(lat);
      var lat2 = parseFloat(p.latitude);
      var lon1 = parseFloat(lng);
      var lon2 = parseFloat(p.longitude);

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

    function registerCb(userId) {
      if (userId == ${shareInfo.friendUserId}) {
        alert('Sorry, you cannot claim your own gift.');
        return;
      }
      $('#facebook-div').hide();
      $('#redeem-div').show();
      preClaimGift(userId);
      $('#redeem-barcode-btn').click(function() {
        claimGift(userId);
      });
    }
    
    function preClaimGift(userId) {
      var url = 'rewards/claimgift/' + userId + '/${code}';
      $.ajax({
        dataType: 'json',
        type: 'GET',
        contentType: 'application/json',
        url: url,
        success: function(json) {
          if (json && json.claimGiftResponse) {
            var resp = json.claimGiftResponse;
            if (resp.status && resp.status === 'EXPIRED') {
              $('#redeem-div p').html('The gift has expired.');
            } else if (resp.status && resp.status === 'LIMIT_REACH') {
              $('#redeem-div p').html('The gift is not available anymore.');
            } else if (resp.status && resp.status === 'NO_GIFTS_AVAILABLE') {
              $('#redeem-div p').html('Sorry, you can only use this offer once.');
            } else if (resp.status !== 'OK') {
              showError();
              return;
            }
            agId = resp.agId;
          } else {
            showError();
          }
        },
        error: showError
      });
    }

    function renderBarcode(json, online) {
      if (json && json.barcodeResponse && json.barcodeResponse.code) {
        var imgUrl = 'unauth/rewards/generateBarcode/' + json.barcodeResponse.code + '/100/200/';
        $('#barcode').attr('src', imgUrl);
        $('#barcode-val').html(json.barcodeResponse.code);
        if (online) {
          $('#barcode-val').css('color', 'red');
          $('#barcode-note').html('Your coupon code');
          $('#barcode').hide();
          $('#barcode-link').attr('href', 'http://www.verizon.com');
          $('#barcode-link').show();
          $('.share-offer').hide();
          $('#share-offer-btn').hide();
          $('#success-msg h3').html('You have claimed your gift.');
          $('#success-msg p').html('You have claimed your gift.');
        } else {
          $('#barcode-val').css('color', 'black');
          $('#barcode').show();
          $('.share-offer').show();
          $('#share-offer-btn').show();
          $('#success-msg h3').html('Bring this offer into ${gift.merchant.name} Store to redeem your gift.');
          $('#success-msg p').html('Please show this screen when paying.');
        }
        <c:choose>
        <c:when test="${not mobile}">
        $('#welcome-msg').hide();
        $('#redeem-btn-div').hide();
        if (!online) {
          $('#header .logo').css('float', 'left');
          $('#header .logo').css('margin-left', '50px');
          $('#green-overlay').css('height', '145px');
          $('#print-btn').show();
        } else {
          $('#green-overlay').css('height', '120px');
        }
        $('#success-msg').show();
        $('#green-overlay').show();
        </c:when>
        <c:otherwise>
        $('#landing-div').hide();
        $('#redeem-gift-success').show();
        </c:otherwise>
        </c:choose>
      } else {
        alert('Sorry, we are currently running out of gift. It will be available soon. Please try later.');  
      }
    }
   
    function claimGift(userId, online) {
      <c:choose>
      <c:when test="${gift.validationType == 'barcode'}">
      if (userId) {
        $.ajax({
          dataType: 'json',
          type: 'GET',
          contentType: 'application/json',
          url: 'rewards/allocateBarcode/' + userId + '/' + agId + '/',
          success: function(json) {
            renderBarcode(json);
          },
          error: function() {
            alert('Sorry, we are currently running out of gift. It will be available soon. Please try later.');  
          }
        });
      } else {
         $.ajax({
          dataType: 'json',
          type: 'GET',
          contentType: 'application/json',
          url: 'unauth/rewards/allocateAnonymousBarcode/${code}',
          success: function(json) {
            if (json && json.barcodeResponse && json.barcodeResponse.code) {
              $('#facebook-div').hide();
              $('#redeem-div').show();
              renderBarcode(json, online);
            }
          },
          error: function() {
            alert('Sorry, we are currently running out of gift. It will be available soon. Please try later.');  
          }
        });
      }
      </c:when>
      </c:choose>
    }

    </script>
    <script src="js/register.js"></script>
</body>
</html>

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
                        <img src="https://graph.facebook.com/${shareInfo.fbFriendId}/picture?width=124&height=124" class="userphoto" width="62px" height="62px" />
                        <c:choose>
                        <c:when test="${not empty shareInfo.caption}">
                        <h3 style="margin-top:5px;">${shareInfo.friendName}</h3>
                        <p>${shareInfo.caption}</p>
                        </c:when>
                        <c:otherwise>
                        <h3 style="float:left;">${shareInfo.friendName}</h3>
                        </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="info-content">
                        <img src="${shareInfo.imageUrl}" width="340px" height="340px" class="main-image" />
                        <div class="overlay"></div>
                        <div class="content-detail clearfix">
                            <h3>${gift.merchant.name}</h3>
                            <a href="https://maps.google.com/maps?q=${encodeMerchantName}%40${location.latitude},${location.longitude}" target="_blank"><div class="location"></div></a>
                            <a href="${gift.merchant.url}" target="_blank"><div class="globe"></div></a>
                        </div>
                    </div>
                    <c:choose>
                    <c:when test="${not empty location}">
                    <div class="info-footer">
                        ${shareInfo.friendName} was at <strong>${gift.merchant.name}</strong> at <strong>${location.address1}, ${location.city}, ${location.state}</strong>
                    </div>
                    </c:when>
                    </c:choose>
                </div>
                <div id="main-right">
                    <div id="header">
                        <a href="${gift.merchant.url}" target="_blank">
                            <h1>${gift.merchant.name}</h1>
                        </a>
                        <c:choose>
                        <c:when test="${gift.validationType == 'barcode'}">
                        <button id="print-btn" style="display:none;" class="btn grd-btn print-btn" onclick="window.print()"><img src="img/printer.png" /><span>  Print page</span></button>
                        </c:when>
                        </c:choose>
                    </div>
                    <div id="success-msg" style="display:none;">
                        <h1>Success!</h1>
                        <h3>You have claimed your gift<br/>Bring this offer into ${gift.merchant.name} to redeem your gift.</h3>
                    </div>
                    <div id="welcome-msg">
                        <h3><strong>${shareInfo.friendName}</strong> used <strong>Kikbak</strong> to give you <br/> an exclusive offer for <strong>${gift.merchant.name}</strong></h3>
                    </div>
                    <div id="ribbon">
                        <h2>${gift.desc}</h2>
                        <p>${gift.detailedDesc}</p>
                        <small>offer valid in-store only</small>
                    </div>
                    <div id="facebook-div">
                        <p class="title">Connect with Facebook to access your gift.</p>
                        <a id="loginFb" href="#"> <img src="img/fb-btn-new.png" width="258" height="55" style="margin: 0 auto;" /></a>
                        <p class="disclaimer">We use your Facebook ID to personalize the offers you share and notify you when you've earned a reward.<br/> We will never post without your permission.</p>
                    </div>
                    <c:choose>
                    <c:when test="${gift.validationType != 'barcode'}">
                    <div id="redeem-div" style="display: none;">
                        <p>Awesome! Your gift is now stored to your Facebook <br/>account. To use your gift, download the Kikbak app. It’ll be<br/> waiting for whenever you’re ready to use it.</p>
                        <div>
                            <a href="https://itunes.apple.com/us/app/kikbak/id707697884?mt=8"><img src="img/app-store.png" /></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <a href="https://play.google.com/store/apps/details?id=com.referredlabs.kikbak&hl=en"><img src="img/google-play.png" /></a>
                        </div>
                    </div>
                    </c:when>
                    <c:otherwise>
                    <div id="redeem-div" style="display: none;">
                        <img id="barcode" src="" />
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
        <div>
            <c:choose>
            <c:when test="${not empty location}">
            <div id="jumbotron">
                <p style="margin-top: 10px; margin-left: 10px;">${shareInfo.friendName}
                was at <strong>${gift.merchant.name}</strong> at <strong>${location.address1},
                    ${location.city}, ${location.state}</strong>
                </p>
            </div>
            </c:when>
            </c:choose>

            <img src="${shareInfo.imageUrl}" width="100%" height="330px" />
            <c:choose>
            <c:when test="${not empty location}">
            <img src="img/blk-shad.png" width="100%" height="330px"
            style="position: absolute; top: 65px;" />
            </c:when>
            <c:otherwise>
            <img src="img/blk-shad.png" width="100%" height="330px"
            style="position: absolute; top: 0;" />
            </c:otherwise>
            </c:choose>
            <div class="widget">
                <div class="span4">
                    <h3
                        style="padding: 0px; margin: 0px; font-family: HelveticaNeueLTPro-Lt">${gift.merchant.name}</h3>
                </div>
                <div class="span4">
                    <div class="pull-left">
                        <a
                            href="https://maps.google.com/maps?q=${encodeMerchantName}%40${location.latitude},${location.longitude}"
                            target="_blank"> <span
                                class="glyphicon glyphicon-map-marker"
                                style="color: #FFFFFF; font-size: 18px; margin-right: 20px; position: relative;"></span>
                        </a>
                    </div>
                    <div class="pull-left">
                        <a href="${gift.merchant.url}" target="_blank"><div
                                class="globe"></div></a>
                    </div>
                    <div class="pull-left">
                        <a href="tel:${location.phoneNumber}"> <span
                                class="glyphicon glyphicon-earphone"
                                style="color: #FFFFFF; font-size: 18px; position: relative;"></span>
                        </a>
                    </div>
                </div>
                <div class="clearfix"></div>
            </div>
            <div class="page-header" style="padding: 0;">
                <div class="row show-grid" style="margin: 0; padding-top: 22px;">
                    <div class="col-md-4" style="padding-left: 5px">
                        <img
                        src="https://graph.facebook.com/${shareInfo.fbFriendId}/picture?width=120&height=120"
                        class="img-rounded" width="60px" />
                    </div>
                    <div class="col-md-8">
                        <c:choose>
                        <c:when test="${not empty shareInfo.caption}">
                        <h3 style="color: #3a3a3a; font-size: 20px; font-weight: bold;">${shareInfo.friendName}</h3>
                        <p>${shareInfo.caption}</p>
                        </c:when>
                        <c:otherwise>
                        <h3
                            style="color: #3a3a3a; font-size: 20px; font-weight: bold; margin-top: 36px;">${shareInfo.friendName}</h3>
                        </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            <div class="page-header">
                <div class="col-md-6">
                    <div
                        style="background-color: #767676; padding-top: 20px; padding-bottom: 10px;">
                        <h3
                            style="font-size: 38px; font-family: HelveticaNeueLTPro-Bd; padding: 0px; margin: 0px;">${gift.desc}</h3>
                        <p
                        style="font-family: HelveticaNeueLTPro-Roman; font-size: 18px; padding: 0px; margin: 0px;">${gift.detailedDesc}</p>
                        <small
                            style="font-family: HelveticaNeueLTPro-Lt; font-size: 12px; padding: 0px; margin: 0px;">offer
                            valid in-store only</small>
                    </div>
                </div>
                <div class="col-md-1"></div>
            </div>
            <div class="clearfix"></div>
            <div id="facebook-div">
                <div class="page-header"
                    style="padding-left: 10px; padding-right: 10px; margin-top: 25px; text-align: center">
                    <p style="font-size: 15px; font-family: HelveticaNeueLTPro-Lt;">Connect
                    with Facebook to access your gift.</p>
                    <div style="height: 40px">
                        <a id="loginFb" href="#"> <img src="img/btn_facebook.png"
                            style="width: 100%;" /> <span
                                style="position: relative; top: -35px; left: 15px; color: #FFFFFF; font-size: 20px;">Connect
                                with Facebook</span>
                        </a>
                    </div>
                    <p align="center"
                    style="padding-top: 20px; font-family: HelveticaNeueLTPro-Lt; font-size: 10px;">
                    We use Facebook to make it easy for you to share and redeem. <br/>
                    We will never post on Facebook without your permission.
                    </p>
                </div>
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
                    <input type="button" id="redeem-barcode-btn" data-code="${code}" class="btn grd-btn" value="Redeem now in store" />
                    <p>
                    The Kikbak app makes it easy to access and use your gift. Download it now and you'll find your gift waiting.
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
    </c:otherwise>
    </c:choose>
    <div id="fb-root"></div>
    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script
        src="//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js"></script>
    <script>
    <c:choose>
    <c:when test="${not mobile}">
    $('#container').css('background-image', 'url(${gift.defaultGiveImageUrl})');
    $('#container').css('background-size', '100%');
    $('#container').css('background-position', 'initial initial');
    $('#container').css('background-repeat', 'initial initial');
    $('#header h1').css('background-image', 'url(${merchantUrl})');
    $('#header h1').css('background-size', '100%');
    $('#header h1').css('background-position', 'initial initial');
    $('#header h1').css('background-repeat', 'no-repeat');
    </c:when>
    </c:choose>
    function registerCb(userId) {
      if (userId == ${shareInfo.friendUserId}) {
        alert('Sorry, you cannot claim your own gift.');
        return;
      }
      $('#redeem-barcode-btn').click(function() {
        window.location.href = '/m/#redeem';
      });
      var url = config.backend + 'rewards/claimgift/' + userId + '/${code}';
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
            <c:choose>
            <c:when test="${gift.validationType == 'barcode'}">
            $('#welcome-msg').hide();
            var imgUrl = 'rewards/generateBarcode/' + userId + '/' + response.agId + '/100/200/';
            $('#barcode').attr('src', imgUrl);
            $('#header h1').css('float', 'left');
            $('#header h1').css('margin-left', '50px');
            $('#print-btn').show();
            $('#success-msg').show();
            $('#green-overlay').show();
            </c:when>
            </c:choose>
            $('#facebook-div').hide();
            $('#print-btn').show();
            $('#redeem-div').show();
          } else {
            showError();
          }
        },
        error: showError
      });
    }
    </script>
    <script src="js/register.js"></script>
</body>
</html>

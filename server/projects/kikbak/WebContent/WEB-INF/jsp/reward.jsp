<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.net.*"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head prefix="og: http://ogp.me/ns# product: http://ogp.me/ns/product#">
        <title>Kikbak</title>
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
            <div id="main" class="clearfix">
                <div id="header">
                    <a href="${credit.merchant.url}" target="_blank">
                        <h1>${credit.merchant.name}</h1>
                    </a>
                </div>
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
                            <c:choose>
                            <c:when test="${not empty redeemCountSingle}">
                            <p style="color:#FFFFFF;">Your gift has been redeemed by ${redeemCount} friend</p>
                            </c:when>
                            <c:otherwise>
                            <p style="color:#FFFFFF;">Your gift has been redeemed by ${redeemCount} friends</p>
                            </c:otherwise>
                            </c:choose>
                        </div>
                        <c:choose>
                        <c:when test="${not empty location}">
                        <div class="info-footer">
                            ${shareInfo.friendName} was at <strong>${gift.merchant.name}</strong> at <strong>${location.address1}, ${location.city}, ${location.state}</strong>
                        </div>
                        </c:when>
                        </c:choose>
                    </div>
                </div>
                <div id="main-right">
                    <h3><strong>You have a reward to be claimed!</strong></h3>
                    <div id="ribbon-blue">
                        <h2><img src="img/victor-icon.png" class="victor-img"/>  ${credit.desc}</h2>
                    </div>
                    <div id="ribbon-blue-dec"></div>
                    <div class="clearfix"></div>
                    <div id="facebook-div">
                        <p class="title">Connect with Facebook to access your reward.</p>
                        <a id="loginFb" href="#"> <img src="img/fb-btn-new.png" width="258" height="55" style="margin: 0 auto;" /></a>
                        <p class="disclaimer">We use your Facebook ID to personalize the offers you share and notify you when you've earned a reward.<br/> We will never post without your permission.</p>
                    </div>
                    <div id="redeem-div" style="display:none;">
                        <form id="claim-credit-form">
                            <div class="frm-fed">
                                <h3>Please provide the following to redeem:</h3>
                                <input type="hidden" name="creditId" value="${credit.id}" class="bsnm" />
                                <input type="text" name="phoneNumber" placeholder="Phone number (number only)" class="bsnm" />
                                <input type="text" name="name" placeholder="First Last Name" class="bsnm" />
                                <input type="text" name="street" placeholder="Street" class="bsnm" />
                                <input type="text" name="apt" placeholder="Apartment or Unit # (number only)" class="bsnm" />
                                <input type="text" name="city" placeholder="City" class="bsnm" />
                                <input type="text" name="state" placeholder="State" class="bsnm" />
                                <input type="text" name="zipcode" placeholder="Zip (number only)" class="bsnm" />
                                <p>Your reward should arrive in 2 - 4 weeks.</p>
                            </div>
                            <button id="claim-credit-btn" class="btn grd-btn" disabled="disabled">Submit</button>
                        </form>
                    </div>
                    <div id="redeem-success" style="display:none;">
                        <h3>Success!</h3>
                        <p>Your reward claim has been submitted.</p>
                    </div>
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
            <div id="jumbotron">
                <p><strong>You have a reward ready to be claimed!</strong></p>
                <c:choose>
                <c:when test="${not empty redeemCountSingle}">
                <p>Your gift has been redeemed by ${redeemCount} friend</p>
                </c:when>
                <c:otherwise>
                <p>Your gift has been redeemed by ${redeemCount} friends</p>
                </c:otherwise>
                </c:choose>
            </div>

            <img src="${shareInfo.imageUrl}" width="100%" height="330px" />
            <img src="img/blk-shad.png" width="100%" height="330px" style="position:absolute;top:65px;" />
            <div class="widget">
                <div class="span4">
                    <h3 style="padding: 0px; margin: 0px; font-family: HelveticaNeueLTPro-Lt">${gift.merchant.name}</h3>
                </div>
                <div class="span4">
                    <div class="pull-left">
                        <a href="https://maps.google.com/maps?q=${encodeMerchantName}%40${location.latitude},${location.longitude}">
                            <span class="glyphicon glyphicon-map-marker" style="color:#FFFFFF;font-size:18px;margin-right: 20px;position:relative;"></span>
                        </a>
                    </div>
                    <div class="pull-left">
                        <a href="${gift.merchant.url}"><div class="globe"></div></a>
                    </div>
                    <div class="pull-left">
                        <a href="tel:${location.phoneNumber}">
                            <span class="glyphicon glyphicon-earphone" style="color:#FFFFFF;font-size:18px;position:relative;"></span>
                        </a>
                    </div>
                </div>
                <div class="clearfix"></div>
            </div>
            <div class="page-header" style="padding:0;">
                <div class="row show-grid" style="margin:0;padding-top:22px;">
                    <div class="col-md-4" style="padding-left:10px">
                        <img src="https://graph.facebook.com/${shareInfo.fbFriendId}/picture?width=120&heoght=120" class="img-rounded" width="60px" />
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
                </div>
            </div>
            <div class="page-header">
                <div class="col-md-6">
                    <div style="background:url(img/blue-btn-bk-hvr.png);background-size:cover;padding-top:30px;padding-bottom:15px;text-align=center">
                        <h3><img src="img/victor-icon.png" class="victor-img"/>  ${credit.desc}</h3>
                    </div>
                    <div class="col-md-1"></div>
                </div>
                <div class="clearfix"></div>
                <div id="facebook-div">
                    <div class="page-header" style="padding-left: 10px; padding-right: 10px; margin-top: 25px; text-align: center">
                        <p style="font-size: 15px; font-family: HelveticaNeueLTPro-Lt;">Connect with Facebook to access your gift.</p>
                        <div style="height: 40px">
                            <a id="loginFb" href="#">
                                <img src="img/btn_facebook.png" style="width: 100%;" />
                                <span style="position: relative; top: -35px; left: 15px; color: #FFFFFF; font-size: 20px;">Connect with Facebook</span>
                            </a>
                        </div>
                        <p align="center" style="padding-top: 20px; font-family: HelveticaNeueLTPro-Lt; font-size: 10px;">
                            We use Facebook to make it easy for you to share and redeem. <br/>
                            We will never post on Facebook without your permission.
                        </p>
                    </div>
                </div>
                <div class="clearfix"></div>
                <div id="footer">
                    <div class="pull-left">
                        <a href="" onclick="window.open('${credit.tosUrl}');return false;" class="lft" style="color:#C3C3C3;font-size:12px;font-family:arial;">Terms & Conditions</a>
                    </div>
                    <div class="pull-right">
                        <a href="https://kikbak.me" class="rit" style="color:#C3C3C3;font-size:12px;font-family:arial;">&copy; Referred Labs, 2013</a>
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
        <script src="//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js"></script>
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
        function registerCb(userId) {
          $('#facebook-div').hide();
          $('#redeem-div').show();
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
            var keys = ['phoneNumber', 'apt', 'zipcode'];
            for (var i in keys) {
              var k = keys[i];
              if (/^\d+$/.test(o[k].replace(/^\s+|\s+$/g, ''))) {
                o[k] = o[k].replace(/^\s+|\s+$/g, '');
              } else {
                alert('Sorry, the information you entered is not recognized as valid.\n\nPlease ensure that is accurate and try again');
                return;
              }
            }
            return;
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
              url: 'rewards/claim/' + userId + '/',
              success: function(json) {
                $('#redeem-div').hide();
            	$('#redeem-success').show();
              },
              error: showError
            });
            return false;
          });  
        }
        </c:when>
        <c:otherwise>
        function registerCb(userId) {
          window.location.href = '/m/#redeem';
        }
        </c:otherwise>
        </c:choose>
        </script>
        <script src="js/register.js"></script>
    </body>
</html>

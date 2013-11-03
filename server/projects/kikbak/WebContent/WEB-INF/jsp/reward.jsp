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
                    <div id="redeem-div">
                        <p>Your reward can be redeemed in-store only.<br />Download the Kikbak app for iOS or Android. Your reward will be waiting for whenever you're ready to use it.</p>
                        <div>
                            <a href="https://itunes.apple.com/us/app/kikbak/id707697884?mt=8"><img src="img/app-store.png" /></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <a href="https://play.google.com/store/apps/details?id=com.referredlabs.kikbak&hl=en"><img src="img/google-play.png" /></a>
                        </div>
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
                <div id="redeem-div"> 
                    <div style="text-align:center;padding-top:20px;">
                        <div style="text-align:center;font-size:16px;padding-top:20px;padding-bottom:20px;font-weight:500">
                            <p>Your reward can be redeemed in-store only.<br /><br />Download the Kikbak app for iOS or Android. Your reward will be waiting for whenever you’re ready to use it.</p>
                        </div>
                    </div>
                    <div style="text-align:center;padding-bottom:20px;">
                        <a href="https://itunes.apple.com/us/app/kikbak/id707697884?mt=8"><img src="img/app-store.png" width="40%" /></a>&nbsp;&nbsp;&nbsp;&nbsp;
                        <a href="https://play.google.com/store/apps/details?id=com.referredlabs.kikbak&hl=en"><img src="img/google-play.png" width="40%" /></a>
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
            if (!window.mobilecheck()) {
                $('#container').css('background-image', 'url(${gift.defaultGiveImageUrl})');
                $('#container').css('background-size', '100%');
                $('#container').css('background-position', 'initial initial');
                $('#container').css('background-repeat', 'initial initial');
                $('#header h1').css('background-image', 'url(${merchantUrl})');
                $('#header h1').css('background-size', '100%');
                $('#header h1').css('background-position', 'initial initial');
                $('#header h1').css('background-repeat', 'no-repeat');
            }
        </script>
    </body>
</html>

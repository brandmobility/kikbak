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
        <meta property="og:description" content="${body}}">
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0" />
        <link href="css/bootstrap.css" rel="stylesheet" media="screen">
        <link href="css/landing.css" rel="stylesheet" type="text/css" />
        <c:choose>
        <c:when test="${not mobile}">
        <style>
            .widget {
            position: absolute;
            margin-top: -70px;
            padding-left: 15px;
            width: 100%;
            }

            .widget h3 {
            color: #FFFFFF;
            font-size: 26px;
            }

            .info {
            margin: 0px;
            padding: 0px;
            list-style-type: none;
            padding-top: 10px;
            }

            .info li {
            font-size: 12px;
            color: #636161;
            background-image: url(img/info-icon.png);
            background-repeat: no-repeat;
            background-position: top left;
            width: 100%;
            padding-left: 30px;
            }

            .info li strong {
            color: #000000;
            }

            @font-face {
            font-family: HelveticaNeueLTPro-Bd;
            src: url('fonts/HelveticaNeueLTPro-Bd.otf');
            }

            @font-face {
            font-family: HelveticaNeueLTPro-Lt;
            src: url('fonts/HelveticaNeueLTPro-Lt.otf');
            }

            @font-face {
            font-family: HelveticaNeueLTPro-Roman;
            src: url('fonts/HelveticaNeueLTPro-Roman.otf');
            }

            .ribbon {
            margin-left: 106px;
            background-image: url(img/ribbon-right-bkg.png);
            background-repeat: repeat-y;
            background-position: right;
            padding-right: 8px;
            color: #FFFFFF;
            text-align: center;
            margin-top: 20px;
            }

            .ribbon h3 {
            font-family: HelveticaNeueLTPro-Bd;
            font-size: 42px;
            margin: 0px;
            padding: 0px;
            }

            .globe {
            background-image: url(img/icon-globe.png);
            background-repeat: no-repeat;
            background-size: 100%;
            width: 18px;
            height: 18px;
            margin-right: 20px;
            margin-top: 7px;
            position: relative;
            }
        </style>
        </c:when>
        <c:otherwise>
        <style>
            body {
            background-image: url(img/background.png);
            background-repeat: repeat;
            overflow-x: hidden;
            }

            .location {
            background-image: url(img/icon-place.png);
            background-repeat: no-repeat;
            width: 12px;
            height: 18px;
            margin-right: 20px;
            margin-top: 5px;
            position: relative;
            }

            .phone {
            background-image: url(img/icon-phone.png);
            background-repeat: no-repeat;
            width: 19px;
            height: 22px;
            margin-top: 5px;
            position: relative;
            }

            h3 {
            color: #FFFFFF;
            }

            .widget {
            top: 0;
            position: relative;
            margin-top: -70px;
            padding-left: 10px;
            padding-right: 10px;
            }

            .widget h3 {
            font-family: HelveticaNeueLTPro-Lt;
            font-size: 28px;
            }

            .col-md-4 {
            width: 70px;
            float: left;
            }

            .img-rounded {
            border-bottom: solid 1px #8f8f8f;
            border-left: solid 1px #8f8f8f;
            border-right: solid 1px #cacaca;
            }

            #footer {
            background: #757575; /* Old browsers */
            background: -moz-linear-gradient(top, #757575 0%, #757575 36%, #454545 100%);
            /* FF3.6+ */
            background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #757575),
            color-stop(36%, #757575), color-stop(100%, #454545));
            /* Chrome,Safari4+ */
            background: -webkit-linear-gradient(top, #757575 0%, #757575 36%, #454545 100%);
            /* Chrome10+,Safari5.1+ */
            background: -o-linear-gradient(top, #757575 0%, #757575 36%, #454545 100%);
            /* Opera 11.10+ */
            background: -ms-linear-gradient(top, #757575 0%, #757575 36%, #454545 100%);
            /* IE10+ */
            background: linear-gradient(to bottom, #757575 0%, #757575 36%, #454545 100%);
            /* W3C */
            filter: progid:DXImageTransform.Microsoft.gradient(  startColorstr='#757575',
            endColorstr='#454545', GradientType=0); /* IE6-9 */
            margin-top: 10px;
            padding: 10px;
            }

            #footer .pull-left {
            color: #FFFFFF;
            }

            #footer .pull-right {
            color: #c3c3c3;
            }

            .col-md-1 {
            float: left;
            width: 5%;
            }

            .col-md-6 {
            float: left;
            width: 98%;
            background-image: url(img/ribbon-right-bkg.png);
            background-repeat: repeat-y;
            background-position: right;
            padding-left: 0;
            padding-right: 7px;
            color: #FFFFFF;
            text-align: center;
            }

            .col-md-6 h3 {
            font-size: 38px;
            font-family: HelveticaNeueLTPro-Bd;
            }

            @font-face {
            font-family: HelveticaNeueLTPro-Bd;
            src: url('fonts/HelveticaNeueLTPro-Bd.otf');
            }

            @font-face {
            font-family: HelveticaNeueLTPro-Lt;
            src: url('fonts/HelveticaNeueLTPro-Lt.otf');
            }

            @font-face {
            font-family: HelveticaNeueLTPro-Roman;
            src: url('fonts/HelveticaNeueLTPro-Roman.otf');
            }

            .globe {
            background-image: url(img/icon-globe.png);
            background-repeat: no-repeat;
            background-size: 100%;
            width: 18px;
            height: 18px;
            margin-right: 20px;
            margin-top: 3px;
            position: relative;
            }

            .page-header {
            margin: 10px 0;
            }
            #footer {
            position: relative;
            height: 40px;
            margin: 0;
            }
        </style>
        </c:otherwise>
        </c:choose>
    </head>
    <body>
        <c:choose>
        <c:when test="${not mobile}">
        <div id="container">
        </div>
        <div class="main-bdy">
            <div id="subcontainer">
                <div id="header">
                    <h1>${gift.merchant.name}</h1>
                </div>
                <div class="clearfix"></div>
                <div id="pageconatiner">
                    <div>
                        <div class="col-md-4">
                            <div class="sidebar">
                                <div class="page-header">
                                    <div>
                                        <div class="col-md-4">
                                            <img src="https://graph.facebook.com/${shareInfo.fbFriendId}/picture?type=square" class="img-rounded" width="80px" height="80px" />
                                        </div>
                                        <div class="col-md-8">
                                            <c:choose>
                                            <c:when test="${not empty shareInfo.caption}">
                                            <h3 style="font-family: HelveticaNeueLTPro-Bd">${shareInfo.friendName}</h3>
                                            <p>${shareInfo.caption}</p>
                                            </c:when>
                                            <c:otherwise>
                                            <h3 style="margin-top:30px;font-family: HelveticaNeueLTPro-Bd">${shareInfo.friendName}</h3>
                                            </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                                <img src="${shareInfo.imageUrl}" width="340px" height="340px" style="margin-top:10px;border-radius:10px;" />
                                <img src="img/blk-shad.png" width="340px" height="220px" style="margin-top:10px;border-radius:10px;position:absolute;top:210px;left:10px;" />
                                <div class="widget">
                                    <div class="span4">
                                        <h3 style="padding: 0px; margin: 0px; font-family: HelveticaNeueLTPro-Lt">${gift.merchant.name}</h3>
                                    </div>
                                    <div class="span4">
                                        <div class="pull-left">
                                            <a href="https://maps.google.com/maps?q=${encodeMerchantName}%40${location.latitude},${location.longitude}">
                                                <span class="glyphicon glyphicon-map-marker" style="color: #FFFFFF; font-size: 18px; margin-right: 20px; margin-top: 5px; position: relative;"></span>
                                            </a>
                                        </div>
                                        <div class="pull-left">
                                            <a href="${gift.merchant.url}"><div class="globe"></div></a>
                                        </div>
                                    </div>
                                    <div class="clearfix"></div>
                                </div>
                                <c:choose>
                                <c:when test="${not empty location}">
                                <ul class="info">
                                    <li style="min-height:20px;">${shareInfo.friendName} was at <strong>${gift.merchant.name}</strong> at <strong>${location.address1}, ${location.city}, ${location.state}</strong>
                                    </li>
                                </ul>
                                </c:when>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-8">
                        <div style="padding-left: 145px; padding-right: 10px; text-align: center; padding-top: 45px;">
                            <h3 style="font-size: 20px;">
                                <strong>${shareInfo.friendName}</strong> used <strong>Kikbak</strong>
                                to give you <br /> an exclusive offer for <strong>${gift.merchant.name}</strong>
                            </h3>
                        </div>
                        <div class="ribbon">
                            <div style="background-color: #767676; padding-top: 10px; padding-bottom: 10px; padding-left:40px;">
                                <h3>${gift.desc}</h3>
                                <p style="font-family: HelveticaNeueLTPro-Lt; font-size: 22px; margin: 0px; padding: 0px;">${gift.detailedDesc}</p>
                                <small style="font-family: HelveticaNeueLTPro-Lt; font-size: 12px; margin: 0px; padding: 0px;">offer valid in-store only</small>
                            </div>
                        </div>
                        <div id="facebook-div">
                            <div style="padding-left: 145px; padding-right: 10px; text-align: center; padding-top: 20px;">
                                <div style="width: 350px; margin: 0 auto;">
                                    <p>Connect with Facebook to access your gift.</p>
                                    <a id="loginFb" href="#">
                                        <img src="img/btn_facebook.png" style="width:100%;" />
                                        <span style="position:relative;top:-35px;left:15px;color:#FFFFFF;font-size:20px;">Connect with Facebook</span>
                                    </a>
                                </div>
                            </div>
                            <div style="padding-left: 165px; padding-right: 30px; text-align: center; padding-top: 20px;">
                                <p style="font-family: HelveticaNeueLTPro-Lt; font-size: 12px;">
                                We use Facebook to make it easy for you to share and redeem. <br /> 
                                We will never post on Facebook without your permission.
                                </p>
                            </div>
                        </div>
                        <div id="redeem-div" style="display:none;">
                            <div style="padding-left:165px;padding-right:30px;text-align:center;padding-top:20px;">
                                <p>Awesome! Your gift is now stored to your Facebook <br /> 
                                account. To use your gift, download the Kikbak app. It’ll be <br />
                                waiting for whenever you’re ready to use it.
                                </p>
                            </div>
                            <div style="padding-left:165px;padding-right:10px;text-align:center;">
                                <a href="#"><img src="img/app-store.png" /></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                <a href="#"><img src="img/google-play.png" /></a>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="clearfix"></div>
            </div>
        </div>
        <div id="footer">
            <div id="footer-content">
                <div class="row show-grid">
                    <div class="col-md-6 w1">
                        <a href="${gift.tosUrl}" class="lft" style="color:#FFFFFF;">Terms & Conditions</a>
                    </div>
                    <div class="col-md-6 w2" style="text-align: right">
                        <a href="https://kikbak.me" class="rit" style="color:#FFFFFF;">&copy; Referred Labs, 2013</a>
                    </div>
                </div>
            </div>
        </div>
        </c:when>
        <c:otherwise>
        <div>
            <c:choose>
            <c:when test="${not empty location}">
            <div id="jumbotron">
                <p style="margin-top:10px;margin-left:10px;">${shareInfo.friendName} was at <strong>${gift.merchant.name}</strong> at <strong>${location.address1}, ${location.city}, ${location.state}</strong>
                </p>
            </div>
            </c:when>
            </c:choose>

            <img src="${shareInfo.imageUrl}" width="100%" height="330px" />
            <c:choose>
            <c:when test="${not empty location}">
            <img src="img/blk-shad.png" width="100%" height="330px" style="position:absolute;top:65px;" />
            </c:when>
            <c:otherwise>
            <img src="img/blk-shad.png" width="100%" height="330px" style="position:absolute;top:0;" />
            </c:otherwise>
            </c:choose>
            <div class="widget">
                <div class="span4">
                    <h3 style="padding: 0px; margin: 0px; font-family: HelveticaNeueLTPro-Lt">${gift.merchant.name}</h3>
                </div>
                <div class="span4">
                    <div class="pull-left">
                        <a href="https://maps.google.com/maps?q=${encodeMerchantName}%40${location.latitude},${location.longitude}">
                            <span class="glyphicon glyphicon-map-marker" style="color:#FFFFFF;font-size:22px;margin-right: 20px;position:relative;"></span>
                        </a>
                    </div>
                    <div class="pull-left">
                        <a href="${gift.merchant.url}"><div class="globe"></div></a>
                    </div>
                    <div class="pull-left">
                        <a href="tel:${location.phoneNumber}">
                            <span class="glyphicon glyphicon-earphone" style="color:#FFFFFF;font-size:22px;position:relative;"></span>
                        </a>
                    </div>
                </div>
                <div class="clearfix"></div>
            </div>
            <div class="page-header" style="padding:0;">
                <div class="row show-grid" style="margin:0;padding-top:22px;">
                    <div class="col-md-4" style="padding-left:5px">
                        <img src="https://graph.facebook.com/${shareInfo.fbFriendId}/picture?type=square" class="img-rounded" width="60px"/>
                    </div>
                    <div class="col-md-8">
                        <c:choose>
                        <c:when test="${not empty shareInfo.caption}">
                        <h3 style="color:#3a3a3a;font-size:20px;font-weight:bold;">${shareInfo.friendName}</h3>
                        <p style="margin-left:70px">${shareInfo.caption}</p>
                        </c:when>
                        <c:otherwise>
                        <h3 style="color:#3a3a3a;font-size:20px;font-weight:bold;margin-top:12px;">${shareInfo.friendName}</h3>
                        </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            <div class="page-header">
                <div class="col-md-6">
                    <div style="background-color:#767676;padding-top:20px;padding-bottom:10px;">
                        <h3 style="font-size:38px;font-family:HelveticaNeueLTPro-Bd;padding:0px;margin:0px;">${gift.desc}</h3>
                        <p style="font-family:HelveticaNeueLTPro-Roman;font-size:18px;padding:0px;margin:0px;">${gift.detailedDesc}</p>
                        <small style="font-family:HelveticaNeueLTPro-Lt;font-size:12px;padding:0px;margin:0px;">offer valid in-store only</small></div>
                </div>
                <div class="col-md-1"></div>
            </div>
            <div class="clearfix"></div>
            <div id="facebook-div">
                <div class="page-header" style="padding-left:10px;padding-right:10px;margin-top:25px;text-align:center">
                    <p style="font-size:15px;font-family:HelveticaNeueLTPro-Lt;">Connect with Facebook to access your gift. </p>
                    <div style="height:40px">
                        <a id="loginFb" href="#">
                            <img src="img/btn_facebook.png" style="width:100%;"/>
                            <span style="position:relative;top:-35px;left:15px;color:#FFFFFF;font-size:20px;">Connect with Facebook</span>
                        </a>
                    </div>
                    <p align="center" style="padding-top:10px;font-family:HelveticaNeueLTPro-Lt;font-size:10px;">
                    We use Facebook to make it easy for you to share and redeem. <br /> 
                    We will never post on Facebook without your permission.
                    </p>
                </div>
            </div>
            <div id="redeem-div" style="display:none;">
                <div style="text-align:center;padding-top:20px;">
                    <p>Awesome! Your gift is now stored to <br />
                    your Facebook account. To use your gift, <br />
                    download the Kikbak app. It’ll be waiting <br />
                    for whenever you’re ready to use it.
                    </p>
                </div>
                <div style="text-align:center;padding-bottom:20px;">
                    <a href="#"><img src="img/app-store.png" width="40%" /></a>&nbsp;&nbsp;&nbsp;&nbsp;
                    <a href="#"><img src="img/google-play.png" width="40%" /></a>
                </div>
            </div>
            <div class="clearfix"></div>
            <div id="footer">
                <div class="pull-left">
                    <a href="${gift.tosUrl}" class="lft" style="color:#FFFFFF;">Terms & Conditions</a>
                </div>
                <div class="pull-right">
                    <a href="https://kikbak.me" class="rit" style="color:#FFFFFF;">&copy; Referred Labs, 2013</a>
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
        if (typeof localStorage !== 'undefined') {
        localStorage.friendUserId = ${shareInfo.friendUserId};
        localStorage.code = '${code}';
        }
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
        $('#container').css('background', 'url(${gift.defaultGiveImageUrl})');
        $('#container').css('background-size', '100%');
        $('#header h1').css('background', 'url(${merchantUrl})');
        $('#header h1').css('background-size', '100%');
        }
    </script>
    <script src="js/landing.js"></script>
</body>
</html>

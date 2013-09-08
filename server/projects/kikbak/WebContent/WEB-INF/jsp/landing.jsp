<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.net.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head prefix="og: http://ogp.me/ns# product: http://ogp.me/ns/product#">
<meta property="fb:app_id" content="493383324061333">
<meta property="og:url" content="${url}">
<meta property="og:type" content="referredlabs:coupon">
<meta property="og:title" content="${title}">
<meta property="og:image" content="${shareInfo.imageUrl}">
<meta property="og:description" content="${body}}">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0"/>
<link href="css/landing.css" rel="stylesheet" type="text/css" />
<title>Kikbak</title>
</head>
<body>
<div id="bdy" class="bdy"></div>
<div class="hedr-wpr"><a href="${gift.merchant.url}" class="logo"><img src="${merchantDefaultUrl}" /></a></div>
<div class="main-bdy">
<p class="top-msg"><strong>${shareInfo.friendName}</strong> used <strong>Kikbak</strong> to give you
an exclusive offer for <strong>${gift.merchant.name}</strong>.</p>
<div class="bdy-wpr">
<div class="lft-colm">
<div class="usr-cmnt">
<img class="usr-img" src="https://graph.facebook.com/${shareInfo.fbFriendId}/picture?type=square" />
<h2>${shareInfo.friendName}</h2>
<p>${shareInfo.caption}</p>
</div>
<div class="img-bnr">
<span class="blkshd"></span>
<div class="cmnt-on-photo wit-grd photup"><p>${shareInfo.friendName} was helped by <strong>${shareInfo.employeeId}</strong> at ${gift.merchant.name} Store:
<strong>${location.address1} ${location.address2}, ${location.city}, ${location.state}</strong>
If you visit the same store, ${shareInfo.employeeId} can help you too.</p></div>
<img src="${shareInfo.imageUrl}" class="mob-bnr"/>
<h3>${gift.merchant.name}</h3>
<div class="optn">
<a href="https://maps.google.com/maps?q=${encodeMerchantName}%40${location.latitude},${location.longitude}"><img src="img/map-mrk.png"/></a>
<a href="${gift.merchant.url}"><img src="img/glob.png"/></a>
<a id="tel-div" href="tel:${location.phoneNumber}"><img src="img/phone.png"/></a>
</div>
</div>
<div class="usr-cmnt blk">
<img class="usr-img" src="https://graph.facebook.com/${shareInfo.fbFriendId}/picture?type=square" />
<h2>${shareInfo.friendName}</h2>
<p>${shareInfo.caption}</p>
</div>
<div class="cmnt-on-photo"><img src="img/info-icon.png" />
<c:choose>
<c:when test="${not empty shareInfo.employeeId}">
<p>${shareInfo.friendName} was helped by <strong>${shareInfo.employeeId}</strong> at ${gift.merchant.name} Store:
</c:when>
<c:otherwise>
<p>At ${gift.merchant.name} Store:
</c:otherwise>
</c:choose>
<strong>${location.address1} ${location.address2}, ${location.city}, ${location.state}</strong>
<c:choose>
<c:when test="${not empty shareInfo.employeeId}">
If you visit the same store, ${shareInfo.employeeId} can help you too.</p></div>
</c:when>
<c:otherwise>
</p></div>
</c:otherwise>
</c:choose>
</div>
<div class="rit-colm">
<p class="tp-msg"><strong>${shareInfo.friendName}</strong> used <strong>Kikbak</strong> to give you
an exclusive offer for <strong>${gift.merchant.name}</strong>.</p>
<div class="tp-bnr">
<div class="tp-bnr-lft"><h1>${gift.desc}</h1><p>${gift.detailedDesc}</p>
<div class="tp-bnr-rit"></div></div>
<div id="fb-root"></div>
</div>
<div id="facebook-div">
<div class="blue-btn" style="margin:0">
<p style="font-weight:normal;"><font size="3">Connect to Facebook to access your gift.</font></p>
</div>
<a id="loginFb" href="#" class="fb-button">Connect with Facebook</a>
<div class="blue-btn" style="margin:0">
<p style="font-weight:normal;"><font size="1">We use Facebook to make it easy for you to store, redeem and share gifts.</font>
<br /><font size="1">We will never post on Facebook without your permission.</font></p>
</div>
</div>
<div id="redeem-div" style="display:none;">
<div class="blue-btn" id="redeem-div-btn">
<c:choose>
<c:when test="${(validationType == 'barcode')}">
<a id="redeemGift" href="#" class="btnn">Generate offer to use in store</a>
</c:when>
</c:choose>
<div class="blue-btn" style="margin:0">
<c:choose>
<c:when test="${(validationType == 'qrcode') and (locationType == 'store')}">
<p style="margin-left:3%;">To access your gift, download the Kikbak app for iOS or Android. Your gift will be waiting for whenever you're ready to use it.</p>
</c:when>
</c:choose>
<c:choose>
<c:when test="${(validationType == 'qrcode') and (locationType == 'all')}">
<p style="margin-left:3%;">To use your gift in the store, download the Kikbak app for iOS or Android. Your gift will be waiting for whenever you're ready to use it.</p>
</c:when>
</c:choose>
<c:choose>
<c:when test="${(validationType == 'barcode')}">
<p style="margin-left:3%;">Or download the Kikbak app for iOS or Android. Your gift will be waiting for whenever you're ready to use it.</p>
</c:when>
</c:choose>
</div>
<div class="blue-btn">
<p class="ap-srt">
<a href="#" class="lft"><img src="img/app-store.png" /></a> 
<a href="#" class="rit"><img src="img/google-play.png" /></a>
</p>
</div>
</div>
</div>
</div>
</div>
<div class="footer">
<div class="fot-wpr"><a href="${gift.tosUrl}" class="lft">Terms & Conditions</a>
<a href="#" class="rit">@ Referred Labs, 2013</a>
</div>
</div>
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.0/jquery.min.js"></script>
<script>
if (typeof localStorage !== 'undefined') {
  localStorage.friendUserId = ${shareInfo.friendUserId};
  localStorage.code = '${code}';
}
window.mobilecheck = function() {
  var check = false;
  (function(a){if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i.test(a)||/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0,4)))check = true})(navigator.userAgent||navigator.vendor||window.opera);
  return check;
}
if (!window.mobilecheck()) {
  $('#bdy').css('background', 'url(${gift.defaultGiveImageUrl})');
  $('#bdy').css('background-size', 'cover');
}
</script>
<script src="js/landing.js"></script>
</body>
</html>
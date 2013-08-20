<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
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
<link href="css/landing.css" rel="stylesheet" type="text/css" />
<title>Kikbak</title>
</head>
<body>
<div>
<div class="bdy" style="background:url(${gift.defaultGiveImageUrl});background-size:cover;"></div>
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
<div id="tel-div">
<a href="tel:${location.phoneNumber}"><img src="img/phone.png"/></a>
</div>
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
<div class="blue-btn">
<p style="font-weight:normal;"><font size="3">Connect to Facebook to access your gift.</font></p>
<a id="loginFb" href="#" class="btnn">Connect with Facebook</a>
<p style="font-weight:normal;"><font size="1">We use Facebook to make it easy for you to store, redeem and share gifts.</font>
<br /><font size="1">We will never post on Facebook without your permission.</font></p>
</div>
<div>
<div id="redemm-div" style="display:none;"> 
<div class="blue-btn">
<a id="redemGift" href="#" class="btnn">Generate offer to use in store</a>
</div>
<p style="margin-left:3%;">The Kikbak app makes it easy to access your gift. 
Download it now -- your gift is already there.</p>
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
</script>
<script src="js/landing.js"></script>
</body>
</html>
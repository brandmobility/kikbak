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
<meta property="og:image" content="${gift.imageUrl}">
<meta property="og:description" content="${body}}">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;"/>
<link href="css/landing.css" rel="stylesheet" type="text/css" />
<title>Kikbak</title>
</head>
<script>
if (typeof localStorage !== 'undefined') {
  localStorage.code = '${code}';
}
</script>
<body>
<div class="bdy">
<div class="hedr-wpr"><a href="#" class="logo"><img src="${gift.defaultGiveImageUrl}" /></a></div>
<div class="main-bdy">
<p class="top-msg"><strong>${gift.friendName}</strong> used <strong>Kikbak</strong> to give you
an exclusive offer for <strong>${gift.merchant.name}</strong>.</p>
<div class="bdy-wpr">
<div class="lft-colm">
<div class="usr-cmnt">
<img class="usr-img" src="https://graph.facebook.com/${gift.fbFriendId}/picture?type=square" />
<h2>${gift.friendName}</h2>
<p>${gift.caption}</p>
</div>
<div class="img-bnr">
<span class="blkshd"></span>
<div class="cmnt-on-photo wit-grd photup"><p>${gift.friendName} was helped by <strong>${employeeId}</strong> at ${gift.merchant.name} Store:
<strong>${location.address1} ${location.address2}, ${location.city}, ${location.state}</strong>
If you visit the same store, ${employeeId} can help you too.</p></div>
<img src="${gift.imageUrl}" class="mob-bnr"/>
<h3>${gift.merchant.name}</h3>
<div class="optn">
<a href="https://maps.google.com/maps?q=${encodeMerchantName}%40${location.latitude},${location.longitude}"><img src="img/map-mrk.png"/></a>
<a href="${gift.merchant.url}"><img src="img/glob.png"/></a>
<a href="tel:${location.phoneNumber}"><img src="img/phone.png"/></a>
</div>
</div>
<div class="usr-cmnt blk">
<img class="usr-img" src="https://graph.facebook.com/${gift.fbFriendId}/picture?type=square" />
<h2>${gift.friendName}</h2>
<p>${gift.caption}</p>
</div>
<div class="cmnt-on-photo"><img src="img/info-icon.png" /><p>${gift.friendName} was helped by <strong>${employeeId}</strong> at ${gift.merchant.name} Store:
<strong>${location.address1} ${location.address2}, ${location.city}, ${location.state}</strong>
If you visit the same store, ${employeeId} can help you too.</p></div>
</div>
<div class="rit-colm">
<p class="tp-msg"><strong>${gift.friendName}</strong> used <strong>Kikbak</strong> to give you
an exclusive offer for <strong>${gift.merchant.name}</strong>.</p>
<div class="tp-bnr">
<div class="tp-bnr-lft"><h1>${gift.desc}</h1><p>${gift.detailedDesc}</p>
<div class="tp-bnr-rit"></div></div>
<div id="fb-root"></div>
<script>
  window.fbAsyncInit = function() {
    FB.init({appId: '493383324061333', status: false, cookie: true, xfbml: true});
  };
  (function() {
    var e = document.createElement('script'); e.async = true;
    e.src = document.location.protocol +
      '//connect.facebook.net/en_US/all.js';
    document.getElementById('fb-root').appendChild(e);
  }());
</script>
</div>
<div class="blue-btn">
<a href="#" onclick="fblogin();return false;" class="btnn">Generate offer to use in store</a>
</div>
<p>The Kikbak app makes it easy to access your gift. 
Download it now -- your gift is already there.</p>
<p class="ap-srt">
<a href="#" class="lft"><img src="img/app-store.png" /></a> 
<a href="#" class="rit"><img src="img/google-play.png" /></a>
</p>
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
  function fblogin() {
    FB.login(function(response) {
      if (response.status === 'connected') {
        window.location.href = '/m/offer.html';
      } else if (response.status === 'not_authorized') {
        FB.login();
      } else {
        FB.login();
      }
    }, {scope:"email,read_friendlists,publish_stream,publish_actions"});
  }
</script>
</body>
</html>
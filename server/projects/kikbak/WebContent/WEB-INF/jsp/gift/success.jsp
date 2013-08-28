<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.net.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="../css/landing.css" rel="stylesheet" type="text/css" />
<title>Kikbak</title>
</head>
<body>

<div class="bdy" style="background:url(${gift.defaultGiveImageUrl});background-size:cover;"></div>
<div class="hedr-wpr"><a href="${gift.merchant.url}" class="logo"><img src="${merchantDefaultUrl}" /></a>
<a href="javascript:window.print();" class="printbtn"><img src="../img/printer.png" />Print page</a>
</div>
<div class="main-bdy-grn">
<div class="sucss-msg blk"><h2>Success!</h2>
<p>You have claimed your gift.<br />
Your code will expire in 24 hours.<br />
Bring this offer into a ${gift.merchant.name} Store to redeem your gift.</p></div>
<div class="bdy-wpr">
<div class="lft-colm">
<div class="usr-cmnt">
<img class="usr-img" src="https://graph.facebook.com/${shareInfo.fbFriendId}/picture?type=square" />
<h2>${shareInfo.friendName}</h2>
<p>${shareInfo.caption}</p>
</div>
<div class="img-bnr">
<span class="blkshd"></span>
<div class="cmnt-on-photo wit-grd photup">
<c:choose>
<c:when test="${not empty shareInfo.employeeId}">
<p>${shareInfo.friendName} was helped by <strong>${shareInfo.employeeId}</strong> at ${gift.merchant.name} Store:
</c:when>
<c:otherwise>
<p>At ${gift.merchant.name} Store:
</c:otherwise>
</c:choose>
<strong>${location.address1} ${location.address2}, ${location.city}, ${location.state}</strong>
If you visit the same store, ${shareInfo.employeeId} can help you too.</p></div>
<img src="${shareInfo.imageUrl}" class="mob-bnr"/>
<h3>${gift.merchant.name}</h3>
<div class="optn">
<a href="https://maps.google.com/maps?q=${encodeMerchantName}%40${location.latitude},${location.longitude}"><img src="../img/map-mrk.png"/></a>
<a href="${gift.merchant.url}"><img src="../img/glob.png"/></a>
</div>
</div>
<div class="usr-cmnt blk">
<img class="usr-img" src="https://graph.facebook.com/${shareInfo.fbFriendId}/picture?type=square" />
<h2>${shareInfo.friendName}</h2>
<p>${shareInfo.caption}</p>
</div>
<div class="cmnt-on-photo"><img src="../img/info-icon.png" /><p>${shareInfo.friendName} was helped by <strong>${shareInfo.employeeId}</strong> at ${gift.merchant.name} Store:
<strong>${location.address1} ${location.address2}, ${location.city}, ${location.state}</strong>
If you visit the same store, ${shareInfo.employeeId} can help you too.</p></div>
</div>
<div class="rit-colm">
<div class="sucss-msg"><h2>Success!</h2>
<p>You have claimed your gift.<br />
Your code will expire in 24 hours.<br />
Bring this offer into a Verizon Store to redeem your gift.</p></div>
<div class="tp-bnr">
<div class="tp-bnr-lft"><h1>${gift.desc}</h1><p>${gift.detailedDesc}</p>
<a class="vld" href="#">offer valid in-store only</a>
<div class="tp-bnr-rit"></div></div>
</div>
<div class="blue-btn">
<img src="${url}" />
</div>
</div>
</div>
</div>
<div class="footer">
<div class="fot-wpr"><a href="${gift.tosUrl}" class="lft">Terms & Conditions</a>
<a href="#" class="rit">@ Referred Labs, 2013</a>
</div>
</div>
</body>
</html>

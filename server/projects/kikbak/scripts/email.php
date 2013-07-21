<?php

require_once('./util.php');

$name = urldecode($_GET['name']);
$code = $_GET['code'];
$desc = urldecode($_GET['desc']);
$image_url = urldecode($_GET['url']);
$login_url = host() . '/s/claim.php?code=' . $code;

$title = $name . ' used Kikbak to give you an exclusive offer';
$body = "<html><body><div><span id='content'>" . $desc . "</div><div><a href='" . $login_url . "'>" . $login_url . "</a></span></div><div><img src='" . $image_url . "'></img></div></body></html>";

echo '{"title" : "' . $title . '", "body" : "' . $body . '"}'

?>

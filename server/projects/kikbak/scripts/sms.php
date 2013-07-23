<?php

require_once('./util.php');

$name = urldecode($_GET['name']);
$code = $_GET['code'];
$desc = urldecode($_GET['desc']);
$image_url = urldecode($_GET['url']);
$login_url = host() . '/s/claim.php?code=' . $code;

$title = $name . ' used Kikbak to give you an exclusive offer';
$body = $name . ' used Kikbak to give you an exclusive offer. Check it out here ' . $login_url;

echo '{"title" : "' . $title . '", "body" : "' . $body . '"}';

?>

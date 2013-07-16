<?php

function host()
{
  $s = empty($_SERVER["HTTPS"]) ? '' : ($_SERVER["HTTPS"] == "on") ? "s" : "";
  $sp = strtolower($_SERVER["SERVER_PROTOCOL"]);
  $protocol = substr($sp, 0, strpos($sp, "/")) . $s;
  $port = ($_SERVER["SERVER_PORT"] == "80") ? "" : (":".$_SERVER["SERVER_PORT"]);
  return $protocol . "://" . $_SERVER['SERVER_NAME'] . $port; 
}

$name = urldecode($_GET['name']);
$code = $_GET['code'];
$desc = urldecode($_GET['desc']);
$image_url = urldecode($_GET['url']);
$login_url = host() . 'm/claim.php?code=' . $code;

$title = $name . ' used Kikbak to give you an exclusive offer';
$body = "
<html>
<body>
  <div>
    <span id='content'>" . $desc . " <a href='" . $login_url . "'>" . $login_url . "</a></span>
  </div>
  <div>
    <img src='" . $image_url . "'></img>
  </div>  
</body>
</html>
";

echo '{"title" : "' . $title . '", "body" : "' . $body . '"}'

?>

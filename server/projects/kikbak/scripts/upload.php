<?php

require_once('ImageManipulator.php');

function full_url_path()
{
  $s = empty($_SERVER["HTTPS"]) ? '' : ($_SERVER["HTTPS"] == "on") ? "s" : "";
  $sp = strtolower($_SERVER["SERVER_PROTOCOL"]);
  $protocol = substr($sp, 0, strpos($sp, "/")) . $s;
  $port = ($_SERVER["SERVER_PORT"] == "80") ? "" : (":".$_SERVER["SERVER_PORT"]);
  $last_index = strrpos($_SERVER['REQUEST_URI'], '/');
  return $protocol . "://" . $_SERVER['SERVER_NAME'] . $port . 
      ($last_index === FALSE ? $_SERVER['REQUEST_URI'] :  substr($_SERVER['REQUEST_URI'], 0, $last_index)) . '/';
}

$dangerous_words = array(" ", '"', "'", "&", "\\", "?", "#", "..");
$div = '';
$pattern = '/';
foreach ($dangerous_words as $word) {
  $pattern .= $div . preg_quote($word);
  $div = '|';
}
$pattern .= '/i';

if (preg_match($pattern, $_POST['userId']) || preg_match($pattern, $_FILES['file']['tmp_name'])
    || preg_match($pattern, basename( $_FILES['file']['name']))) {
  header('HTTP/1.1 500 Internal Server Error');
  exit(-1);
}

$manipulator = new ImageManipulator($_FILES['file']['tmp_name'], isset($_POST['ios']));

$width  = $manipulator->getWidth();
$height = $manipulator->getHeight();

if (isset($_POST['x']) && isset($_POST['y']) && isset($_POST['w']) && isset($_POST['h'])) {
	$newImage = $manipulator->crop($_POST['x'] * $width, $_POST['y'] * $height,
			($_POST['x'] + $_POST['w']) * $width, ($_POST['y'] + $_POST['h']) * $height);
}

$upload_dir = 'upload/' . $_POST['userId'];
$target_path = $upload_dir . '/' . mt_rand();

mkdir($upload_dir, 0754, true);

$manipulator->save($target_path);

$url_path = full_url_path();
echo '{"url":' . '"' . $url_path . $target_path . '"}';

?>


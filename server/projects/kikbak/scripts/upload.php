<?

function full_url_path()
{
  $s = empty($_SERVER["HTTPS"]) ? '' : ($_SERVER["HTTPS"] == "on") ? "s" : "";
  $sp = strtolower($_SERVER["SERVER_PROTOCOL"]);
  $protocol = substr($sp, 0, strpos($sp, "/")) . $s;
  $port = ($_SERVER["SERVER_PORT"] == "80") ? "" : (":".$_SERVER["SERVER_PORT"]);
  $last_index = strrpos($_SERVER['REQUEST_URI'], '/');
  return $protocol . "://" . $_SERVER['SERVER_NAME'] . $port . 
      $last_index === FALSE ? $_SERVER['REQUEST_URI'] . '/' :  substr($_SERVER['REQUEST_URI'], 0, last_index);
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

$upload_dir = 'upload/' . $_POST['userId'];
$target_path = $upload_dir . '/' . mt_rand();

mkdir($upload_dir, 0754, true);

if (!move_uploaded_file($_FILES['file']['tmp_name'], $target_path)) {
    header('HTTP/1.1 500 Internal Server Error');
  exit(-1);
}

$url_path = full_url_path();
echo '{"url":' . '"' . $full_url_path . '"}';

?>


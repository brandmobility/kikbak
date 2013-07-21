<?php

require_once('./util.php');

$code = urldecode($_GET['code']);

echo "
<script>
if (typeof localStorage !== 'undefined') {
  localStorage.code = '". $code . "';
  window.location.href='../m/index.html';
}
</script>
";

?>

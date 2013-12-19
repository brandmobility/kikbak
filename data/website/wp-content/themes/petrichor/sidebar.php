<?php
/**
 * Petrichor
 * (c) Web factory Ltd, 2013
 */

  echo '<div class="sidebar-inner">';
  if (is_page()) {
    dynamic_sidebar('wf-pages-sidebar');
  } else {
    dynamic_sidebar('wf-main-sidebar');
  }
  echo '</div>';
?>
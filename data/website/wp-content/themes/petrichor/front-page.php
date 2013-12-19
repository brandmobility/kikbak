<?php
/**
 * Petrichor
 * (c) Web factory Ltd, 2013
*/
  get_header();


if (is_active_sidebar('wf-fp-section')) {
  echo '<section id="content-boxes"><div class="container"><div class="row">';
  dynamic_sidebar('wf-fp-section');
  echo '</div></div></section>';
}

global $post;
  $pages = array();
  $pages = get_pages(array('sort_column' => 'menu_order',
                           'post_status' => 'publish',
                           'meta_key'    => '_wp_page_template',
                           'meta_value'  => 'page-front-section.php'));

  $done = array();
  $i = 0;
  foreach ($pages as $page) {
    $post = $page;

    if (in_array($page->ID, $done)) {
      continue;
    } else {
      $done[] = $page->ID;
      $i++;
    }
    if ($i % 2) {
      $even = 'even';
    } else {
      $even = 'odd';
    }
    
    if (stripos($page->post_content, '[gallery') !== false) {
      $gallery = true;
      $section_id = 'gallery';
    } else {
      $gallery = false;
      $section_id = 'front-section-' . $page->ID;
    }

    echo '<section class="section section-' . $even . '" id="' . $section_id . '">';
    if ($gallery) {
      echo '<div class="elevated">';
    }
    echo '        <div class="container">
              <div class="row">';
    if (!has_post_thumbnail($page->ID)) {
      echo '<div class="span12">';
      echo apply_filters('the_content', $page->post_content);
      echo '</div>';
    } else {
      echo '<div class="span7 content-extended">';
      echo apply_filters('the_content', $page->post_content);
      echo '</div>';
      echo '<div class="span5">';
      echo '<div class="cutoff-container">';
      echo get_the_post_thumbnail($page->ID, 'pe-front-section', array('class' => 'section-image-big hidden-tablet hidden-phone'));
      echo '</div>';
      echo '</div>';
    }
    echo '</div></div>';
    if ($gallery) {
      echo '</div><div class="texture"></div><div class="clear"></div>';
    }
    echo '</section>';
  } // foreach page

  get_footer();
?>
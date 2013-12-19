<?php
/**
 * Petrichor
 * (c) Web factory Ltd, 2013
 */

  get_header();
?>
<section class="plain-section">
  <div class="container">
    <div class="row">
      <div class="span9" id="main-content">
<?php
  $page_404_id = wf_theme_get_option('page_404_id');

  if ($page_404_id && get_post_field('post_content', $page_404_id)) {
    $content = apply_filters('the_content', get_post_field('post_content', $page_404_id));
    $title = get_the_title($page_404_id);
  } else {
    $content = __('<p>Looks like the page you\'re looking for isn\'t here any more. Sorry.</p>', WF_THEME_TEXTDOMAIN);
    $title = 'Page not found';
  }
?>
        <h3 class="page-title"><?php echo $title; ?></h3>

<?php
  if (get_post_meta($page_404_id, '_tagline', true)) {
    echo '<p class="box-subtitle">' . get_post_meta($page_404_id, '_tagline', true) . '</p>';
  }
  echo $content;
  edit_post_link(null, '<div class="clear"></div><br />', '<br /><br />', null);
?>
      </div>
      <div id="sidebar" class="span3 hidden-phone hidden-tablet">
        <?php get_sidebar(); ?>
      </div>
    </div>
 </div>
</section>
<?php
  get_footer();
?>
<?php
/**
 * Template Name: Front Page Section
 *
 * Petrichor
 * (c) Web factory Ltd, 2013
 */

  get_header();
  while (have_posts()) {
    the_post();
?>
<section class="plain-section">
  <div class="container">
    <div class="row">
      <div class="span9" id="main-content">
        <h3 class="page-title"><?php the_title(); ?></h3>
<?php
  if (get_post_meta($post->ID, '_tagline', true)) {
    echo '<p class="box-subtitle">' . get_post_meta($post->ID, '_tagline', true) . '</p>';
  }
  the_content();
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
  } // while have posts

  get_footer();
?>
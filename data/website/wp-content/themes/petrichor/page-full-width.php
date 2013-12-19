<?php
/**
 * Template Name: Full Width Page
 *
 * Petrichor
 * (c) Web factory Ltd, 2013
 */

  get_header();
  while (have_posts()) {
    the_post();
?>
<style>
.page-id-539 ol{}
.page-id-539 ol li{list-style: decimal !important; margin-left: 20px;}
.page-id-539 ol li ol li{list-style: lower-alpha !important; margin-left: 20px;}
.page-id-539 ol li ol li ul li{list-style: disc !important; margin-left: 20px;}
</style>
<section class="plain-section">
  <div class="container">
    <div class="row">
      <div class="span12" id="main-content">
        <h3 class="page-title"><?php the_title(); ?></h3>
<?php
  if (get_post_meta($post->ID, '_tagline', true)) {
    echo '<p class="box-subtitle">' . get_post_meta($post->ID, '_tagline', true) . '</p>';
  }
  the_content();
  edit_post_link(null, '<div class="clear"></div><br />', '<br /><br />', null);
?>
      </div>
    </div>
 </div>
</section>
<?php
  } // while have posts

  get_footer();
?>
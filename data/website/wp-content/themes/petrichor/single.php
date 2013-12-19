<?php
/**
 * Default Post Template With Sidebar on Right
 *
 * Petrichor
 * (c) Web factory Ltd, 2013
 */

  get_header();

  while (have_posts()) {
    the_post();
?>
<div class="page-content">
  <div class="container">
    <div class="row">
      <div id="main-content" class="span9">
        <div <?php post_class(); ?> id="post-<?php the_ID(); ?>">
        <div class="post-meta">
          <?php
          the_time(get_option('date_format') . ' @ ' . get_option('time_format'));
          echo __(' | Category: ', WF_THEME_TEXTDOMAIN) . get_the_category_list(', ');
          echo __(' | Tagged as: ', WF_THEME_TEXTDOMAIN);
          if (get_the_tags()) {
            the_tags('', ', ');
          } else {
            echo __('No tags', WF_THEME_TEXTDOMAIN);
          }
          ?>
        </div>
<?php
  echo '<h3 class="page-title">';
  the_title();
  echo '</h3>';
  the_content();
  wp_link_pages();
  edit_post_link(null, '<div class="clear"></div><br />', null, null);
?>
        <div id="comment-area">
          <?php comments_template(); ?>
        </div>
      </div>
      </div>
      <div id="sidebar" class="span3 hidden-phone hidden-tablet">
        <?php get_sidebar(); ?>
      </div>
    </div>
  </div>
</div>
<?php
  } // while have posts

  get_footer();
?>
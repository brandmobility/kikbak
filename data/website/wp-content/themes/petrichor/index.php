<?php
/**
 * Petrichor
 * (c) Web factory Ltd, 2013
 */

  if (!function_exists('get_header')) {
    header('location: /');
    die();
  }
  
  get_header();
?>
<div id="content" class="page-content">
  <div class="container">
    <div class="row">
      <div class="span9" id="main-content">
        <h2 class="archive-title"><?php
    if (is_singular()) {
      echo get_the_title();
    } elseif (is_home()) {
      echo get_the_title(get_option('page_for_posts'));
    } elseif (is_category()) {
      echo single_cat_title(null, false);
    } elseif (is_tag()) {
      echo __('Posts Tagged &#8216;', WF_THEME_TEXTDOMAIN) . single_tag_title(null, false) . '&#8217;';
    } elseif (is_day()) {
      echo __('Archive for ', WF_THEME_TEXTDOMAIN) . get_the_time('F jS, Y');
    } elseif (is_month()) {
      echo __('Archive for ', WF_THEME_TEXTDOMAIN) . get_the_time('F, Y');
    } elseif (is_search()) {
      echo __('Search results for: ', WF_THEME_TEXTDOMAIN) . '<span class="search-query">' . get_search_query() . '</span>';
    } elseif (is_year()) {
      echo __('Archive for ', WF_THEME_TEXTDOMAIN) . get_the_time('Y');
    } elseif (is_author()) {
      echo __('Author Archive', WF_THEME_TEXTDOMAIN);
    } elseif (isset($_GET['paged']) && !empty($_GET['paged'])) {
      echo __('Blog Archives', WF_THEME_TEXTDOMAIN);
    } elseif (is_404()) {
      $page_404_id = wf_theme_get_option('page_404_id');
      if (get_the_title($page_404_id)) {
        echo get_the_title($page_404_id);
      } else {
        echo __('Page not found (error 404)', WF_THEME_TEXTDOMAIN);
      }
    } else {
      echo __('Archive', WF_THEME_TEXTDOMAIN);
    }
?></h2>
<?php
if(have_posts()) {
    $post = $posts[0];
  while(have_posts()) {
      get_template_part('loop', 'main');
  }
} else {
?><div class="clear"></div>
      <h3><?php _e('No posts found', WF_THEME_TEXTDOMAIN); ?></h3>
      <p><?php _e('Looks like the content you\'re looking for isn\'t here any more. Sorry!', WF_THEME_TEXTDOMAIN); ?></p>
    <?php } ?>
    <div class="clear"></div>
    <div class="post-navigation">
      <div class="left"><?php next_posts_link(__('&laquo; Older Entries', WF_THEME_TEXTDOMAIN)) ?></div>
      <div class="right"><?php previous_posts_link(__('Newer Entries &raquo;', WF_THEME_TEXTDOMAIN)) ?></div>
    </div>
   <div class="clear"></div>
    </div>
    <div id="sidebar" class="span3 hidden-phone hidden-tablet">
      <?php get_sidebar(); ?>
    </div>
</div>

  </div>
</div>
<?php
  get_footer();
?>
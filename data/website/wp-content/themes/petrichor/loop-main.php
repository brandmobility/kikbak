<?php
/**
 * Petrichor
 * (c) Web factory Ltd, 2013
 */

  the_post();
?>
    <div <?php post_class(); ?> id="post-<?php the_ID(); ?>">
  <div class="post-meta"><?php the_time(get_option('date_format') . ' @ ' . get_option('time_format')); ?> | <?php _e('Category:', WF_THEME_TEXTDOMAIN); ?>
<?php if (get_the_category_list(', ')) echo get_the_category_list(', '); else echo 'No categories'; ?> | <?php _e('Tagged as:', WF_THEME_TEXTDOMAIN); ?> <?php if(get_the_tags()) the_tags('',', '); else echo 'No tags'; ?></div>
    <h2><a href="<?php the_permalink(); ?>"><?php the_title(); ?></a></h2>
<?php
  if ($tmp = get_the_post_thumbnail($post->ID, 'thumbnail')) {
    echo '<div class="featured-post-image"><a href="' . get_permalink() . '" title="' . get_the_title() .'">' . $tmp . '</a></div>';
  }
?>
      <div class="entry">
        <?php the_excerpt(); ?>
      </div>
      <a class="btn" href="<?php the_permalink(); ?>"><?php _e('Read more', WF_THEME_TEXTDOMAIN); ?></a>
  </div>
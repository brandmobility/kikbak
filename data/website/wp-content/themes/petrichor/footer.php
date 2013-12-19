<?php
/**
 * Petrichor
 * (c) Web factory Ltd, 2013
 */
?>

<?php
  if (!is_front_page() && !is_home() && wf_theme_get_option('twitter_feed')) {
    echo '<section id="twitter-feed">
  <div class="container">
    <div class="row">
      <div class="span12">';
    echo do_shortcode('[twitter]' . wf_theme_get_option('twitter_feed') . '[/twitter]');
    echo '</div>
    </div>
  </div>
</section>';
  }
?>

<footer>
  <div class="elevated">
    <div class="container">
      <div class="row">
        <div class="span12">
          <p class="copytext"><?php wf_theme_option('footer_left'); ?></p>
          <p class="outtro"><?php wf_theme_option('footer_right'); ?></p>
        </div>
      </div>
    </div>
  </div>
  <div class="texture"></div>
  <div class="clear"></div>
</footer>
<?php
  wp_footer();
?>
</body>
</html>
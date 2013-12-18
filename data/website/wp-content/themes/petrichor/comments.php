<?php
/**
 * Petrichor
 * (c) Web factory Ltd, 2013
 */

  if (!empty($_SERVER['SCRIPT_FILENAME']) && 'comments.php' == basename($_SERVER['SCRIPT_FILENAME']))
    die(__('Please do not load this page directly. Thank you!', WF_THEME_TEXTDOMAIN));
  if (!empty($post->post_password)) { // if there's a password
    if ($_COOKIE['wp-postpass_' . COOKIEHASH] != $post->post_password) { ?>
      <p class="nocomments"><?php _e('This post is password protected. Enter the password to view comments.', WF_THEME_TEXTDOMAIN); ?></p>
      <?php return;
    }
  }
  $oddcomment = 'class="alt" ';
?>
<?php
  if ($comments) {
?>
<h3>Comments for this article (<?php comments_number('0','1','%'); ?>)</h3>
<div class="comments-pagination">
  <?php paginate_comments_links(); ?>
</div>
<ul class="commentlist">
  <?php wp_list_comments('type=comment&callback=wf_theme_comment'); ?>
</ul><div class="clear"></div>
<?php
    if ('open' != $post->comment_status) {
      echo '<p class="nocomments">' . __('Comments are closed.', WF_THEME_TEXTDOMAIN) . '</p>';
    }
  } else { // no comments
  if ('open' == $post->comment_status) {
  } else {
?>
  <p class="nocomments"><?php _e('Comments are closed.', WF_THEME_TEXTDOMAIN); ?></p>
<?php
    }
  }
  if ('open' == $post->comment_status) {
?>
<div class="comments-pagination">
  <?php paginate_comments_links(); ?>
</div>
<?php
  if (get_option('comment_registration') && !$user_ID ) {
?>
<p><?php _e('You must be', WF_THEME_TEXTDOMAIN); ?> <a href="<?php echo get_option('siteurl'); ?>/wp-login.php?redirect_to=<?php echo urlencode(get_permalink()); ?>"><?php _e('logged in', WF_THEME_TEXTDOMAIN); ?></a> <?php _e('to post a comment.', WF_THEME_TEXTDOMAIN); ?></p>
<?php
  } else {
    comment_form();
  }
}
?>
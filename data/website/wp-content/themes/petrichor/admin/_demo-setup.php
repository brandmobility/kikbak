<?php
/**
 * Petrichor
 * (c) Web factory Ltd, 2013
 */

  @session_start();
  define('DISALLOW_FILE_EDIT', true);
  define('WF_THEME_DEMO', true);
  define('WF_THEME_OPTIONS', 'wf_theme_options_tmp_' . session_id());

  if (!get_option(WF_THEME_OPTIONS)) {
    $tmp['slider_category'] = 3;
    $tmp['header_text'] = 'Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. See some <a href="/shortcodes/">shortcodes</a>.

    [download-button href="/contact/"]';
    update_option(WF_THEME_OPTIONS, $tmp);
  }


  add_filter('logout_url', 'wf_theme_admin_redirect');
  function wf_theme_admin_redirect($redirect_to, $url_redirect_to = '', $user = null) {
    return 'http://petrichor-wp.webfactoryltd.com/';
  }

  if(is_admin() && (!defined('DOING_AJAX') || !@DOING_AJAX) && strpos($_SERVER['SCRIPT_FILENAME'], 'wp-admin/customize.php') === false && strpos($_SERVER['SCRIPT_FILENAME'], 'wp-admin/async-upload.php') === false) {
    header('location: /demo-logout.php');
  }

  function customize_theme_button() {
    if(is_demo() && !is_theme_customize() && !current_user_can('administrator')) {
      echo '<div class="options-panel-closed hidden-phone hidden-tablet" style="display: block;">';
      echo '<a target="_parent" href="/demo-login.php" title="Customize the theme">';
      echo '<span class="customize">';
      echo 'Click here<br />to customize<br />the theme</span>';
      echo '<img src="' . get_template_directory_uri() . '/images/tools-white.png" width="64" alt="Customize" title="Customize" />';
      echo '</a></div>';
    }
  } // customize_theme_button
  add_action('wp_footer', 'customize_theme_button');
<?php
/**
 * Petrichor
 * (c) Web factory Ltd, 2013
 */

define('WF_THEME_NAME', 'Petrichor');
define('WF_THEME_VERSION', '1.35');
define('WF_THEME_TEXTDOMAIN', 'wf_petrichor');

require_once get_template_directory() . '/admin/common-functions.php';

if(is_demo() && file_exists(get_template_directory() . '/admin/demo-setup.php') && !current_user_can('administrator')) {
  require_once get_template_directory() . '/admin/demo-setup.php';
} else {
  define('WF_THEME_OPTIONS', 'pe_theme_options');
}

require_once get_template_directory() . '/admin/theme-customize.php';
require_once get_template_directory() . '/admin/shortcodes.php';
require_once get_template_directory() . '/admin/widgets.php';
require_once get_template_directory() . '/admin/simple-page-ordering.php';

if (!version_compare(get_bloginfo('version'), 3.4, '>=')) {
  if (is_admin()) {
    add_action('admin_notices', 'wf_theme_min_version_notice');
  } else {
    echo '<p>' . __('<b>' . WF_THEME_NAME . '</b> theme requires <b>WordPress v3.4</b> or higher to function properly. Please upgrade.', WF_THEME_TEXTDOMAIN) . '</p>';
    die();
  }
}

if (!isset($content_width)) {
  $content_width = 900;
}

$wf_theme_js_vars = array('ajaxurl' => admin_url('admin-ajax.php'),
                          'theme_folder' => get_template_directory_uri(),
                          'theme_options' => WF_THEME_OPTIONS,
                          'is_home' => (int) (is_home() || is_front_page()),
                          'newsletter_msg_ok' => wf_theme_get_option('newsletter_msg_ok'),
                          'slider_pause' => wf_theme_get_option('slider_pause'),
                          'slider_pause_hover' => wf_theme_get_option('slider_pause_hover'),
                          'slider_controls' => wf_theme_get_option('slider_controls'),
                          'slider_animation' => wf_theme_get_option('slider_animation'),
                          'contact_form_msg_ok' => wf_theme_get_option('contact_form_msg_ok'),
                          'twitter_feed' => wf_theme_get_option('twitter_feed'));

function enable_threaded_comments() {
  if (!is_admin() && is_singular() && comments_open() && (get_option('thread_comments') == 1)) {
    wp_enqueue_script('comment-reply');
  }
} // enable threaded comments
add_action('get_header', 'enable_threaded_comments');

function wf_theme_customize_enqueue() {
  wp_enqueue_style('wf-theme-admin', get_template_directory_uri() . '/admin/css/common.css', array(), WF_THEME_VERSION);
  wp_enqueue_script('wf-theme-admin', get_template_directory_uri() . '/admin/js/common.js', array('jquery'), WF_THEME_VERSION);
} // wf_theme_customize_enqueue
add_action('customize_controls_enqueue_scripts', 'wf_theme_customize_enqueue');

function wf_theme_customizer_js() {
  wp_enqueue_script('wf-theme-admin', get_template_directory_uri() . '/admin/js/theme-customizer.js', array('jquery'), WF_THEME_VERSION);
} // wf_theme_cutomizer_js
add_action('customize_preview_init', 'wf_theme_customizer_js');

function wf_setup_theme(){
  register_nav_menu('primary', 'Primary Menu');
  add_theme_support('post-thumbnails');
  add_theme_support('automatic-feed-links');

  add_image_size('pe-slider', 270, 417, true);
 add_image_size('pe-slider-iphone5', 209, 371, true);
  add_image_size('pe-front-section', 640, 500, true);
  add_image_size('pe-gallery-thumb', 250, 190, true);
  add_image_size('pe-gallery-thumb-large', 500, 390, true);

  add_filter('widget_text', 'do_shortcode');
  add_filter('widget_title', 'do_shortcode');

  register_nav_menu('primary', __('Primary Menu', WF_THEME_TEXTDOMAIN));
  global $primary_menu_options;
  $primary_menu_options = array('theme_location'  => 'primary',
                                'menu'            => __('Primary', WF_THEME_TEXTDOMAIN),
                                'depth'           => 2,
                                'container'       => false,
                                'container_class' => false,
                                'container_id'    => false,
                                'menu_class'      => false,
                                'menu_id'         => null,
                                'echo'            => false,
                                'fallback_cb'     => null,
                                'before'          => null,
                                'after'           => null,
                                'link_before'     => null,
                                'link_after'      => null,
                                'items_wrap'      => '<ul id="main-navigation" class="hidden-phone hidden-tablet">%3$s</ul>');

  load_theme_textdomain(WF_THEME_TEXTDOMAIN, get_template_directory() . '/languages');
} // wf_setup_theme
add_action('after_setup_theme', 'wf_setup_theme');

function wf_theme_widgets_init() {
  register_sidebar(array(
    'name' => __('Main Sidebar', WF_THEME_TEXTDOMAIN),
    'id' => 'wf-main-sidebar',
    'description' => __('Sidebar area for all content except single pages.', WF_THEME_TEXTDOMAIN),
    'before_widget' => '<div id="%1$s" class="widget %2$s">',
    'after_widget' => "</div>",
    'before_title' => '<h4 class="widget-title">',
    'after_title' => '</h4>',
  ));
  register_sidebar(array(
    'name' => __('Pages Sidebar', WF_THEME_TEXTDOMAIN),
    'id' => 'wf-pages-sidebar',
    'description' => __('Sidebar area for single pages.', WF_THEME_TEXTDOMAIN),
    'before_widget' => '<div id="%1$s" class="widget %2$s">',
    'after_widget' => "</div>",
    'before_title' => '<h4 class="widget-title">',
    'after_title' => '</h4>',
    ));
  register_sidebar(array(
    'name' => __('First Section on Front Page', WF_THEME_TEXTDOMAIN),
    'id' => 'wf-fp-section',
    'description' => __('Area below the header on front page, 3 widgets per row. "Text With Icon" looks best in this sidebar.', WF_THEME_TEXTDOMAIN),
    'before_widget' => '<div id="%1$s" class="widget span4">',
    'after_widget' => '</div>',
    'before_title' => '<h3>',
    'after_title' => '</h3>',
  ));
} // wf_theme_widgets_init
add_action('widgets_init', 'wf_theme_widgets_init');

function wf_theme_ajax_reset_colors() {
  $options = wf_theme_get_option();
  $options['main_color'] = '';

  update_option(WF_THEME_OPTIONS, $options);
  die('1');
} // wf_theme_ajax_reset_colors

function wf_theme_wp_print_scripts() {
  if (is_admin()) {
    return;
  }

  $out = '';

  if (wf_theme_get_option('header_image')) {
    $out .= '#header { background-image: url(\'' . wf_theme_get_option('header_image') . '\') !important;} ';
  }

  if (wf_theme_get_option('main_color')) {
    $out .= '#content-boxes h3, .page-title, #main-navigation li a:hover { color: ' . wf_theme_get_option('main_color') . ';} ';
    $out .= 'footer a { color: ' . wf_theme_get_option('main_color') . ';} ';
    $out .= '.download-btn { background-color: ' . wf_theme_get_option('main_color') . ' !important;} ';
    $out .= '#newsletter, #blockquote-rotator,#logo a,#twitter-feed, .section-even { background: ' . wf_theme_get_option('main_color') . ';} ';
    $out .= '.thumbnail:hover { border: 1px solid ' . wf_theme_get_option('main_color') . ' !important;} ';
  }

  if (wf_theme_get_option('custom_css')) {
    $out .= wf_theme_get_option('custom_css') . ' ';
  }

  if ($out) {
    $out = '<style media="all" type="text/css">' . $out . '</style>';
    echo "\n" . $out . "\n";
  }
} // wf_theme_wp_print_scripts
add_action('wp_print_scripts', 'wf_theme_wp_print_scripts');

function wf_theme_add_options_page() {
  add_theme_page(__('Customize Theme', WF_THEME_TEXTDOMAIN), __('Customize Theme', WF_THEME_TEXTDOMAIN), 'edit_theme_options', 'wf_customize', 'wf_theme_customize_redirect');
  add_theme_page(__(WF_THEME_NAME . ' Tools', WF_THEME_TEXTDOMAIN), __(WF_THEME_NAME, WF_THEME_TEXTDOMAIN), 'edit_theme_options', 'wf_theme_options', 'wf_theme_options_page');
} // wf_theme_add_options_page
add_action('admin_menu', 'wf_theme_add_options_page');

function wf_theme_options_page() {
  update_option('wf_theme_activated', false);
  wf_theme_process_demo_import();
  wf_theme_process_import();
  wf_theme_process_options_reset();
  settings_errors();

  echo '<div class="wrap">' .  get_screen_icon('tools') . '<h2>' . WF_THEME_NAME . ' Tools</h2>';
  echo '<h3 class="title">Import Demo Data</h3>';
  echo '<p>Importing demo data (post, pages, images, theme settings, ...) is the easiest way to setup your theme. It will
  allow you to quickly edit everything instead of creating content from scratch. When you import the data following things will happen:</p>';
  echo '<ul class="bullets">
  <li>no existing posts, pages, categories, images, custom post types or any other data will be deleted or modified</li>
  <li>no WordPress settings will be modified except the site title and front page type</li>
  <li>about 10 posts, a few pages, 10+ images, some widgets and one menu will get imported</li>
  <li>images will be downloaded from our server; these images are copyrighted and watermarked by Envato\'s PhotoDune and are for demo use only</li>
  <li>please click import only once and wait, it can take a couple of minutes</li>
  </ul>';
  echo '<form method="post" action="' . admin_url('themes.php?page=wf_theme_options') .'">';
  echo get_submit_button('Import Demo Data', 'primary', 'wf-theme-import-demo-data');
  echo '</form>';

  echo '<hr /><h3 class="title">Theme Options</h3>';
  echo '<p>All theme options are configured using the built-in <a href="' . admin_url('customize.php') . '" title="Open Theme Customizer">Theme Customizer</a>.</p>';

  echo '<hr /><h3 class="title">Export Options</h3>';
  echo '<p>Please note: the export data contains <b>only</b> theme options. It doesn\'t contain any general WordPress
        settings found under the <i>Settings</i> menu, posts, pages, images or anything other besides settings found under <i>Theme - Customize</i>.</p>';
  echo '<textarea rows="3" cols="100" id="wf-theme-export-options">' . wf_theme_export_options() . '</textarea>';
  echo get_submit_button('Download Options Export', 'secondary', 'wf-theme-download-export');

  echo '<hr /><h3 class="title">Import Options</h3>';
  echo '<form method="post" enctype="multipart/form-data" action="' . admin_url('themes.php?page=wf_theme_options') .'">';
  echo '<p>Use only options exported from another site using the same version of ' . WF_THEME_NAME . ' theme.<br />
  Copy/paste the data into the textarea or choose a file with export data.</p>';
  echo '<textarea rows="3" cols="100" id="wf-theme-import-options" name="wf-theme-import-options"></textarea><br />';
  echo '<input type="file" name="wf-theme-import-options-file" />';
  echo get_submit_button('Import Options', 'secondary', 'wf-theme-process-import');
  echo '</form>';

  echo '<hr /><h3 class="title">Reset Options</h3>';
  echo '<form method="post" action="' . admin_url('themes.php?page=wf_theme_options') .'">';
  echo '<p>This will reset <b>only</b> the theme options to default values. No WordPress related options will be modified.<br />
  There is no undo! Export Options before resetting if you want to make a backup copy.</p>';
  echo get_submit_button('Reset Options', 'secondary', 'wf-theme-reset-options');
  echo '</form>';

  echo '</div>'; // wrap
} // wf_theme_options_page

function wf_theme_box_page() {
  global $post;
  $tagline = get_post_meta($post->ID, '_tagline', true);

  wp_nonce_field('wf_theme_save', 'wf_theme_nonce');
  echo '<p><label for="wf_theme_tagline">Tagline:</label> <input type="text" value="' . $tagline . '" class="regular-text" name="wf_theme_tagline" id="wf_theme_tagline" /></p>';
  echo '<p><i>' . __('Tagline appears below the page title on single pages.', WF_THEME_TEXTDOMAIN) . '</i></p>';
} // wf_theme_box_pages

function wf_theme_box_seo() {
    global $post;
    $title = get_post_meta($post->ID, '_wf_theme_title', true);
    $description = get_post_meta($post->ID, '_wf_theme_description', true);
    $keywords = get_post_meta($post->ID, '_wf_theme_keywords', true);

    wp_nonce_field('wf_theme_save', 'wf_theme_nonce');
    echo '<p><label for="wf_theme_title">Title:</label> <input type="text" value="' . $title . '" class="regular-text" name="wf_theme_title" id="wf_theme_title" /></p>';
    echo '<p><label for="wf_theme_description">Meta Description:</label> <input type="text" value="' . $description . '" class="regular-text" name="wf_theme_description" id="wf_theme_description" /></p>';
    echo '<p><label for="wf_theme_keywords">Meta Keywords:</label> <input type="text" value="' . $keywords . '" class="regular-text" name="wf_theme_keywords" id="wf_theme_keywords" /></p>';
    echo '<p><i>' . __('Available variables: {site-title}, {site-tagline}, {site-description}, {site-keywords} and {page-title}.', WF_THEME_TEXTDOMAIN) . '</i><br />';
    echo '<i>' . __('Leave any field blank to use the default value set in <a href="customize.php">theme customizer</a>.', WF_THEME_TEXTDOMAIN) . '</i></p>';
} // wf_theme_box_pages

function wf_theme_box_post() {
  global $post;
  $link = get_post_meta($post->ID, '_slide_link', true);

  wp_nonce_field('wf_theme_save', 'wf_theme_nonce');
  echo '<p><b>' . __('Following setting only applies to posts that are used in the header slider.', WF_THEME_TEXTDOMAIN) . '</b></b></p>';
  echo '<p><label for="wf_theme_link">' . __('Link:', WF_THEME_TEXTDOMAIN) . '</label> <input type="text" value="' . $link . '" class="regular-text" name="wf_theme_link" id="wf_theme_link" /></p>';
} // wf_theme_box_post

function wf_theme_add_meta_box() {
  add_meta_box('wf-theme-page', __('Tagline', WF_THEME_TEXTDOMAIN), 'wf_theme_box_page', 'page', 'normal', 'high');
  add_meta_box('wf-theme-post', __('Slide options', WF_THEME_TEXTDOMAIN), 'wf_theme_box_post', 'post', 'normal', 'high');

  add_meta_box('wf-theme-seo', __('SEO Options', WF_THEME_TEXTDOMAIN), 'wf_theme_box_seo', 'page', 'normal', 'high');
  add_meta_box('wf-theme-seo', __('SEO Options', WF_THEME_TEXTDOMAIN), 'wf_theme_box_seo', 'post', 'normal', 'high');
} // wf_theme_add_meta_box
add_action('add_meta_boxes', 'wf_theme_add_meta_box');

function wf_theme_save_postdata($post_id) {
  if (!wp_verify_nonce(@$_POST['wf_theme_nonce'], 'wf_theme_save') || (defined('DOING_AUTOSAVE') && DOING_AUTOSAVE)) {
    return $post_id;
  }

  if (isset($_POST['wf_theme_tagline'])) {
    update_post_meta($post_id, '_tagline', $_POST['wf_theme_tagline']);
  }
  if (isset($_POST['wf_theme_link'])) {
    update_post_meta($post_id, '_slide_link', $_POST['wf_theme_link']);
  }
  if (isset($_POST['wf_theme_title'])) {
    update_post_meta($post_id, '_wf_theme_title', $_POST['wf_theme_title']);
    update_post_meta($post_id, '_wf_theme_description', $_POST['wf_theme_description']);
    update_post_meta($post_id, '_wf_theme_keywords', $_POST['wf_theme_keywords']);
  }

  return $post_id;
} // wf_theme_save_postdata
add_action('save_post', 'wf_theme_save_postdata');

function wf_theme_init() {
  global $wf_theme_js_vars;

  if (!is_admin() && ! wf_theme_is_login_page()) {
    wp_enqueue_style('wf-reset', get_template_directory_uri() . '/css/reset.css', array(), WF_THEME_VERSION);
    wp_enqueue_style('wf-bootstrap', get_template_directory_uri() . '/css/bootstrap.min.css', array(), WF_THEME_VERSION);
    wp_enqueue_style('wf-bootstrap-responsive', get_template_directory_uri() . '/css/bootstrap-responsive.min.css', array(), WF_THEME_VERSION);
    wp_enqueue_style('wf-bootstrap-modal', get_template_directory_uri() . '/css/bootstrap-modal.css', array(), WF_THEME_VERSION);
    wp_enqueue_style('wf-style', get_template_directory_uri() . '/css/style.css', array(), WF_THEME_VERSION);
    wp_enqueue_style('wf-style-responsive', get_template_directory_uri() . '/css/style-responsive.css', array(), WF_THEME_VERSION);
    wp_enqueue_style('wf-prettyphoto', get_template_directory_uri() . '/css/prettyPhoto.css', array(), WF_THEME_VERSION);

    wp_enqueue_style('wf-skin', get_template_directory_uri() . '/css/color-themes/' . wf_theme_get_option('skin') . '.css', array(), WF_THEME_VERSION);
    wp_enqueue_style('wf-texture', get_template_directory_uri() . '/css/textures/' . wf_theme_get_option('texture') . '.css', array(), WF_THEME_VERSION);
    wp_enqueue_style('wf-background', get_template_directory_uri() . '/css/header-images/' . wf_theme_get_option('background') . '.css', array(), WF_THEME_VERSION);

    if (wf_theme_get_option('header_type') == '1' || wf_theme_get_option('header_type') == '2' || wf_theme_get_option('header_type') == '3' || wf_theme_get_option('header_type') == '5' || wf_theme_get_option('header_type') == '8' || wf_theme_get_option('header_type') == '9' || wf_theme_get_option('header_type') == '6' || wf_theme_get_option('header_type') == '7' || is_theme_customize()) {
      wp_enqueue_style('wf-flexslider', get_template_directory_uri() . '/css/flexslider.css', array(), WF_THEME_VERSION);
    }

    wp_enqueue_script('jquery');
    wp_enqueue_script('wf_bootstrap', get_template_directory_uri() . '/js/bootstrap.min.js', array('jquery'), WF_THEME_VERSION, true);
    wp_enqueue_script('wf_twitter', get_template_directory_uri() . '/js/jquery.tweet.js', array('jquery'), WF_THEME_VERSION, true);
    wp_enqueue_script('wf_prettyphoto', get_template_directory_uri() . '/js/jquery.prettyPhoto.js', array('jquery'), WF_THEME_VERSION, true);
    wp_enqueue_script('wf_quvolver', get_template_directory_uri() . '/js/jquery.quovolver.js', array('jquery'), WF_THEME_VERSION, true);
    wp_enqueue_script('wf_form', get_template_directory_uri() . '/js/jquery.form.js', array('jquery'), WF_THEME_VERSION, true);
    wp_enqueue_script('wf_form_validate', get_template_directory_uri() . '/js/jquery.validate.js', array('jquery'), WF_THEME_VERSION, true);
    wp_enqueue_script('wf_redirect', get_template_directory_uri() . '/js/redirect.js', array('jquery'), WF_THEME_VERSION, true);

    if (wf_theme_get_option('header_type') == '1' || wf_theme_get_option('header_type') == '2' || wf_theme_get_option('header_type') == '3' || wf_theme_get_option('header_type') == '5' || wf_theme_get_option('header_type') == '6' || wf_theme_get_option('header_type') == '7' || wf_theme_get_option('header_type') == '8' || wf_theme_get_option('header_type') == '9' || is_theme_customize()) {
      wp_enqueue_script('wf_flexslider', get_template_directory_uri() . '/js/jquery.flexslider.js', array('jquery'), WF_THEME_VERSION, true);
    }

    wp_enqueue_script('wf_theme_common', get_template_directory_uri() . '/js/common.js', array('jquery'), WF_THEME_VERSION, true);
  }

  if (is_admin()) {
    wp_enqueue_style('wf-theme-admin', get_template_directory_uri() . '/admin/css/common.css', array(), WF_THEME_VERSION);
    wp_enqueue_script('wf-theme-admin', get_template_directory_uri() . '/admin/js/common.js', array('jquery'), WF_THEME_VERSION);
  }

  wp_localize_script('jquery', 'wf_theme', $wf_theme_js_vars);
  add_action('wp_ajax_wf_reset_colors', 'wf_theme_ajax_reset_colors');

  if (!session_id()) {
    @session_start();
  }
} // wf_theme_init
add_action('init', 'wf_theme_init');

function wf_theme_ajax_newsletter() {
  require_once get_template_directory() . '/admin/newsletter-mailchimp.php';
} // wf_theme_ajax_newsletter
add_action('wp_ajax_wf_theme_newsletter', 'wf_theme_ajax_newsletter');
add_action('wp_ajax_nopriv_wf_theme_newsletter', 'wf_theme_ajax_newsletter');

function wf_theme_comment($comment, $args, $depth) {
  $GLOBALS['comment'] = $comment;

  echo '<li ' . comment_class('', NULL, NULL, false) . ' id="li-comment-' . get_comment_ID() . '">';
  echo '<div id="comment-' . get_comment_ID() . '"><div class="comment-author vcard">';
  echo get_avatar($comment, $size = '48');
  printf(__('<cite class="fn">%s</cite> <span class="says">says:</span>', WF_THEME_TEXTDOMAIN), get_comment_author_link());
  echo '</div>';
  if ($comment->comment_approved == '0') {
    echo '<em>' . __('Your comment is awaiting moderation.', WF_THEME_TEXTDOMAIN) . '</em>';
    echo '<br />';
  }

  echo '<div class="comment-meta commentmetadata">';
  echo '<a href="' . htmlspecialchars(get_comment_link($comment->comment_ID)) . '">';
  printf('%1$s at %2$s', get_comment_date(), get_comment_time());
  echo '</a>';
  edit_comment_link('(Edit)', '  ', '');
  echo '</div>';
  comment_text();
  echo '<div class="reply">';
  comment_reply_link(array_merge($args, array('depth' => $depth, 'max_depth' => $args['max_depth'])));
  echo '</div></div>';
} // wf_theme_comment

function wf_theme_manage_pages_columns($columns) {
  $columns['template'] = __('Template', WF_THEME_TEXTDOMAIN);

  return $columns;
} // wf_theme_manage_pages_columns
add_filter('manage_pages_columns', 'wf_theme_manage_pages_columns', 10, 2);

function wf_theme_manage_pages_custom_column($column_name, $post_id) {
  global $post_type;

  if ($column_name == 'template') {
    $tmp = get_file_description(get_page_template($post_id));
    $tmp = str_replace('Page Template', '', $tmp);
    $tmp = trim($tmp);
    if(empty($tmp)) {
      $tmp = 'Default Page Template';
    }
    echo $tmp;
  }
} //wf_theme_manage_pages_custom_column
add_action('manage_pages_custom_column', 'wf_theme_manage_pages_custom_column', 10, 2);

function wf_theme_ajax_captcha() {
  if (isset($_REQUEST['check'])) {
      if ($_REQUEST['captcha'] == $_SESSION['captcha']) {
        die('true');
      } else {
        die('false');
      }
  } else {
    $a = rand(1, 10);
    $b = rand(1, 10);

    if ($a > $b) {
      $out = "$a - $b";
      $_SESSION['captcha'] = $a - $b;
    } else {
      $out = "$a + $b";
      $_SESSION['captcha'] = $a + $b;
    }
    die("$out");
  }
}
add_action('wp_ajax_wf_theme_captcha', 'wf_theme_ajax_captcha');
add_action('wp_ajax_nopriv_wf_theme_captcha', 'wf_theme_ajax_captcha');

function wf_theme_ajax_contact_send() {
  $details = $extra_details = '';
  $ok = 0;

  $details .= 'Name: ' . $_POST['name'] . "\r\n";
  $details .= 'Email: ' . $_POST['email'] . "\r\n";
  $details .= 'Message: ' . $_POST['message'] . "\r\n";

  $extra_details .= 'IP address: ' . $_SERVER['REMOTE_ADDR'] . "\r\n";
  $extra_details .= 'User agent: ' . $_SERVER['HTTP_USER_AGENT'] . "\r\n";

  $admin = "Someone has contacted you trough the site's contact form with the following details:\r\n{$details}{$extra_details}\r\nYou can reply to this email to respond.";

  $body = wf_theme_get_option('contact_form_email_body');
  $body .= "\r\n" . $details . $extra_details;

  $headers = 'Reply-to: ' . $_POST['name'] . ' <' . $_POST['email'] . '>' . "\r\n";
  $headers .= 'From: ' . get_bloginfo('name') . ' <' . get_bloginfo('admin_email') . '>' . "\r\n";
  $ok += wp_mail(wf_theme_get_option('contact_form_email_address'), wf_theme_get_option('contact_form_email_subject'), $body, $headers);

  die("$ok");
}
add_action('wp_ajax_wf_theme_contact_send', 'wf_theme_ajax_contact_send');
add_action('wp_ajax_nopriv_wf_theme_contact_send', 'wf_theme_ajax_contact_send');

function wf_theme_exclude_category($query) {
  if ($query->is_home() && wf_theme_get_option('slider_category')) {
    $query->set('cat', '-' . wf_theme_get_option('slider_category'));
  }
  return $query;
}
add_filter('pre_get_posts', 'wf_theme_exclude_category');

function wf_theme_dequeue_scripts() {
  global $twitter_js, $contact_js, $quote_js, $gallery_js;


  if (!$twitter_js) {
    wp_dequeue_script('wf_twitter');
  }
  if (!$contact_js) {
    wp_dequeue_script('wf_form');
    wp_dequeue_script('wf_form_validate');
  }
  if (!$quote_js) {
    wp_dequeue_script('wf_quvolver');
  }
  if (!$gallery_js) {
    wp_dequeue_script('wf_prettyphoto');
  }
  if (!is_front_page()) {
    wp_dequeue_script('wf_slider');
    wp_dequeue_style('wf_slider');
  }
}
add_action('wp_footer', 'wf_theme_dequeue_scripts');
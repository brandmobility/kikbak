<?php
/**
 * Web factory Themes - Common functions
 * (c) Web factory Ltd, 2013
 */

function wf_theme_get_option($name = false, $echo = false) {
  $options = get_option(WF_THEME_OPTIONS);
  if ($name) {
    if (!isset($options[$name])) {
      global $wf_theme_customize_options;
      $out = $wf_theme_customize_options[$name]['default'];
    } else {
      $out = $options[$name];
    }
  } else {
    $out = $options;
    if (!is_array($options)) {
      $options = array();
    }
  }

  if ($echo) {
    echo $out;
    return null;
  } else {
    return $out;
  }
} // wf_theme_get_option

function wf_theme_option($name = false) {
  return wf_theme_get_option($name, true);
} // wf_theme_option

function wf_theme_title() {
  global $post;

  if (is_singular() && get_post_meta($post->ID, '_wf_theme_title', true)) {
    $tmp = get_post_meta($post->ID, '_wf_theme_title', true);
    $tmp = str_replace(array('{site-title}', '{site-tagline}', '{site-description}', '{site-keywords}', '{page-title}'),
                       array(get_bloginfo('name'), get_bloginfo('description'), wf_theme_get_option('meta_description'), wf_theme_get_option('meta_keywords'), get_the_title($post->ID)),
                       $tmp);
    echo $tmp;
  } else {
      $org_title = wp_title('-', false, 'right');
      if (is_home() || is_front_page()) {
          $title = $title = get_bloginfo('title') . ' - ' . get_bloginfo('description', 'display');
      } else {
          $title = $org_title . get_bloginfo('title');
      }
      echo $title;
  }
} // wf_theme_title

function wf_theme_description() {
    global $post;

    if (is_singular() && get_post_meta($post->ID, '_wf_theme_description', true)) {
        $tmp = get_post_meta($post->ID, '_wf_theme_description', true);
        $tmp = str_replace(array('{site-title}', '{site-tagline}', '{site-description}', '{site-keywords}', '{page-title}'),
                           array(get_bloginfo('name'), get_bloginfo('description'), wf_theme_get_option('meta_description'), wf_theme_get_option('meta_keywords'), get_the_title($post->ID)),
                           $tmp);
        echo $tmp;
    } else {
        echo wf_theme_get_option('meta_description');
    }
} // wf_theme_description

function wf_theme_keywords() {
    global $post;

    if (is_singular() && get_post_meta($post->ID, '_wf_theme_keywords', true)) {
        $tmp = get_post_meta($post->ID, '_wf_theme_keywords', true);
        $tmp = str_replace(array('{site-title}', '{site-tagline}', '{site-description}', '{site-keywords}', '{page-title}'),
                           array(get_bloginfo('name'), get_bloginfo('description'), wf_theme_get_option('meta_description'), wf_theme_get_option('meta_keywords'), get_the_title($post->ID)),
                           $tmp);
        echo $tmp;
    } else {
        echo wf_theme_get_option('meta_keywords');
    }
} // wf_theme_keywords

function wf_theme_customize_redirect() {
  echo '<script type="text/javascript">window.location.href = "customize.php";</script>';
  echo '<p>If you are not automatically redirected <a href="customize.php">click here</a>.</p>';
} //wf_theme_customize_redirect

function wf_theme_export_options() {
  $data = wf_theme_get_option();
  $data = serialize($data);
  $data = base64_encode(gzdeflate($data, 9));

  return $data;
} // wf_theme_export_options

function wf_theme_import_options($data) {
  $data = @gzinflate(base64_decode(trim($data)));
  $data = @unserialize($data);

  if (is_array($data) && sizeof($data)) {
    update_option(WF_THEME_OPTIONS, $data);
    return true;
  } else {
    return false;
  }
} // wf_theme_import_options

function wf_theme_min_version_notice() {
  echo '<div class="error"><p>' . __('<b>' . WF_THEME_NAME . '</b> theme requires <b>WordPress v3.4</b> or higher to function properly.', WF_THEME_TEXTDOMAIN) .
       ' Please <a href="' . admin_url('update-core.php') . '">update</a> your WP core.</p></div>';
} // wf_theme_min_version_notice

function wf_theme_download_export() {
  if(isset($_GET['wf_theme_download_export']) && $_GET['wf_theme_download_export'] == '1') {
    $data = wf_theme_export_options();
    header('Content-type: text/plain');
    header('Cache-Control: must-revalidate, post-check=0, pre-check=0');
    header('Content-Description: File Transfer');
    header('Content-Disposition: attachment; filename="' . sanitize_key(WF_THEME_NAME) . '-export-' . date('Y-m-d') . '.txt"');
    die($data);
  }
} // wf_theme_download_export
add_action('init', 'wf_theme_download_export');

function wf_theme_process_import() {
  if (isset($_POST['wf-theme-process-import'])) {
    if (!empty($_POST['wf-theme-import-options'])) {
      if (wf_theme_import_options($_POST['wf-theme-import-options'])) {
        add_settings_error('wf-theme-import-options', 'wf-theme-import-options', __('Options successfully imported.', WF_THEME_TEXTDOMAIN), 'updated');
      } else {
        add_settings_error('wf-theme-import-options', 'wf-theme-import-options', __('No options were imported. Provided import data is corrupted.', WF_THEME_TEXTDOMAIN));
      }
    } elseif (!empty($_FILES['wf-theme-import-options-file']['tmp_name'])) {
      if (is_uploaded_file($_FILES['wf-theme-import-options-file']['tmp_name'])) {
        $data = file_get_contents($_FILES['wf-theme-import-options-file']['tmp_name']);
        if (wf_theme_import_options($data)) {
          add_settings_error('wf-theme-import-options', 'wf-theme-import-options', __('Options successfully imported.', WF_THEME_TEXTDOMAIN), 'updated');
        } else {
          add_settings_error('wf-theme-import-options', 'wf-theme-import-options', __('No options were imported. Provided import file is corrupted.', WF_THEME_TEXTDOMAIN));
        }
      } else {
        add_settings_error('wf-theme-import-options', 'wf-theme-import-options', __('Problem uploading file.', WF_THEME_TEXTDOMAIN));
      }
    } else {
      add_settings_error('wf-theme-import-options', 'wf-theme-import-options', __('No options were imported. Either copy/paste the export data into the textarea or choose an export file.', WF_THEME_TEXTDOMAIN));
    }
  }
} // wf_theme_process_import

function wf_theme_process_demo_import() {
  if (!empty($_POST['wf-theme-import-demo-data'])) {
    require_once get_template_directory() . '/admin/import-demo.php';
    $tmp = wf_theme_import_demo();
    if ($tmp) {
      add_settings_error('wf-theme-import-demo', 'wf-theme-import-demo', __('Demo data imported!', WF_THEME_TEXTDOMAIN) . ' <a target="_blank" href="' . home_url(). '">View</a> or <a href="' . admin_url('customize.php') . '">customize</a> the site.', 'updated');
      if (!get_option('permalink_structure')) {
        add_settings_error('wf-theme-import-demo-permalinks', 'wf-theme-import-demo-permalinks', 'Please enable <a href="' .  admin_url('options-permalink.php') . '">permalinks</a>!', 'error');
      }
    }
  }
} // wf_theme_process_import

function wf_theme_process_options_reset() {
  if (isset($_POST['wf-theme-reset-options'])) {
    update_option(WF_THEME_OPTIONS, array());
    add_settings_error('wf-theme-reset-options', 'wf-theme-reset-options', __('Theme options have been successfully reset to default values.', WF_THEME_TEXTDOMAIN), 'updated');
  }
} // wf_theme_process_options_reset

function template_directory_uri() {
  echo get_template_directory_uri();
} // template_directory_uri

function is_demo() {
  if (strpos($_SERVER['HTTP_HOST'], '.webfactoryltd.com') !== false) {
    return true;
  } else {
    return false;
  }
} //is_demo

function is_theme_customize() {
  global $wp_customize;

  if (is_object($wp_customize) && $wp_customize->is_preview()) {
    return true;
  } else {
    return false;
  }
} // is_theme_customize

function wf_theme_admin_bar_render() {
  global $wp_admin_bar;

  if (current_user_can('edit_themes')) {
    $wp_admin_bar->add_menu( array(
      'parent' => false,
      'id' => 'customize-wf-theme',
      'title' => __('Customize Theme', WF_THEME_TEXTDOMAIN),
      'href' => admin_url('customize.php'),
      'meta' => false));
  }
} // wf_theme_admin_bar_render
add_action('wp_before_admin_bar_render', 'wf_theme_admin_bar_render');

function wf_theme_activated() {
  update_option('wf_theme_activated', true);
} // wf_theme_activated
add_action('after_switch_theme', 'wf_theme_activated');

function wf_theme_activated_notice() {
  $screen = get_current_screen();

  if (get_option('wf_theme_activated') && $screen->base != 'appearance_page_wf_theme_options') {
    echo '<div class="updated"><p>' . __('Thank you for activating <b>' . WF_THEME_NAME . '</b>!', WF_THEME_TEXTDOMAIN) . ' Please open <a href="' . admin_url('themes.php?page=wf_theme_options') . '">' . WF_THEME_NAME . ' Tools</a>
    to import demo data. It\'s the <b>fastest and easiest way</b> to setup the theme.</p></div>';
  }
} // wf_theme_activated_notice
add_action('admin_notices', 'wf_theme_activated_notice');

function wf_theme_body_class() {
  if (is_front_page() || is_home()) {
    $tmp = get_body_class();
    foreach ($tmp as $i => $class) {
      if ($class == 'page') {
        unset($tmp[$i]);
        break;
      }
    }
    echo 'class="' . join(' ',$tmp ) . '"';
  } else {
    body_class('single');
  }
} // wf_theme_body_class

function wf_theme_sanitize_hex_color( $color ) {
  if (!$color || $color == 'false' )
    return '';

  // 3 or 6 hex digits, or the empty string.
  if ( preg_match('|^#([A-Fa-f0-9]{3}){1,2}$|', $color ) )
    return $color;

  return null;
}

// check if user is on login page
function wf_theme_is_login_page() {
  $tmp = in_array($GLOBALS['pagenow'], array('wp-login.php', 'wp-register.php'));

  return $tmp;
} // wf_theme_is_login_page
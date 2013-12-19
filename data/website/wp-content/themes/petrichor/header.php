<?php
/**
 * Petrichor
 * (c) Web factory Ltd, 2013
 */
?>

<!DOCTYPE html>
<!--[if lt IE 7 ]><html class="ie ie6" <?php language_attributes(); ?>> <![endif]-->
<!--[if IE 7 ]><html class="ie ie7" <?php language_attributes(); ?>> <![endif]-->
<!--[if IE 8 ]><html class="ie ie8" <?php language_attributes(); ?>> <![endif]-->
<!--[if (gte IE 9)|!(IE)]><!--><html <?php language_attributes(); ?>> <!--<![endif]-->
<head>
<meta charset="<?php bloginfo('charset'); ?>" />
<title><?php wf_theme_title(); ?></title>
<meta name="description" content="<?php wf_theme_description(); ?>" />
<meta name="keywords" content="<?php wf_theme_keywords(); ?>" />
<meta name="author" content="<?php wf_theme_option('meta_author'); ?>" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<!--[if lt IE 9]>
  <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->
<!--[if IE 8]>
  <link rel="stylesheet" href="<?php template_directory_uri(); ?>/css/ie8.css">
<![endif]-->
<!--[if IE 9]>
  <link rel="stylesheet" href="<?php template_directory_uri(); ?>/css/ie9.css">
<![endif]-->
<!-- favicons -->
<link rel="shortcut icon" href="http://kikbak.me/wp-content/uploads/2013/10/fav.png">
<?php
  wp_head();
?>
</head>
<?php
  echo '<body ';
  wf_theme_body_class();
  echo '>';
?>

<div id="hasOffer" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="width: 100%; top: 0; left: 0; margin: 0; border:0; border-radius:0;">
    <div class="modal-header" style="border:0;">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="opacity:1;padding:0;margin:-10px -20px;">
            <img src="<?php template_directory_uri(); ?>/images/close.png">
        </button>
        <p id="myModalLabel" style="font-size:16px;margin-top:20px;"></p>
    </div>
    <div class="modal-footer" style="border:0;padding-top:5px; width: 100%; left: 0; margin: 0; border:0;;">
        <button id="redirect-btn" class="btn btn-primary" data-dismiss="modal" style="background-color:#3585ED;background-image:none;width:90%;height:45px;margin:0 10% 0 0;font-size:24px;"></button>
    </div>
</div>

<?php
  if (is_front_page() && wf_theme_get_option('front_page_menu')) {
    global $primary_menu_options;
    if (wp_nav_menu($primary_menu_options)) {
?>
  <div id="navigation" class="front-page">
    <div class="container">
      <div class="row">
        <div class="span12">
          <nav>
<?php
  echo wp_nav_menu($primary_menu_options);
?>
        <form name="nav" class="hidden-desktop">
          <select id="main-menu-select" data-autogenerate="true"></select>
        </form>
          </nav>
        </div>
      </div>
    </div>
  </div>
<?php
    }
  }
?>
<!-- Header -->
<section id="header">
  <div class="elevated">
    <div class="container">
	<?php if(is_front_page()) : ?><a href="http://kikbak.me/business/"><div class="header-flag"><img src="http://kikbak.me/wp-content/uploads/2013/10/newflag.png" width="153" height="160" /></div></a><?php endif; ?>
      <div class="row">
        <div class="span12">
          <div id="logo">
<?php
  if (wf_theme_get_option('logo')) {
?>
            <a href="<?php echo home_url(); ?>"><img src="<?php wf_theme_option('logo'); ?>" alt="<?php bloginfo('name'); ?>" title="<?php bloginfo('name'); ?>"></a>
<?php
  }
?>
            <p class="site-description"><?php bloginfo('description'); ?></p>
            <h1><a href="<?php echo home_url(); ?>" title="<?php bloginfo('description'); ?>">
<?php
  if (wf_theme_get_option('logo_down')) {
    echo '<img src="' . wf_theme_get_option('logo_down') . '" alt="' . get_bloginfo('name') . '" title="' . get_bloginfo('name') . '">';
  } else {
    bloginfo('name');
  }
?>
            </a></h1>
          </div>
          <div id="social-icons">
            <ul>
<?php
  if (wf_theme_get_option('dribble_link')) {
    echo '<li><a class="sprite-dribble" title="Dribble" href="' . wf_theme_get_option('dribble_link') . '"></a></li>';
  }
  if (wf_theme_get_option('facebook_link')) {
    echo '<li><a class="sprite-facebook" title="Facebook" href="' . wf_theme_get_option('facebook_link') . '"></a></li>';
  }
  if (wf_theme_get_option('flickr_link')) {
    echo '<li><a class="sprite-flickr" title="Flickr" href="' . wf_theme_get_option('flickr_link') . '"></a></li>';
  }
  if (wf_theme_get_option('google_link')) {
    echo '<li><a class="sprite-google" title="Google+" href="' . wf_theme_get_option('google_link') . '"></a></li>';
  }
  if (wf_theme_get_option('linkedin_link')) {
    echo '<li><a class="sprite-linkedin" title="LinkedIn" href="' . wf_theme_get_option('linkedin_link') . '"></a></li>';
  }
  if (wf_theme_get_option('pinterest_link')) {
    echo '<li><a class="sprite-pinterest" title="Pinterest" href="' . wf_theme_get_option('pinterest_link') . '"></a></li>';
  }
  if (wf_theme_get_option('twitter_link')) {
    echo '<li><a class="sprite-twitter" title="Twitter" href="' . wf_theme_get_option('twitter_link') . '"></a></li>';
  }
  if (wf_theme_get_option('vimeo_link')) {
    echo '<li><a class="sprite-vimeo" title="Vimeo" href="' . wf_theme_get_option('vimeo_link') . '"></a></li>';
  }
?>
            </ul>
          </div>
        </div>
<?php
  if (is_front_page()) {
?>
        <div id="teaser">
<?php
  if (wf_theme_get_option('header_type') == '1'
      || wf_theme_get_option('header_type') == '2'
|| wf_theme_get_option('header_type') == '8'
|| wf_theme_get_option('header_type') == '9'
      || wf_theme_get_option('header_type') == '5'
      || wf_theme_get_option('header_type') == '6'
      || wf_theme_get_option('header_type') == '7'
      || wf_theme_get_option('header_type') == '3') { // slider
?>
          <div class="span6">
<?php
  if (wf_theme_get_option('header_type') == '1' ) {
    echo '<div id="teaser-slider-2">';
  } elseif (wf_theme_get_option('header_type') == '2' ) {
    echo '<div id="teaser-slider">';
} elseif (wf_theme_get_option('header_type') == '8' ) {
    echo '<div id="teaser-slider-iphone5">';
} elseif (wf_theme_get_option('header_type') == '9' ) {
    echo '<div id="teaser-slider-motox">';
  } elseif (wf_theme_get_option('header_type') == '5' ) {
    echo '<div id="teaser-slider-android">';
  } elseif (wf_theme_get_option('header_type') == '6' ) {
    echo '<div id="teaser-slider-blackberry">';
  } elseif (wf_theme_get_option('header_type') == '7' ) {
    echo '<div id="teaser-slider-plain">';
  } else {
    echo '<div id="teaser-slider-3">';
  }
?>
              <div class="flexslider">
                <ul class="slides">
<?php
if (!wf_theme_get_option('slider_category') || wf_theme_get_option('slider_category') == '-1') {
  $slides = array();
} else {
  $slides = get_posts(array('numberposts' => 12, 'category' => wf_theme_get_option('slider_category'), 'orderby' => 'post_date'));
}

    foreach ($slides as $slide) {
      setup_postdata($slide);
      $link = get_post_meta($slide->ID, '_slide_link', true);
      echo '<li>';
      if ($link) {
        echo '<a title="' . get_the_title($slide->ID) . '" href="' . $link . '">';
      }
if(wf_theme_get_option('header_type') == '8' ) {
      echo get_the_post_thumbnail($slide->ID, 'pe-slider-iphone5');
} else {
echo get_the_post_thumbnail($slide->ID, 'pe-slider');
}
      if ($link) {
        echo '</a>';
      }
      echo "</li>\n";
    }
?>
                </ul>
              </div>
            </div>
          </div>
          <div class="span6">
            <div id="teaser-right">
              <h2><?php wf_theme_option('header_title'); ?></h2>
              <?php echo wpautop(do_shortcode(wf_theme_get_option('header_text'))); ?>
            </div>
          </div>
<?php
  } else { // video
?>
          <div class="span7">
            <div id="teaser-video">
              <?php wf_theme_option('header_video'); ?>
            </div>
          </div>
          <div class="span5">
            <div id="video-teaser-right">
              <h2><?php wf_theme_option('header_title'); ?></h2>
              <?php echo wpautop(do_shortcode(wf_theme_get_option('header_text'))); ?>
            </div>
          </div>
<?php
  }
?>
        </div>
<?php
  } // end front page
?>
      </div>
    </div>
  </div>
  <div class="texture"></div>
  <div class="clear"></div>
<?php
  if (!is_front_page()) {
    global $primary_menu_options;
    if (wp_nav_menu($primary_menu_options)) {
?>
  <div id="navigation">
    <div class="container">
      <div class="row">
        <div class="span12">
          <nav>
<?php
  echo wp_nav_menu($primary_menu_options);
?>
        <form name="nav" class="hidden-desktop">
          <select id="main-menu-select" data-autogenerate="true"></select>
        </form>
          </nav>
        </div>
      </div>
    </div>
  </div>
<?php
    }
  }
?>
</section>
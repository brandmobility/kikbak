<?php
/**
 * Petrichor
 * (c) Web factory Ltd, 2013
 */

function wf_sc_twitter($atts, $content = '') {
  global $twitter_js;
  extract(shortcode_atts(array('count' => 1, 'avatar_size' => 0), $atts));
  $username = trim($content);
  $out = '';
  $twitter_js = true;

  if (!$username) {
    $username = 'WebFactoryLtd';
  }

  $count = (int) $count;
  $avatar_size = (int) $avatar_size;

  if (!wf_theme_get_option('twitter_consumer_key') || !wf_theme_get_option('twitter_consumer_secret') || !wf_theme_get_option('twitter_user_token') || !wf_theme_get_option('twitter_user_secret')) {
    $out .= '<div class="twitter_wrap">Please configure the Twitter feed via Theme Customizer.</div>';
  } else {
    $out .= '<div class="twitter_wrap"><div data-count="' . $count . '" data-avatar-size="' . $avatar_size . '" data-username="' . $username . '" class="tweet"></div></div>';
  }


  return $out;
}
add_shortcode('twitter', 'wf_sc_twitter');

function wf_sc_gmap($atts, $content = '') {
  extract(shortcode_atts(array('zoom' => 12, 'height' => '400', 'bubble' => 'true'), $atts));
  $bubble = (bool) $bubble;
  $zoom = (int) $zoom;
  $height = (int) $height;
  $out = '';

  $out .= '<iframe data-address="' . $content . '" data-zoom="' . $zoom . '" data-bubble="' . $bubble . '" height="' . $height . '" class="gmap"></iframe>';

  return $out;
}
add_shortcode('gmap', 'wf_sc_gmap');

function wf_sc_contact_form($atts, $content = '') {
  extract(shortcode_atts(array(), $atts));
  $out = '';
  global $contact_js;
  $contact_js = true;

  $out .= '<form method="post" action="' . get_permalink() . '" id="contact_form"><label for="name">Name *</label><input type="text" class="input-field" id="name" name="name" value=""><label for="email">E-mail *</label><input type="text" class="input-field" id="email" name="email" value=""><label for="message">Message *</label><textarea id="message" name="message" cols="50" rows="4"></textarea><p class="captcha-container"><span>How much is <strong id="captcha-img">2 + 2</strong> =</span><input type="text" class="captcha-input-field" id="captcha" name="captcha"></p><input type="submit" name="submit" class="btn btn-large btn-inverse form-btn" value="SEND MESSAGE" /></form>';

  return $out;
}
add_shortcode('contact-form', 'wf_sc_contact_form');
add_shortcode('contact_form', 'wf_sc_contact_form');


function wf_sc_quote($atts, $content = '') {
  extract(shortcode_atts(array(
    'smallprint' => '',
    'quotes' => true), $atts));
  $out = '';
  global $quote_js;
  $quote_js = true;

  if ($quotes) {
   $content = '&quot;' . $content . '&quot;';
  }
  $out .= '<blockquote><p>' . $content . '</p>';
  if ($smallprint) {
   $out .= '<cite>' . $smallprint . '</cite>';
  }
  $out .= '</blockquote>';

  return $out;
}
add_shortcode('quote', 'wf_sc_quote');

function wf_sc_quoterotate($atts, $content = '') {
  extract(shortcode_atts(array(), $atts));
  $out = '';
  global $quote_js;
  $quote_js = true;

  $out .= '<div class="quote-group">';
  $out .= do_shortcode($content);
  $out .= '</div>';

  return $out;
}
add_shortcode('quoterotate', 'wf_sc_quoterotate');

function wf_sc_button($atts, $content = '') {
  extract(shortcode_atts(array(
    'href' => '#',
    'size' => '',
    'color' => '',
    'class' => '',
    'target' => ''), $atts));
  $out = '';
  $class2 = '';
  $data = '';

  if ($size) {
    $class2 .= ' btn-' . $size;
  }
  if ($color) {
    $class2 .= ' btn-' . $color;
  }
  $class2 .= ' ' . $class;
  $class2 = ' ' . trim($class2);

  if ($target) {
   $target = 'target="' . $target . '"';
  }

  $out .= '<a ' . $target . ' href="' . $href . '" class="btn' . $class2 . '"' . $data . '>' . do_shortcode($content) . '</a>';

  return $out;
}
add_shortcode('button', 'wf_sc_button');

function wf_sc_button_download($atts, $content = '') {
  extract(shortcode_atts(array(
    'href' => '#',
    'class' => '',
    'target' => ''), $atts));
  $out = '';

  if ($target) {
   $target = 'target="' . $target . '"';
  }

  $out .= '<a ' . $target . ' href="' . $href . '" class="download-btn ' . $class . '"></a>';

  return $out;
}
add_shortcode('download-button', 'wf_sc_button_download');
add_shortcode('download_button', 'wf_sc_button_download');

function wf_sc_button_download_google($atts, $content = '') {
  extract(shortcode_atts(array(
    'href' => '#',
    'class' => '',
    'target' => ''), $atts));
  $out = '';

  if ($target) {
   $target = 'target="' . $target . '"';
  }

  $out .= '<a ' . $target . ' href="' . $href . '" class="download-btn-google ' . $class . '"></a>';

  return $out;
}
add_shortcode('download-button-google', 'wf_sc_button_download_google');
add_shortcode('download_button_google', 'wf_sc_button_download_google');

function wf_sc_clear($atts, $content = '') {
  $out = '';

  $out .= '<div class="clear"></div>';

  return $out;
}
add_shortcode('clear', 'wf_sc_clear');

function wf_sc_border($atts, $content = '') {
  $out = '';

  $out .= '<div class="border-break"><span></span></div>';

  return $out;
}
add_shortcode('border', 'wf_sc_border');

function wf_sc_title($atts, $content = '') {
  extract(shortcode_atts(array('size' => 4), $atts));
  $out = '';

  $content = do_shortcode($content);

  switch ($size) {
   case 4:
     $out .= '<h4>' . $content . '</h4>';
   break;
   case 5:
     $out .= '<h5>' . $content . '</h5>';
   break;
   case 6:
     $out .= '<h6>' . $content . '</h6>';
   break;
   default:
     $out .= '<h3>' . $content . '</h3>';
  }

  return $out;
}
add_shortcode('title', 'wf_sc_title');

function wf_sc_icon($atts, $content = '') {
  extract(shortcode_atts(array('size' => 64), $atts));
  $out = '';

  $size = (int) $size;
  $content = str_replace(' ', '-', trim($content));
  if (substr($content, -4) != '.png') {
   $content .= '.png';
  }

  $out .= '<img width="' . $size . '" class="sa-icon" alt="Icon" title="Icon" src="' . get_template_directory_uri() . '/images/widget-icons/' . $content . '" />';

  return $out;
}
add_shortcode('icon', 'wf_sc_icon');

function wf_sc_half($atts, $content = '') {
  $out = '';

  $out .= '<div class="half">' . do_shortcode($content) . '</div>';

  return $out;
}
add_shortcode('half', 'wf_sc_half');

function wf_sc_half_last($atts, $content = '') {
  $out = '';

  $out .= '<div class="half-last">' . do_shortcode($content) . '</div>';

  return $out;
}
add_shortcode('half_last', 'wf_sc_half_last');
add_shortcode('half-last', 'wf_sc_half_last');

function wf_sc_quarter($atts, $content = '') {
  $out = '';

  $out .= '<div class="quarter">' . do_shortcode($content) . '</div>';

  return $out;
}
add_shortcode('quarter', 'wf_sc_quarter');

function wf_sc_quarter_last($atts, $content = '') {
  $out = '';

  $out .= '<div class="quarter-last">' . do_shortcode($content) . '</div>';

  return $out;
}
add_shortcode('quarter-last', 'wf_sc_quarter_last');
add_shortcode('quarter_last', 'wf_sc_quarter_last');

function wf_sc_third($atts, $content = '') {
  $out = '';

  $out .= '<div class="third">' . do_shortcode($content) . '</div>';

  return $out;
}
add_shortcode('third', 'wf_sc_third');

function wf_sc_third_last($atts, $content = '') {
  $out = '';

  $out .= '<div class="third-last">' . do_shortcode($content) . '</div>';

  return $out;
}
add_shortcode('third-last', 'wf_sc_third_last');
add_shortcode('third_last', 'wf_sc_third_last');

function wf_sc_sc($atts, $content = '') {
  $out = '';

  $out .= '<pre><span class="shortcode-raw">' . $content . '</span></pre>';

  return $out;
}
add_shortcode('sc', 'wf_sc_sc');

function wf_sc_newsletter($atts, $content = '') {
  extract(shortcode_atts(array('default_name' => 'Enter your name ...', 'default_email' => 'Enter your email ...'), $atts));
  $out = '';
  global $contact_js;
  $contact_js = true;

  if (!$content) {
    $content = '[title size="3"]Join our newsletter.[/title]Stay up to date with the latest news.';
  }
  $content = do_shortcode($content);

  $out .= '<div id="newsletter">' . $content .  '<form method="post" action="' . admin_url('admin-ajax.php') . '" id="newsletterform" data-newslettertype="file"><div><input type="text" class="input-field" id="newsletter-name" name="newsletter-name" value="' . $default_name . '"><input type="text" class="input-field" id="newsletter-email" name="newsletter-email" value="' . $default_email . '"><a id="newslettersubmit" href="#" class="btn btn-large" title="Subscribe">subscribe</a></div></form></div>';

  return $out;
}
add_shortcode('newsletter', 'wf_sc_newsletter');

function wf_sc_tabgroup($atts, $content = '') {
  global $tabs;
  $out = '';
  if (!is_array($tabs)) {
    $tabs = array();
  }
  $first_tab = sizeof($tabs);

  do_shortcode($content);

  if($tabs) {
    $out .= '<div class="tabbable"><ul class="nav nav-tabs">';
    for ($id = $first_tab; $id < sizeof($tabs); $id++) {
      $tab = $tabs[$id];

      if ($id == $first_tab) {
        $out .= '<li class="active"><a data-toggle="tab" href="#tab-' . $id .'" title="' . $tab['title'] .'">' . $tab['title'] . '</a></li>';
      } else {
        $out .= '<li><a data-toggle="tab" href="#tab-' . $id .'" title="' . $tab['title'] .'">' . $tab['title'] . '</a></li>';
      }
    } // foreach header
    $out .= '</ul>';

    $out .= '<div class="tab-content">';
    for ($id = $first_tab; $id < sizeof($tabs); $id++) {
      $tab = $tabs[$id];

      if ($id == $first_tab) {
        $out .= '<div class="tab-pane active" id="tab-' . $id . '">' . $tab['content'] . '</div>';
      } else {
        $out .= '<div class="tab-pane" id="tab-' . $id . '">' . $tab['content'] . '</div>';
      }
    } // foreach content

    $out .= '</div></div>';
  } // if tabs

  return $out;
}
add_shortcode('tabgroup', 'wf_sc_tabgroup');

function wf_sc_tab($atts, $content = ''){
  global $tabs;
  extract(shortcode_atts(array('title' => 'Default tab title'), $atts));

  $tabs[] = array('title' => $title, 'content' => do_shortcode($content));

  return null;
}
add_shortcode('tab', 'wf_sc_tab');

function wf_sc_spacer($atts, $content = '') {
  $out = '';

  $out .= '<div class="vertical-spacer"></div>';

  return $out;
}
add_shortcode('spacer', 'wf_sc_spacer');

function wf_theme_gallery_shortcode($attr, $content = '') {
  global $post;
  global $gallery_instance;
  $gallery_instance++;
  global $gallery_js;
  $gallery_js = true;

  if ( isset( $attr['orderby'] ) ) {
    $attr['orderby'] = sanitize_sql_orderby( $attr['orderby'] );
    if ( !$attr['orderby'] )
      unset( $attr['orderby'] );
  }

  extract(shortcode_atts(array(
    'order'      => 'ASC',
    'orderby'    => 'menu_order',
    'id'         => $post->ID,
    'itemtag'    => 'li',
    'icontag'    => 'div',
    'captiontag' => 'div',
    'columns'    => 4,
    'size'       => 'pe-gallery-thumb',
    'link'       => 'file',
    'include'    => '',
    'exclude'    => '',
    'lead_image' => false
  ), $attr));

  $id = intval($id);

  if ( 'RAND' == $order )
    $orderby = 'none';

  if ( !empty($include) ) {
    $include = preg_replace( '/[^0-9,]+/', '', $include );
    $_attachments = get_posts( array('include' => $include, 'post_status' => 'inherit', 'post_type' => 'attachment', 'post_mime_type' => 'image', 'order' => $order, 'orderby' => $orderby) );

    $attachments = array();
    foreach ( $_attachments as $key => $val ) {
      $attachments[$val->ID] = $_attachments[$key];
    }
  } elseif ( !empty($exclude) ) {
    $exclude = preg_replace( '/[^0-9,]+/', '', $exclude );
    $attachments = get_children( array('post_parent' => $id, 'exclude' => $exclude, 'post_status' => 'inherit', 'post_type' => 'attachment', 'post_mime_type' => 'image', 'order' => $order, 'orderby' => $orderby) );
  } else {
    $attachments = get_children( array('post_parent' => $id, 'post_status' => 'inherit', 'post_type' => 'attachment', 'post_mime_type' => 'image', 'order' => $order, 'orderby' => $orderby) );
  }

  if ( empty($attachments) )
    return '';

  if ( is_feed() ) {
    $output = "\n";
    foreach ( $attachments as $att_id => $attachment )
      $output .= wp_get_attachment_link($att_id, $size, true) . "\n";
    return $output;
  }

  $itemtag = tag_escape($itemtag);
  $captiontag = tag_escape($captiontag);
  $columns = intval($columns);
  $itemwidth = $columns > 0 ? floor(100/$columns) : 100;
  $float = is_rtl() ? 'right' : 'left';

  $selector = "gallery-{$gallery_instance}";

  $gallery_style = $gallery_div = '';

  $size_class = sanitize_html_class( $size );

  $output = '<div class="gallery">';
  $output .= '<ul class="thumbnails">';


  $i = 0;
  foreach ( $attachments as $id => $attachment ) {
    if ($i == 0 && $lead_image) {
      $span_size = 'span4';
      $size_tmp = $size . '-large';
    } else {
      $span_size = 'span2';
      $size_tmp = $size;
    }
    $link = wp_get_attachment_link($id, $size_tmp, false, false);
    $link = str_replace('<a ', '<a data-gal="prettyPhoto[gallery1]" ', $link);

    $output .= "<{$itemtag} class='{$span_size}'>";
    $output .= "<{$icontag} class='thumbnail'>$link</{$icontag}>";
    if (0 && $captiontag && trim($attachment->post_excerpt)) {
      $output .= "<{$captiontag} class='wp-caption-text gallery-caption'>" . wptexturize($attachment->post_excerpt) . "</{$captiontag}>";
    }
    $output .= "</{$itemtag}>";
    if ( $columns > 0 && ++$i % $columns == 0 ) {
      $output .= '';
    }
  }

  $output .= "</ul></div>\n";

  return $output;
} // wf_theme_gallery_shortcode
remove_shortcode('gallery');
add_shortcode('gallery', 'wf_theme_gallery_shortcode');
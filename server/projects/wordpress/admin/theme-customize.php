<?php
/**
 * Petrichor
 * (c) Web factory Ltd, 2013
 */

$wf_theme_customize_options = array(
  'dummy' => array('default' => ''),
  'meta_author' => array('default' => 'Your company Ltd, 2013.'),
  'meta_description' => array('default' => 'Awesome WordPress site based on the Petrichor theme.'),
  'meta_keywords' => array('default' => 'app, creative, portfolio, awesome, wordpress, theme, wp'),
  'favicon' => array('default' => get_template_directory_uri() . '/images/favicon.ico'),
  
  'skin' => array('default' => 'orange', 'transport' => 'postMessage'),
  'texture' => array('default' => 'stripes', 'transport' => 'postMessage'),
  'background' => array('default' => 'urban', 'transport' => 'postMessage'),
  'main_color' => array('default' => '', 'sanitize_callback' => 'wf_theme_sanitize_hex_color', 'transport' => 'postMessage'),
  'custom_css' => array('default' => ''),

  'logo' => array('default' => get_template_directory_uri() . '/images/petrichor-logo.png'),
  'logo_down' => array('default' => ''),
  'header_title' => array('default' => 'Blast your pixels'),
  'header_image' => array('default' => ''),
  'header_text' => array('default' => 'Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. See some <a href="/shortcodes/">shortcodes</a>.

  [download-button href="#"] [download-button-google href="#"]'),
  'header_type' => array('default' => '1'),
  'front_page_menu' => array('default' => '0'),
  'header_video' => array('default' => '<iframe src="http://player.vimeo.com/video/57477038?badge=0&amp;color=c9ff23" height="380"></iframe>'),
  'slider_category' => array('default' => '0'),
  'slider_pause' => array('default' => '3000'),
  'slider_pause_hover' => array('default' => '1'),
  'slider_controls' => array('default' => '1'),
  'slider_animation' => array('default' => 'slide'),

  'twitter_link' => array('default' => '#'),
  'facebook_link' => array('default' => '#'),
  'google_link' => array('default' => '#'),
  'pinterest_link' => array('default' => '#'),
  'linkedin_link' => array('default' => '#'),
  'dribble_link' => array('default' => '#'),
  'flickr_link' => array('default' => '#'),
  'vimeo_link' => array('default' => '#'),

  'newsletter_api_key' => array('default' => '#'),
  'newsletter_list' => array('default' => '#'),
  'newsletter_msg_ok' => array('default' => 'Thank you for subscribing! Please confirm your subscription in the email you\'ll receive shortly.'),

  'footer_left' => array('default' => 'Copyright <a href="#">yourcompany.com</a> 2013.'),
  'footer_right' => array('default' => 'Premium WordPress landing page. Available via <a href="http://themeforest.net/item/petrichor-responsive-wordpress-landing-page/4337943?ref=WebFactory">Themeforest</a>.'),
  'twitter_feed' => array('default' => 'Envato'),
  'page_404_id' => array('default' => 0),
  
  'twitter_consumer_key' => array('default' => ''),
  'twitter_consumer_secret' => array('default' => ''),
  'twitter_user_token' => array('default' => ''),
  'twitter_user_secret' => array('default' => ''),

  'contact_form_email_address' => array('default' => get_option('admin_email')),
  'contact_form_email_subject' => array('default' => 'New contact from your website'),
  'contact_form_email_body' => array('default' => 'Someone has contacted you trough the site\'s contact form with the following details; '),
  'contact_form_msg_ok' => array('default' => 'Thank you for contacting us! We\'ll get back to you ASAP.')
  );

function wf_theme_customize_register($wp_customize) {
  global $wf_theme_customize_options;
  require get_template_directory() . '/admin/theme-customize-custom-controls.php';

  // register sections
  $wp_customize->add_section('title_tagline', array('title' => __('Title, Tagline & SEO', WF_THEME_TEXTDOMAIN), 'priority' => 20));
  $wp_customize->add_section('wf_style', array('title' => __('Colors & Backgrounds', WF_THEME_TEXTDOMAIN), 'priority' => 25));
  $wp_customize->add_section('wf_header', array('title' => __('Header', WF_THEME_TEXTDOMAIN), 'priority' => 30));
  $wp_customize->add_section('wf_frontpage', array('title' => __('Front Page', WF_THEME_TEXTDOMAIN), 'priority' => 35));
  $wp_customize->add_section('wf_footer', array('title' => __('Footer', WF_THEME_TEXTDOMAIN), 'priority' => 40));
  $wp_customize->add_section('wf_newsletter', array('title' => __('Newsletter', WF_THEME_TEXTDOMAIN), 'priority' => 45));
  $wp_customize->add_section('wf_contact_form', array('title' => __('Contact Form', WF_THEME_TEXTDOMAIN), 'priority' => 47));
  $wp_customize->add_section('wf_twitter', array('title' => __('Twitter Feed', WF_THEME_TEXTDOMAIN), 'priority' => 48));
  $wp_customize->add_section('wf_misc', array('title' => __('Miscellaneous', WF_THEME_TEXTDOMAIN), 'priority' => 50));

  // register settings
  $default_setting_details = array('type' => 'option', 'default' => '', 'transport' => 'refresh');
  foreach ($wf_theme_customize_options as $option_name => $details) {
    if (substr($option_name, 0, 1) == '_') {
      continue;
    }
    $details = array_merge($default_setting_details, $details);
    $wp_customize->add_setting(WF_THEME_OPTIONS . '[' . $option_name . ']', $details);
  }

  // section - title
  $wp_customize->get_control('blogdescription')->label = 'Site Tagline';
  $wp_customize->get_control('blogname')->priority = 1;
  $wp_customize->get_control('nav_menu_locations[primary]')->label = 'Menu';
  $wp_customize->get_control('nav_menu_locations[primary]')->section = 'wf_header';
  $wp_customize->get_control('nav_menu_locations[primary]')->priority = 30;

  $wp_customize->add_control(new WP_Customize_Textarea($wp_customize, 'meta_description', array(
   'label'   => 'Meta Description',
   'section' => 'title_tagline',
   'settings'   => WF_THEME_OPTIONS . '[meta_description]' )));
  $wp_customize->add_control('meta_keywords', array(
    'label'   => 'Meta Keywords',
    'section' => 'title_tagline',
    'settings' => WF_THEME_OPTIONS . '[meta_keywords]',
    'type'    => 'text'));
  $wp_customize->add_control(new WP_Customize_Help($wp_customize, 'seo_help', array(
    'label'    => 'SEO options can be configured for each individual post and page when editing them. The settings above are used for the front page and as default values.',
    'section'  => 'title_tagline',
    'settings' => WF_THEME_OPTIONS . '[dummy]')));
  $wp_customize->add_control('meta_author', array(
    'label'   => 'Meta Author',
    'section' => 'title_tagline',
    'settings' => WF_THEME_OPTIONS . '[meta_author]',
    'type'    => 'text'));
  $wp_customize->add_control(new WP_Customize_Image_Control($wp_customize, 'favicon', array(
   'label'   => 'Favicon',
   'section' => 'title_tagline',
   'settings'   => WF_THEME_OPTIONS . '[favicon]')));

  // section - style
  $wp_customize->add_control('skin', array(
    'label'   => 'Skin',
    'section' => 'wf_style',
    'priority' => 1,
    'settings' => WF_THEME_OPTIONS . '[skin]',
    'type'    => 'select',
    'choices' => array(
        'black' => 'Black',
        'blue' => 'Blue',
        'brown' => 'Brown',
        'cyan' => 'Cyan',
        'green' => 'Green',
        'orange' => 'Orange',
        'pink' => 'Pink',
        'purple' => 'Purple',
        'red' => 'Red')));
  $wp_customize->add_control('texture', array(
    'label'   => 'Texture',
    'section' => 'wf_style',
    'priority' => 2,
    'settings' => WF_THEME_OPTIONS . '[texture]',
    'type'    => 'select',
    'choices' => array(
      'clean' => 'Clean',
      'dots' => 'Dots',
      'minimal' => 'Minimal',
      'rich' => 'Rich',
      'stripes' => 'Stripes')));
  $wp_customize->add_control('background', array(
    'label'   => 'Header Background Image',
    'section' => 'wf_style',
    'priority' => 2,
    'settings' => WF_THEME_OPTIONS . '[background]',
    'type'    => 'select',
    'choices' => array(
      'highway' => 'Highway',
      'no-image' => 'No image',
      'spring' => 'Spring',
      'universe' => 'Universe',
      'urban' => 'Urban',
      'winter' => 'Winter')));
  $wp_customize->add_control(new WP_Customize_Color_Control($wp_customize, 'main_color', array(
    'label'    => 'Custom Skin Color',
    'section'  => 'wf_style',
    'priority' => 11,
    'settings' => WF_THEME_OPTIONS . '[main_color]')));
  $wp_customize->add_control(new WP_Customize_Html($wp_customize, 'html_color', array(
    'label'    => '<input type="button" id="reset_colors" value="Reset all custom colors" />',
    'section'  => 'fw_style',
    'priority' => 12,
    'settings' => WF_THEME_OPTIONS . '[dummy]')));
  $wp_customize->add_control(new WP_Customize_Textarea($wp_customize, 'custom_css', array(
    'label'   => 'Custom CSS',
    'section' => 'wf_style',
    'priority' => 15,
    'settings'   => WF_THEME_OPTIONS . '[custom_css]')));
   $wp_customize->add_control(new WP_Customize_Help($wp_customize, 'custom_css_help', array(
    'label'   => 'If you need to add custom CSS to the theme copy/paste it here (without the &lt;style&gt; tags).',
    'section' => 'wf_style',
    'priority' => 16,
    'settings' => WF_THEME_OPTIONS . '[dummy]')));

  // section - header
  $wp_customize->add_control(new WP_Customize_Image_Control($wp_customize, 'logo', array(
   'label'   => 'Logo (smaller, on top)',
   'priority' => 2,
   'section' => 'wf_header',
   'settings'   => WF_THEME_OPTIONS . '[logo]')));
  $wp_customize->add_control(new WP_Customize_Image_Control($wp_customize, 'logo_down', array(
   'label'   => 'Logo (will replace text title if selected)',
   'priority' => 3,
   'section' => 'wf_header',
   'settings'   => WF_THEME_OPTIONS . '[logo_down]')));
  $wp_customize->add_control(new WP_Customize_Image_Control($wp_customize, 'header_image2', array(
   'label'   => 'Header Background Image',
   'priority' => 4,
   'section' => 'wf_header',
   'settings'   => WF_THEME_OPTIONS . '[header_image]')));
  $wp_customize->add_control(new WP_Customize_Help($wp_customize, 'header_image2_help', array(
   'label'   => 'Image height should be at least 700px and width about 2000px.',
   'section' => 'wf_header',
   'priority' => 5,
   'settings' => WF_THEME_OPTIONS . '[dummy]')));
  $wp_customize->add_control('twitter_link', array(
    'label'   => 'Twitter Link',
    'section' => 'wf_header',
    'settings' => WF_THEME_OPTIONS . '[twitter_link]',
    'type'    => 'text'));
  $wp_customize->add_control('facebook_link', array(
    'label'   => 'Facebook Link',
    'section' => 'wf_header',
    'settings' => WF_THEME_OPTIONS . '[facebook_link]',
    'type'    => 'text'));
  $wp_customize->add_control('google_link', array(
    'label'   => 'Google+ Link',
    'section' => 'wf_header',
    'settings' => WF_THEME_OPTIONS . '[google_link]',
    'type'    => 'text'));
  $wp_customize->add_control('pinterest_link', array(
    'label'   => 'Pinterest Link',
    'section' => 'wf_header',
    'settings' => WF_THEME_OPTIONS . '[pinterest_link]',
    'type'    => 'text'));
  $wp_customize->add_control('linkedin_link', array(
    'label'   => 'LinkedIn Link',
    'section' => 'wf_header',
    'settings' => WF_THEME_OPTIONS . '[linkedin_link]',
    'type'    => 'text'));
  $wp_customize->add_control('dribble_link', array(
    'label'   => 'Dribble Link',
    'section' => 'wf_header',
    'settings' => WF_THEME_OPTIONS . '[dribble_link]',
    'type'    => 'text'));
  $wp_customize->add_control('flickr_link', array(
    'label'   => 'Flickr Link',
    'section' => 'wf_header',
    'settings' => WF_THEME_OPTIONS . '[flickr_link]',
    'type'    => 'text'));
  $wp_customize->add_control('vimeo_link', array(
    'label'   => 'Vimeo Link',
    'section' => 'wf_header',
    'settings' => WF_THEME_OPTIONS . '[vimeo_link]',
    'type'    => 'text'));
  $wp_customize->add_control(new WP_Customize_Help($wp_customize, 'help1', array(
   'label'   => 'If any link is left blank the associated icon will be hidden.',
   'section' => 'wf_header',
   'priority' => 14,
   'settings'   => WF_THEME_OPTIONS . '[dummy]')));

  // front page
  $wp_customize->add_control('front_page_menu', array(
    'label'   => 'Menu on Front Page',
    'section' => 'wf_frontpage',
    'priority' => 1,
    'settings' => WF_THEME_OPTIONS . '[front_page_menu]',
    'type'    => 'select',
    'choices' => array(
        '0' => 'Hide on front page show on all other pages',
        '1' => 'Show on front page and all other pages')));
  $wp_customize->add_control('header_type', array(
    'label'   => 'Header Type',
    'section' => 'wf_frontpage',
    'priority' => 2,
    'settings' => WF_THEME_OPTIONS . '[header_type]',
    'type'    => 'select',
    'choices' => array(
        '1' => 'iPhone slider (black) with hand',
        '2' => 'iPhone slider (black)',
        '8' => 'iPhone 5',
        '9' => 'Moto X',
        '3' => 'iPhone slider (white)',
        '5' => 'Nexus 7 slider (black)',
        '6' => 'BlackBerry slider (black)',
        '7' => 'Plain slider (white border)',
        '4' => 'Video')));
  $wp_customize->add_control('header_title', array(
   'label'   => 'Header Title',
   'section' => 'wf_frontpage',
   'priority' => 3,
   'settings'   => WF_THEME_OPTIONS . '[header_title]',
   'type' => 'text'));
  $wp_customize->add_control(new WP_Customize_Textarea($wp_customize, 'header_text', array(
   'label'   => 'Header Text',
   'section' => 'wf_frontpage',
   'priority' => 4,
   'settings'   => WF_THEME_OPTIONS . '[header_text]')));
  $wp_customize->add_control(new WP_Customize_Category($wp_customize, 'slider_category', array(
   'label'   => 'Slider Posts Category',
   'section' => 'wf_frontpage',
   'show_option_none' => 'No category selected',
   'priority'=> 11,
   'settings'   => WF_THEME_OPTIONS . '[slider_category]')));
  $wp_customize->add_control('slider_controls', array(
    'label'   => 'Slider Controls',
    'section' => 'wf_frontpage',
    'priority' => 13,
    'settings' => WF_THEME_OPTIONS . '[slider_controls]',
    'type'    => 'select',
    'choices' => array(
        '0' => 'None',
        '1' => 'Arrows')));
  $wp_customize->add_control('slider_animation', array(
    'label'   => 'Slider Animation',
    'section' => 'wf_frontpage',
    'priority' => 13,
    'settings' => WF_THEME_OPTIONS . '[slider_animation]',
    'type'    => 'select',
    'choices' => array(
        'fade' => 'Fade',
        'slide' => 'Slide')));
  $wp_customize->add_control('slider_pause', array(
    'label'   => 'Slider Pause Time',
    'section' => 'wf_frontpage',
    'priority' => 14,
    'settings' => WF_THEME_OPTIONS . '[slider_pause]',
    'type'    => 'select',
    'choices' => array(
        '0' => 'Disable auto slide',
        '1000' => '1 second',
        '1500' => '1.5 seconds',
        '2000' => '2 seconds',
        '2500' => '2.5 seconds',
        '3000' => '3 seconds',
        '3500' => '3.5 seconds',
        '4000' => '4 seconds',
        '4500' => '4.5 seconds',
        '5000' => '5 seconds',
        '5500' => '5.5 seconds',
        '6000' => '6 seconds',
        '6500' => '6.5 seconds',
        '7000' => '7 seconds',
        '7500' => '7.5 seconds',
        '8000' => '8 seconds',
        '8500' => '8.5 seconds',
        '9000' => '9 seconds',
        '9500' => '9.5 seconds',
        '10000' => '10 seconds')));
    $wp_customize->add_control('slider_pause_hover', array(
    'label'   => 'Pause Slider on Hover',
    'section' => 'wf_frontpage',
    'priority' => 14,
    'settings' => WF_THEME_OPTIONS . '[slider_pause_hover]',
    'type'    => 'select',
    'choices' => array(
        '0' => 'False',
        '1' => 'True')));
  $wp_customize->add_control(new WP_Customize_Textarea($wp_customize, 'header_video', array(
   'label'   => 'Video Embed HTML Code',
   'section' => 'wf_frontpage',
   'priority' => 17,
   'settings'   => WF_THEME_OPTIONS . '[header_video]')));
  $wp_customize->add_control(new WP_Customize_Help($wp_customize, 'help_video', array(
   'label'   => 'Recomended video height is 380px. Width is auto adjusted.',
   'section' => 'wf_frontpage',
   'priority' => 18,
   'settings' => WF_THEME_OPTIONS . '[dummy]')));
  $wp_customize->add_control(new WP_Customize_Help($wp_customize, 'help_1section', array(
   'label'   => '<div class="help_heading">First Section</div>First Section\' content is managed as <a href="widgets.php">widgets</a>. "Text With Icon" widgets look best but you can use any widget you like. Place them in the "First Section on Front Page" sidebar.',
   'section' => 'wf_frontpage',
   'priority' => 18,
   'settings' => WF_THEME_OPTIONS . '[dummy]')));

  // section - footer
  $wp_customize->add_control('twitter_feed', array(
    'label'   => 'Twitter Feed Account',
    'priority' => 1,
    'section' => 'wf_footer',
    'settings' => WF_THEME_OPTIONS . '[twitter_feed]',
    'type'    => 'text'));
  $wp_customize->add_control(new WP_Customize_Help($wp_customize, 'help188', array(
   'label'   => 'Feed is shown above the footer on all pages except the front page. Leave it blank to completely remove the Twitter feed section.',
   'section' => 'wf_footer',
   'priority' => 2,
   'settings'   => WF_THEME_OPTIONS . '[dummy]')));
  $wp_customize->add_control(new WP_Customize_Textarea($wp_customize, 'footer_left', array(
   'label'   => 'Footer Text Left',
   'section' => 'wf_footer',
   'priority' => 3,
   'settings'   => WF_THEME_OPTIONS . '[footer_left]')));
  $wp_customize->add_control(new WP_Customize_Textarea($wp_customize, 'footer_right', array(
   'label'   => 'Footer Text Rght',
   'section' => 'wf_footer',
   'priority' => 4,
   'settings'   => WF_THEME_OPTIONS . '[footer_right]')));

  // section - newsletter
  $wp_customize->add_control('newsletter_api_key', array(
    'label'   => 'MailChimp API Key',
    'section' => 'wf_newsletter',
    'settings' => WF_THEME_OPTIONS . '[newsletter_api_key]',
    'type'    => 'text'));
  $wp_customize->add_control('newsletter_list', array(
    'label'   => 'MailChimp List Name',
    'section' => 'wf_newsletter',
    'settings' => WF_THEME_OPTIONS . '[newsletter_list]',
    'type'    => 'text'));
  $wp_customize->add_control(new WP_Customize_Textarea($wp_customize, 'newsletter_msg_ok', array(
   'label'   => 'Message After Successful Subscribe',
   'section' => 'wf_newsletter',
   'priority' => 11,
   'settings' => WF_THEME_OPTIONS . '[newsletter_msg_ok]')));
  $wp_customize->add_control(new WP_Customize_Help($wp_customize, 'help351', array(
   'label'   => 'Use the [newsletter] shortcode to place the subscription box anywhere on the site.',
   'section' => 'wf_newsletter',
   'priority' => 20,
   'settings' => WF_THEME_OPTIONS . '[dummy]')));

   // section - contact form
   $wp_customize->add_control(new WP_Customize_Textarea($wp_customize, 'contact_form_msg_ok', array(
    'label'   => 'Message After Successful Form Submit',
    'section' => 'wf_contact_form',
    'priority' => 2,
    'settings' => WF_THEME_OPTIONS . '[contact_form_msg_ok]',
    'type'    => 'text')));
   $wp_customize->add_control(new WP_Customize_Textarea($wp_customize, 'contact_form_email_subject', array(
    'label'   => 'Email Subject',
    'section' => 'wf_contact_form',
    'priority' => 4,
    'settings' => WF_THEME_OPTIONS . '[contact_form_email_subject]',
    'type'    => 'text')));
   $wp_customize->add_control(new WP_Customize_Textarea($wp_customize, 'contact_form_email_body', array(
    'label'   => 'Email Body',
    'section' => 'wf_contact_form',
    'priority' => 6,
    'settings' => WF_THEME_OPTIONS . '[contact_form_email_body]',
    'type'    => 'text')));
   $wp_customize->add_control('contact_form_email_address', array(
    'label'   => 'Your Email Address',
    'section' => 'wf_contact_form',
    'priority' => 8,
    'settings' => WF_THEME_OPTIONS . '[contact_form_email_address]',
    'type'    => 'text'));
    
   // section - Twitter
   $wp_customize->add_control(new WP_Customize_Help($wp_customize, 'help-twitter-1', array(
   'label'   => 'Due to new Twitter API rules you have to create an application in order to show custom Twitter feeds. The process is very simple and free. Go to <a href="https://dev.twitter.com/" target="_blank">dev.twitter.com</a> and login with your Twitter account (any account will do). Click "My applications" under your account\'s avatar and then click "Create a new application". Application details are really not important, just fill them in. After the app is created click "Create my access token" (blue button on the bottom of the app page) and copy/paste 4 strings in appropriate fields here.' ,
   'section' => 'wf_twitter',
   'priority' => 1,
   'settings'   => WF_THEME_OPTIONS . '[dummy]')));
   $wp_customize->add_control(new WP_Customize_Textarea($wp_customize, 'twitter_consumer_key', array(
    'label'   => 'Consumer Key',
    'section' => 'wf_twitter',
    'priority' => 5,
    'settings' => WF_THEME_OPTIONS . '[twitter_consumer_key]')));
   $wp_customize->add_control(new WP_Customize_Textarea($wp_customize, 'twitter_consumer_secret', array(
    'label'   => 'Consumer Secret',
    'section' => 'wf_twitter',
    'priority' => 6,
    'settings' => WF_THEME_OPTIONS . '[twitter_consumer_secret]',
    'type'    => 'text')));
   $wp_customize->add_control(new WP_Customize_Textarea($wp_customize, 'twitter_user_token', array(
    'label'   => 'Access Token',
    'section' => 'wf_twitter',
    'priority' => 7,
    'settings' => WF_THEME_OPTIONS . '[twitter_user_token]',
    'type'    => 'text')));
   $wp_customize->add_control(new WP_Customize_Textarea($wp_customize, 'twitter_user_secret', array(
    'label'   => 'Access Token Secret',
    'section' => 'wf_twitter',
    'priority' => 8,
    'settings' => WF_THEME_OPTIONS . '[twitter_user_secret]',
    'type'    => 'text')));
   $wp_customize->add_control(new WP_Customize_Help($wp_customize, 'help-twitter-2', array(
   'label'   => 'Please note that this only sets up the Twitter feed authorization. In order to show it you haveto use the shortcode - [twitter count=1 avatar_size=32]account-name[/twitter]',
   'section' => 'wf_twitter',
   'priority' => 10,
   'settings'   => WF_THEME_OPTIONS . '[dummy]')));

   // section - miscellaneous
   $tmp = get_pages(array('post_status' => 'publish,draft,private'));
   $pages = array('0' => '-none-');
   foreach ($tmp as $tmp2) {
     $pages[$tmp2->ID] = get_the_title($tmp2->ID);
  }
  $wp_customize->add_control('page_404_id', array(
    'label'   => '404 Page',
    'section' => 'wf_misc',
    'priority' => 2,
    'settings' => WF_THEME_OPTIONS . '[page_404_id]',
    'type'    => 'select',
    'choices' => $pages));

  // remove default sections
  $wp_customize->remove_section('static_front_page');
  $wp_customize->remove_section('nav');
} // wf_theme_customize_register
add_action('customize_register', 'wf_theme_customize_register');
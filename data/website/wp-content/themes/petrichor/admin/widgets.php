<?php
function wf_theme_widgets_register() {
  register_widget('textIconWidget');
  register_widget('aweberFormWidget');
}
add_action('widgets_init', 'wf_theme_widgets_register');


class textIconWidget extends WP_Widget {
  function textIconWidget() {
    $widget_ops = array('classname' => 'tiw', 'description' => 'Arbitrary text or HTML with an icon.');
    $control_ops = array('width' => 400, 'height' => 350);
    $this->WP_Widget('tiw', __('Text With Icon', WF_THEME_TEXTDOMAIN), $widget_ops, $control_ops);
  }

  function widget($args, $instance) {
    extract($args);
    $text = apply_filters('widget_text', $instance['text'], $instance);

    echo $before_widget;
    echo '<img src="' . get_template_directory_uri() . '/images/widget-icons/' . $instance['icon'] . '" title="Icon" alt="Icon" />';
    if (!empty($instance['title'])) {
      echo $before_title . $instance['title'] . $after_title;
    }
    echo '<p class="box-subtitle">' . $instance['subtitle'] . '</p>';
    echo wpautop($text);
    echo $after_widget;
  }

  function update($new_instance, $old_instance) {
    $instance = $old_instance;
    $instance['title'] = $new_instance['title'];
    $instance['subtitle'] = $new_instance['subtitle'];
    $instance['icon'] = $new_instance['icon'];
    if (current_user_can('unfiltered_html')) {
      $instance['text'] =  $new_instance['text'];
    } else {
      $instance['text'] = stripslashes( wp_filter_post_kses( addslashes($new_instance['text']) ) );
    }

    return $instance;
  }

  function form($instance) {
    $instance = wp_parse_args((array) $instance, array('title' => '', 'text' => ''));
    $title = $instance['title'];
    $subtitle = $instance['subtitle'];
    $text = esc_textarea($instance['text']);
?>
    <p><label for="<?php echo $this->get_field_id('title'); ?>"><?php _e('Title:', WF_THEME_TEXTDOMAIN); ?></label>
    <input class="widefat" id="<?php echo $this->get_field_id('title'); ?>" name="<?php echo $this->get_field_name('title'); ?>" type="text" value="<?php echo esc_attr($title); ?>" /></p>

    <p><label for="<?php echo $this->get_field_id('subtitle'); ?>"><?php _e('Subtitle:', WF_THEME_TEXTDOMAIN); ?></label>
    <input class="widefat" id="<?php echo $this->get_field_id('subtitle'); ?>" name="<?php echo $this->get_field_name('subtitle'); ?>" type="text" value="<?php echo esc_attr($subtitle); ?>" /></p>

    <textarea class="widefat" rows="16" cols="20" id="<?php echo $this->get_field_id('text'); ?>" name="<?php echo $this->get_field_name('text'); ?>"><?php echo $text; ?></textarea>
<?php

$imagesdir = get_theme_root() . '/' . get_template() . '/images/widget-icons/';
if ($handle = opendir($imagesdir)) {
    while (false !== ($file = readdir($handle))) {
        if ($file != "." && $file != ".." && $file[0] != '_') {
            $icons[] = array('val' => $file, 'label' => $file);
        }
    }
    closedir($handle);
    asort($icons);
}
    echo '<p><label for="' . $this->get_field_id('icon') . '">Icon:</label> <select id="' . $this->get_field_id('icon') . '" name="' . $this->get_field_name('icon') . '">';
    self::create_select_options($icons, $instance['icon']);
    echo '</select>';
    if ($instance['icon']) {
      echo '<span class="widget-icon-preview"><img width="76" src="' . get_template_directory_uri() . '/images/widget-icons/' . $instance['icon'] . '" alt="Icon preview" title="Icon preview" /></span>';
    }
    echo '<br /><i>Click "Save" to preview icon.</i>';
    echo '</p>';
  } // form

  function create_select_options($options, $selected = null, $output = true) {
    $out = "\n";

    foreach ($options as $tmp) {
      $tmp['label'] = ucfirst(str_replace(array('.png', '.gif', '.jpg', '.jpeg'), array('', '', '', ''), $tmp['label']));
      if ($selected == $tmp['val']) {
        $out .= "<option selected=\"selected\" value=\"{$tmp['val']}\">{$tmp['label']}&nbsp;</option>\n";
      } else {
        $out .= "<option value=\"{$tmp['val']}\">{$tmp['label']}&nbsp;</option>\n";
      }
    }

    if($output) {
      echo $out;
    } else {
      return $out;
    }
  } // create_select_options
} // textIconWidget

class aweberFormWidget extends WP_Widget {
  function aweberFormWidget() {
    $widget_ops = array('classname' => 'afw', 'description' => 'Add an AWeber mailing list subscription form to your site.');
    $control_ops = array('width' => 500, 'height' => 350);
    $this->WP_Widget('afw', __('AWeber web form', WF_THEME_TEXTDOMAIN), $widget_ops, $control_ops);
  }

  function widget($args, $instance) {
    extract($args);
    $text = $instance['text'];

    echo $before_widget;
    if (!empty($instance['title'])) {
      echo $before_title . $instance['title'] . $after_title;
    }
    echo '<div class="aweber-form">' . $text . '</div>';
    echo $after_widget;
  }

  function update($new_instance, $old_instance) {
    $instance = $old_instance;
    $instance['title'] = $new_instance['title'];
    $instance['text'] =  $new_instance['text'];

    return $instance;
  }

  function form($instance) {
    $instance = wp_parse_args((array) $instance, array('title' => 'AWeber subscription form', 'text' => ''));
    $title = $instance['title'];
    $text = esc_textarea($instance['text']);
?>

<p>This widget allows you to add an AWeber web subscription form to a sidebar on your website. Please follow these instructions to set it up;</p>

<ol>
  <li>Go to your <a href="http://www.aweber.com/users/web_forms" target="_blank">AWeber web forms page</a></li>
  <li>Create a new form or choose an existing one</li>
  <li>Under publishing options choose "JavaScript Snippet"</li>
  <li>Copy/paste the provided code into the field below</li>
</ol>

    <p><label for="<?php echo $this->get_field_id('title'); ?>"><?php _e('Title:', WF_THEME_TEXTDOMAIN); ?></label>
    <input class="widefat" id="<?php echo $this->get_field_id('title'); ?>" name="<?php echo $this->get_field_name('title'); ?>" type="text" value="<?php echo esc_attr($title); ?>" /></p>

    <p><label for="<?php echo $this->get_field_id('text'); ?>"><?php _e('JavaScript snippet:', WF_THEME_TEXTDOMAIN); ?></label><textarea class="widefat" rows="2" cols="20" id="<?php echo $this->get_field_id('text'); ?>" name="<?php echo $this->get_field_name('text'); ?>"><?php echo $text; ?></textarea><br>
    It should look something like: <code>&lt;script type="text/javascript" src="http://forms.aweber.com/form/12/12345.js"&gt;&lt;/script&gt;</code></p>
<?php
  } // form
} // aweberFormWidget
?>
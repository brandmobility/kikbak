<?php
/**
 * Web factory Themes
 * (c) Web factory Ltd, 2013
 */

class WP_Customize_Textarea extends WP_Customize_Control {
  public $type  = 'textarea';
  public $label = 'Textarea';
  public $context;

  public function render_content() {
    echo '<label>
            <span class="customize-control-title">' . esc_html($this->label) . '</span>
            <textarea rows="3"' . $this->get_link() . '>' . esc_attr($this->value()) . '</textarea><br />
          </label>';
  }
}

class WP_Customize_Help extends WP_Customize_Control {
  public $type    = 'help';
  public $label   = 'Help text';

  public function render_content() {
    echo '<div class="customizer-help">' . $this->label . '</div>';
  }
}

class WP_Customize_Html extends WP_Customize_Control {
  public $type    = 'html';
  public $label   = 'Custom HTML';

  public function render_content() {
    echo '<div class="customizer-html">' . $this->label . '</div>';
  }
}

class WP_Customize_Category extends WP_Customize_Control {
  public $type    = 'category';
  public $label   = 'Choose a category';
  public $show_option_all = null;
  public $show_option_none = null;

  public function render_content() {
    $dropdown = wp_dropdown_categories(array('show_option_all'    => $this->show_option_all,
                                             'show_option_none'   => $this->show_option_none,
                                             'orderby'            => 'name',
                                             'order'              => 'ASC',
                                             'show_count'         => true,
                                             'hide_empty'         => false,
                                             'child_of'           => null,
                                             'exclude'            => null,
                                             'echo'               => false,
                                             'selected'           => $this->value(),
                                             'hierarchical'       => false,
                                             'name'               => null,
                                             'id'                 => null,
                                             'class'              => 'theme-customize-dropdown',
                                             'depth'              => 0,
                                             'tab_index'          => false,
                                             'taxonomy'           => 'category',
                                             'hide_if_empty'      => false));
    $dropdown = str_replace('<select', '<select ' . $this->get_link(), $dropdown);

    echo '<label>
        <span class="customize-control-title">' . esc_html($this->label) . '</span>';
    echo $dropdown;
    echo '<br /></label>';
  }
}
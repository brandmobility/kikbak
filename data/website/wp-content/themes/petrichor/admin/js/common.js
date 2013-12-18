/**
 * Petrichor
 * (c) Web factory Ltd, 2013
 */

jQuery(function($){
  $('#wf-theme-export-options').click(function() {
    $(this).select();
  });

  $('#wf-theme-download-export').click(function() {
    window.location = 'themes.php?page=wf_theme_options&wf_theme_download_export=1';
  });

  $('#customize-control-header_type select').change(function() {
    change_header_type();
  });
  change_header_type();
}); // onload

function change_header_type() {
  val = jQuery('#customize-control-header_type select').val();

  jQuery('#customize-section-wf_frontpage .customize-control').hide();
  jQuery('#accordion-section-wf_frontpage .customize-control').hide();
  jQuery('#customize-control-front_page_menu').show();
  jQuery('#customize-control-header_type').show();
  jQuery('#customize-control-header_title').show();
  jQuery('#customize-control-header_text').show();
  jQuery('#customize-control-help_1section').show();

  if (val == 1 || val == 2 || val == 3 || val == 5 || val == 6|| val == 7 || val == 8 || val == 9) {
    jQuery('#customize-control-slider_category').show();
    jQuery('#customize-control-slider_pause').show();
    jQuery('#customize-control-slider_animation').show();
    jQuery('#customize-control-slider_pause_hover').show();
    jQuery('#customize-control-slider_controls').show();
  } else if (val == 4) {
    jQuery('#customize-control-header_video').show();
    jQuery('#customize-control-help_video').show();
  }
} // change_header_type
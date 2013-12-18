/**
 * Petrichor
 * (c) Web factory Ltd, 2013
 */

jQuery(function($){
  wp.customize(wf_theme.theme_options + '[main_color]', function(value) {
    value.bind(function(to) {
      if (to == 'false' || !to || to == false) {
        return;
      }

      $('#content-boxes h3, .page-title, #main-navigation li a:hover').css('color', to);
      $('footer a').attr('style', 'color: ' + to + '!important');
      $('.download-btn').css('background-color', to);
      $('#newsletter, #blockquote-rotator,#logo a,#twitter-feed, .section-even').css('background', to);
      $('.thumbnail:hover').css('border', '1px solid ' + to);
    });
  });
  
  wp.customize(wf_theme.theme_options + '[skin]', function(value) {
    value.bind(function(to) {
      $('#wf-skin-css').attr('href', wf_theme.theme_folder + '/css/color-themes/' + to + '.css');
    });
  });
  
  wp.customize(wf_theme.theme_options + '[texture]', function(value) {
    value.bind(function(to) {
      $('#wf-texture-css').attr('href', wf_theme.theme_folder + '/css/textures/' + to + '.css');
    });
  });
  
  wp.customize(wf_theme.theme_options + '[background]', function(value) {
    value.bind(function(to) {
      $('#wf-background-css').attr('href', wf_theme.theme_folder + '/css/header-images/' + to + '.css');
    });
  });
});
/*
 * Petrichor
 * (c) 2013, Web factory Ltd
 */

jQuery(function($) {
  // flex slider
  if ($('.flexslider').length) {
    $('.flexslider').flexslider({
      animation: wf_theme.slider_animation,
      directionNav: (wf_theme.slider_controls == '1' || wf_theme.slider_controls == '3')? true: false,
      controlNav: (wf_theme.slider_controls == '2' || wf_theme.slider_controls == '3')? true: false,
      pauseOnAction: true,
      pauseOnHover: !!parseInt(wf_theme.slider_pause_hover, 10),
      direction: 'horizontal',
      slideshow: !!parseInt(wf_theme.slider_pause, 10),
      slideshowSpeed: parseInt(wf_theme.slider_pause, 10),
    });
  }

  // lightbox gallery
  if($("a[data-gal^='prettyPhoto']").length) {
    $("a[data-gal^='prettyPhoto']").prettyPhoto({ social_tools: '', deeplinking: '' });
  }

  // blockquote rotator
  if ($('section blockquote').length) {
    $('section blockquote').quovolver(500, 6000);
  }

  // gmap init
  var gmap = $('.gmap');
  var addr = '//maps.google.com/maps?hl=en&ie=utf8&output=embed&iwd=1&mrt=loc&t=m&q=' + encodeURIComponent(gmap.attr('data-address'));
  addr += '&z=' + gmap.attr('data-zoom');
  if (gmap.attr('data-bubble')) {
    addr += '&iwloc=addr';
  } else {
    addr += '&iwloc=near';
  }
  gmap.attr('src', addr);

  // init twitter feeds
  $('.tweet').each(function(index, element){
    $(element).tweet({
            username: $(element).attr('data-username'),
            avatar_size: $(element).attr('data-avatar-size'),
            count: $(element).attr('data-count'),
            join_text: 'auto',
            auto_join_text_default: ' we said, ',
            auto_join_text_ed: ' we ',
            auto_join_text_ing: ' we were ',
            auto_join_text_reply: ' we replied to ',
            auto_join_text_url: ' we were checking out ',
            loading_text: 'loading tweets...',
            modpath: wf_theme.ajaxurl + '?action=wf_theme_twitter_api'
    });
  }); // each twitter widget

  // main dropdown menu
  $('ul#main-navigation li').hover(function(){
      $(this).children('ul').delay(20).fadeIn(200);
    }, function(){
      $(this).children('ul').delay(20).fadeOut(200);
  });

  // generate mobile menu
  if ($('#main-menu-select').length && $('#main-menu-select').attr('data-autogenerate') == 'true') {
    var mobile_menu = $('#main-menu-select');
    $('#main-navigation li a').each(function(index, elem) {
      if ($(elem).parents('ul.sub-menu').length) {
        tmp = '&nbsp;&nbsp;-&nbsp;' + $(elem).html();
      } else {
        tmp = $(elem).html();
      }
      
      if ($(elem).parent('li').hasClass('current-menu-item')) {
        mobile_menu.append($('<option></option>').val($(elem).attr('href')).html(tmp).attr('selected', 'selected'));
      } else {
        mobile_menu.append($('<option></option>').val($(elem).attr('href')).html(tmp));
      }
    });
  }
  
  // mark submenus
  $('#main-navigation ul.sub-menu').parent('li').children('a').html(function(){ return $(this).html() + ' +'; });

  // mobile menu click
  $('#main-menu-select').change(function() {
    link = $(this).val();
    if (!link) {
      return;
    }

    document.location.href = link;
  });

  // hide/show default text when user focuses on newsletter subscribe field
  var defaultNameTxt = $('#newsletter-name').val();
  var defaultEmailTxt = $('#newsletter-email').val();
  $('#newsletter-name').focus(function() {
    if ($('#newsletter-name').val() == defaultNameTxt) {
      $('#newsletter-name').val('');
    }
  });
  $('#newsletter-name').blur(function() {
    if ($('#newsletter-name').val() == '') {
      $('#newsletter-name').val(defaultNameTxt);
    }
  });
  $('#newsletter-email').focus(function() {
    if ($('#newsletter-email').val() == defaultEmailTxt) {
      $('#newsletter-email').val('');
    }
  });
  $('#newsletter-email').blur(function() {
    if ($('#newsletter-email').val() == '') {
      $('#newsletter-email').val(defaultEmailTxt);
    }
  });
  
  // init newsletter subscription AJAX handling
  if ($('#newsletterform').length > 0) {
    $('#newsletterform').ajaxForm({ dataType: 'json',
                                    data: {action: 'wf_theme_newsletter'},
                                    timeout: 15000,
                                    error: function() { $('#newsletter-email').removeClass('preloading'); alert('MailChimp is currently unavailable. Please try again later.'); },
                                    success: newsletterResponseMailchimp});
    $('#newslettersubmit').click(function() { $('#newsletter-email').addClass('preloading'); $('#newsletterform').submit(); return false; });
  } // if newsletter form
  
    // init contact form validation and AJAX handling
  if ($("#contact_form").length > 0) {
    $("#contact_form").validate({ rules: { name: 'required',
                                      email: { required: true, email: true },
                                      message: 'required',
                                      captcha: {required: true, remote: { url: wf_theme.ajaxurl, type: 'post', data: {action: 'wf_theme_captcha', check: 1, captcha: function() { return jQuery('#captcha').val(); } } }}},
                                messages: { name: "This field is required.",
                                            message: "This field is required.",
                                            email: { required: "This field is required.",
                                                     email: "Please enter a valid email address."},
                                            captcha: 'Are you sure you\'re a human? Please recheck.'},
                                submitHandler: function(form) { $(form).ajaxSubmit({url: wf_theme.ajaxurl, data: { action: 'wf_theme_contact_send' }, type: 'post', dataType: 'html', success: contactFormResponse}); }
                              });
  }
  
  // load captcha question
  if ($('#captcha-img').length) {
    $.post(wf_theme.ajaxurl, {action: 'wf_theme_captcha', generate: 1}, function(response) {
      $('#captcha-img').html(response);
    });
  }
}); // onload

// handle contact form AJAX response
function contactFormResponse(response) {
  if (response == '1') {
    alert(wf_theme.contact_form_msg_ok);
  } else {
    alert('We are having some technical difficulties. Please reload the page and try again.');
  }
} // contactFormResponse

// handle newsletter subscribe AJAX response - Mailchimp ver
function newsletterResponseMailchimp(response) {
  if (response.responseStatus == 'err') {
    if (response.responseMsg == 'ajax') {
      alert('Error - this script can only be invoked via an AJAX call.');
    } else if (response.responseMsg == 'name') {
      alert('Please enter a valid name.');
    } else if (response.responseMsg == 'email') {
      alert('Please enter a valid email address.');
    } else if (response.responseMsg == 'listid') {
      alert('Invalid MailChimp list name.');
    } else if (response.responseMsg == 'duplicate') {
      alert('You are already subscribed to our newsletter.');
    } else {
      alert('Undocumented error (' + response.responseMsg + '). Please refresh the page and try again.');
    }
  } else if (response.responseStatus == 'ok') {
    alert('Thank you for subscribing! Please confirm your subscription in the email you\'ll receive shortly.');
  } else {
    alert('Undocumented error. Please refresh the page and try again.');
  }
} // newsletterResponseMailchimp
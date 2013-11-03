(function(){

'use strict';

$(document).ready(function() {

  window.twttr = (function (d,s,id) {
    var t, js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return; js=d.createElement(s); js.id=id;
    js.src="https://platform.twitter.com/widgets.js"; fjs.parentNode.insertBefore(js, fjs);
    return window.twttr || (t = { _e: [], ready: function(f){ t._e.push(f) } });
  }(document, "script", "twitter-wjs"));

  window.twttr.ready(function() {
    var str = "https://twitter.com/share?"
    var params = [
      {name:"url", value:"https://m.kikbak.me/m/#offer"},
      {name:"via", value:"kikbak"},
      {name:"count", value:"none"},
      {name:"text", value:"Check out this offer!"},
    ];
    $.each(params, function (i, item) {
      str += encodeURIComponent(item.name) + "=" + encodeURIComponent(item.value) + "&";
    });
    $('#custom-tweet-button a').attr("href", str);
    $('#custom-tweet-button').show();
  });
});

})();

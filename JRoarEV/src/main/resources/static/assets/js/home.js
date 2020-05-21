window.reproducir = function(idVideo) {
            document.getElementById(idVideo).play();
        };

        window.pausar = function(idVideo) {
            document.getElementById(idVideo).pause();
        };

        function playPause(idVideo) {
            var myVideo = document.getElementById(idVideo);
            if (myVideo.paused)
                myVideo.play();
            else
                myVideo.pause();
        }

        function makeBig(idVideo) {
            var myVideo = document.getElementById(idVideo);
            myVideo.style.width = '900px';
        }

        function makeSmall(idVideo) {
            var myVideo = document.getElementById(idVideo);
            myVideo.style.width = '320px';
        }

        function makeNormal(idVideo) {
            var myVideo = document.getElementById(idVideo);
            myVideo.style.width = '600px';
        }
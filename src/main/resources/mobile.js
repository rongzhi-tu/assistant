window.onload = function(){

    $env.appPlatform = 7168;
    $env.defaultLoader = "mobile-vue";
    //$env.resourcesHome = "resources/"

    $create("ssdev.ux.WebLoader").then(function (loader) {
        loader.on("moduleLoaded",function (m) {
            $event.emit("moduleLoaded",m);
        });
        loader.on("moduleLoadError",function (e) {
            $event.emit("moduleLoadError",e);
            console.error(e);
        });
        loader.init();
    },function (e) {
        console.error(e);
        $event.emit("moduleLoadError",e);
    });

};

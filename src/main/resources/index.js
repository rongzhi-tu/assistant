window.onload = function () {
    //支持 electron 环境
    try {
        window.nodeRequire = require
        delete window.require
        delete window.exports
        delete window.module
    } catch (ignored) {}

    $create('ssdev.ux.WebLoader').then(function (loader) {
        loader.on('moduleLoaded', function (m) {
            $event.emit('moduleLoaded', m)
        })
        loader.on('moduleLoadError', function (e) {
            $event.emit('moduleLoadError', e)
            console.error(e)
        })
        loader.init('extras');               //配置额外env参数, 实际文件名: extras.json
    }, function (e) {
        console.error(e)
        $event.emit('moduleLoadError', e)
    })
}
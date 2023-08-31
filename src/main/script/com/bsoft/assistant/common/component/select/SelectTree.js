$class("com.bsoft.assistant.common.component.select.SelectTree", {
    mixins: '/ssdev.utils.ServiceSupport',
    initChild: $emptyFunction,
    setOtherParam:function(data) {
        var me = this;
        if(data) {
            me.data.otherParam = data;
            me.initLoad()
        }
    },
    initLoad: function() {
        var me = this;
        me.getLoadServiceData({}).then(function (data) {
            me.data.treeList = data
        })
    },
    /** 异步加载树数据 */
    getLoadServiceData: function (req) {
        var me = this, defer = $Defer();
        if(me.data.otherParam && Object.keys(me.data.otherParam).length > 0) {
            req = Object.assign({}, req, me.data.otherParam);
        }
        req = me.deleteAttr(req);
        // console.log("req", req)
        var serviceId = me.data.service && me.data.service.serviceId, method = me.data.service && me.data.service.method;
        var url = me.data.service && me.data.service.url;
        if(serviceId && method) {
            var adapter = ssdev.rmi.serviceAdapter;
            var service = adapter.bindMethod({
                beanName: serviceId,
                method: method,
                useHttpHeader:true
            });
            service(req).then(function(json){
                defer.resolve(json);
            });
        } else if(url) {
            $ajax({
                url: url,
                jsonData: [req]
            }).then(function(res){
                defer.resolve(res.body);
            }).fail(function(e){
                defer.resolve([]);
            })
        } else {
            defer.resolve([]);
        }
        return defer.promise;
    },

    deleteAttr: function(obj) {
        var me = this;
        if(Object.prototype.toString.call(obj) == '[object Object]') {
            for(var attr in obj) {
                if(obj[attr] && Object.keys(obj[attr]).length > 0) {
                    me.deleteAttr(obj[attr])
                }
                if(String(obj[attr]).length == 0) {
                    delete obj[attr]
                }
            }
        }
        return obj
    }
});
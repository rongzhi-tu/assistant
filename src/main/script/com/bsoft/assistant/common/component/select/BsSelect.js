$class("com.bsoft.assistant.common.component.select.BsSelect", {
    extend: "/ssdev.ux.vue.VueContainer",
    mixins: "com.bsoft.assistant.common.component.select.SelectTree",
    tpl: true,
    initComponent: function (conf) {
        var me = this;
        me.data = {
            // 启用多选
            multiple: conf.multiple || false,
            // select值
            value: "",
            optionValue: [],
            // 树是否采用多选
            showCheckbox: conf.multiple || false,
            // 树是否采用懒加载
            lazy: conf.treeLazy || false,
            // 点击节点的时候是否展开
            expandOnClick: false,
            // 树节点属性值
            defaultProps: {
                label: conf.props && conf.props.label || "label",
                children: conf.props && conf.props.children || "children",
                isLeaf: conf.props && conf.props.isLeaf || "isLeaf"
            },
            // 树节点key值
            nodeKey: conf.props && conf.props.key || "key",
            // 初始checked的节点
            defaultChecked: [],
            // 初始展开的节点
            defaultExpanded: [],
            // 树的数据
            treeList: [],
            // 树懒加载的服务名
            service: conf.service || {},
            // 树请求的其他参数
            otherParam: {},
            // 是否只取叶子节点值
            useLeaf: conf.useLeaf || false,
            size: conf.size || "medium",
        };
        me.evtHandlers = {
            // 树异步加载数据
            loadData: function(node, resolve) {
                if(node.level === 0) {
                    if (me.treeList && me.treeList.length > 0) {
                        return resolve(me.treeList);
                    }
                    return ;
                }
                var req = {};
                if (node && node.level > 0) {
                    req = node.data;
                }
                me.getLoadServiceData(req).then(function (data) {
                    resolve(data)
                });
            },
            // 树多选
            checkChange: function() {
                let res = me.vue.$refs.refTree.getCheckedNodes(true, true); //这里两个true，1. 是否只是叶子节点 2. 是否包含半选节点（就是使得选择的时候不包含父节点）
                let arrLabel = [];
                let arr = [];
                res.forEach(item => {
                    arrLabel.push(item[me.data.defaultProps.label]);
                    arr.push(item);
                });
                me.data.optionValue = arr;
                me.data.value = arrLabel;
                me.fireEvent("check", res)
            },
            // 树节点点击
            nodeClick: function (data) {
                if(me.data.multiple) {
                    return
                }
                if(me.data.useLeaf) {
                    if(data[me.data.defaultProps.isLeaf]) {
                        me.data.optionValue = data;
                        me.data.value = data[me.data.defaultProps.label];
                        me.vue.$refs.refSelect.blur();
                        me.fireEvent("click", data)
                    }
                    return;
                }
                me.data.optionValue = data;
                me.data.value = data[me.data.defaultProps.label];
                me.vue.$refs.refSelect.blur();
                me.fireEvent("click", data)
            },
            // 清除事件
            nodeClear: function () {
                me.fireEvent("clear")
            }

        };
        me.callParent(arguments)
    },
});
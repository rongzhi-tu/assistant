$define("com.bsoft.assistant.common.component.tree.BsTree", {
    extend: 'ssdev.ux.vue.VueContainer',
    tpl: "/",
    deps:["/ssdev.rmi.serviceAdapter"]
}, function (html) {
    Vue.component("bs-tree-label", {
        template: html,
        props: ['filter', 'treeRef', 'props', 'treeLazy', 'treeList', 'service',
            'showCheckbox', 'checkedKeys', 'expandedKeys', 'otherParam', 'menus', 'rightFlag', 'hasAdd', 'defaultHeight'],
        data: function () {
            return {
                addIcon: this.hasAdd || false,
                refTree: this.treeRef || 'comTree',
                // 树的框度 默认
                treeWidth: "",
                // input过滤的值
                filterText: this.filter && this.filter.value || "",
                // 树节点属性值
                defaultProps: {
                    label: this.props.label || "label",
                    children: this.props.children || "children",
                    isLeaf: this.props.isLeaf || "isLeaf"
                },
                // 树节点key值
                nodeKey: this.props.key || "key",
                // checked的节点
                checkKeys: this.checkedKeys || [],
                // 展开的节点
                expandKeys: this.expandedKeys || [],
                treeData: [],
                lazy: this.treeLazy || false,
                // 弹窗left值
                clientX: "",
                // 弹窗top值
                clientY: "",
                contextMenu: this.menus || [],
                listener: null,
                hoverIndex: -1,
                // 当前树右键点击的节点DOM对象
                eventNode: null,
                expandOnClickNode: false,
                // 整个树组件的高度
                treeHeight: "",
                // 右键菜单menu的高度
                menuHeight: 0,
                // 右键node的数据
                rightClickData: {},
                // 右键node
                rightClickNode: {}
            }
        },
        computed: {
            // 是否显示弹窗
            Flag: {
                get: function() {
                    return this.rightFlag
                },
                set: function(val) {
                    this.updateTreeMenu(val)
                }
            }
        },
        watch: {
            /** 监听input过滤数据的内容
             * treeLazy: true 采用异步接口加载数据
             * treeLazy: false 整棵树内容中过滤 */
            filterText:  _.debounce(function(val) {
                var me = this;
                if(!this.lazy) {
                    return this.$refs[this.refTree].filter(val)
                }
                this.getLoadServiceData({}).then(function (data) {
                    me.treeData = data
                })
            }, 500),
            // 监听传给树的其他参数
            otherParam: function(newVal, oldVal) {
                var me = this;
                if(!this.lazy) {
                    return
                }
                this.getLoadServiceData({}).then(function (data) {
                    me.treeData = data
                })
            },
            // 监听传给树的数据列表
            treeList: function (newVal, oldVal) {
                this.treeData =  newVal;
            },
            menus: function (newVal, oldVal) {
                this.contextMenu = newVal;
                this.clientX = this.eventNode.clientX;
                this.clientY = this.eventNode.clientY;
            }
        },
        mounted: function() {
            this.handleClickDoc();
            this.$once("hook:updated", function () {
                this.updateHeight();
            })
        },
        destroyed: function() {
            window.removeEventListener("click", this.listener, false)
        },
        methods: {
            // 计算弹窗的高度
            clacHeihgtFun: function() {
                this.$nextTick(function () {
                    // 获取整个树组件对象距离浏览器顶部的宽度
                    var treeTop = this.$refs['tree-wrapper-label'].getBoundingClientRect().top;
                    // popover中fixed定位不需要减距离顶部的高度了
                    // this.clientY = this.clientY - treeTop;
                    // 获取菜单的高度
                    var popoverWrap = this.$refs['tree-wrapper-label'].querySelector("#contextMenu");
                    var popover = popoverWrap.querySelector(".el-popover");
                    this.menuHeight = popover.offsetHeight;

                    // 计算当前右键的node对象和树高度的差的绝对值
                    var dis = Math.abs(this.clientY - this.treeHeight);
                    if(dis < this.menuHeight) {
                        this.clientY = this.clientY - this.menuHeight;
                    }
                })
            },
            updateHeight: function() {
                if(!this.defaultHeight) {
                    this.treeHeight = this.$refs['tree-wrapper-label'].offsetHeight - 12 ;
                    return;
                }
                this.treeHeight = this.defaultHeight;
            },
            // 增加按钮的鼠标移入事件
            handleMouseover: function(event) {
                this.handleRightClick(event)
            },
            /**  监听点击是否显示右键菜单 */
            updateTreeMenu: function(val) {
                this.$emit("update:rightFlag", val)
            },
            /** 监听点击事件取消右键事件 */
            handleClickDoc: function() {
                var me = this;
                me.listener = function (e) {
                    me.hoverIndex = -1;
                    var contextMenu = document.getElementById("contextMenu");
                    if (!contextMenu) return;
                    !contextMenu.contains(e.target) && me.updateTreeMenu( false);
                };
                window.addEventListener("click",me.listener , false);
            },
            /** 右键菜单点击事件 */
            handleClickMenu: function(callback, item) {
                if(!item.show){
                    this.$set(item, 'show', false);
                    item.show = !item.show
                } else {
                    item.show = !item.show
                }
                var currentNode = this.rightClickData;
                $is.Function(callback) && callback(currentNode, this.rightClickNode);
            },
            /** 树右键事件 */
            handleRightClick: function (event, obj, node, self) {
                /** 如果默认要显示右键菜单按钮 */
                this.eventNode = event;
                this.clientX = event.clientX;
                this.clientY = event.clientY;
                this.updateTreeMenu(true);
                this.clacHeihgtFun();
                this.rightClickData = obj;
                this.rightClickNode = node;
                this.$emit("rightClick", obj);
            },
            /** 树节点异步方法 */
            loadTreeData: function (node, resolve) {
                var me = this;
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
                })
            },
            /** 点击树节点展开*/
            handleNodeExpand: function (data, node, self) {
                this.updateTreeMenu( false); // 点击树节点的时候，将右键菜单隐藏。
                this.$emit('nodeExpand', data);
            },
            /** 点击树节点*/
            handleNodeClick: function (data) {
                this.updateTreeMenu( false);
                this.$emit('nodeClick', data);
            },
            /** 非异步过滤数据*/
            filterNode: function (value, data) {
                if(!value) return true;
                return data[this.props.label].indexOf(value) !== -1
            },
            /** 异步加载数据 */
            getLoadServiceData: function (req) {
                var me = this, defer = $Defer();
                if(me.filter && me.filter.show) {
                    var key = me.filter.key || "filterVal";
                    req = Object.assign(req, { [key] : me.filterText });
                }
                if(me.otherParam && Object.keys(me.otherParam).length > 0) {
                    req = Object.assign({}, req, me.otherParam);
                }
                req = me.deleteAttr(req);
                var serviceId = me.service && me.service.serviceId, method = me.service && me.service.method;
                var url = me.service && me.service.url;
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
        }
    })
});

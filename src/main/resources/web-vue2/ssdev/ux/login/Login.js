$class("ssdev.ux.login.Login", {
    extend: "/ssdev.ux.vue.VueContainer",
    mixins: [
        "/ssdev.utils.ServiceSupport",
        "/ssdev.ux.login.LoginApi"
    ],
    deps: ["com.bsoft.assistant.common.aes.aes"],
    tpl: ".",
    css: ".ssdev.ux.login.css.login",
    requestDefineDeep: 3,
    autoLoginFirstUrt: false,
    winConf: {
        title: "系统登录",
        autoShow: false
    },
    initComponent: function (conf) {
        var me = this;
        var evtHandlers = {
            tidEnterClick: function () {
                var fm = me.data.formData, el = me.el;
                if (!fm.uid) {
                    el.querySelector("#uid").focus();
                }
                else {
                    me.evtHandlers.confirmBtnClick();
                }
            },
            uidEnterClick: function () {
                var fm = me.data.formData, el = me.el;
                if (!fm.pwd) {
                    el.querySelector("#pwd").focus();
                }
                else {
                    me.evtHandlers.confirmBtnClick();
                }
            },
            pwdEnterClick: function () {
                var fm = me.data.formData;
                if (fm.pwd) {
                    me.evtHandlers.confirmBtnClick();
                }
            },
            urtChange: function () {
                var fm = me.data.formData;
                if (fm.urt) {
                    me.confirmFieldFocus();
                }
            },
            confirmBtnClick: _.debounce(function () {
                var fm = me.data.formData, el = me.el;
                if (!fm.tenantId) {
                    me.tidFieldFocus();
                    return;
                }
                if (!fm.uid) {
                    me.uidFieldFocus();
                    return;
                }
                if (!fm.pwd) {
                    me.pwdFieldFocus();
                    return;
                }
                if (!fm.urt) {
                    me.on("loadrole", function (urs) {
                        if (urs) {
                            fm.urt = urs[0].ud_id || urs[0].id;
                            me.confirmFieldFocus();
                        }
                    }, '', true);
                    me.evtHandlers.doExpand(true);
                    return;
                }
                me.data.loading = true;
                me.data.errorInfo = null;
                me.userRoleToken = me.data.urs.find(function (it) {
                    var id = it.ud_id || it.id;
                    return id === fm.urt;
                });
                var urtDept = me.userRoleToken.userRoleDepts ? me.userRoleToken.ud_id : '';
                me.userRoleToken.md5Pwd = ssdev.utils.codec.md5.MD5(fm.pwd).toString()
                me.doLoadAppDefines(me.userRoleToken.id, urtDept).then(function () {
                    me.data.loading = false;
                    if (fm.rememberMe) {
                        $env.setLocalStorage("userData", {
                            uid: fm.uid,
                            tenantId: fm.tenantId,
                            rememberMe: fm.rememberMe,
                            userId: me.userRoleToken.userId
                        })
                    }
                    else {
                        $env.removeLocalStorage("userData");
                    }
                }).then(function () {
                    if (me.userId != me.userRoleToken.userId) {
                        me.loadSkin(me.userRoleToken.userId);
                    }
                }).fail(function () {
                    me.data.loading = false;
                });
            }, 200),
            doExpand: function (bool) {
                if (!bool) {
                    return;            //下拉框展开时调用
                }
                var fm = me.data.formData, el = me.el;
                if (!fm.tenantId) {
                    me.tidFieldFocus();
                    return;
                }
                if (!fm.uid) {
                    me.uidFieldFocus();
                    return;
                }
                if (!fm.pwd) {
                    me.pwdFieldFocus();
                    return;
                }

                fm.urt = "";
                me.data.errorInfo = null;
                me.data.roleLoad = true;
                me.doLogin(fm).then(function (urs) {
                    me.data.roleLoad = false;
                    me.data.urs = urs;
                    me.fireEvent("loadrole", urs);
                })
            }
        };
        me.setupService([{
            beanName: "bbp.skinService",
            method: ["getSkinTheme"]
        }]);
        me.evtHandlers = evtHandlers;

        var formData = me.getLocalUserData();
        if (!formData.tenantId) {
            formData.tenantId = $env.tenantId
        }
        me.data = {
            roleLoad: false,
            loading: false,
            formData: formData,
            urs: [],
            errorInfo: null
        };
        me.callParent(arguments);
    },
    tidFieldFocus: function () {
        var me = this, el = me.el, fm = me.data.formData;
        el.querySelector("#tid").focus();
        fm.tenantId = "";
    },
    pwdFieldFocus: function () {
        var me = this, el = me.el, fm = me.data.formData;
        el.querySelector("#pwd").focus();
        fm.pwd = "";
    },
    confirmFieldFocus: function () {
        var me = this, el = me.el;
        el.querySelector("#confirm").focus();
    },
    uidFieldFocus: function () {
        var me = this, el = me.el, fm = me.data.formData;
        el.querySelector("#uid").focus();
        fm.uid = "";
    },
    showError: function (s) {
        var me = this;
        me.data.errorInfo = s;
    },
    encrypt: function (word, passKey) {
        var key = CryptoJS.enc.Utf8.parse(passKey);
        var str = CryptoJS.enc.Utf8.parse(word);
        var encrypted = CryptoJS.AES.encrypt(str, key, {mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7});
        return encrypted.toString();
    },
    afterAppend: function () {
        var me = this;

        if ($env.urlParams && Object.keys($env.urlParams)[0]) {
            var paramsArray = window.atob(Object.keys($env.urlParams)[0]).split("&");
            paramsArray.forEach(function (item) {
                var urlParamsValue = item.split("=");
                $env.urlParams[urlParamsValue[0]] = urlParamsValue[1];
            });
            me.getSessionUrt().then(function (json) {
                var urt = json.body;
                if (urt) {
                    var urtDept = urt.userRoleDepts ? urt.ud_id : '';
                    me.doLoadAppDefines(urt.id, urtDept);
                }
            }).fail(function (json) {
                var params = {
                    uid: $env.urlParams.uid, pwd: $env.urlParams.pwd, tenantId: $env.urlParams.tenantId
                };
                me.doLogin(params).then(function (urs) {
                    me.userRoleToken = urs.find(function (it) {
                        // return it.id === $env.urlParams.id;
                        return it.roleCd === $env.urlParams.roleCd;
                    });

                    var urtDept = me.userRoleToken.userRoleDepts ? me.userRoleToken.ud_id : '';
                    me.userRoleToken.md5Pwd = ssdev.utils.codec.md5.MD5($env.urlParams.pwd).toString()
                    me.doLoadAppDefines(me.userRoleToken.id, urtDept)
                })
            });
        } else {
            me.getSessionUrt().then(function (json) {
                var urt = json.body;
                if (urt) {
                    me.userRoleToken = urt;
                    var urtDept = me.userRoleToken.userRoleDepts ? me.userRoleToken.ud_id : '';
                    me.doLoadAppDefines(urt.id, urtDept);
                    me.loadSkin(urt.userId)
                }
            }).fail(function (json) {
                me.fireEvent("loginShow");
                var userData = $env.getLocalStorage("userData", true) || {};
                me.userId = userData.userId;
                if (me.userId) {
                    me.loadSkin(me.userId);
                }
                me.onActive();
            });
        }
    },
    loadSkin: function (userId) {
        var me = this;
        me.service.getSkinTheme(userId).then(function (res) {
            if (res) {
                var skinName = res.skinName;
                var skin = {
                    name: skinName || '_default',
                    path: {
                        vue: skinName ? "ssdev.bbp.theme." + skinName : '',
                        ext: skinName ? "ssdev.bbp.theme.ext." + skinName : ''
                    }
                };
                $env.globalContext.put("skinTheme", skin);
                if (skin.path.vue) {
                    me.skinTheme = skinName;
                    $styleSheet(skin.path.vue);
                }
            }
        }).fail(function (json) {

        });
    },
    onActive: function () {
        var me = this, el = this.el;
        setTimeout(function () {
            var tidEl = el.querySelector("#tid");
            var uidEl = el.querySelector("#uid");
            var pwdEl = el.querySelector("#pwd");

            var fm = me.data.formData;
            if (!fm.tenantId) {
                tidEl.focus();
            }
            else if (!fm.uid) {
                uidEl.focus();
            }
            else {
                pwdEl.focus();
            }
            me.fireEvent("ready");
        }, 100);
    }
});


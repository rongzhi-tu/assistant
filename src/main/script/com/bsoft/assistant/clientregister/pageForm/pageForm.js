$class("com.bsoft.assistant.clientregister.pageForm.pageForm",{
    extend:"/ssdev.ux.vue.VueContainer",
    mixins: 'ssdev.utils.ServiceSupport',
    css: 'com.bsoft.assistant.clientregister.pageForm.pageForm',
    tpl:true,
    initComponent: function (conf) {
        let me = this;
        me.setupService([{
            // beanName:'learning.person',
          beanName: "assistant.ssoAssistantClientTegisterService",
          method: ["selectSsoAssistantClientTegister", "insertSsoAssistantClientTegister",
            "deleteSsoAssistantClientTegister","updateSsoAssistantClientTegister","selectServerByCode"]
        }]);
        me.data = {
          tipsTypeOptions:[{key:'1',text:'服务转发'},{key:'2',text:'消息提醒'},{key:'3',text:'截留提醒'}],
          server: {
              serverCode: '',
              serverName: '',
              serverUrl: '',
              code:'',
              tipsType:'',
              serverUrlBak:'',
              serverDiscreption:'',
              insertUser: ''
            },
            //orgOptions: $env.global_constants.orgOptions(),//获取机构列表信息,
            orgOptions: [],


        }
        me.evtHandlers = {
           confirm: function () {
             let token = $env.globalContext.get('urt')
             let date = new Date();
             let insertOrUpName = '';
             if(token){
               insertOrUpName = token.userName
               //me.data.server.orgName = token.manageUnitName.split("-")[0]
               //me.data.server.orgId = token.orgId
             }
               if(this.server.serverName===''||this.server.serverName===null||this.server.serverName==undefined){
                   me.vue.$message({
                       message: '请完善服务信息!',
                       type: "error",
                       duration: "1000"
                   });
                   return
               }
               /*校验服务是否存在*/
             me.service.selectServerByCode({option:me.conf.opt,serverCode:me.data.server.serverCode,serverName:me.data.server.serverName}).then(res=>{
               if(res.isHaveServer == true){
                 me.vue.$message({
                   message: "服务编码或服务名称已存在!",
                   type: "error",
                   duration: "1000"
                 });
                 return;
               }
               if( me.conf.opt=='upd'){ // 修改操作
                 let rawData = me.conf.rowData;
                 let updateObj = {};
                 updateObj.id = rawData.id;
                 //updateObj.fgActive = rawData.fgActive;
                 updateObj.updateUser = insertOrUpName;
                 //updateObj.updateTime = insertOrUptime;
                 updateObj.serverCode = me.data.server.serverCode;
                 updateObj.serverName = me.data.server.serverName;
                 updateObj.serverUrl = me.data.server.serverUrl;
                 updateObj.code = me.data.server.code;
                 updateObj.tipsType = me.data.server.tipsType;
                 updateObj.serverUrlBak = me.data.server.serverUrlBak;
                 updateObj.serverDiscreption = me.data.server.serverDiscreption;
                 me.service.updateSsoAssistantClientTegister(updateObj).then(res=>{
                   me.conf.sub.close();
                   me.vue.$message({
                     message: "服务更新成功!",
                     type: "success",
                     duration: "1000"
                   });
                   me.conf.parent.changParamLoadDatas();
                 }).fail(rej=>{
                   me.vue.$message({
                     message: rej.msg,
                     type: "error",
                     duration: "1000"
                   });
                 })
               }else{ //
                 let updateObj = {};
                 updateObj.insertUser = insertOrUpName;
                 //updateObj.insertTime = insertOrUptime;
                 //updateObj.fgActive = 1;
                 updateObj.id = this.createId();
                 updateObj.serverCode = me.data.server.serverCode;
                 updateObj.serverName = me.data.server.serverName;
                 updateObj.serverUrl = me.data.server.serverUrl;
                 updateObj.code = me.data.server.code;
                 updateObj.tipsType = me.data.server.tipsType;
                 updateObj.serverUrlBak = me.data.server.serverUrlBak;
                 updateObj.serverDiscreption = me.data.server.serverDiscreption;
                 me.service.insertSsoAssistantClientTegister(updateObj).then(res=>{
                   me.conf.sub.close();
                   me.vue.$message({
                     message: "服务创建成功!",
                     type: "success",
                     duration: "1000"
                   });
                   me.conf.parent.changParamLoadDatas();
                 }).fail(rej=>{
                   me.vue.$message({
                     message: rej.msg,
                     type: "error",
                     duration: "1000"
                   });
                 })
               }
             }).fail(rej=>{
               me.vue.$message({
                 message: rej.msg,
                 type: "error",
                 duration: "1000"
               });
             })


           },
           createId: function () {
             var s = [];
             var hexDigits = "0123456789abcdef";
             for (var i = 0; i < 36; i++) {
               s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
             }
             s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
             s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
             s[8] = s[13] = s[18] = s[23] = "-";
             var uuid = s.join("");
             return uuid;
           },
           close: function () {
               me.conf.sub.close();
           }
        }
        me.callParent(arguments)
    },
    afterAppend:function () {
        debugger
        let me = this;
        if(me.conf.opt=='upd'){
            me.data.server.serverCode=me.conf.rowData.serverCode
            me.data.server.serverName=me.conf.rowData.serverName
            me.data.server.serverUrl = me.conf.rowData.serverUrl
            me.data.server.code = me.conf.rowData.code
            me.data.server.tipsType = me.conf.rowData.tipsType
            me.data.server.serverUrlBak = me.conf.rowData.serverUrlBak
            me.data.server.serverDiscreption = me.conf.rowData.serverDiscreption
            me.data.server.insertUser = me.conf.rowData.insertUser
        }else{
            let token = $env.globalContext.get('urt')
            let date = new Date();
            if(token){
                me.data.server.insertUser = token.userName
                //me.data.server.orgName = token.manageUnitName.split("-")[0]
                //me.data.server.orgId = token.orgId
            }
        }
    }
})
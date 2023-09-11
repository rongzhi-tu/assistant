$class("com.bsoft.assistant.clientregister.userform.userForm",{
    extend:"/ssdev.ux.vue.VueContainer",
    mixins: 'ssdev.utils.ServiceSupport',
    css: 'com.bsoft.assistant.clientregister.userform.userForm',
    tpl:true,
    initComponent: function (conf) {
        let me = this;
        me.setupService([{
            // beanName:'learning.person',
          beanName: "assistant.ssoAssistantClientTegisterService",
          method: ["selectSsoAssistantClientTegister",
                   "insertSsoAssistantClientTegister",
                   "deleteSsoAssistantClientTegister",
                   "insertSsoAssistantClientTegisterUser",
                   "selectSsoAssistantClientTegisterUser"]
        }]);
        me.data = {
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
          tipsTypeOptions:[{key:'1',text:'服务转发'},{key:'2',text:'消息提醒'},{key:'3',text:'截留提醒'}],
          data: [
            // {userCode:'1',userName:'用户1',disabled:false},
            // {userCode:'2',userName:'用户2',disabled:false},
            // {userCode:'3',userName:'用户3',disabled:false},
            // {userCode:'4',userName:'用户4',disabled:false},
          ],
          value: [],
          value4: []
        }
        me.evtHandlers = {
          handleChange(value, direction, movedKeys) {
            console.log(value, direction, movedKeys);
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
           confirm: function () {
            debugger
               let params = {};
               let userlist = [];
               //me.data.server.insertUser = me.conf.rowData.insertUser
               /*根据 value数组中的userCode将 data中的人员获取并组装数据*/
               let users = [];
               for(let i in me.data.value){
                 let userCode = me.data.value[i];
                 for(let j in me.data.data){
                     if(me.data.data[j].userCode == userCode){
                       let user = {};
                       user.id = this.createId();
                       user.insertUser = me.data.server.insertUser;
                       user.serverCode = me.data.server.serverCode;
                       user.userCode = userCode;
                       user.userName = me.data.data[j].userName;
                       user.jgbm = '';
                       user.jgmc = '';
                       userlist.push(user);
                     }
                 }
               }
             userlist = userlist
             params.serverCode =  me.data.server.serverCode;
             params.data =  userlist;
               me.service.insertSsoAssistantClientTegisterUser(params).then(res=>{
                   me.conf.sub.close();
                   me.vue.$message({
                   message: "用户保存成功!",
                   type: "success",
                   duration: "1000"
                 });
               }).fail(rej=>{
                   me.vue.$message({
                       message: rej.msg,
                       type: "error",
                       duration: "1000"
                   });
               })
           },
        }
        me.callParent(arguments)
    },
    afterAppend:function () {
        let me = this;
      let userIdlist = [];
      let token = $env.globalContext.get('urt')
      if(token){
        me.data.server.insertName = token.userName
        //me.data.server.orgName = token.manageUnitName.split("-")[0]
        //me.data.server.orgId = token.orgId
      }
      if(me.conf.opt=='upd'){
        me.data.server.serverCode=me.conf.rowData.serverCode
        me.data.server.insertUser = me.conf.rowData.insertUser
        me.data.server.tipsType = me.conf.rowData.tipsType
        me.service.selectSsoAssistantClientTegisterUser(me.data.server.serverCode).then(res=>{
          debugger
          let data = res;
          for (let i in data){
            me.data.value.push(data[i]["userCode"]);
          }
        }).fail(rej=>{
          me.vue.$message({
            message: rej.msg,
            type: "error",
            duration: "1000"
          });
        })
      }
      //通过bbp获取用户信息    ···········如果从助手服务的库中进行获取需要重新查询,写接口。·
      $ajax({
        url: 'sys.auth.user.dic',             // 字典编码+'.dic'，必填
        params: {sliceType: 0},             // 照抄
        method: 'GET'                         // 照抄
      }).then(function (res) {
        debugger
        let code = res.code                   // 响应代码
        if (code === 200) {
          debugger
          let list = res.body.items
          for(let i in list){
              let usr = {userCode:list[i]["key"],userName:list[i]["text"]};
             me.data.data.push(usr);
          }
        }
      });

    }
})
$class('com.bsoft.assistant.clientregister.registerManage', {
  extend: '.ssdev.ux.component.table.Table',
  tpl: true,
  mixins: "/ssdev.utils.ServiceSupport",
  css: 'com.bsoft.assistant.clientregister.registerManage',
  deps: [".ssdev.ux.modal.Modal"],

  afterVueConfInited: function (vueConf) {
    var me = this, data = me.conf;
    var reportSuffix = ".bsoft_rpt";
    var pageSuffix = ".bsoft_page_rpt";
    me.setupService([{
      beanName: "assistant.ssoAssistantClientTegisterService",
      method: ["selectSsoAssistantClientTegister", "insertSsoAssistantClientTegister",
        "deleteSsoAssistantClientTegister","updateSsoAssistantClientTegister"]
    }]);


    var data = {
      insertOrUpName: '',
      total: 5,
      tableData: [
       /* {serverCode:'123456',serverName:'服务一',serverUrl:'http://baidu.com',code:'123',tipsType:'1',serverUrlBak:'http',serverDiscreption:'456',insertTime:'2023-09-02 14:01:59',insertUser:'王菲'},
        {serverCode:'123457',serverName:'服务二',serverUrl:'http://tenxun.com',code:'1234',tipsType:'2',serverUrlBak:'http',serverDiscreption:'456',insertTime:'2023-09-02 14:01:59',insertUser:'王刚'},
        {serverCode:'123458',serverName:'服务三',serverUrl:'http://kuaishou.com',code:'1235',tipsType:'3',serverUrlBak:'http',serverDiscreption:'456',insertTime:'2023-09-02 14:01:59',insertUser:'王其'},
     */ ],
      currentRow: null,
      orgdic: [],
      tableConfig: {
        pageNo: 1,
        pageSize: 20,
        withoutPage: false,
        pageSizes: [10, 20, 30, 40, 50, 100],
      },
      form: {serverName: '', start: 1, limit: 20}
    }
    $apply(vueConf.data, data);


    var pevtHandlers = {

      //改变每一页的个数是触发的操作
      handleSizeChange(val) {
        console.log(`每页 ${val} 条`);
        this.tableConfig.pageSize = val;
        this.changParamLoadDatas();
      },

      //分页的上下页翻页操作 以及去那一页的操作
      handleCurrentChange(val) {
        console.log(`当前页: ${val}`);
        this.tableConfig.pageNo = val;
        this.changParamLoadDatas();
      },

      findBtnClick: function () {        //查询数据
        this.changParamLoadDatas();
      },
      createBtnClick: function () {        //查询数据
        let parent = this;
        console.log(parent.pageForm)
        let panel = new ssdev.ux.modal.Modal({
          title:"新建服务",
          //modal: false,          // 是否显示遮罩层, 默认modal:true;
          //renderTo: me.el,       // renderTo 表示在指定的节点区域内展示弹出窗体; 默认展示在document.body
          width:800,
          height:500
        });
        let cls = 'com.bsoft.assistant.clientregister.pageForm.pageForm';
        panel.setModule(cls, {'sub':panel,'opt':'save','parent':this,'type':'2'}).then(function(mod){
          panel.show();
        });
      },
      deleteBtnClick: function () {    //删除
        debugger
        let that = this;
        var item = me.data.currentRow;
        if (item == null) {
          me.vue.$message({
            type: 'warning',
            message: '请选择一条数据!'
          });
          return;
        }
        me.vue.$confirm('将删除服务[' + item.serverName + '], 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          me.service.deleteSsoAssistantClientTegister({id:item.id, deleteUser:me.data.insertOrUpName}).success(function () {
            me.vue.$message({
              type: 'success',
              message: '删除成功!'
            });
            that.changParamLoadDatas();
          }).fail(function () {
            me.vue.$message({
              type: 'error',
              message: '删除失败!'
            });
          })
        }).catch(() => {
          me.vue.$message({
            type: 'warning',
            message: '已取消删除'
          });
        });
      },
      updateBtnClick: function () {        //查询数据
        let parent = this;
        if(!parent.currentRow){
          me.vue.$message({
            message: '请选择一行列表内容',
            type: 'warning',
            duration: "1000"
          });
          return ;
        }
        let panel = new ssdev.ux.modal.Modal({
          title:"修改服务信息",
          //modal: false,          // 是否显示遮罩层, 默认modal:true;
          //renderTo: me.el,       // renderTo 表示在指定的节点区域内展示弹出窗体; 默认展示在document.body
          width:1000,
          height:500
        });
        let cls = 'com.bsoft.assistant.clientregister.pageForm.pageForm';

        /* setModule方法中的参数说明：
         包含两个参数：
         参数一：1、为string类型，如：'ssdev.ux.person.PersonList'
         2、为object类型，属性可以为cls也可以为url，如：{cls: 'ssdev.ux.person.PersonList'}
         参数二：为额外参数，类型为object，传递到cls指定的模块中，在模块中可以使用me.conf.xxx获取，如:{arg: 'hi'}，模块中可以使用me.conf.arg获取该属性的值
         */
        panel.setModule(cls, {'sub':panel,'opt':'upd','rowData':parent.currentRow,'parent':this}).then(function(mod){
          //mod为ssdev.demo.base.ux.person.PersonList模块对象
          panel.show();
        });
      },
      editorBtnClick: function () {
        let parent = this;
        if(!parent.currentRow){
          me.vue.$message({
            message: '请选择一行列表内容',
            type: 'warning',
            duration: "1000"
          });
          return ;
        }
        let panel = new ssdev.ux.modal.Modal({
          title:"编辑服务用户信息",
          //modal: false,          // 是否显示遮罩层, 默认modal:true;
          //renderTo: me.el,       // renderTo 表示在指定的节点区域内展示弹出窗体; 默认展示在document.body
          width:800,
          height:450
        });
        let cls = 'com.bsoft.assistant.clientregister.userform.userForm';

        /* setModule方法中的参数说明：
         包含两个参数：
         参数一：1、为string类型，如：'ssdev.ux.person.PersonList'
         2、为object类型，属性可以为cls也可以为url，如：{cls: 'ssdev.ux.person.PersonList'}
         参数二：为额外参数，类型为object，传递到cls指定的模块中，在模块中可以使用me.conf.xxx获取，如:{arg: 'hi'}，模块中可以使用me.conf.arg获取该属性的值
         */
        panel.setModule(cls, {'sub':panel,'opt':'upd','rowData':parent.currentRow,'parent':this}).then(function(mod){
          //mod为ssdev.demo.base.ux.person.PersonList模块对象
          panel.show();
        });
      },
      handlerCurrentChange: function (currentRow, oldCurrentRow) {  //单选
        me.data.currentRow = currentRow;
      },
      filterData: function () {
        me.data.tableConfig.pageNo = 1;
        this.changParamLoadDatas();
      },

      changParamLoadDatas: function () {
        debugger
        var form = this.form;
        let params = {pageNo:me.data.tableConfig.pageNo,pageSize:me.data.tableConfig.pageSize,serverName:form.serverName == ""? '':form.serverName}
        me.service.selectSsoAssistantClientTegister(params).then(function (data) {
          me.data.tableData = data;
        }).fail(function () {
          me.vue.$message({
            type: 'error',
            message: '获取服务列表失败!'
          });
        })
      },
    }
    $apply(vueConf.methods, pevtHandlers);

  },

  afterInitComponent: function () {
    var me = this;
    //me.loadDicData();
    let token = $env.globalContext.get('urt')
    let date = new Date();
    let insertOrUpName = '';
    if(token){
      me.data.insertOrUpName = token.userName;
    }
    me.loadTableDatas();
    //me.data.total = me.data.tableData.length;
  },

  //根据条件查询所有的报表
  loadTableDatas: function () {
    debugger
    let me = this;
    let form = this.data.form;
    let params = {pageNo:this.data.tableConfig.pageNo,pageSize:this.data.tableConfig.pageSize,serverName:form.serverName}
    me.service.selectSsoAssistantClientTegister(params).then(function (data) {
      me.data.tableData = data.list;
      me.data.total = data.total;
    }).fail(function () {
      me.vue.$message({
        type: 'error',
        message: '获取服务列表失败!'
      });
    })
  },


  loadDicData: function () {
    var me = this;
    me.data.orgdic = $env.global_constants.orgOptions(),//获取机构列表信息,
    me.data.statusdic = [{key: '0', text: '未建'}, {key: '1', text: '已建'}, {key: '3', text: '已发布'}];
    me.data.typedic = [{key: '3', text: '报表'}, {key: '2', text: '仪表板'}, {key: '1', text: '移动端'}];
  },
})
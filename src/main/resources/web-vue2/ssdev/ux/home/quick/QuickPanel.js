$class('ssdev.ux.home.quick.QuickPanel', {
  extend: '/ssdev.ux.vue.VueContainer',
  mixins: '/ssdev.utils.ServiceSupport',
  css: ['.ssdev.ux.home.quick.css.quick'],
  tpl: '.',
  winConf: {
    title: '首页',
    autoShow: false
  },
  mods: [],
  initComponent: function (conf) {
    var me = this
    me.data = {
      defaultProps: {
        children: 'items',
        label: 'name'
      },
      newModuleList: [],
      serviceError: false,
      indexCancelBtn: false,
      indexEditBtn: false,
      indexSaveBtn: false,
      previousLocation: -1,
      editPanel: {
        visibility: false,
        nameList: false,
        filterText: '',
        moduleDisabled: false,
        tree: true,
        treeNum: -1,
        isEditIn: false,
        shape: 0,
        location: {},
        colorIndex: 0,
        currentColorIndex: -1,
        currentColor: '#26c6da',
        bgColorValues: ['#26c6da', '#1e88e5', '#7460ee', '#fc4b6c', '#ffb22b', '#626260', '#bfbfc0'],
        icon: 'fa fa-user',
        chosenIconIndex: -1,
        iconValues: ['fa fa-user', ' fa fa-bars', 'fa fa-bell', 'fa fa-book', 'fa fa-bullhorn', 'fa fa-th-large',
          'fa fa-cube', 'fa fa-database', 'fa fa-diamond', 'fa fa-envelope', 'fa fa-deaf', 'fa fa-cloud',
          'fa fa-film', 'fa fa-undo', 'fa fa-compass', 'fa fa-table', 'fa fa-repeat', 'fa fa-paste', 'fa fa-bicycle', 'fa fa-ship', 'fa fa-train'],
      },
      moduleInfo: {},
      mods: true,
      modsData: [],
      isCollapse: false,
      apps: [{ items: [] }],
      appId: '',
      moduleArrs: [],
      props: { label: 'name' },
      gridLiWidth: 0,
      gridLiHeight: 80,
      gridLiSpace: 20,
      gridLiIndex: 0,
      gridLiNum: 24,
      gridLis: '',
      gridDivs: '',
      gridLiPlus: '',
      gridDivPlus: '',
      gridDivEdits: '',
      isEdit: false,
      delPanelVisibility: false,
    }
    var evtHandlers = {
      filterNode: function (value, data) {
        if (!value) return true
        return (data.py && data.py.indexOf(value) !== -1) || (data.name && data.name.indexOf(value) !== -1) || (data.title && data.title.indexOf(value) !== -1)
      },
      hideModuleTree: function () {
        if (me.data.editPanel.treeNum != 1) {
          me.data.editPanel.tree = false
        }
        me.data.editPanel.treeNum = -1

      },
      showModuleTree: function (event) {
        me.data.editPanel.treeNum = 1
        me.data.editPanel.tree = true
      },
      dragEditPanel: function (ev) {
        var odiv = document.querySelector('.edit-panel')
        var disX = ev.clientX - odiv.offsetLeft
        var disY = ev.clientY - odiv.offsetTop
        document.onmousemove = function (ev2) {
          var ev2 = ev2 || event
          var left = ev2.clientX - disX
          var top = ev2.clientY - disY
          var w = document.documentElement.clientWidth || document.body.clientWidth
          var h = document.documentElement.clientHeight || document.body.clientHeight
          if (left > w - odiv.offsetWidth) {
            left = w - odiv.offsetWidth
          }
          if (left < 0) {
            left = 0
          }
          if (top < 0) {
            top = 0
          }
          if (top > h - odiv.offsetHeight) {
            top = h - odiv.offsetHeight
          }
          odiv.style.left = left + 'px'
          odiv.style.top = top + 'px'
          odiv.style.margin = 0

        }
        document.onmouseup = function (ev2) {
          document.onmousemove = null
        }
      },
      addModule: function (index, event) {
        document.querySelector('.shape-two').style.display = 'block'
        document.querySelector('.shape-three').style.display = 'block'
        me.data.editPanel.visibility = true
        me.data.isEditIn = false
        me.data.editPanel.moduleDisabled = false
        var lis = document.querySelectorAll('.chose-shape li')
        lis.forEach(function (val) {
          val.classList.remove('active')
        })
        var lis = document.querySelectorAll('.chose-shape li')
        if (event.target.getAttribute('class') == 'plus') {
          me.data.isEdit = true
          me.data.gridLiIndex = index
          me.data.editPanel.tree = true
          me.data.editPanel.visibility = true
          me.data.editPanel.filterText = ''
          me.data.editPanel.icon = 'fa fa-user'
          me.data.editPanel.shape = 0
          document.querySelector('.shape-one').classList.add('active')
          me.data.editPanel.chosenIconIndex = 0
          me.data.editPanel.currentColorIndex = 0
          if ((me.data.gridLiIndex + 1) % 6 == 0 || me.data.gridDivs[me.data.gridLiIndex + 1].classList.contains('edit')) {
            document.querySelector('.shape-three').style.display = 'none'
          }
          if (me.data.gridLiIndex >= 18 || me.data.gridDivs[me.data.gridLiIndex + 6].classList.contains('edit')) {
            document.querySelector('.shape-two').style.display = 'none'
          }
          var editArr = document.querySelectorAll('.grid-content .grid-item.edit'), locations = []
          editArr.forEach(function (item) {
            locations.push(JSON.parse(item.getAttribute('location')))
          })
          var x1 = parseInt(me.data.gridLiIndex % 6),
            y1 = parseInt(me.data.gridLiIndex / 6), x2x1, y2y1, x2x2, y2y2
          x2x1 = x1
          y2y1 = y1 + 1
          x2x2 = x1 + 1
          y2y2 = y1
          locations.forEach(function (item) {
            if (item.x2 == x2x1 && item.y2 == y2y1) {
              document.querySelector('.shape-two').style.display = 'none'
            }
            if (item.x2 == x2x2 && item.y2 == y2y2) {
              document.querySelector('.shape-three').style.display = 'none'
            }
          })
        }
      },
      closeEdit: function () {
        me.data.editPanel.visibility = false
        document.querySelector('.sub-portal .main-container').removeAttribute('style')
      },
      jumpModule: function (index) {
        if (!me.data.isEdit) {
          var mid = me.data.gridDivs[index].getAttribute('mid')
          var appId = me.data.gridDivs[index].getAttribute('appid')
          me.$subPortal.onOpen(appId, mid)
        }
      },
      moduleEnter: function (index, event) {
        if(!me.data.indexEditBtn){
          event.target.querySelector('.edit-btn').style.display = 'block'
          event.target.querySelector('.del-btn').style.display = 'block'
        }

      },
      moduleLeave: function (index, event) {
        if(!me.data.indexEditBtn){
          event.target.querySelector('.edit-btn').style.display = 'none'
          event.target.querySelector('.del-btn').style.display = 'none'
        }
      },
      nodeClick: function (node) {
        me.data.moduleInfo = node
        me.data.editPanel.filterText = ""
        var nodeSame = false
        me.mods.forEach(function (val) {
          if (val.moduleId == node.id) {
            me.vue.$message.error('您已选择该模块')
            nodeSame = true
          }
        })
        if (!nodeSame) {
          me.choseModuleConfirm()
        }
      },
      showModuleList: function () {
        me.data.editPanel.nameList = true
      },
      choseColor: function (index) {
        me.data.editPanel.currentColorIndex = index
        me.data.editPanel.currentColor = me.data.editPanel.bgColorValues[index]
      },
      choseShape: function (item, event) {
        me.data.editPanel.shape = item
        var lis = document.querySelectorAll('.chose-shape li')
        lis.forEach(function (val) {
          val.classList.remove('active')
        })
        event.target.classList.add('active')

      },
      choseIcon: function (index) {
        me.data.editPanel.chosenIconIndex = index
        me.data.editPanel.icon = me.data.editPanel.iconValues[index]
      },
      editSave: function () {
        var bgShapeWidth, bgShapeHeight, x1, y1, x2, y2
        x1 = parseInt(me.data.gridLiIndex % 6)
        y1 = parseInt(me.data.gridLiIndex / 6)
        if (me.data.editPanel.shape == 0) {
          x2 = x1
          y2 = y1
          bgShapeWidth = me.data.gridLiWidth
          bgShapeHeight = me.data.gridLiHeight
        } else if (me.data.editPanel.shape == 1) {
          x2 = x1
          y2 = y1 + 1
          bgShapeWidth = me.data.gridLiWidth
          bgShapeHeight = me.data.gridLiHeight * 2 + me.data.gridLiSpace
        } else if (me.data.editPanel.shape == 2) {
          x2 = x1 + 1
          y2 = y1
          bgShapeWidth = me.data.gridLiWidth * 2 + me.data.gridLiSpace
          bgShapeHeight = me.data.gridLiHeight
        } else {
          var location = me.data.gridDivs[me.data.gridLiIndex].getAttribute('location')
          var x = parseInt(location.x2) - parseInt(location.x1)
          var y = parseInt(location.y2) - parseInt(location.y1)
          bgShapeWidth = me.data.gridLiWidth * (x + 1) + me.data.gridLiSpace * x
          bgShapeHeight = me.data.gridLiHeight * (y + 1) + me.data.gridLiSpace * y
        }
        me.data.editPanel.location = JSON.stringify({ x1: x1, y1: y1, x2: x2, y2: y2 })
        var module = me.data.editPanel
        var formData = {
          moduleIndex: me.data.gridLiIndex,
          na: module.filterText,
          moduleIcon: module.icon,
          bgColor: module.currentColor,
          location: module.location
        }

        if (me.data.isEditIn == true) {
          var node = me.data.gridDivs[me.data.gridLiIndex]
          node.style.width = bgShapeWidth + 'px'
          node.style.height = bgShapeHeight + 'px'
          node.style.backgroundColor = me.data.editPanel.currentColor
          node.children[0].children[0].setAttribute('class', me.data.editPanel.icon)
          node.children[0].children[1].innerHTML = me.data.editPanel.filterText
          formData.moduleId = node.getAttribute('moduleid')
          formData.cd = node.getAttribute('mid')
          formData.id = node.getAttribute('id')
          formData.appId = node.getAttribute('appid')
          if (!me.data.serviceError) {
            if(node.getAttribute("save") != "noSave"){
              me.service.updateQuickModule(formData).then(function (data) {
                me.vue.$message.success('修改模块成功!')
              })
            }else{
              me.data.newModuleList.forEach(function(val,index){
                if(val.moduleIndex == me.data.gridLiIndex){
                  delete formData.id;
                  me.data.newModuleList[index] = formData
                }
              })
            }

          } else {
            me.vue.$message.success('修改模块成功!')
          }
        } else {
          if (!me.data.moduleInfo.name || (me.data.moduleInfo.items && me.data.moduleInfo.items.length != 0)) {
            me.vue.$message.error('您还未选择模块')
            return false
          }
          if(me.data.editPanel.filterText == ""){
            me.vue.$message.error('模块名不能为空')
            return false
          }
          me.data.moduleArrs.forEach(function (val, index) {
            if (val.name == me.data.moduleInfo.name) {
              me.data.appId = val.parentCd
            }
          })
          formData.cd = me.data.moduleInfo.cd
          formData.moduleId = me.data.moduleInfo.id
          formData.appId = me.data.appId
          function saveQuickModule (data) {
            me.vue.$nextTick(function () {
              me.loadAppendDiv(me.data.gridLiIndex, bgShapeWidth, bgShapeHeight, data)
              me.data.gridDivs[me.data.gridLiIndex].setAttribute('save', 'noSave')
              if (me.data.serviceError) {
                me.vue.$message.success('当前数据未进入数据库,仅缓存在本地!')
              }
              me.data.editPanel.visibility = false
              me.mods.push(formData)
              me.data.indexCancelBtn = false
              me.data.indexSaveBtn = true
              me.data.isEdit = true
            })
          }
          document.querySelector('.sub-portal .main-container').removeAttribute('style')
          me.data.newModuleList.push(formData)
          saveQuickModule(formData)
        }
        me.data.editPanel.visibility = false
        me.data.moduleInfo = {}
        setTimeout(function(){
          me.data.gridDivEdits = document.querySelectorAll('.grid-content .grid-item.edit')
        },200)
      },
      cancelQuickMethod: function () {
        me.data.indexEditBtn = true
        me.data.indexSaveBtn = false
        me.data.indexCancelBtn = false
        if (me.mods.length == 0) {
          me.data.mods = false
        }
      },
      createFastMethod: function (name) {
        if (name == 'new') {
          me.data.indexCancelBtn = true
          me.data.indexSaveBtn = false
        } else {
          me.data.indexCancelBtn = false
          me.data.indexSaveBtn = true
        }
        me.data.mods = true
        me.data.indexEditBtn = false
        me.data.isEdit = true
        for (var i = 0; i < me.data.gridLis.length; i++) {
          me.data.gridLis[i].style.visibility = 'visible'
        }
      },
      createFastSave: function () {
        me.data.indexEditBtn = true
        me.data.indexSaveBtn = false
        me.data.indexCancelBtn = false
        me.data.isEdit = false
        for (var i = 0; i < me.data.gridLis.length; i++) {
          me.data.gridLis[i].style.visibility = 'hidden'
        }
        if (!me.data.serviceError) {
          me.data.newModuleList.forEach(function(val){
            me.data.gridDivs[val.moduleIndex].removeAttribute("save")
          })
          me.service.saveBatchQuickModule(me.data.newModuleList).then(function (data) {

            me.vue.$message.success('保存模块成功!')

          })

        }
        if (me.mods.length == 0) {
          me.data.mods = false
        }
        me.data.newModuleList = []
      },
      editModule: function (event) {
        me.data.isEdit = true
        me.data.isEditIn = true
        me.data.indexSaveBtn = true
        me.data.editPanel.tree = false
        me.data.editPanel.moduleDisabled = true
        for (var i = 0; i < me.data.gridLis.length; i++) {
          me.data.gridLis[i].style.visibility = 'visible'
        }
        function colorRGBtoHex (color) {
          var rgb = color.split(',')
          var r = parseInt(rgb[0].split('(')[1])
          var g = parseInt(rgb[1])
          var b = parseInt(rgb[2].split(')')[0])
          var hex = '#' + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1)
          return hex
        }
        var lis = document.querySelectorAll('.chose-shape li')
        lis.forEach(function (val) {
          val.classList.remove('active')
        })
        var node = event.target.parentNode
        var location = JSON.parse(node.getAttribute('location'))
        if ((parseInt(location.y2) - parseInt(location.y1) == 0 && (parseInt(location.x2) - parseInt(location.x1) == 0))) {
          me.data.editPanel.shape = 0
          document.querySelector('.shape-one').classList.add('active')
        } else if ((parseInt(location.y2) - parseInt(location.y1) == 1 && (parseInt(location.x2) - parseInt(location.x1) == 0))) {
          me.data.editPanel.shape = 1
          document.querySelector('.shape-two').classList.add('active')
        } else if ((parseInt(location.y2) - parseInt(location.y1) == 0 && (parseInt(location.x2) - parseInt(location.x1) == 1))) {
          me.data.editPanel.shape = 2
          document.querySelector('.shape-three').classList.add('active')
        } else {
          me.data.editPanel.shape = -1
        }
        me.data.gridLiIndex = parseInt(node.getAttribute('index'))
        me.data.editPanel.icon = node.children[0].children[0].getAttribute('class')
        me.data.editPanel.currentColor = colorRGBtoHex(node.style.backgroundColor)
        me.data.editPanel.visibility = true
        me.data.editPanel.filterText = node.children[0].children[1].innerHTML
        for (var i = 0; i < me.data.editPanel.bgColorValues.length; i++) {
          if (me.data.editPanel.bgColorValues[i] == me.data.editPanel.currentColor) {
            me.data.editPanel.currentColorIndex = i
          }
        }

        for (var i = 0; i < me.data.editPanel.iconValues.length; i++) {
          if (me.data.editPanel.iconValues[i] == me.data.editPanel.icon) {
            me.data.editPanel.chosenIconIndex = i
          }
        }
        var editArr = document.querySelectorAll('.grid-content .grid-item.edit'), locations = []
        editArr.forEach(function (item, index) {
          locations.push(JSON.parse(item.getAttribute('location')))
        })
        var x1 = parseInt(me.data.gridLiIndex % 6),
          y1 = parseInt(me.data.gridLiIndex / 6), x2x1, y2y1, x2x2, y2y2
        x2x1 = x1
        y2y1 = y1 + 1
        x2x2 = x1 + 1
        y2y2 = y1
        document.querySelector('.shape-two').style.display = 'block'
        document.querySelector('.shape-three').style.display = 'block'
        if (((me.data.gridLiIndex + 1) % 6 == 0) || me.data.gridDivs[me.data.gridLiIndex + 1].classList.contains('edit')) {
          document.querySelector('.shape-three').style.display = 'none'
        }
        if (me.data.gridLiIndex >= 18 || me.data.gridDivs[me.data.gridLiIndex + 6].classList.contains('edit')) {
          document.querySelector('.shape-two').style.display = 'none'
        }
        locations.forEach(function (item) {
          if ((item.x1 == x2x1 && item.y1 == y2y1) || (item.x2 == x2x1 && item.y2 == y2y1)) {
            document.querySelector('.shape-two').style.display = 'none'
            if ((location.x2 == x2x1 && location.y2 == y2y1)) {
              document.querySelector('.shape-two').style.display = 'block'
            }
          }
          if ((item.x1 == x2x2 && item.y1 == y2y2) || (item.x2 == x2x2 && item.y2 == y2y2)) {
            document.querySelector('.shape-three').style.display = 'none'
            if (location.x2 == x2x2 && location.y2 == y2y2) {
              document.querySelector('.shape-three').style.display = 'block'
            }
          }
        })

      },
      delModule: function () {
        me.data.gridLiIndex = parseInt(event.target.parentNode.getAttribute('index'))
        me.data.delPanelVisibility = true
      },
      northResize: function (key) {
        var location = JSON.parse(me.data.gridDivs[key].getAttribute('location'))
        x11 = parseInt(location.x1)
        y11 = parseInt(location.y1)
        x22 = parseInt(location.x2)
        y22 = parseInt(location.y2)
        me.data.previousLocation = 6 * y11 + x11

        var filterMods = []
        var filterYArray = []
        var mods = [].concat(JSON.parse(JSON.stringify(me.mods)))
        mods.forEach(function (val, index) {
          if (val.location != '') {
            if (location.x2 >= JSON.parse(val.location).x1 &&
              location.x1 <= JSON.parse(val.location).x2 && key > val.moduleIndex) {
              filterMods.push(mods[index])
            }
          }
        })
        filterMods.forEach(function (val, index) {
          filterYArray.push(JSON.parse(val.location).y2)
        })
        var maxY = filterYArray.sort(function (a, b) {
          return b - a
        })[0]

        var docY, liY, subY, lastHeight, isMouseDown, liHeight
        if (me.data.isEdit) {
          isMouseDown = true
          liHeight = me.data.gridDivs[key].offsetHeight
          liY = event.pageY
          liTop = parseInt(me.data.gridDivs[key].style.top)
          document.onmousemove = function (event) {
            docY = event.pageY
            var clientY = me.data.gridLis[0].getBoundingClientRect().y
            if (docY < clientY) {
              docY = clientY
            }
            subY = liY - docY
            lastHeight = liHeight + subY
            if (subY <= 0 && lastHeight < me.data.gridLiHeight) {
              lastHeight = me.data.gridLiHeight
              return false
            }
            if (isMouseDown) {
              me.data.gridDivs[key].style.height = lastHeight + 'px'
              me.data.gridDivs[key].style.top = liTop - subY + 'px'
            }
          }

          document.onmouseup = function (e) {
            if (isMouseDown) {
              var num = Math.round(lastHeight / me.data.gridLiHeight)
              var lastY1
              if (filterYArray.length != 0) {
                if (location.y2 - num + 1 <= maxY) {
                  lastY1 = maxY + 1
                } else {
                  lastY1 = location.y2 - num + 1
                }
              } else {
                lastY1 = location.y2 - num + 1
              }
              me.data.gridDivs[key].style.height = (location.y2 - lastY1 + 1) * me.data.gridLiHeight
                + (location.y2 - lastY1) * me.data.gridLiSpace + 'px'
              var moduleIndex = 6 * lastY1 + location.x1
              me.data.gridDivs[key].style.top = me.data.gridLis[moduleIndex].offsetTop - 10 + 'px'
              location = JSON.stringify({ 'x1': location.x1, 'y1': lastY1, 'x2': location.x2, 'y2': location.y2 })
              me.data.gridDivs[key].setAttribute('location', location)
              me.loadLiResize(key)
            }
            isMouseDown = false
          }
        }
      },
      eastResize: function (key) {
        var location = JSON.parse(me.data.gridDivs[key].getAttribute('location'))
        x11 = parseInt(location.x1)
        y11 = parseInt(location.y1)
        x22 = parseInt(location.x2)
        y22 = parseInt(location.y2)
        me.data.previousLocation = 6 * y11 + x11
        var filterMods = []
        var filterXArray = []
        var mods = [].concat(JSON.parse(JSON.stringify(me.mods)))
        mods.forEach(function (val, index) {
          if (val.location != '') {
            if ((JSON.parse(val.location).y2 >= location.y1 && JSON.parse(val.location).y1
              <= location.y2) && val.moduleIndex % 6 > key % 6 && val.moduleIndex != location.y1 * 6 + location.x1) {
              filterMods.push(mods[index])
            }
          }

        })
        filterMods.forEach(function (val, index) {
          filterXArray.push(JSON.parse(val.location).x1)
        })
        var minX = filterXArray.sort(function (a, b) {
          return a - b
        })[0]
        var docX, liX, subX, lastWidth, isMouseDown, liWidth
        if (me.data.isEdit) {
          isMouseDown = true
          liWidth = me.data.gridDivs[key].offsetWidth
          liX = event.pageX
          document.onmousemove = function (event) {
            docX = event.pageX
            if (docX > (document.body.clientWidth - 36)) {
              docX = document.body.clientWidth - 36
            }
            subX = docX - liX
            lastWidth = liWidth + subX
            if (lastWidth < me.data.gridLiWidth) {
              lastWidth = me.data.gridLiWidth
            }
            if (isMouseDown) {
              me.data.gridDivs[key].style.width = lastWidth + 'px'
            }
          }
          document.onmouseup = function (e) {
            if (isMouseDown) {
              var num = Math.round(lastWidth / me.data.gridLiWidth)
              var lastX2
              if (filterXArray.length != 0) {
                if (location.x1 + num - 1 >= minX) {
                  lastX2 = minX - 1
                } else {
                  lastX2 = location.x1 + num - 1
                }
              } else {
                lastX2 = location.x1 + num - 1
              }
              me.data.gridDivs[key].style.width = (lastX2 - location.x1 + 1) * me.data.gridLiWidth
                + (lastX2 - location.x1) * me.data.gridLiSpace + 'px'
              location = JSON.stringify({ 'x1': location.x1, 'y1': location.y1, 'x2': lastX2, 'y2': location.y2 })
              me.data.gridDivs[key].setAttribute('location', location)
              me.loadLiResize(key)
            }
            isMouseDown = false
          }
        }
      },
      southResize: function (key) {
        var location = JSON.parse(me.data.gridDivs[key].getAttribute('location'))
        x11 = parseInt(location.x1)
        y11 = parseInt(location.y1)
        x22 = parseInt(location.x2)
        y22 = parseInt(location.y2)
        me.data.previousLocation = 6 * y11 + x11
        var filterMods = []
        var filterYArray = []
        var mods = [].concat(JSON.parse(JSON.stringify(me.mods)))
        mods.forEach(function (val, index) {
          if (val.location != '') {
            if (!(JSON.parse(val.location).x2 < location.x1 || JSON.parse(val.location).x1
              > location.x2) && val.moduleIndex > key && val.moduleIndex != location.y1 * 6 + location.x1) {
              filterMods.push(mods[index])
            }
          }

        })
        filterMods.forEach(function (val, index) {
          filterYArray.push(JSON.parse(val.location).y1)
        })
        var minY = filterYArray.sort(function (a, b) {
          return a - b
        })[0]
        var docY, liY, subY, lastHeight, isMouseDown, liHeight
        if (me.data.isEdit) {
          isMouseDown = true
          liHeight = me.data.gridDivs[key].offsetHeight
          liY = event.pageY
          document.onmousemove = function (event) {
            docY = event.pageY
            var clientHeight = document.body.clientHeight
            if (docY > clientHeight - 36) {
              docY = clientHeight - 36
            }
            subY = docY - liY
            lastHeight = liHeight + subY
            if (lastHeight < me.data.gridLiHeight) {
              lastHeight = me.data.gridLiHeight
            }
            if (isMouseDown) {
              me.data.gridDivs[key].style.height = lastHeight + 'px'
            }
          }
          document.onmouseup = function (e) {
            if (isMouseDown) {
              var num = Math.round(lastHeight / me.data.gridLiHeight)
              var lastY2
              if (filterYArray.length != 0) {
                if (location.y1 + num - 1 >= minY) {
                  lastY2 = minY - 1
                } else {
                  lastY2 = location.y1 + num - 1
                }
              } else {
                lastY2 = location.y1 + num - 1
              }
              me.data.gridDivs[key].style.height = (lastY2 - location.y1 + 1) * me.data.gridLiHeight
                + (lastY2 - location.y1) * me.data.gridLiSpace + 'px'
              location = JSON.stringify({ 'x1': location.x1, 'y1': location.y1, 'x2': location.x2, 'y2': lastY2 })
              me.data.gridDivs[key].setAttribute('location', location)
              me.loadLiResize(key)
            }
            isMouseDown = false
          }
        }
      },
      westResize: function (key) {
        var location = JSON.parse(me.data.gridDivs[key].getAttribute('location'))
        x11 = parseInt(location.x1)
        y11 = parseInt(location.y1)
        x22 = parseInt(location.x2)
        y22 = parseInt(location.y2)
        me.data.previousLocation = 6 * y11 + x11
        var filterMods = []
        var filterXArray = []
        var mods = [].concat(JSON.parse(JSON.stringify(me.mods)))
        mods.forEach(function (val, index) {
          if (val.location != '') {
            if ((JSON.parse(val.location).y2 >= location.y1 && JSON.parse(val.location).y1
              <= location.y2) && val.moduleIndex % 6 < key % 6 && val.moduleIndex != location.y1 * 6 + location.x1) {
              filterMods.push(mods[index])
            }
          }
        })
        filterMods.forEach(function (val, index) {
          filterXArray.push(JSON.parse(val.location).x2)
        })
        var maxX = filterXArray.sort(function (a, b) {
          return b - a
        })[0]
        var docX, liX, subX, lastWidth, isMouseDown, liWidth
        if (me.data.isEdit) {
          isMouseDown = true
          liWidth = me.data.gridDivs[key].offsetWidth
          liX = event.pageX
          liLeft = parseInt(me.data.gridDivs[key].style.left)
          document.onmousemove = function (event) {
            docX = event.pageX
            var clientX = me.data.gridLis[0].getBoundingClientRect().x
            if (docX < clientX) {
              docX = clientX
            }
            subX = liX - docX
            lastWidth = liWidth + subX
            if (subX <= 0 && lastWidth <= me.data.gridLiWidth) {
              lastWidth = me.data.gridLiWidth
              return false
            }
            if (isMouseDown) {
              me.data.gridDivs[key].style.width = lastWidth + 'px'
              me.data.gridDivs[key].style.left = liLeft - subX + 'px'
            }
          }
          document.onmouseup = function (e) {
            if (isMouseDown) {
              var num = Math.round(lastWidth / me.data.gridLiWidth)
              var lastX1
              if (filterXArray.length != 0) {
                if (location.x2 - num + 1 <= maxX) {
                  lastX1 = maxX + 1
                } else {
                  lastX1 = location.x2 - num + 1
                }
              } else {
                lastX1 = location.x2 - num + 1

              }
              me.data.gridDivs[key].style.width = (location.x2 - lastX1 + 1) * me.data.gridLiWidth
                + (location.x2 - lastX1) * me.data.gridLiSpace + 'px'
              var moduleIndex = 6 * location.y1 + lastX1
              me.data.gridDivs[key].style.left = me.data.gridLis[moduleIndex].offsetLeft + 'px'
              location = JSON.stringify({ 'x1': lastX1, 'y1': location.y1, 'x2': location.x2, 'y2': location.y2 })
              me.data.gridDivs[key].setAttribute('location', location)
              me.loadLiResize(key)
            }
            isMouseDown = false
          }
        }
      },
      createFastCancel: function () {
        me.data.indexEditBtn = false
        for (var i = 0; i < me.data.gridLiPlus.length; i++) {
          me.data.gridLiPlus[i].style.visibility = 'hidden'
        }
      },

      delCancel: function () {
        me.data.delPanelVisibility = false
      },
      delConfirm: function () {
        function removeByModuleIndex () {
          me.vue.$message.success('删除模块成功!')
          node.children[0].children[0].setAttribute('class', 'icon')
          node.children[0].children[1].innerHTML = ''
          node.classList.remove('edit')
          node.classList.add('plus')
          node.removeAttribute('id')
          node.removeAttribute('appid')
          node.removeAttribute('mid')
          node.removeAttribute('moduleid')
          node.removeAttribute('location')
          node.removeAttribute('style')
          me.mods.forEach(function (val, index) {
            if (val.moduleIndex == me.data.gridLiIndex) {
              me.mods.splice(index, 1)
              if (me.mods.length == 0) {
                me.data.mods = false
                me.data.indexSaveBtn = false
                me.data.indexCancelBtn = false
              }
            }
          })
        }

        me.data.delPanelVisibility = false
        var node = me.data.gridDivs[me.data.gridLiIndex]
        if (!me.data.serviceError) {
          me.service.removeByModuleIndex(me.data.gridLiIndex).then(function (data) {
            removeByModuleIndex()
          })
        } else {
          removeByModuleIndex()
        }
        setTimeout(function(){
          me.data.gridDivEdits = document.querySelectorAll('.grid-content .grid-item.edit')
        },200)
      }
    }
    me.evtHandlers = evtHandlers
    function getApps (apps, index, arr) {
      if (apps.items) {
        apps.items.forEach(function (val) {
          getApps(val, index, arr)
        })
      } else {
        apps.parentCd = arr[index].cd
        me.data.moduleArrs.push(apps)
      }
      return me.data.moduleArrs
    }

    me.on('addporal', function () {
      var apps = me.loadApps()
      apps.forEach(function (val, index, arr) {
        getApps(val, index, arr)
      })
      me.data.apps = apps
    })
    me.setupService([{
      beanName: 'bbp.quick',
      method: ['removeByModuleIndex', 'saveQuickModule', 'saveBatchQuickModule','updateQuickModule', 'loadModules']
    }])
    me.callParent(arguments)
  },
  afterVueConfInited: function (vueconf) {
    var me = this
    vueconf.watch = {
      'editPanel.filterText': function (val) {
        this.$refs.moduleTree.filter(val)
        me.data.editPanel.tree = true
        if (me.data.isEditIn) {
          me.data.editPanel.tree = false
        }
      }
    }
  },
  choseModuleConfirm: function () {
    var me = this
    if (me.data.moduleInfo.items) {
      return false
    }
    me.data.editPanel.filterText = me.data.moduleInfo.name
    setTimeout(function () {
      me.data.editPanel.tree = false
    }, 100)
  },
  loadApps: function () {
    const me = this, subPortal = me.$subPortal
    subPortal.on('collapse', function () {
      setTimeout(function(){
        me.quickResize()
      },200)
    })
    if (subPortal) {
      var topPortal = subPortal.getTopPortal()
      if (topPortal) {
        return topPortal.getApps()
      }
    }
  },
  quickResize: function (w, h) {
    const me = this
    var quickWidth = document.querySelector(".top-tabs").clientWidth - 12;
    me.data.gridLiWidth = (quickWidth - 7 * me.data.gridLiSpace) / 6
    for (var i = 0; i < me.data.gridLis.length; i++) {
      me.data.gridLis[i].style.width = me.data.gridLiWidth + 'px'
    }
    for (var i = 0; i < me.data.gridDivEdits.length; i++) {
      var location = JSON.parse(me.data.gridDivEdits[i].getAttribute('location'))
      var key = location.y1 * 6 + location.x1
      me.data.gridDivEdits[i].style.width = (location.x2 - location.x1 + 1) * me.data.gridLiWidth + (location.x2 - location.x1) * me.data.gridLiSpace + 'px'
      me.data.gridDivEdits[i].style.height = (location.y2 - location.y1 + 1) * me.data.gridLiHeight + (location.y2 - location.y1) * me.data.gridLiSpace + 'px'
      me.data.gridDivEdits[i].style.left = location.x1 * me.data.gridLiWidth + (location.x1 + 1) * me.data.gridLiSpace + 'px'
      me.data.gridDivEdits[i].style.top = location.y1 * me.data.gridLiHeight + location.y1 * me.data.gridLiSpace + 'px'
    }
  },
  loadLiResize: function (key) {
    const me = this
    var node = me.data.gridDivs[key]
    var location = JSON.parse(node.getAttribute('location'))
    var moduleIndex = location.y1 * 6 + location.x1
    var yNum = location.y2 - location.y1
    var formData = {
      moduleIndex: moduleIndex,
      moduleId: node.getAttribute('moduleid'),
      na: node.children[0].children[1].innerHTML,
      moduleIcon: node.children[0].children[0].getAttribute('class'),
      bgColor: node.style.backgroundColor,
      location: node.getAttribute('location'),
      id: node.getAttribute('id'),
      appId: node.getAttribute('appid'),
      cd: node.getAttribute('mid')
    }
    me.mods.forEach(function (val, index) {
      if (val.moduleIndex == me.data.previousLocation) {
        me.mods[index].moduleIndex = moduleIndex
        me.mods[index].location = node.getAttribute('location')
      }
    })
    if (!me.data.serviceError) {
      me.service.updateQuickModule(formData).then(function (data) {
      })
    }

  },
  loadAppendDiv: function (key, bgShapeWidth, bgShapeHeight, data) {
    const me = this
    if (bgShapeHeight < 80) {
      bgShapeHeight = 80
    }
    me.data.gridDivs[key].style.width = bgShapeWidth + 'px'
    me.data.gridDivs[key].style.height = bgShapeHeight + 'px'
    me.data.gridDivs[key].style.backgroundColor = data.bgColor
    me.data.gridDivs[key].setAttribute('id', data.id)
    me.data.gridDivs[key].setAttribute('appid', data.appId)
    me.data.gridDivs[key].setAttribute('mid', data.cd)
    me.data.gridDivs[key].setAttribute('moduleid', data.moduleId)
    me.data.gridDivs[key].children[0].children[0].setAttribute('class', data.moduleIcon)
    me.data.gridDivs[key].querySelector('.grid-item-content').innerHTML = data.na
    me.data.gridDivs[key].setAttribute('location', data.location)
    me.data.gridDivs[key].classList.remove('plus')
    me.data.gridDivs[key].classList.add('edit')
    me.data.gridDivs[key].style.left = me.data.gridLis[key].offsetLeft + 'px'
    me.data.gridDivs[key].style.top = me.data.gridLis[key].offsetTop - 10 + 'px'
  },
  afterAppend: function () {
    var me = this, el = me.el, bgShapeWidth, bgShapeHeight, x1, y1, x2, y2,
      quickWidth = el.clientWidth,
      quickHeight = el.clientHeight - 62
    me .data.gridLiWidth = (quickWidth - 7 * me.data.gridLiSpace ) / 6
    me.data.gridLiHeight = (quickHeight - 4 * me.data.gridLiSpace) / 4
    if (me.data.gridLiHeight < 80) {
      me.data.gridLiHeight = 80
    }

    me.data.gridLis = document.querySelectorAll('.quick .grid-stack li')
    me.data.gridDivs = document.querySelectorAll('.quick .grid-content .grid-item')
    me.data.gridLiPlus = document.querySelectorAll('.quick .grid-stack li.plus')
    for (var i = 0; i < me.data.gridLis.length; i++) {
      me.data.gridLis[i].style.width = me.data.gridLiWidth + 'px'
      me.data.gridLis[i].style.height = me.data.gridLiHeight + 'px'
      me.data.gridLis[i].style.lineHeight = me.data.gridLiHeight + 'px'
    }
    $event.on('resize', function (w, h) {
      me.quickResize(w, h)
    })
    for (var i = 0; i < me.data.gridLis.length; i++) {
      me.data.gridLis[i].style.visibility = 'hidden'
    }

    me.service.loadModules().then(function (data) {
      if (data.length == 0) {
        me.data.mods = false
      } else {
        me.data.indexEditBtn = true
      }
      me.data.modsData = me.mods
      for (var i = 0; i < data.length; i++) {
        me.mods = data
      }
      me.mods.forEach(function (val, index) {
        if (val.moduleIndex == undefined) {
          return false
        }
        if (val.location != '') {
          var location = JSON.parse(val.location)
          x1 = parseInt(location.x1)
          y1 = parseInt(location.y1)
          x2 = parseInt(location.x2)
          y2 = parseInt(location.y2)
          bgShapeWidth = me.data.gridLiWidth * (x2 - x1 + 1) + me.data.gridLiSpace * (x2 - x1)
          bgShapeHeight = me.data.gridLiHeight * (y2 - y1 + 1) + me.data.gridLiSpace * (y2 - y1)
          me.loadAppendDiv(val.moduleIndex, bgShapeWidth, bgShapeHeight, val)
        }
      })
      me.data.gridDivPlus = document.querySelectorAll('.grid-content .grid-item.plus')
      me.data.gridDivEdits = document.querySelectorAll('.grid-content .grid-item.edit')
    }).fail(function () {
      me.data.serviceError = true
      me.data.mods = false
    })

  }
})

$define("com.bsoft.assistant.common.component.panel.SplitPane",{
    extend: 'ssdev.ux.vue.VueContainer',
    tpl: true,
    css: "com.bsoft.assistant.common.component.panel.SplitPane"
}, function (html) {
    Vue.component("split-pane", {
        template: html,
        props: {
            //分割值 百分比
            value: {
                type: Number,
                default: 0.5,
            },
            //按钮宽度 像素px
            triggerWidth: {
                type: Number,
                default: 8,
            },
            // 最小分割值 百分比
            min: {
                type: Number,
                default: 0.1,
            },
            // 最大分割值 百分比
            max: {
                type: Number,
                default: 0.9,
            }
        },
        data: function() {
            return {
                splitOuter: "outer"+ Date.now(),
                canMove: false,
                initOffset: 0
            };
        },
        computed: {
            //计算左边面板的宽度
            leftOffsetPercent() {
                return `calc(${this.value * 100}% + ${this.triggerWidth}px)`;
            },
            //右边面板的marginleft
            triggerLeft() {
                return `${this.value * 100}% `;
            },
        },
        methods: {
            //鼠标点击  鼠标移动事件  计算偏差
            handleMousedown: function(event) {
                document.addEventListener("mousemove", this.handleMousemove);
                document.addEventListener("mouseup", this.handleMouseup);
                this.initOffset =
                    event.pageX - event.srcElement.getBoundingClientRect().left;
                this.canMove = true;
            },
            //鼠标移动事件 计算移动距离
            handleMousemove: function(event) {
                if (!this.canMove) return;
                const outerRect = this.$refs[this.splitOuter].getBoundingClientRect();
                let offsetPercent = (event.pageX - this.initOffset +
                    this.triggerWidth / 2 - outerRect.left) / outerRect.width;
                if (offsetPercent < this.min) offsetPercent = this.min;
                if (offsetPercent > this.max) offsetPercent = this.max;
                // this.$emit('input', offsetPercent)
                this.$emit("update:value", offsetPercent);
            },
            handleMouseup: function() {
                this.canMove = false;
            },
        }
    })
});

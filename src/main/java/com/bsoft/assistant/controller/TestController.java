package com.bsoft.assistant.controller;

import com.alibaba.fastjson.JSON;
import com.bsoft.assistant.common.websocket.FaceWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * Author: tuz
 * Date: 2023/09/02
 */
@Controller
public class TestController {

    @Autowired
    private  SimpMessagingTemplate messagingTemplate;

    //发送到具体个人订阅消息-测试档案调阅功能
    @RequestMapping(value = "/sendDoc/{destUsername}")
    @ResponseBody
    public String sendUserMsg(@PathVariable String destUsername){
        messagingTemplate.convertAndSendToUser(destUsername, "/doc", JSON.toJSON(testDoc()));
        return "send to " + destUsername + " 1 message" ;
    }

    private Map<String,Object> testDoc(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("msgType",1);
        int i = (int) (Math.random() * 100) % 3;
        switch (i){
            case 0 :
//                map.put("message","创业慧康医院工作量总报表");
//                map.put("docUrl","http://127.0.0.1:9002/bip/?clz=com.bsoft.bip.report.view.ReportViewPrint&" +
//                        "idReport=01003200017169449861570067501056&" +
//                        "uid=com001&pwd=123456&udId=64af555692488d22e4cf6293&bi");  //测试调阅BI报表展示
                map.put("message","创业企业门户");
                map.put("docUrl","https://web.bsoft.com.cn:8443/#/");  //测试调阅BI报表展示
                break;
            case 1:
                map.put("message","electron官网");
                map.put("docUrl","https://www.electronjs.org/");  //测试调阅BI报表展示
                break;
            case 2:
                map.put("message","百度electron");
                map.put("docUrl","https://www.baidu.com/s?wd=electron");  //测试调阅BI报表展示
            default:         map.put("message","创业慧康医院工作量总报表");
                map.put("docUrl","http://127.0.0.1:9002/bip/?clz=com.bsoft.bip.report.view.ReportViewPrint&" +
                        "idReport=01003200017169449861570067501056&" +
                        "uid=com001&pwd=123456&udId=64af555692488d22e4cf6293&bi");  //测试调阅BI报表展示
        }

        return map;
    }

    //发送到具体个人订阅消息-测试提醒及次数功能
    @RequestMapping(value = "/sendTime/{destUsername}")
    @ResponseBody
    public String sendMsg(@PathVariable String destUsername){
        messagingTemplate.convertAndSendToUser(destUsername, "/times", JSON.toJSON(testMsgTip()));
        return "send to " + destUsername + " 1 message" ;
    }

    private Map<String,Object> testMsgTip(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("msgType",2);
        int i = (int) (Math.random() * 100) % 3;
        switch (i){
            case 0 :
                map.put("message","你的上班打卡时间马上结束，如果还没有打卡，请立即前往公司考勤app打卡.");
                break;
            case 1:
                map.put("message","为贯彻落实《国务院办公厅关于推动公立医院高质量发展的意见》" +
                        "（国办发〔2021〕18号）要求，巩固“进一步改善医疗服务行动计划”成果，充分发挥公立医院在保障和改善民生中的重要作用，" +
                        "国家卫生健康委和国家中医药管理局制定了《公立医院高质量发展促进行动（2021-2025年）》，现印发给你们，请认真贯彻落实。");
                break;
            case 2:
                map.put("message","资格复审。面试前安排资格复审，应聘人员需按指定时间、地点参加资格复审。" +
                        "应届毕业生须提供已有各层次学历学位证书、就业协议书、就业推荐表、身份证、英语等级证明、" +
                        "在校学习成绩单、就读学校政审意见、其他证明符合报考条件（如职称或职业资格证、政治面貌等）" +
                        "的证明材料的原件及复印件等；已有工作经历者须提供已有各层次学历学位证书、身份证、英语等级证明、" +
                        "其他证明符合报考条件（如职称或职业资格证、政治面貌、工作经历等）的证明材料的原件及复印件等；" +
                        "国（境）外留学人员还应提供教育部留学服务中心出具的境外学历学位认证书等相关证明材料。");
                break;
            default: map.put("message","建设高水平公立医院网络。加快优质医疗资源扩容和区域均衡布局，" +
                    "在“十四五”时期围绕重大疾病、医学前沿、平台专科推进国家医学中心（含国家中医医学中心）、" +
                    "国家区域医疗中心（含国家区域中医医疗中心）、省级区域医疗中心（含省级区域中医医疗中心）" +
                    "建设设置和管理工作，新建一批国家医学中心、国家区域医疗中心、省级区域医疗中心。" +
                    "实施“千县工程”县医院能力建设项目，县级中医医院提标扩能项目，发挥公立医院在医疗联合体中的牵头引领作用。" +
                    "开展中医特色重点医院、中西医协同“旗舰”医院、国家中医疫病防治和紧急医学救援基地等项目建设，" +
                    "促进中医医院特色发展，发挥中西医协同引领作用。到2025年，形成国家级医学中心和国家级、省级区域医疗中心为骨干，" +
                    "高水平市级和县级医院为支点，紧密型城市医疗集团和县域医共体为载体的高水平公立医院网络，在疑难疾病、重大疾病、" +
                    "重大疫情的医疗救治、多中心研究、大数据集成、科研成果转化等方面发挥协同作用，带动城乡医疗服务体系实现高质量发展");
        }


        return map;
    }
    //发送一条广播主题  - 测试截留消息功能
    @RequestMapping(value = "/sendTopic")
    @ResponseBody
    public String sendTopicMsg(){
        messagingTemplate.convertAndSend("/topic/system", JSON.toJSON(testMsgBroad()));
        return  "send 1 broadcast message.";
    }

    private Map<String,Object> testMsgBroad(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("msgType",3);
        int i = (int) (Math.random() * 100) % 3;
        switch (i){
            case 0 :
                map.put("message","建设“三位一体”智慧医院。将信息化作为医院基本建设的优先领域，" +
                        "建设电子病历、智慧服务、智慧管理“三位一体”的智慧医院信息系统，完善智慧医院分级评估顶层设计。" +
                        "鼓励有条件的公立医院加快应用智能可穿戴设备、人工智能辅助诊断和治疗系统等智慧服务软硬件，" +
                        "提高医疗服务的智慧化、个性化水平，推进医院信息化建设标准化、规范化水平，落实国家和行业信息化标准。到2022年，" +
                        "全国二级和三级公立医院电子病历应用水平平均级别分别达到3级和4级，智慧服务平均级别力争达到2级和3级，" +
                        "智慧管理平均级别力争达到1级和2级，能够支撑线上线下一体化的医疗服务新模式。到2025年，" +
                        "建成一批发挥示范引领作用的智慧医院，线上线下一体化医疗服务模式形成，医疗服务区域均衡性进一步增强。");
                break;
            case 1:
                map.put("message","5月6日上午，医院高质量发展研讨会暨树兰医疗学术委员会会议在杭州召开。" +
                        "会议认真贯彻落实习近平新时代中国特色社会主义思想的精神及党的二十大重要部署，全面总结了集团学术与临床研究、" +
                        "科研创新、医院管理等方面工作，分析了面临的形势，提出了当前和今后一个时期集团在学术发展和创新突破等工作的总体要求、" +
                        "工作思路和重点工作。");
                break;
            case 2:
                map.put("message","作为目前国内规模最大、层次最高、最早开展视觉科学研究的机构之一，" +
                        "眼视光医院是全国唯一一家国家级创新平台“大满贯”医疗单位（国家眼部疾病临床医学研究中心、" +
                        "眼视光学和视觉科学国家重点实验室、国家眼视光工程技术研究中心、国家药监局眼科疾病医疗器械和药物临床研" +
                        "究与评价重点实验室、教育部近视防控与诊治工程研究中心等等），是国家强制性标准对数视力表发明单位。" +
                        "建有转化基地中国眼谷，是全国首家眼健康产业综合体。率先在全国开展“眼视光医院集团”的探索与建设，" +
                        "已在长三角、华南、西南等地区设立10余家集团分院和门诊部。");
                break;
            default: map.put("message","5月6日上午，医院高质量发展研讨会暨树兰医疗学术委员会会议在杭州召开。" +
                    "会议认真贯彻落实习近平新时代中国特色社会主义思想的精神及党的二十大重要部署，全面总结了集团学术与临床研究、" +
                    "科研创新、医院管理等方面工作，分析了面临的形势，提出了当前和今后一个时期集团在学术发展和创新突破等工作的总体要求、" +
                    "工作思路和重点工作。");
        }


        return map;
    }
}

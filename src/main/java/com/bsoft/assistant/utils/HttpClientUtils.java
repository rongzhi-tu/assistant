package com.bsoft.assistant.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;

/**
 * @Author Cl2
 * @Description: webService远程调用工具
 * @Date Created in 2023/2/13
 */
public class HttpClientUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpClientUtils.class);
    /**
     * get 请求
     * @return
     */
    public static String doHttpGet(String url){
        //1.获取httpclient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //接口返回结果
        CloseableHttpResponse response = null;
        String resMsg;
        try {
            //2.创建get请求
            HttpGet httpGet = new HttpGet(url);
            //3.设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
            httpGet.setConfig(requestConfig);
            /*此处可以添加一些请求头信息，例如：
            httpGet.addHeader("content-type","text/xml");*/
            //4.提交参数
            response = httpClient.execute(httpGet);
            //5.得到响应信息
            int statusCode = response.getStatusLine().getStatusCode();
            //6.判断响应信息是否正确
            if(HttpStatus.SC_OK != statusCode){
                //终止并抛出异常
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            //7.转换成实体类
            HttpEntity entity = response.getEntity();
            if(null != entity){
                resMsg = EntityUtils.toString(entity,"UTF-8");
            }else{
                resMsg = "null";//远程接口返回数据为null
            }
            EntityUtils.consume(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("远程调用失败：",e);
            resMsg = "远程调用失败：" + e.getMessage();
        } catch (ClientProtocolException e) {
            log.error("远程调用失败：",e);
            resMsg = "远程调用失败：" + e.getMessage();
        } catch (SocketTimeoutException | ConnectTimeoutException e) {
            log.error("远程调用失败：",e);
            resMsg = "远程调用失败：" + e.getMessage();
        }catch (IOException e) {
            log.error("远程调用失败：",e);
            resMsg = "远程调用失败：" + e.getMessage();
        }finally {
            //8.关闭所有资源连接
            if(null != response){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(null != httpClient){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resMsg;
    }

    /**
     * http post 请求
     */
    public static String doPost(String url, String params){
        String resMsg = null;
        //1. 获取httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            //2. 创建post请求
            HttpPost httpPost = new HttpPost(url);
            //3.设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
            httpPost.setConfig(requestConfig);
            //4.提交参数发送请求
            StringEntity stringEntity = new StringEntity(params);
            /*此处可以设置传输时的编码格式，和数据格式
            urlEncodedFormEntity.setContentEncoding("UTF-8");
            urlEncodedFormEntity.setContentType("application/json");*/
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            //5.得到响应信息
            int statusCode = response.getStatusLine().getStatusCode();
            //6. 判断响应信息是否正确
            if(HttpStatus.SC_OK != statusCode){
                //结束请求并抛出异常
                httpPost.abort();
                resMsg= "HttpClient,error status code :" + statusCode;
            }
            //7. 转换成实体类
            HttpEntity entity = response.getEntity();
            if(null != entity){
                resMsg = EntityUtils.toString(entity,"UTF-8");
            }else{
                resMsg = "null";//远程接口返回数据为null
            }
            EntityUtils.consume(entity);
        } catch (UnsupportedEncodingException e) {
            log.error("远程调用失败：",e);
            resMsg = "远程调用失败：" + e.getMessage();
        } catch (ClientProtocolException e) {
            log.error("远程调用失败：",e);
            resMsg = "远程调用失败：" + e.getMessage();
        }catch (SocketTimeoutException | ConnectTimeoutException e) {
            log.error("远程调用失败：",e);
            resMsg = "远程调用失败：" + e.getMessage();
        } catch (IOException e) {
            log.error("远程调用失败：",e);
            resMsg = "远程调用失败：" + e.getMessage();
        }finally {
            //8. 关闭所有资源连接
            if(null != response){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(null != httpClient){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resMsg;
    }
}

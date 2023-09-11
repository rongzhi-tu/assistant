package com.bsoft.assistant.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Cl2
 * @Description: webService远程调用工具
 * @Date Created in 2023/2/13
 */
public class HttpClientUtils {

    /**
     * get 请求
     * @return
     */
    public static Map<String,String> doHttpGet(String url, List<NameValuePair> params){
        Map<String,String> result = new HashMap<>(4);
        String resCode = "000000";
        String resMsg = "请求成功";
        //1.获取httpclient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //接口返回结果
        CloseableHttpResponse response = null;
        String paramStr = null;
        try {
            paramStr = EntityUtils.toString(new UrlEncodedFormEntity(params));
            //拼接参数
            StringBuffer sb = new StringBuffer();
            sb.append("http://localhost:8099/hbase/get");
            sb.append("?");
            sb.append("aa=1");
            //2.创建get请求
            HttpGet httpGet = new HttpGet(sb.toString());
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
                resMsg = EntityUtils.toString(entity);
            }
            EntityUtils.consume(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException | ConnectTimeoutException e) {
            resCode = "503";
            resMsg= "调用同步程序超时,请确认远程同步程序是否开启！";
        }catch (IOException e) {
            e.printStackTrace();
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
        result.put("code",resCode);
        result.put("msg",resMsg);
        return result;
    }

    /**
     * http post 请求
     */
    public static Map<String,String> doPost(String url, String params){
        Map<String,String> result = new HashMap<>(4);
        String resCode = "000000";
        String resMsg = "请求成功";
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
                resCode = String.valueOf(statusCode);
                resMsg= "HttpClient,error status code :" + statusCode;
            }
            //7. 转换成实体类
            HttpEntity entity = response.getEntity();
            if(null != entity){
                resMsg = EntityUtils.toString(entity,"UTF-8");
            }
            EntityUtils.consume(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }catch (SocketTimeoutException | ConnectTimeoutException e) {
            resCode = "503";
            resMsg= "调用同步程序超时,请确认远程同步程序是否开启或系统路径是否正确！";
        } catch (IOException e) {
            e.printStackTrace();
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
        result.put("code",resCode);
        result.put("msg",resMsg);
        return result;
    }
}

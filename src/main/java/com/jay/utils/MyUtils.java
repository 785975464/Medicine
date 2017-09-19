package com.jay.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;

/**
 * Created by Jay on 2017/9/15.
 */
public class MyUtils {

    Logger logger = Logger.getLogger(MyUtils.class);

    /**
     * 通过token模拟登录，返回cookie，用于后续操作
     * @param url
     * @return List<Cookie>
     */
    public List<Cookie> getCookies(String url){
        List<Cookie> cookies;
        try {
            HttpClient client = new DefaultHttpClient();                            //构建一个Client
            HttpPost post = new HttpPost(url);                                      //构建一个POST请求
            HttpResponse httpResponse = client.execute(post);                       //提交POST请求
            cookies = ((AbstractHttpClient)client).getCookieStore().getCookies();   //获取cookie
            HttpEntity result = httpResponse.getEntity();
            String content = EntityUtils.toString(result);
            post.abort();
            client.getConnectionManager().shutdown();
            logger.info("createClient, content:" + content + " url:" + url);
        }catch (Exception e){
            e.printStackTrace();
            cookies = null;
        }
        return cookies;
    }

    /**
     * 通过药品信息获取药品数量
     * @param url
     * @param cookies
     * @return number
     */
    public int getMedicineNumber(String url, List<Cookie> cookies){
        int number;
        try {
            Document numberDoc = Jsoup.connect(url).cookie(cookies.get(0).getName(), cookies.get(0).getValue()).get();
            Element numberElement = numberDoc.select("tr.t-tr").eq(0).select("td").eq(2).get(0);
            number = Integer.parseInt(numberElement.html().trim());
            logger.info("总共可回收的数量为: "+number);
        }catch (Exception e){
            e.printStackTrace();
            number = 0;
        }
        return number;
    }

    /**
     * 通过药品信息获取药品序号
     * @param recycleUrl
     * @param cookies
     * @return order
     */
    public String getMedicineOrder(String recycleUrl, List<Cookie> cookies) {
        String order;
        try {
            Document orderDoc = Jsoup.connect(recycleUrl).cookie(cookies.get(0).getName(), cookies.get(0).getValue()).get();
            Element orderElement = orderDoc.select("input[name=inv_id]").get(0);
            order = orderElement.val();
            logger.info("药品order为: "+order);
        }catch (Exception e){
            e.printStackTrace();
            order = "";
        }
        return order;
    }

    /**
     * 回收指定药品的指定数量
     * @param url 药品回收url
     * @param cookies
     * @param recycleNumber 要回收的数量
     * @param medicineNumber 总共可以回收的数量
     * @param order 药品序号
     */
    public void recycleMedicineByNumber(String url, List<Cookie> cookies, int recycleNumber, int medicineNumber, String order) {
        Connection.Response resp;
        String json;
        int total;              //回收数量取recycleNumber和medicineNumber较小值
        if (recycleNumber > medicineNumber){
            total = medicineNumber;
        }
        else {
            total = recycleNumber;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("inv_id", order);
        try {
            for (int i=0 ; i<total ; i++){
                resp = Jsoup.connect(url).ignoreContentType(true).method(Connection.Method.POST).data(map).cookie(cookies.get(0).getName(), cookies.get(0).getValue()).execute();
                json = resp.body();
                if (json.indexOf("成功")>-1){
                    logger.info("已成功回收第"+i+"瓶！");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 使用Jsoup方法，通过token模拟登录，返回cookie，用于后续操作（有bug）
     * @param url
     * @return List<Cookie>
     */
//    public Map<String, String> getCookiesByJsoup(String url){
    public List<Cookie> getCookiesByJsoup(String url){
        List<Cookie> cookieList = null;
        Map<String, String> cookies = null;
        try {
            Connection.Response resp = Jsoup.connect(url).ignoreContentType(true).method(Connection.Method.POST).execute();
            cookies = resp.cookies();
            Set s = cookies.keySet();
//            Collection<String> valueCollection = cookies.values();
//            List<String> valueList = new ArrayList<String>(valueCollection);

            final String name = s.iterator().next().toString();
            final String value = cookies.get(name);
//            final String value = cookies.get(valueList.get(0));

//            javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(name, value);
//            Cookie cookie = new Cookie() {
//                public String getName() {
//                    return name;
//                }
//
//                public String getValue() {
//                    return value;
//                }
//
//                public String getComment() {
//                    return null;
//                }
//
//                public String getCommentURL() {
//                    return null;
//                }
//
//                public Date getExpiryDate() {
//                    return null;
//                }
//
//                public boolean isPersistent() {
//                    return false;
//                }
//
//                public String getDomain() {
//                    return null;
//                }
//
//                public String getPath() {
//                    return null;
//                }
//
//                public int[] getPorts() {
//                    return new int[0];
//                }
//
//                public boolean isSecure() {
//                    return false;
//                }
//
//                public int getVersion() {
//                    return 0;
//                }
//
//                public boolean isExpired(Date date) {
//                    return false;
//                }
//            };
//            cookieList.add(cookie);
//            logger.info("cookies: "+cookies);
//            String json = resp.body();
//            logger.info("结果json: "+json);
        }catch (Exception e){
            e.printStackTrace();
        }
        return cookieList;
    }



}

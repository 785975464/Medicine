package com.jay.utils;

import com.alibaba.fastjson.JSON;
import com.jay.model.Inventory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
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
     * 回收指定药品的指定数量
     * @param url 药品回收url
     * @param cookies
     * @param recycleNumber 要回收的数量
     * @param medicineNumber 总共可以回收的数量
     * @param order 药品序号
     * @return count 已成功回收的数量
     */
    public int recycleMedicineByNumber(String url, List<Cookie> cookies, int recycleNumber, int medicineNumber, String order) {
        Connection.Response resp;           //返回的网页内容
        String json;                        //解析内容
        int total;                          //回收数量取recycleNumber和medicineNumber较小值
        int count = 0;                      //已成功回收的数量
        Map<String, String> map = new HashMap<String, String>();    //存放键值对
        try {
            if (recycleNumber > medicineNumber){
                total = medicineNumber;
            }
            else {
                total = recycleNumber;
            }
            map.put("inv_id", order);       //回收参数
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
        try {
            for (int i=0 ; i<total ; i++){
                try {
                    resp = Jsoup.connect(url).ignoreContentType(true).method(Connection.Method.POST).data(map).cookie(cookies.get(0).getName(), cookies.get(0).getValue()).timeout(6000).execute();
                    json = resp.body();
                    logger.info(json);
                    if (json.indexOf("确认回收")>-1){
                        i--;
                        continue;
                    }
                    if (json.indexOf("收货")>-1){
                        return -1;      //-1表示需要先收货
                    }
                    else if (json.indexOf("废瓶回收成功")>-1){
                        logger.info("已成功回收"+order+"第"+i+"瓶！");
                        count++;
                    }
                    else{
                        return -2;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    continue;
                }
            }
            return count;
        }catch (Exception e){
            e.printStackTrace();
            return count;
        }
    }

    /**
     * 通过用户名、密码模拟登录，获取cookie，用于后续操作
     * @param url
     * @return
     */
    public List<Cookie> getLoginCookie(String url) {
        List<Cookie> cookies = null;
        try {
            HttpClient client = new DefaultHttpClient();                        //构建一个Client
            HttpPost post = new HttpPost(url);                                  //构建一个POST请求
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();    //构建表单参数
            formParams.add(new BasicNameValuePair("username", Config.USERNAME));
            formParams.add(new BasicNameValuePair("password", Config.PASSWORD));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, "UTF-8");        //将表单参数转化为“实体”
            post.setEntity(entity);                                             //将实体设置到POST请求里
            HttpResponse httpResponse = client.execute(post);                   //提交POST请求
            if (httpResponse.getStatusLine().getStatusCode() == 200){           //登录成功，并保存本次cookie
                cookies = ((AbstractHttpClient)client).getCookieStore().getCookies();
                logger.info("cookies:"+cookies);
            }
            client.getConnectionManager().shutdown();
        }catch (Exception e){
            e.printStackTrace();
            cookies = null;
        }
        return cookies;
    }

    /**
     * 获取库存列表（用于初始化、或更新）
     * @param listUrl
     * @param cookies
     * @return
     */
    public List<Inventory> getInventoryList(String listUrl, List<Cookie> cookies){
        List<Inventory> inventoryList = new ArrayList<Inventory>();
        try {
            Document inventoryListDoc = null;
            try {
                inventoryListDoc = Jsoup.connect(listUrl).cookie(cookies.get(0).getName(), cookies.get(0).getValue()).timeout(6000).get();
            }catch (Exception e){
                e.printStackTrace();
                logger.info("连接超时！");
                return null;
            }
            int size = inventoryListDoc.select("div").size();
            logger.info("总共行数为: "+size);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Element bodyElement = inventoryListDoc.select("body").get(0);
            bodyElement.select("i").remove();                               //删除i标签
            bodyElement.select("span").remove();
            bodyElement.select("div").remove();
            bodyElement.select("button").remove();
            bodyElement.select("[value='']").remove();
//            logger.info("标签内容为: "+inventoryListDoc.select("body").get(0).childNodes());
//            logger.info("标签内容为: "+bodyElement.childNodes());
            List<Node> nodeList = new ArrayList<Node>();
            Node node = null;
            Iterator<Node> iterator = bodyElement.childNodes().iterator();
            while (iterator.hasNext()){
                node = iterator.next();
                if (node != null && !node.toString().trim().equals("")){
                    nodeList.add(node);
                }
            }
            Inventory inventory;    //暂存inventory
            String goodsid;         //暂存goodsid
            for (int i=0 ;i<size ; i++){
                inventory = new Inventory();
                try {
                    goodsid = nodeList.get(0+i*9).toString().trim();
                    inventory.setGoodsid(goodsid.substring(goodsid.indexOf("SN"),goodsid.indexOf("SN")+14));
                    inventory.setOrderid(nodeList.get(0+i*9).attr("href").substring(5));
                    inventory.setCreatetime(sdf.parse(nodeList.get(1+i*9).toString().trim()));
                    inventory.setCreator(nodeList.get(2+i*9).toString().trim());
                    inventory.setMedicinename(nodeList.get(3+i*9).toString().trim());
//                    inventory.setMsds(nodeList.get(4+i*9).toString().trim());
                    inventory.setTotalquantity(nodeList.get(5+i*9).toString().trim());
                    inventory.setAmount(Integer.parseInt(nodeList.get(6+i*9).toString().trim()));
                    inventory.setRemain(Integer.parseInt(nodeList.get(6+i*9).toString().trim()));
                    inventory.setLocation(nodeList.get(7+i*9).toString().trim());
                    inventory.setNotes(nodeList.get(8+i*9).toString().trim());
                    logger.info("第"+i+"个inventory为："+inventory);
                    inventoryList.add(inventory);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return inventoryList;
    }

    /**
     * 用于更新药品库存（余量）信息
     * @param updateRemainInventoryUrl
     * @param cookies
     * @return List<Map<String, Integer>>
     */
    public List<Map<String, Integer>> updateRemainInventory(String updateRemainInventoryUrl, List<Cookie> cookies) {
        Map<String, Integer> map = new HashMap<String, Integer>();      //用来存放凭证统计结果
        List<Map<String, Integer>> mapList = new ArrayList<Map<String, Integer>>();
        try {
            Document inventoryListDoc = null;
            try {
                inventoryListDoc = Jsoup.connect(updateRemainInventoryUrl).cookie(cookies.get(0).getName(), cookies.get(0).getValue()).timeout(6000).get();
            }catch (Exception e){
                e.printStackTrace();
                logger.info("连接超时！");
            }
            Elements aLinks = inventoryListDoc.select("a.text-muted");
            int size = aLinks.size();
            logger.info("总共行数为: "+size);
            String certificateUrl;
            for (int i=1 ; i< size ; i++){
                certificateUrl = Config.BOTTLEURL + "/" + aLinks.get(i).attr("href").trim();
                logger.info("第"+i+"个a标签的连接为: " + certificateUrl);
                map = getCertificateInfo(certificateUrl, cookies);
                if (map != null) {
                    mapList.add(map);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mapList;
    }

    /**
     * 获取凭证信息（被updateRemainInventory调用，用于更新库存）
     * @param url
     * @param cookies
     * @return
     */
    private Map<String, Integer> getCertificateInfo(String url, List<Cookie> cookies) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        try {
            Document certificateDoc = null;
            try {
                certificateDoc = Jsoup.connect(url).cookie(cookies.get(0).getName(), cookies.get(0).getValue()).timeout(6000).get();
            }catch (Exception e){
                e.printStackTrace();
                logger.info("连接超时！");
            }
            Elements trs = certificateDoc.select("table tr");
            int size = trs.size();
            logger.info("tr总共行数为: "+(size-1));
            String key;
            for (int i=1 ; i<size ; i++){
                key = trs.select("tr").eq(i).select("td").eq(2).text().toString().trim();
                if (map.containsKey(key)){
                    map.put(key , map.get(key)+1);
                }
                else {
                    map.put(key , 1);
                }
            }
            for (Map.Entry<String,Integer> entry : map.entrySet()) {
                logger.info("key: " + entry.getKey() + " , value:" + entry.getValue());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 消息转json，传给前台（String转Json工具）
     * @param msg
     * @return
     */
    public String jsonMessage(String msg){
        Map<String, String> map=new HashMap<String, String>();
        map.put("msg", msg);
        return JSON.toJSONString(map);
    }

    /**
     * 通过药品信息获取药品数量（已弃用）
     * @param url
     * @param cookies
     * @return number
     */
//    public int getMedicineNumber(String url, List<Cookie> cookies){
//        int number;
//        try {
//            Document numberDoc = Jsoup.connect(url).cookie(cookies.get(0).getName(), cookies.get(0).getValue()).get();
//            Element numberElement = numberDoc.select("tr.t-tr").eq(0).select("td").eq(2).get(0);
//            number = Integer.parseInt(numberElement.html().trim());
//            logger.info("总共可回收的数量为: "+number);
//        }catch (Exception e){
//            e.printStackTrace();
//            number = 0;
//        }
//        return number;
//    }

    /**
     * 通过药品信息获取药品序号（已弃用）
     * @param recycleUrl
     * @param cookies
     * @return order
     */
//    public String getMedicineOrder(String recycleUrl, List<Cookie> cookies) {
//        String order;
//        try {
//            Document orderDoc = Jsoup.connect(recycleUrl).cookie(cookies.get(0).getName(), cookies.get(0).getValue()).get();
//            Element orderElement = orderDoc.select("input[name=inv_id]").get(0);
//            order = orderElement.val();
//            logger.info("药品order为: "+order);
//        }catch (Exception e){
//            e.printStackTrace();
//            order = "";
//        }
//        return order;
//    }

    /**
     * 使用Jsoup方法，通过token模拟登录，返回cookie，用于后续操作（有bug）（已弃用）
     * @param url
     * @return List<Cookie>
     */
//    public Map<String, String> getCookiesByJsoup(String url){
//    public List<Cookie> getCookiesByJsoup(String url){
//        List<Cookie> cookieList = null;
//        Map<String, String> cookies = null;
//        try {
//            Connection.Response resp = Jsoup.connect(url).ignoreContentType(true).method(Connection.Method.POST).execute();
//            cookies = resp.cookies();
//            Set s = cookies.keySet();
//            final String name = s.iterator().next().toString();
//            final String value = cookies.get(name);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return cookieList;
//    }

    /**
     * 从主页Home获取库存信息列表（已弃用）
     * @param url
     * @param cookies
     * @return
     */
//    public List<Inventory> getInventoryListFromHome(String url, List<Cookie> cookies) {
//        List<Inventory> inventoryList = new ArrayList<Inventory>();
//        try {
//            Document inventoryDoc = Jsoup.connect(url).cookie(cookies.get(0).getName(), cookies.get(0).getValue()).get();
//            int size = inventoryDoc.select("table>tbody.app-inventory-list-container").select("tr").size()-2;   //去掉2个加载数据的tr
//            logger.info("总共行数为: "+size);
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            for (int i=0 ;i<size ; i++){
//                Inventory inventory = new Inventory();
//                try {
//                    inventory.setGoodsid(inventoryDoc.select("tbody tr").eq(i).select("td").eq(0).get(0).text().trim());
//                    inventory.setCreatetime(sdf.parse(inventoryDoc.select("tbody tr").eq(i).select("td").eq(1).get(0).text().trim()));
//                    inventory.setCreator(inventoryDoc.select("tbody tr").eq(i).select("td").eq(2).get(0).text().trim());
//                    inventory.setMedicinename(inventoryDoc.select("tbody tr").eq(i).select("td").eq(3).get(0).text().trim());
//                    inventory.setMsds(inventoryDoc.select("tbody tr").eq(i).select("td").eq(4).get(0).text().trim());
//                    inventory.setTotalquantity(inventoryDoc.select("tbody tr").eq(i).select("td").eq(5).get(0).text().trim());
//                    inventory.setAmount(Integer.parseInt(inventoryDoc.select("tbody tr").eq(i).select("td").eq(6).get(0).text().trim()));
//                    inventory.setRemain(Integer.parseInt(inventoryDoc.select("tbody tr").eq(i).select("td").eq(6).get(0).text().trim()));
//                    inventory.setLocation(inventoryDoc.select("tbody tr").eq(i).select("td").eq(7).get(0).text().trim());
//                    inventory.setNotes(inventoryDoc.select("tbody tr").eq(i).select("td").eq(8).get(0).text().trim());
//                    logger.info("第"+i+"个inventory为："+inventory);
//                    inventoryList.add(inventory);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return inventoryList;
//    }
}

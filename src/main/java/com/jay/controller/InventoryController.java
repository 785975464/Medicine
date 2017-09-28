package com.jay.controller;

/**
 * Created by Jay on 2017/9.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jay.model.Inventory;
import com.jay.service.InventoryService;
import com.jay.utils.Config;
import com.jay.utils.MyUtils;
import org.apache.http.cookie.Cookie;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/inventory")
public class InventoryController {
    Logger logger = Logger.getLogger(InventoryController.class);
//    String baseUrl="http://inventory.njust.labmai.com";
//  String tokenUrl = "http://inventory.njust.labmai.com/exhibit/stock/SN201709150035/?wx-token=ab5b15c5c9176220f2f588f7fc6d4498&gapper-token=&gapper-group=";
//  goodsid = "SN201709150035";

    @Resource
    private InventoryService inventoryService;

    /**
     * 通过id获取指定药品信息
     * @param request
     * @param response
     * @throws IOException
     */
//    @RequestMapping("/get")
//    public void selectMedicine(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        request.setCharacterEncoding("UTF-8");
//        response.setCharacterEncoding("UTF-8");
//        int id = Integer.parseInt(request.getParameter("id"));
//        Medicine medicine = this.medicineService.selectMedicine(id);
//        logger.info("medicine:"+medicine);
//        ObjectMapper mapper = new ObjectMapper();
//        response.getWriter().write(mapper.writeValueAsString(medicine));
//        response.getWriter().close();
//    }

    /**
     * 获取全部药品信息
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getAll")
    public void getAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        List<Inventory> inventoryList = this.inventoryService.getAll();
        logger.info("inventoryList:"+inventoryList);
        ObjectMapper mapper = new ObjectMapper();
        HashMap map = new HashMap();
        map.put("draw",0);
        map.put("data",inventoryList);
        map.put("totalRecords",inventoryList.size());
        map.put("filterRecords",inventoryList.size());
        response.getWriter().write(mapper.writeValueAsString(map));
        response.getWriter().close();
    }

    /**
     * 初始化库存信息，初始化数量为可选（如：更新最近25条数据及其余量）
     * @param request
     * @param response
     */
    @RequestMapping(value = "/init", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String initInventoryInfo(HttpServletRequest request, HttpServletResponse response) {
        List<Cookie> cookies;                       //获取cookie，用于后续登录
//        String loginUrl = Config.BASEURL + "/ajax/gapper/auth/gapper/login";                        //模拟登录url
        String loginUrl = "http://inventory.njust.labmai.com/ajax/gapper/auth/gapper/login";          //模拟登录url
        String inventoryUrl = "http://inventory.njust.labmai.com/ajax/inventory/more/0/1";            //库存列表URL
        int pageSizeInventory;                      //要更新的库存数量
        int pageSizeCertificate;                    //要更新的凭证数量
        List<Inventory> inventoryList;              //库存列表信息
        List<Map<String, Integer>> remainMapList;   //保存药品余量信息
        MyUtils myUtils = new MyUtils();            //实例化工具类
        try {
            //0、初始化
            try {
                cookies = myUtils.getLoginCookie(loginUrl);
                try {
                    pageSizeInventory = Integer.parseInt(request.getParameter("pageSizeInventory"));
                    pageSizeCertificate = Integer.parseInt(request.getParameter("pageSizeCertificate"));
                }catch (Exception e){
                    pageSizeInventory = Config.PAGESIZE;
                    pageSizeCertificate = Config.PAGESIZE;
                    logger.error("pageSize参数错误，已重置！");
                }
            }catch (Exception e){
                logger.error("初始化失败！"+e.getMessage());
                return myUtils.jsonMessage("初始化失败！");
            }
            //一、获取库存列表信息
            try {
                inventoryList = new ArrayList<Inventory>();
                for (int i=0 ; i< pageSizeInventory/Config.PAGESIZE ; i++){
//                    inventoryUrl = Config.BASEURL + "/ajax/inventory/more/"+i*Config.PAGESIZE+"/1";
                    inventoryUrl = "http://inventory.njust.labmai.com/ajax/inventory/more/0/1";
                    inventoryList.addAll(myUtils.getInventoryList(inventoryUrl, cookies));
                }
                if (inventoryList == null){
                    logger.warn("获取库存列表信息为空！");
                    return myUtils.jsonMessage("获取库存列表信息为空！");
                }
            }catch (Exception e){
                logger.error("获取库存列表信息失败！原因："+e.getMessage());
                return myUtils.jsonMessage("获取库存列表信息失败！"+e.getMessage());
            }
            //二、库存信息插入数据库
            List<Inventory> tempList;                //临时变量，保存库存信息
            try {
                for (int i=0 ; i<inventoryList.size() ; i++){
                    try {
                        tempList = this.inventoryService.getInventoryByGoodsid(inventoryList.get(i).getGoodsid());
                        if (tempList.size()!=1){
                            if (tempList.size()>1){
                                this.inventoryService.deleteInventoryByGoodsid(inventoryList.get(i).getGoodsid());
                            }
                            this.inventoryService.insertInventory(inventoryList.get(i));
                            logger.info("成功插入第"+i+"条数据！");
                        }
                        else {
                            logger.info("已发现第"+i+"条数据！" + inventoryList.get(i).getGoodsid());
                        }
                    }catch (Exception e){
                        logger.error("获取并插入第"+i+"条数据失败！"+e.getMessage());
                        continue;
                    }
                }
            }catch (Exception e){
                logger.error("库存信息插入数据库失败！"+e.getMessage());
                return myUtils.jsonMessage("库存信息插入数据库失败！");
            }
            //三、更新库存药品余量
            Inventory inventory;                    //临时变量，保存库存变量
            String updateRemainInventoryUrl;        //临时变量，保存余量库存url
            try {
                for (int i=0 ; i< pageSizeCertificate/Config.PAGESIZE ; i++){
                    updateRemainInventoryUrl = Config.BOTTLEURL + "/ajax/certificate/index/more/"+i*Config.PAGESIZE+"/1";
                    remainMapList = myUtils.updateRemainInventory(updateRemainInventoryUrl, cookies);
                    logger.info("完成第" + i + "次循环，统计药品余量结果如下：（共计" + remainMapList.size() + "项）");
                    for (int j=0 ; j<remainMapList.size() ; j++){
                        for (Map.Entry<String,Integer> entry : remainMapList.get(j).entrySet()) {
                            try {
                                logger.info("key: " + entry.getKey() + " , value:" + entry.getValue());
                                if ( this.inventoryService.getInventoryByGoodsid(entry.getKey()).isEmpty() ){
                                    throw new Exception("该药品"+entry.getKey()+"信息暂未列入库存中，请重新初始化！");
                                }
                                try {
                                    inventory = this.inventoryService.getInventoryByGoodsid(entry.getKey()).get(0);
                                }catch (Exception e){
                                    logger.warn("该药品"+entry.getKey()+"为空");
                                    continue;
                                }
                                if (inventory.getAmount() <= 0){
                                    logger.warn("该药品"+inventory.getGoodsid()+"总量≤0");
                                    continue;
                                }
                                else if (inventory.getAmount() >= entry.getValue()){
                                    inventory.setRemain(inventory.getAmount() - entry.getValue());
                                }
                                else{
                                    throw new Exception("该药品库存异常！");
                                }
                                this.inventoryService.updateInventory(inventory);
                            }catch (Exception e){
                                logger.error("该药品"+entry.getKey()+"库存异常！"+e.getMessage());
                                continue;
                            }
                        }
                        logger.info("完成第"+j+"项药品的余量更新任务");
                    }
                }
                logger.info("完成全部药品的余量更新任务！");
            }catch (Exception e){
                logger.error("更新库存药品余量出错！"+e.getMessage());
                return myUtils.jsonMessage("更新库存药品余量出错！");
            }
        }catch (Exception e){
            logger.error("初始化过程中出错！"+e.getMessage());
            return myUtils.jsonMessage("初始化过程中出错！");
        }
        logger.info("已完成全部初始化任务！");
        return myUtils.jsonMessage("已完成全部初始化任务！");
    }

//    @RequestMapping("/test")
//    @ResponseBody
//    public void test(HttpServletRequest request, HttpServletResponse response) {
//        List<Map<String, Integer>> mapList;
//        Inventory inventory;
//        try {
//            //三、更新库存药品余量
//            String updateRemainInventoryUrl;
//            List<Cookie> cookies = null;
//            MyUtils myUtils = new MyUtils();
//            int pageSize;
//            try {
//                pageSize = Integer.parseInt(request.getParameter("pageSize"));
//            }catch (Exception e){
//                pageSize = 25;
//            }
//            String loginUrl="http://bottles.njust.labmai.com/ajax/gapper/auth/gapper/login";          //模拟登录url
//            cookies = myUtils.getLoginCookie(loginUrl);
//            for (int i=0 ; i< pageSize/25 ; i++){
//                updateRemainInventoryUrl = "http://bottles.njust.labmai.com" + "/ajax/certificate/index/more/"+i*25+"/1";
//                mapList = myUtils.updateRemainInventory(updateRemainInventoryUrl, cookies);
//
//                logger.info("完成第" + i + "次循环，统计药品余量结果如下：（共计" + mapList.size() + "项）");
//                for (int j=0 ; j<mapList.size() ; j++){
//                    for (Map.Entry<String,Integer> entry : mapList.get(j).entrySet()) {
//                        try {
//                            logger.info("key: " + entry.getKey() + " , value:" + entry.getValue());
//                            if ( this.inventoryService.getInventoryByGoodsid(entry.getKey()).isEmpty() ){
//                                throw new Exception("该药品"+entry.getKey()+"信息暂未列入库存中，请重新初始化！");
//                            }
//                            inventory = this.inventoryService.getInventoryByGoodsid(entry.getKey()).get(0);
//                            if (inventory.getAmount() == 0){
//                                continue;
//                            }
//                            else if (inventory.getAmount() >= entry.getValue()){
//                                inventory.setRemain(inventory.getAmount() - entry.getValue());
//                            }
//                            else{
//                                throw new Exception("该药品库存异常！");
//                            }
//                            this.inventoryService.updateInventory(inventory);
//                        }catch (Exception e){
//                            e.getMessage();
////                            e.printStackTrace();
//                            continue;
//                        }
//                    }
//                }
//                logger.info("完成本次循环任务");
//            }
//            logger.info("完成全部任务");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }

}

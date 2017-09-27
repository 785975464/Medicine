package com.jay.controller;

/**
 * Created by Jay on 2017/9.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jay.model.Inventory;
import com.jay.model.Medicine;
import com.jay.service.InventoryService;
import com.jay.service.MedicineService;
import com.jay.utils.Config;
import com.jay.utils.ExcelUtils;
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
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/medicine")
public class MedicineController {
    Logger logger = Logger.getLogger(MedicineController.class);

    @Resource
    private MedicineService medicineService;
    @Resource
    private InventoryService inventoryService;

    MyUtils myUtils = new MyUtils();        //实例化工具类
    /**
     * 通过id获取指定药品信息
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/get")
    public void selectMedicine(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        int id = Integer.parseInt(request.getParameter("id"));
        Medicine medicine = this.medicineService.selectMedicine(id);
        logger.info("medicine:"+medicine);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(medicine));
        response.getWriter().close();
    }

    /**
     * 获取全部药品信息
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/getAll")
    @ResponseBody
    public void getAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            List<Medicine> medicinelist = this.medicineService.getAll();
            logger.info("medicinelist:"+medicinelist);
            ObjectMapper mapper = new ObjectMapper();
            HashMap map = new HashMap();
            map.put("draw",0);
            map.put("data",medicinelist);
            map.put("totalRecords",medicinelist.size());
            map.put("filterRecords",medicinelist.size());
            response.getWriter().write(mapper.writeValueAsString(map));
            response.getWriter().close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 添加药品信息
     * @param medicine
     */
    @RequestMapping(value = "/add" , produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String addMedicineInfo(Medicine medicine) {
        try {
            logger.info("add medicine:"+medicine);
            this.medicineService.insertMedicine(medicine);
            return myUtils.jsonMessage("添加成功！");
        }catch (Exception e){
            e.printStackTrace();
            return myUtils.jsonMessage("添加失败！");
        }
    }

    /**
     * 删除药品信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete" , produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String deleteMedicineInfo(int id) {
        try {
            logger.info("delete id:"+id);
            this.medicineService.deleteMedicine(id);
            return myUtils.jsonMessage("删除成功！");
        }catch (Exception e){
            e.printStackTrace();
            return myUtils.jsonMessage("删除失败！");
        }
    }

    /**
     * 更新药品信息
     * @param medicine
     * @return
     */
    @RequestMapping(value = "/update" , produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String updateMedicineInfo(Medicine medicine) {
        try {
            logger.info("update medicine:"+medicine);
            int result = this.medicineService.updateMedicine(medicine);
            logger.info("更新成功！result:"+result);
            return myUtils.jsonMessage("更新成功！");
        }catch (Exception e){
            e.printStackTrace();
            return myUtils.jsonMessage("更新失败！");
        }
    }

    /**
     * 回收药品
     * @param request
     * @param response
     */
    @RequestMapping(value = "/recycle", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String recycleMedicine(HttpServletRequest request, HttpServletResponse response) {
        String[] orderList;                     //用于存放前台传过来的orderid
        String[] recycleNumberList;             //用于存放前台传过来的回收数量list
        String orderid;                         //商品序号orderid
        String token;                           //获取token（有效时间为5分钟）
        List<Cookie> cookies;                   //获取cookie，用于后续登录
        MyUtils myUtils = new MyUtils();        //实例化工具类
        try {                                   //获取参数
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            orderList = request.getParameterValues("orderid");
            recycleNumberList = request.getParameterValues("number");
            logger.info("orderList.length为: " + orderList.length + " recycleNumberList.length为: "+recycleNumberList.length);
        }catch (Exception e){
            e.printStackTrace();
            logger.info("回收参数错误");
            return myUtils.jsonMessage("回收参数错误");
        }
        try {
            String tokenLink = request.getParameter("tokenLink");
            int start = tokenLink.indexOf("=");
            int end = tokenLink.indexOf("&");
            if ( end < 0 ) {
                end = tokenLink.length()-1;
            }
            token = tokenLink.substring(start+1, end);
            logger.info("token为: "+token);
            if (token.length()!=32){
                throw new Exception("token长度错误");
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.info("token不合法");
            return myUtils.jsonMessage("token不合法");
        }
        //随便给个商品ID，用来获取cookies
        String tokenUrl = Config.BASEURL + "/exhibit/stock/SN201709210042/?wx-token="+token+"&gapper-token=&gapper-group=";
        String recycleUrl;      //回收药品的URL，会根据商品ID变化
        try {
            cookies = myUtils.getCookies(tokenUrl);
        }catch (Exception e){
            e.printStackTrace();
            return myUtils.jsonMessage("cookie获取失败，请刷新页面或稍后重试！");
        }
        try {
            Inventory inventory;
            int count;          //每次回收的数量
            for (int i=0; i<orderList.length ; i++){
                if (orderList[i].equals("") || recycleNumberList[i].equals("")){
                    continue;
                }
                try {
                    orderid = orderList[i];
                    inventory = this.inventoryService.getInventoryByOrderid(orderid).get(0);
                    recycleUrl = Config.BASEURL + "/exhibit/add-bottle/" + inventory.getGoodsid();
                    //回收指定药品的指定数量，参数（回收url，cookie，要回收的数量，药品总数，药品序号）
                    count = myUtils.recycleMedicineByNumber(recycleUrl, cookies, Integer.parseInt(recycleNumberList[i]), inventory.getAmount(), orderid);
                    if (count == -1){
                        logger.info("回收前请先确认收货！");
                        return myUtils.jsonMessage("回收前请先确认收货！");
                    }
                    else if (count<0){
                        logger.info("token错误，请用微信重试（可能已过期）！");
                        return myUtils.jsonMessage("token错误，请用微信重试（可能已过期）！");
                    }
                    if (inventory.getAmount() >= count){
                        inventory.setRemain(inventory.getAmount()-count);
                        this.inventoryService.updateInventory(inventory);
                    }
                    else{
                        throw new Exception("回收数量超过药品总数");
                    }
                    logger.info("编号为"+orderid+"的药品成功回收"+count+"瓶");
                }catch (Exception e){
                    e.printStackTrace();
                    logger.info("回收过程发生错误");
                    continue;
                }
            }
            return myUtils.jsonMessage("回收成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("token或回收过程发生错误");
            return myUtils.jsonMessage("token或回收过程发生错误");
        }
    }

    @RequestMapping("/exportExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response){
        String[] headers = {"ID","LocatoinID","Name","CAS","Remark"};
        try {
            ExcelUtils.export("D501药品清单",null,headers,headers,null,null,this.medicineService.getAll(),request,response);
        }catch (Exception e){
            e.getStackTrace();
        }
    }
}

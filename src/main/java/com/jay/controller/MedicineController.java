package com.jay.controller;

/**
 * Created by Jay on 2017/9.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jay.model.Medicine;
import com.jay.service.MedicineService;
import com.jay.utils.MyUtils;
import org.apache.http.cookie.Cookie;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
    String baseUrl="http://inventory.njust.labmai.com";
//  String tokenUrl = "http://inventory.njust.labmai.com/exhibit/stock/SN201709150035/?wx-token=ab5b15c5c9176220f2f588f7fc6d4498&gapper-token=&gapper-group=";
//  goodsid = "SN201709150035";

    @Resource
    private MedicineService medicineService;

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
    public void getAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
    }

    /**
     * 回收药品
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/recycle")
    public void recycleMedicine(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String goodsid = "";
        String token = "";
        List<Cookie> cookies = null;    //获取cookie，用于后续登录
        int recycleNumber = 0;
        try {
            String tokenLink = request.getParameter("tokenLink");
            goodsid = request.getParameter("goodsid");
            recycleNumber = Integer.parseInt(request.getParameter("number"));
            int start = tokenLink.indexOf("=");
            int end = tokenLink.indexOf("&");
            if ( end < 0 ) {
                end = tokenLink.length()-1;
            }
            token = tokenLink.substring(start+1, end);
            logger.info("token为: "+token);
        }catch (Exception e){
            e.printStackTrace();
        }
        String tokenUrl = baseUrl + "/exhibit/stock/"+goodsid+"/?wx-token="+token+"&gapper-token=&gapper-group=";
        String recycleUrl = baseUrl + "/exhibit/add-bottle/" + goodsid;
        String numberUrl = baseUrl + "/exhibit/show-order-info/" + goodsid;
        try {
            MyUtils myUtils = new MyUtils();
            cookies = myUtils.getCookies(tokenUrl);
            //获取药品数量
            int medicineNumber = myUtils.getMedicineNumber(numberUrl, cookies);
            //获取药品序号order
            String order = myUtils.getMedicineOrder(recycleUrl, cookies);
            //回收指定药品的指定数量
            myUtils.recycleMedicineByNumber(recycleUrl, cookies, recycleNumber, medicineNumber, order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.jay.service.impl;

import com.jay.dao.InventoryDao;
import com.jay.model.Inventory;
import com.jay.service.InventoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Jay on 2017/6/21.
 */
@Service("inventoryService")
public class InventoryServiceImpl implements InventoryService {

    @Resource
    private InventoryDao inventoryDao;

//    public Inventory selectInventory(int id) {
//        return this.inventoryDao.selectInventory(id);
//    }
    public List<Inventory> getInventoryByGoodsid(String goodsid){return this.inventoryDao.getInventoryByGoodsid(goodsid); };

    public List<Inventory> getInventoryByOrderid(String orderid){return this.inventoryDao.getInventoryByOrderid(orderid); }

    public List<Inventory> getAll() {
        return this.inventoryDao.getAll();
    }

    public int updateInventory(Inventory inventory){return this.inventoryDao.updateInventory(inventory);}

    public void deleteInventoryByGoodsid(String goodsid){this.inventoryDao.deleteInventoryByGoodsid(goodsid); }

    public void insertInventory(Inventory inventory){
        this.inventoryDao.insertInventory(inventory);
    }
}

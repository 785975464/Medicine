package com.jay.service;

import com.jay.model.Inventory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Jay on 2017/6/21.
 */
@Transactional
public interface InventoryService {
//    Inventory selectInventory(int id);
    List<Inventory> getInventoryByGoodsid(String goodsid);

    List<Inventory> getInventoryByOrderid(String orderid);

    List<Inventory> getAll();

    int updateInventory(Inventory inventory);

    void deleteInventoryByGoodsid(String goodsid);

    void insertInventory(Inventory inventory);
}

package com.jay.dao;

import com.jay.model.Inventory;

import java.util.List;

/**
 * Created by Jay on 2017/6/21.
 */
public interface InventoryDao {
    List<Inventory> getInventoryByGoodsid(String goodsid);

    List<Inventory> getInventoryByOrderid(String orderid);

    List<Inventory> getAll();

    int updateInventory(Inventory inventory);

    void deleteInventoryByGoodsid(String goodsid);

    void insertInventory(Inventory inventory);
}

package com.jay.dao;

import com.jay.model.Medicine;

import java.util.List;

/**
 * Created by Jay on 2017/6/21.
 */
public interface MedicineDao {
    Medicine selectMedicine(int id);

    List<Medicine> getAll();
}

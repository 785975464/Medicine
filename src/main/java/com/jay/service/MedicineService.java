package com.jay.service;

import com.jay.model.Medicine;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Jay on 2017/6/21.
 */
@Transactional
public interface MedicineService {
    Medicine selectMedicine(int id);

    List<Medicine> getAll();
}

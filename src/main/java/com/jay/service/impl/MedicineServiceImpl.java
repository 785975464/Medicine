package com.jay.service.impl;

import com.jay.dao.MedicineDao;
import com.jay.model.Medicine;
import com.jay.service.MedicineService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Jay on 2017/6/21.
 */
@Service("medicineService")
public class MedicineServiceImpl implements MedicineService {

    @Resource
    private MedicineDao medicineDao;

    public Medicine selectMedicine(int id) {
        return this.medicineDao.selectMedicine(id);
    }

    public List<Medicine> getAll() { return this.medicineDao.getAll(); }

    public int updateMedicine(Medicine medicine){return this.medicineDao.updateMedicine(medicine);}

    public int deleteMedicine(int id){return this.medicineDao.deleteMedicine(id);}

    public void insertMedicine(Medicine medicine){this.medicineDao.insertMedicine(medicine);}

}

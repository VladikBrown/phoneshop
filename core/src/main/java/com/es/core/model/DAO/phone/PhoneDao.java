package com.es.core.model.DAO.phone;

import com.es.core.model.DAO.phone.consts.SortField;
import com.es.core.model.DAO.phone.consts.SortOrder;
import com.es.core.model.entity.phone.Phone;

import java.util.List;
import java.util.Optional;

public interface PhoneDao {
    Optional<Phone> get(Long key);

    Optional<Phone> get(String model);

    void save(Phone phone);

    List<Phone> findAll(int offset, int limit);

    List<Phone> findAll(SortField sort, SortOrder order, String query, int offset, int limit);

    int getRecordsQuantity(String query);
}

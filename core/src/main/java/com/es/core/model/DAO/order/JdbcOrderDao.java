package com.es.core.model.DAO.order;

import com.es.core.model.DAO.CommonJdbcDaoUtils;
import com.es.core.model.DAO.exceptions.IdUniquenessException;
import com.es.core.model.entity.order.Order;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcOrderDao implements OrderDao {

    private static final String SELECT_ONE_BY_ID_SQL_QUERY = "SELECT ordersFound.id AS id, " +
            "secureId, firstName, lastName, deliveryAddress, contactPhoneNo, additionalInformation, " +
            "subtotal, deliveryPrice, totalPrice, " +
            "orderItems.id AS orderItems_id," +
            "orderItems.quantity AS orderItems_quantity, " +
            "phones.id AS orderItems_phone_id, " +
            "phones.brand AS orderItems_phone_brand, " +
            "phones.model AS orderItems_phone_model, " +
            "phones.price AS orderItems_phone_price, " +
            "phones.displaySizeInches AS orderItems_phone_displaySizeInches, " +
            "phones.weightGr AS orderItems_phone_weightGr, " +
            "phones.lengthMm AS orderItems_phone_lengthMm, " +
            "phones.widthMm AS orderItems_phone_widthMm, " +
            "phones.heightMm AS orderItems_phone_heightMm, " +
            "phones.announced AS orderItems_phone_announced, " +
            "phones.deviceType AS orderItems_phone_deviceType, " +
            "phones.os AS orderItems_phone_os, " +
            "phones.displayResolution AS orderItems_phone_displayResolution, " +
            "phones.pixelDensity AS orderItems_phone_pixelDensity, " +
            "phones.displayTechnology AS orderItems_phone_displayTechnology, " +
            "phones.backCameraMegapixels AS orderItems_phone_backCameraMegapixels, " +
            "phones.frontCameraMegapixels AS orderItems_phone_frontCameraMegapixels, " +
            "phones.ramGb AS orderItems_phone_ramGb, " +
            "phones.internalStorageGb AS orderItems_phone_internalStorageGb, " +
            "phones.batteryCapacityMah AS orderItems_phone_batteryCapacityMah, " +
            "phones.talkTimeHours AS orderItems_phone_talkTimeHours, " +
            "phones.standByTimeHours AS orderItems_phone_standByTimeHours, " +
            "phones.bluetooth AS orderItems_phone_bluetooth, " +
            "phones.positioning AS orderItems_phone_positioning, " +
            "phones.imageUrl AS orderItems_phone_imageUrl, " +
            "phones.description AS orderItems_phone_description, " +
            "colors.id AS orderItems_phone_colors_id, " +
            "colors.code AS orderItems_phone_colors_code " +
            "FROM (SELECT * FROM orders WHERE orders.secureId = ?) AS ordersFound " +
            "JOIN orderItems ON ordersFound.id = orderItems.orderId  " +
            "JOIN phones ON orderItems.phoneId = phones.id " +
            "JOIN phone2color ON phones.id = phone2color.phoneId " +
            "JOIN colors ON colors.id = phone2color.colorId";

    private static final String ORDERS_TABLE_NAME = "orders";
    private static final String ORDER_ITEMS_TABLE_NAME = "orderItems";
    private final ResultSetExtractor<List<Order>> resultSetExtractor = JdbcTemplateMapperFactory
            .newInstance().addKeys("secureId", "orderItems_phone_id")
            .newResultSetExtractor(Order.class);

    @Autowired
    CommonJdbcDaoUtils commonJdbcDaoUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    @Override
    public Optional<Order> get(String secureId) {
        List<Order> queryResult = jdbcTemplate.query(SELECT_ONE_BY_ID_SQL_QUERY, new Object[]{secureId}, resultSetExtractor);

        if (queryResult.size() > 1) {
            throw new IdUniquenessException(secureId, queryResult.size());
        }
        if (queryResult.size() == 0) {
            return Optional.empty();
        }

        Order resultOrder = queryResult.get(0);
        resultOrder.getOrderItems().forEach(orderItem -> orderItem.setOrder(resultOrder));
        return Optional.of(resultOrder);
    }

    @Transactional(rollbackFor = DataAccessException.class)
    @Override
    public void save(Order order) {
        boolean isEntityExist = commonJdbcDaoUtils.isEntityExist(ORDERS_TABLE_NAME,
                Map.of("secureId", order.getSecureId()));

        if (!isEntityExist) {
            insert(order);
        } else {
            throw new IdUniquenessException(order.getSecureId(), 1);
        }
    }

    private void insert(Order order) {
        saveOrder(order);
        saveOrderItems(order);
    }

    private void saveOrder(Order order) {
        Number newId = commonJdbcDaoUtils.insertAndReturnGeneratedKey("orders",
                new BeanPropertySqlParameterSource(order), "id");
        order.setId(newId.longValue());
    }

    private void saveOrderItems(Order order) {
        for (var orderItem : order.getOrderItems()) {
            var simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            simpleJdbcInsert.withTableName(ORDER_ITEMS_TABLE_NAME)
                    .execute(new MapSqlParameterSource()
                            .addValue("orderId", order.getId())
                            .addValue("phoneId", orderItem.getPhone().getId())
                            .addValue("quantity", orderItem.getQuantity())
                            .addValue("contactPhoneNo", order.getContactPhoneNo()));
        }
    }
}

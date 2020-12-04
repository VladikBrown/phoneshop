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

    private static final String SELECT_ONE_BY_SUBQUERY_WITH_UNIQUE_ID_SQL_QUERY = "SELECT ordersFound.id AS id, " +
            "secureId, firstName, lastName, deliveryAddress, contactPhoneNo, additionalInformation, " +
            "subtotal, deliveryPrice, totalPrice, orderStatus, orderDate," +
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
            "FROM ( %s ) AS ordersFound " +
            "JOIN orderItems ON ordersFound.id = orderItems.orderId  " +
            "JOIN phones ON orderItems.phoneId = phones.id " +
            "JOIN phone2color ON phones.id = phone2color.phoneId " +
            "JOIN colors ON colors.id = phone2color.colorId";

    private static final String SELECT_AlL_SQL_QUERY = "SELECT ordersFound.id AS id, " +
            "secureId, firstName, lastName, deliveryAddress, contactPhoneNo, additionalInformation, " +
            "subtotal, deliveryPrice, totalPrice, orderStatus, orderDate, " +
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
            "FROM orders AS ordersFound " +
            "JOIN orderItems ON ordersFound.id = orderItems.orderId  " +
            "JOIN phones ON orderItems.phoneId = phones.id " +
            "JOIN phone2color ON phones.id = phone2color.phoneId " +
            "JOIN colors ON colors.id = phone2color.colorId " +
            "ORDER BY ordersFound.id DESC";

    private static final String SELECT_BY_ORDER_NUMBER_SUQUERY = "SELECT * FROM orders WHERE orders.id = %d";

    private static final String SELECT_BY_SECURE_ID_SUQUERY = "SELECT * FROM orders WHERE orders.secureId = '%s'";

    private static final String ORDERS_TABLE_NAME = "orders";

    private static final String ORDER_ITEMS_TABLE_NAME = "orderItems";

    private final static String UPDATE_ORDER_STATUS_BY_ORDER_NUMBER = "UPDATE orders SET orderStatus = ? WHERE id = ?";

    private final ResultSetExtractor<List<Order>> orderResultSetExtractor = JdbcTemplateMapperFactory
            .newInstance().addKeys("secureId", "orderItems_phone_id")
            .newResultSetExtractor(Order.class);

    @Autowired
    CommonJdbcDaoUtils commonJdbcDaoUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    @Override
    public Optional<Order> getBySecureId(String secureId) {
        String subQuery = String.format(SELECT_BY_SECURE_ID_SUQUERY, secureId);
        String query = String.format(SELECT_ONE_BY_SUBQUERY_WITH_UNIQUE_ID_SQL_QUERY, subQuery);
        return getByUniqueField(query);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Order> getByOrderNumber(Long id) {
        String subQuery = String.format(SELECT_BY_ORDER_NUMBER_SUQUERY, id);
        String query = String.format(SELECT_ONE_BY_SUBQUERY_WITH_UNIQUE_ID_SQL_QUERY, subQuery);
        return getByUniqueField(query);
    }

    @Transactional(rollbackFor = DataAccessException.class)
    @Override
    public void save(Order order) {
        boolean isEntityExist = commonJdbcDaoUtils.isEntityExist(ORDERS_TABLE_NAME,
                Map.of("secureId", order.getSecureId()));

        if (isEntityExist) {
            update(order);
        } else {
            insert(order);
        }
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = jdbcTemplate.query(SELECT_AlL_SQL_QUERY, orderResultSetExtractor);
        orders.forEach(order -> order.getOrderItems()
                .forEach(orderItem -> orderItem.setOrder(order)));
        return orders;
    }

    private Optional<Order> getByUniqueField(String query) {
        List<Order> queryResult = jdbcTemplate.query(query, orderResultSetExtractor);

        if (queryResult.size() > 1) {
            throw new IdUniquenessException();
        }
        if (queryResult.size() == 0) {
            return Optional.empty();
        }

        Order resultOrder = queryResult.get(0);
        resultOrder.getOrderItems().forEach(orderItem -> orderItem.setOrder(resultOrder));
        return Optional.of(resultOrder);
    }

    private void update(Order order) {
        jdbcTemplate.update(UPDATE_ORDER_STATUS_BY_ORDER_NUMBER, order.getOrderStatus().toString(), order.getId());
    }

    private void insert(Order order) {
        saveOrder(order);
        saveOrderItems(order);
    }

    private void saveOrder(Order order) {
        var parameterSource = new BeanPropertySqlParameterSource(order);
        Number newId = commonJdbcDaoUtils.insertAndReturnGeneratedKey("orders",
                parameterSource, "id");
        order.setId(newId.longValue());
    }

    private void saveOrderItems(Order order) {
        for (var orderItem : order.getOrderItems()) {
            var simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            simpleJdbcInsert.withTableName(ORDER_ITEMS_TABLE_NAME)
                    .execute(new MapSqlParameterSource()
                            .addValue("orderId", order.getId())
                            .addValue("phoneId", orderItem.getPhone().getId())
                            .addValue("quantity", orderItem.getQuantity()));
        }
    }
}

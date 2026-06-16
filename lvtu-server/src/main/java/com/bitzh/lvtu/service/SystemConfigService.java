package com.bitzh.lvtu.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigService {

    public static final String MAX_REVISION_REQUEST_COUNT_KEY = "order.max_revision_request_count";
    public static final String ACCOUNT_CANCEL_COOLING_DAYS_KEY = "account.cancel_cooling_days";
    private static final int DEFAULT_MAX_REVISION_REQUEST_COUNT = 2;
    private static final int DEFAULT_ACCOUNT_CANCEL_COOLING_DAYS = 7;

    private final JdbcTemplate jdbcTemplate;

    public SystemConfigService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getMaxRevisionRequestCount() {
        return getInt(MAX_REVISION_REQUEST_COUNT_KEY, DEFAULT_MAX_REVISION_REQUEST_COUNT);
    }

    public void updateMaxRevisionRequestCount(int value) {
        if (value < 0 || value > 10) {
            throw new IllegalArgumentException("修改次数上限必须在0到10之间");
        }
        upsert(MAX_REVISION_REQUEST_COUNT_KEY, String.valueOf(value), "订单服务结果最多可申请修改次数");
    }

    public int getAccountCancelCoolingDays() {
        return getInt(ACCOUNT_CANCEL_COOLING_DAYS_KEY, DEFAULT_ACCOUNT_CANCEL_COOLING_DAYS);
    }

    public void updateAccountCancelCoolingDays(int value) {
        if (value < 1 || value > 30) {
            throw new IllegalArgumentException("账号注销冷静期必须在1到30天之间");
        }
        upsert(ACCOUNT_CANCEL_COOLING_DAYS_KEY, String.valueOf(value), "账号注销冷静期天数");
    }

    private int getInt(String key, int defaultValue) {
        try {
            String value = jdbcTemplate.queryForObject(
                    "SELECT config_value FROM system_config WHERE config_key = ?",
                    String.class,
                    key
            );
            return value == null ? defaultValue : Integer.parseInt(value);
        } catch (EmptyResultDataAccessException | NumberFormatException ex) {
            return defaultValue;
        } catch (DataAccessException ex) {
            return defaultValue;
        }
    }

    private void upsert(String key, String value, String description) {
        jdbcTemplate.update("""
                INSERT INTO system_config (config_key, config_value, description)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    config_value = VALUES(config_value),
                    description = VALUES(description),
                    updated_at = CURRENT_TIMESTAMP
                """, key, value, description);
    }
}

package com.fenglin.springboottest.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 轻量级幂等迁移：schema.sql 用的是 CREATE TABLE IF NOT EXISTS，无法给「已存在」的表补列。
 * 这里在启动后检查并在缺失时 ALTER，保证老库也能拿到新增字段（如 user.avatar）。
 */
@Component
public class SchemaInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SchemaInitializer.class);

    private final DataSource dataSource;

    public SchemaInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (Connection conn = dataSource.getConnection(); Statement st = conn.createStatement()) {
            ensureColumn(st, "user", "avatar",
                    "ALTER TABLE `user` ADD COLUMN `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像地址'");
        } catch (Exception e) {
            log.warn("SchemaInitializer 迁移失败（可忽略，若功能异常请手动补列）：{}", e.getMessage());
        }
    }

    private void ensureColumn(Statement st, String table, String column, String alterSql) throws Exception {
        String sql = "SELECT COUNT(*) FROM information_schema.COLUMNS "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = '" + table + "' "
                + "AND COLUMN_NAME = '" + column + "'";
        try (ResultSet rs = st.executeQuery(sql)) {
            boolean exists = rs.next() && rs.getInt(1) > 0;
            if (exists) {
                log.info("列 {}.{} 已存在，跳过", table, column);
                return;
            }
        }
        st.executeUpdate(alterSql);
        log.info("已为表 {} 新增列 {}", table, column);
    }
}

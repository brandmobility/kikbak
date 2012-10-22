package com.kikbak.db;

import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;


public class DBCPDataSourceFactory extends AbstractDataSourceFactory {
    @Override
    public DataSource createDataSource() {
        BasicDataSource ds = new BasicDataSource();

        ds.setDriverClassName(getDriverClassName());
        ds.setUrl(getUrl());
        ds.setUsername(getUsername());
        ds.setPassword(getPassword());
        ds.setDefaultAutoCommit(defaultAutoCommit);
        ds.setValidationQuery("SELECT 1");

        return ds;
    }
}
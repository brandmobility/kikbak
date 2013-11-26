package com.kikbak;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kikbak.config.ContextUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:WebContent/WEB-INF/applicationContext.xml",
        "file:WebContent/WEB-INF/kikbak-servlet.xml", "file:WebContent/WEB-INF/dao.xml",
        "file:WebContent/WEB-INF/dataSource.xml", "file:WebContent/WEB-INF/sessionFactory.xml" })
public class KikbakTest {

    static {
        try {
            // get the default test log4j properties
            Properties log4jProperties = new Properties();
            log4jProperties.load(new FileReader(new File("testcfg/log4j.properties")));

            Properties localProperties = new Properties();
            log4jProperties.load(new FileReader(new File("testcfg/local.properties")));
            PropertyConfigurator.configure(log4jProperties);
            PropertyConfigurator.configure(localProperties);

            final String dir = System.getProperty("user.dir");
            ContextUtil.setRealPath(dir + "/WebContent/");

        } catch (IOException e) {
            // Just ignore it
            e.printStackTrace();
        }
    }

    protected void setupDb(String dataset) throws Exception {
        setupDb(new ByteArrayInputStream(dataset.getBytes("UTF-8")));
    }

    protected void setupDbFromRes(String resource) throws Exception {
        String dbTestPath = ClassLoader.getSystemResource(resource).getPath();
        setupDb(new FileInputStream(dbTestPath));
    }

    protected void setupDb(InputStream stream) throws Exception {
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        IDataSet dataSet = builder.build(stream);
        Connection jdbcConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kikbak", "root", "root");
        IDatabaseConnection dbConnection = new DatabaseConnection(jdbcConnection);
        dbConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
    }

    @Test
    @Ignore
    public void stub() {
    }

}

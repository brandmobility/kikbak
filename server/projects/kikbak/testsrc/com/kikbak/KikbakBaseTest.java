package com.kikbak;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:WebContent/WEB-INF/applicationContext.xml",
        "file:WebContent/WEB-INF/kikbak-servlet.xml",
        "file:WebContent/WEB-INF/dao.xml", 
        "file:WebContent/WEB-INF/dataSource.xml",
        "file:WebContent/WEB-INF/sessionFactory.xml"})

public class KikbakBaseTest {
	
private static String DATA_SOURCE = "testdataset.xml";
    
	static {
		try {
			// get the default test log4j properties
			Properties log4jProperties = new Properties();
			log4jProperties.load(new FileReader(new File("testcfg/log4j.properties")));

			PropertyConfigurator.configure(log4jProperties);
		} catch (IOException e) {
			// Just ignore it
			e.printStackTrace();
		}		
	}
	
    @Before
    public void setUp() throws Exception {
    	
        String dbTestPath = ClassLoader.getSystemResource(DATA_SOURCE).getPath();
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        IDataSet dataSet = builder.build(new FileInputStream((dbTestPath)));

        Connection jdbcConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kikbak", "root", "root");
        IDatabaseConnection dbConnection = new DatabaseConnection(jdbcConnection);
        dbConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
        
    }
    
    @After
    public void tearDown() throws Exception {
    	
    }
    
	@Test
	public void stub() {
	}   


}

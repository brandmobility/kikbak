package com.kikbak.aps;


import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kikbak.config.ContextUtil;
import com.kikbak.dao.ReadOnlyDeviceTokenDAO;
import com.kikbak.dto.Devicetoken;
import com.kikbak.push.service.impl.ApsNotifierImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:WebContent/WEB-INF/applicationContext.xml",
        "file:WebContent/WEB-INF/kikbak-servlet.xml",
        "file:WebContent/WEB-INF/dao.xml", 
        "file:WebContent/WEB-INF/dataSource.xml",
        "file:WebContent/WEB-INF/sessionFactory.xml"})

public class PushTest {
	
	@Autowired
	ReadOnlyDeviceTokenDAO roDeviceTokenDao;
	
	@Test
	public void testPush(){
		final String dir = System.getProperty("user.dir");
        System.out.println("current dir = " + dir);
		ContextUtil.setRealPath(dir + "/WebContent/");
		
		Devicetoken token = roDeviceTokenDao.findByUserId(1L);
		
		ApsNotifierImpl notifier = new ApsNotifierImpl();
		try {
			notifier.sendNotification(token, "test alert");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(true){
			try {
			    Thread.sleep(1000);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
	}
}

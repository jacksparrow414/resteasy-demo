package com.demo.resteasy;

import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sound.midi.Soundbank;
import javax.sql.DataSource;

/**
 * @author jacksparrow414
 * @date 2021/11/27
 */
public class WebAppListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        try
        {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            DataSource ds = (DataSource) envCtx.lookup("jdbc/myLocalJPADB");
            EntityManagerFactory restEasy = Persistence.createEntityManagerFactory("restEasy");
            System.out.println("success");
        } catch (NamingException ex) {
            System.out.println("!!!! Got NamingException:");
            ex.printStackTrace(System.out);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}

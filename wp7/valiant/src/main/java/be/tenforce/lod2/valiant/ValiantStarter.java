package be.tenforce.lod2.valiant;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.log4j.Logger;

public class ValiantStarter {

  public static void main(String[] args) {
    ClassPathXmlApplicationContext classPathXmlApplicationContext = setup();
    Valiant valiant = (Valiant) classPathXmlApplicationContext.getBean("valiant");
    try {
        valiant.execute();
    } catch (Exception e) {
        // properly exit the application
        System.err.println("ERROR: " + e.getMessage());
        System.exit(1);
    } finally {
        classPathXmlApplicationContext.close();
    }
  }

  private static ClassPathXmlApplicationContext setup() {
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-configs/config.xml");
    applicationContext.registerShutdownHook();
    applicationContext.start();
    return applicationContext;
  }
}

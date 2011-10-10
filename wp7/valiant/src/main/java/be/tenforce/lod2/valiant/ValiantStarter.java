package be.tenforce.lod2.valiant;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ValiantStarter {

  public static void main(String[] args) {
    ClassPathXmlApplicationContext classPathXmlApplicationContext = setup();
    Valiant valiant = (Valiant) classPathXmlApplicationContext.getBean("valiant");
    valiant.execute();
    classPathXmlApplicationContext.close();
  }

  private static ClassPathXmlApplicationContext setup() {
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-configs/config.xml");
    applicationContext.registerShutdownHook();
    applicationContext.start();
    return applicationContext;
  }
}

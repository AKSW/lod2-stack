package be.tenforce.lod2.valiant;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ValiantStarter {

  public static void main(String[] args) {
    System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
    ClassPathXmlApplicationContext classPathXmlApplicationContext = setup();
    Valiant valiant = (Valiant) classPathXmlApplicationContext.getBean("valiant");
    valiant.execute(args);
    classPathXmlApplicationContext.close();
  }

  private static ClassPathXmlApplicationContext setup() {
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-configs/config.xml");
    applicationContext.registerShutdownHook();
    applicationContext.start();
    return applicationContext;
  }
}

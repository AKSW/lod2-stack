package be.tenforce.lod2.valiant;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ValiantStarter {

  public static void main(String[] args) {
    String mode = args[0];
    if(args[0].equals("saxon")){System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");}
    else if(args[0].equals("xalan")){System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");}
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

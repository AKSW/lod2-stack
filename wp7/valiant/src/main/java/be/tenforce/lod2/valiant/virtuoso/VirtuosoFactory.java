package be.tenforce.lod2.valiant.virtuoso;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: remy
 * Date: Sep 5, 2011
 * Time: 2:04:46 PM
 * To change this template use File | Settings | File Templates.
 */
public interface VirtuosoFactory {

  public void add(ByteArrayOutputStream baos, String fileName);

  public void addToGraph(ByteArrayOutputStream baos, String fileName, String graphName);

  //public void dropGraph(String fileName);

  //public void dropGraphSilent(String fileName);
}

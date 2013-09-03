package eu.lod2.convertor;

import eu.lod2.Graphs;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;

import java.io.IOException;

public class GraphsConverter extends AbstractHttpMessageConverter<Graphs> {

  // here should come a proper media type for json
  public GraphsConverter() {
    super(MediaType.APPLICATION_JSON);
  }

  @Override
  protected Graphs readInternal(Class<? extends Graphs> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
    return null;
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return Graphs.class.isAssignableFrom(clazz);
  }

  @Override
  protected void writeInternal(Graphs something, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {


    ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
    JsonFactory f = new JsonFactory();
    JsonGenerator g = f.createJsonGenerator(outputMessage.getBody());
    g.setCodec(mapper);
    g.writeObject(something);
   
  }
}

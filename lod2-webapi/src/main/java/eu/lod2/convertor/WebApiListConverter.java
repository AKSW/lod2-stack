package eu.lod2.convertor;

import eu.lod2.WebApiList;
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

public class WebApiListConverter extends AbstractHttpMessageConverter<WebApiList> {

  // here should come a proper media type for json
  public WebApiListConverter() {
    super(MediaType.APPLICATION_JSON);
  }

  @Override
  protected WebApiList readInternal(Class<? extends WebApiList> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
    return null;
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return WebApiList.class.isAssignableFrom(clazz);
  }

  @Override
  protected void writeInternal(WebApiList something, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {


    ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
    JsonFactory f = new JsonFactory();
    JsonGenerator g = f.createJsonGenerator(outputMessage.getBody());
    g.setCodec(mapper);
    g.writeObject(something);
   
  }
}

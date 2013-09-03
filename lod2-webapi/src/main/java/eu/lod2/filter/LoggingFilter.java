package eu.lod2.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author Pieter.Fannes
 */
public class LoggingFilter implements Filter {
  private Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

  private ServletContext servletContext;

  public void init(FilterConfig filterConfig) throws ServletException {
    servletContext = filterConfig.getServletContext();
    Assert.notNull(servletContext);


    if (logger == null) {
      logger = LoggerFactory.getLogger(LoggingFilter.class);
    }
  }

  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpReq = (HttpServletRequest) req;

    long startTime = System.currentTimeMillis();

    String remoteAddress = httpReq.getRemoteAddr();
    String remoteHost = httpReq.getRemoteHost();
    String remoteUser = httpReq.getRemoteUser();
    String reqURI = httpReq.getRequestURI();

    HttpSession session = httpReq.getSession();
    String sessionId = session != null ? session.getId() : "null";

    StringBuilder builder = new StringBuilder("Incoming request ")
      .append(" for resource ").append(reqURI).append("(");

    addParams(httpReq, builder);
    builder.append(")")
      .append("[session = ").append(sessionId)
      .append(" - remote addr = ").append(remoteAddress)
      .append(" - remote host = ").append(remoteHost)
      .append(" - remote user = ").append(remoteUser)
      .append("]");

    access(builder.toString());

    chain.doFilter(req, resp);

    builder = new StringBuilder("Accessed resource ")
      .append(reqURI).append(" and used ").append((System.currentTimeMillis() - startTime)).append(" ms");

    access(builder.toString());
  }

  private void addParams(HttpServletRequest httpReq, StringBuilder builder) {
    Enumeration paramEnum = httpReq.getParameterNames();
    if (paramEnum.hasMoreElements()) {
      String paramName = (String) paramEnum.nextElement();
      builder.append(paramName).append(" = '").append(httpReq.getParameter(paramName)).append("'");
    }
    //    else {
    //      builder.append("<none>");
    //    }
    while (paramEnum.hasMoreElements()) {
      String paramName = (String) paramEnum.nextElement();
      builder.append(", ").append(paramName).append(" = '").append(httpReq.getParameter(paramName)).append("'");
    }
  }

  public void destroy() {
  }

  private void access(String msg) {
    if (logger != null) {
      logger.info(msg);
    } else {
      servletContext.log(msg);
    }
  }

}

package uk.gov.hmpo.dropwizard.smartLogging.bundle;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.commons.validator.routines.RegexValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;

/**
 * Dropwizard filter to log all requests
 */
public class RequestLogBundle implements Bundle {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run(Environment environment) {

        String[] regexs = new String[]{"^/healthcheck$"};
        RegexValidator validator = new RegexValidator(regexs, true);

        environment.servlets().addFilter("Request logging filter", new Filter() {

            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
            }

            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain
                    filterChain) throws IOException, ServletException {

                long startTime = System.currentTimeMillis();
                filterChain.doFilter(servletRequest, servletResponse);

                HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
                if (!validator.isValid(httpRequest.getRequestURI())) {
                    long elapsed = System.currentTimeMillis() - startTime;

                    HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

                    logger.info("{} {} took {}ms and returned {}", httpRequest.getMethod(), httpRequest.getRequestURI(), elapsed, httpResponse.getStatus());
                }
            }

            @Override
            public void destroy() {
            }
        }).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }
}

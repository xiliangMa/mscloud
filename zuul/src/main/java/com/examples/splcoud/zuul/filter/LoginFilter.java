package com.examples.splcoud.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Create by $(xiliangMa) on 2019-07-15
 */
@Component
public class LoginFilter extends ZuulFilter {
    public String filterType() {
        return "pre";
    }

    public int filterOrder() {
        return 0;
    }

    public boolean shouldFilter() {
        return true;
    }

    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String token = request.getParameter("token");
        if (token == null) {
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(401);
            try {
                context.getResponse().getWriter().write("Token is enpty");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

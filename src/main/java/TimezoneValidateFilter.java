import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.TimeZone;

@WebFilter("/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String timezoneParam = req.getParameter("timezone");
        PrintWriter writer = res.getWriter();
        if (timezoneParam != null) {
            if (!isValidTimezone(timezoneParam)) {
                writer.println("Invalid timezone");
                res.setStatus(400);
                return;
            }
        }
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }

    private boolean isValidTimezone(String timezone) {
        try {
            TimeZone.getTimeZone(timezone);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

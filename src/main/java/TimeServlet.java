import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet(urlPatterns = "/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() {
        engine = new TemplateEngine();

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/html");

        String timezone = req.getParameter("timezone");
        LocalDateTime now;

        if (timezone == null || timezone.isEmpty()) {
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("lastTimezone")) {
                        timezone = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (timezone == null || timezone.isEmpty()) {
            now = LocalDateTime.now(ZoneId.of("UTC"));
            timezone = "UTC";
        } else {
            now = LocalDateTime.now(ZoneId.of(timezone));
        }

        String currentTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " " + timezone;

        Cookie cookie = new Cookie("lastTimezone", timezone);
        resp.addCookie(cookie);

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("currentTime", currentTime)
        );

        engine.process("test", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }
}


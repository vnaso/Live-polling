package cn.leancloud.demo.todo;

import cn.leancloud.demo.task.LivePolling;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@WebServlet(name = "TimeServlet", urlPatterns = {"/time"})
public class TimeServlet extends HttpServlet {

  private static final long serialVersionUID = 110533133254086356L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    LivePolling.poll();
    req.setAttribute("currentTime", new Date());
    req.getRequestDispatcher("/time.jsp").forward(req, resp);
  }
}

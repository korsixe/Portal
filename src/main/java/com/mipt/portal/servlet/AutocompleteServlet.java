package com.mipt.portal.servlet;

import com.mipt.portal.announcement.AdsService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import com.google.gson.Gson;

@WebServlet("/autocomplete")
public class AutocompleteServlet extends HttpServlet {
  private AdsService adsService = new AdsService();
  private Gson gson = new Gson();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    String query = request.getParameter("query");

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    if (query == null || query.length() < 2) {
      response.getWriter().write("[]");
      return;
    }

    try {
      List<String> suggestions = adsService.getSearchSuggestionsFromActiveAds(query);
      String json = gson.toJson(suggestions);
      response.getWriter().write(json);
    } catch (SQLException e) {
      response.getWriter().write("[]");
    }
  }
}
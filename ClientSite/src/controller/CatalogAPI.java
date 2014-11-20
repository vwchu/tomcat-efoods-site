package controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import model.catalog.*;
import model.common.*;

/**
 * Servlet implementation class CartAPI
 * Cart API Endpoint.
 */
//@WebServlet("/api/catalog")
public class CatalogAPI extends HttpServlet {

    private static final String
        JSP_FILE    = "/WEB-INF/xmlres/APIResponse.jspx";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String         jspFile = JSP_FILE;
        ServletContext sc      = getServletContext();
        String         type    = req.getParameter("type");
        Catalog        catalog = (Catalog)sc.getAttribute("catalog");
        StringWriter   sw      = new StringWriter();

        if (catalog == null) {
            sc.setAttribute("catalog", catalog = Catalog.getCatalog()); }

        try {
            if (type == null) {
                type = "itemlist";
            }
            switch (type) {
                case "itemlist":
                    ItemList items = catalog.getItemList(
                            req.getParameter("orderBy"),
                            req.getParameter("searchTerm"),
                            req.getParameter("category"),
                            req.getParameter("offset"),
                            req.getParameter("fetch"),
                            req.getParameter("minPrice"),
                            req.getParameter("maxPrice"));
                    req.setAttribute("items", items);
                    req.setAttribute("data", XMLUtil.generate(sw, items).toString());
                    break;
                case "item":
                    Item item = catalog.getItem(req.getParameter("number"));
                    req.setAttribute("item", item);
                    req.setAttribute("data", XMLUtil.generate(sw, item).toString());
                    break;
                case "catlist":
                    CategoryList categories = catalog.getCategoryList(
                            req.getParameter("orderBy"),
                            req.getParameter("searchTerm"),
                            req.getParameter("offset"),
                            req.getParameter("fetch"));
                    req.setAttribute("categories", categories);
                    req.setAttribute("data", XMLUtil.generate(sw, categories).toString());
                    break;
                case "category":
                    Category cat = catalog.getCategory(req.getParameter("id"));
                    req.setAttribute("category", cat);
                    req.setAttribute("data", XMLUtil.generate(sw, cat).toString());
                    break;
                default:
                    throw new RuntimeException("Bad action");
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }

        req.getRequestDispatcher(jspFile).forward(req, res);
    } // doGet

} // CartAPI

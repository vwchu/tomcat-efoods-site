package controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import model.cart.*;
import model.catalog.*;
import model.common.*;

/**
 * Servlet implementation class CartAPI Cart API Endpoint.
 */
// @WebServlet("/api/cart")
public class CartAPI extends HttpServlet {

    private static final String
        JSP_FILE = "/WEB-INF/xmlres/APIResponse.jspx"
      , XSL_FILE = "/WEB-INF/xmlres/CartElementDigest.xslt"
      ;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String         jspFile  = JSP_FILE;
        ServletContext sc       = getServletContext();
        HttpSession    sess     = req.getSession();
        String         action   = req.getParameter("action");
        String         number   = req.getParameter("number");
        String         quantity = req.getParameter("quantity");
        Catalog        catalog  = (Catalog)sc.getAttribute("catalog");
        Cart           cart     = (Cart)sess.getAttribute("cart");
        StringWriter   sw       = new StringWriter();
        File           xslt     = new File(sc.getRealPath(XSL_FILE));

        if (catalog == null) {
            sc.setAttribute("catalog", catalog = Catalog.getCatalog());}
        if (cart == null) {
            sess.setAttribute("cart", cart = new Cart());}

        try {

            // TODO: add variable for listener as analytics
            // Management wants to be able to determine the average time it
            // takes a client to add an item to the cart and the average time
            // between a fresh visit and checkout (in the same session).
            // Provide a mechanism by which these two averages can be viewed
            // in real time.

            if (action == null) {
                action = "list";
            }
            switch (action) {
                case "add":
                    cart.add(number);
                    req.setAttribute("status", "Successfully Added");
                    req.setAttribute("data", XMLUtil.generate(sw, cart, null, xslt).toString());
                    break;
                case "remove":
                    if (cart.hasElement(number)) {
                        cart.remove(number);
                        req.setAttribute("status", "Successfully Removed");
                        req.setAttribute( "data", XMLUtil.generate(sw, cart, null, xslt).toString());
                    } else {
                        req.setAttribute("status", "Nothing to remove");
                    }
                    break;
                case "bulk":
                    cart.bulkUpdate(number, quantity);
                    req.setAttribute("status", "Successfully Performed Bulk Update");
                    req.setAttribute( "data", XMLUtil.generate(sw, cart, null, xslt).toString());
                    break;
                case "list":
                    req.setAttribute("data", XMLUtil.generate(sw, cart).toString());
                    break;
                default:
                    throw new RuntimeException("Bad action");
            }
            req.setAttribute("cart", cart);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }

        req.getRequestDispatcher(jspFile).forward(req, res);
    } // doGet

} // CartAPI

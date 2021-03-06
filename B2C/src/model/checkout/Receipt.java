package model.checkout;

import java.util.*;
import java.text.*;
import javax.xml.bind.annotation.*;
import model.cart.*;
import model.account.*;

/**
 * A receipt for an purchase order.
 * This object is not the same as the
 * PO (purchase order) specified in the
 * requirements documentation, but has
 * all the data necessary for its output
 * to be transformed via a XSLT into a
 * document that match PO Schema.
 */
@XmlRootElement(name="order")
public class Receipt {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private int     orderId;
    private Date    submitDate;
    private Account customer;
    private Cart    cart;
    private String  url;

    public Receipt() { } // To make JAXB happy

    /**
     * Create a receipt object for a new
     * Purchase order. Only the CheckoutClerk
     * should be allowed to create a receipt.
     * Package level access only.
     *
     * @param  orderId  the order unique identifier
     * @param  customer the Account info object
     * @param  cart     the customer's shopping cart
     * @param  orderUri the URI to the PO XML output
     * @return          this new object
     */
    Receipt(int orderId, Account customer, Cart cart) {
        this.orderId    = orderId;
        this.submitDate = Calendar.getInstance().getTime(); // Date now
        this.customer   = customer;
        this.cart       = cart;
    }

    // Getters

    @XmlAttribute
    public int getOrderId() {
        return orderId;
    }

    @XmlAttribute
    public String getSubmitDate() {
        return SDF.format(submitDate).toString().split(" ")[0];
    }

    @XmlElement(name="customer")
    public Account getCustomer() {
        return customer;
    }

    @XmlElement(name="cart")
    public Cart getCart() {
        return cart;
    }

    @XmlAttribute
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

} // Receipt

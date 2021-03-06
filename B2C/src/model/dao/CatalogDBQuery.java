package model.dao;

import java.sql.*;
import model.catalog.*;

/**
 * Static utility class that provides the Catalog database DAO with methods that
 * enable it to generate SQL statements and prepare statements given a set of
 * filtering rules.
 */
class CatalogDBQuery {

    public static final String
        CATALOG_GET_CATEGORIES         = "SELECT C.* FROM Category C"
      , CATALOG_GET_ITEMS              = "SELECT I.* FROM Item I"
      , CATALOG_ITEM_BY_NUMBER         = "I.number = ?"
      , CATALOG_CATEGORY_BY_ID         = "C.id = ?"
      , CATALOG_FILTER_BY_SEARCH_ITEMS = "UPPER(I.name) LIKE UPPER(?)"
      , CATALOG_FILTER_BY_SEARCH_CAT   = "UPPER(C.name) LIKE UPPER(?)"
      , CATALOG_FILTER_BY_MIN_PRICE    = "I.price >= ?"
      , CATALOG_FILTER_BY_MAX_PRICE    = "I.price <= ?"
      , CATALOG_FILTER_BY_CATEGORY     = "I.catid IN (SELECT C.id FROM Category C WHERE C.id = ?)"
      , CATALOG_ITEM_ORDER_BY          = "ORDER BY I.${orderBy}"
      , CATALOG_CAT_ORDER_BY           = "ORDER BY C.${orderBy}"
      , CATALOG_PAGINATION_OFFSET      = "OFFSET ? ROWS"
      , CATALOG_PAGINATION_FETCH_LIMIT = "FETCH FIRST ? ROWS ONLY"
      , CATALOG_ITEM_MAX_PRICE         = "SELECT MAX(I.price) AS price FROM Item I"
      ;

    /**
     * Returns the WHERE keyword if the WHERE clause has not been used yet,
     * given boolean, else returns the conjunction AND keyword.
     *
     * @param where     flag for whether or not the WHERE clause has been used yet.
     * @return          " WHERE " if `where` is false, otherwise " AND ".
     */
    private static String whereConj(boolean where) {
        return where ? " AND " : " WHERE ";
    }

    /**
     * Generates and returns a SQL statement for querying for a list of Items
     * from the Item relation in the Catalog database, given a set of filtering
     * rules.
     *
     * @param filter    object defining rules to filtering and specifies
     *                  the format of the result data.
     * @return          the SQL statement generated
     */
    private static String generateGetItemsQuery(ItemFilter filter) {
        boolean w = false;
        String query = CATALOG_GET_ITEMS;

        if (filter == null) { return query; }

        if (filter.getSearchTerm() != null) {
            query += whereConj(w) + CATALOG_FILTER_BY_SEARCH_ITEMS; w = true; }
        if (filter.getCategory() >= 0) {
            query += whereConj(w) + CATALOG_FILTER_BY_CATEGORY; w = true; }
        if (filter.getMinPrice() >= 0) {
            query += whereConj(w) + CATALOG_FILTER_BY_MIN_PRICE; w = true; }
        if (filter.getMaxPrice() >= 0) {
            query += whereConj(w) + CATALOG_FILTER_BY_MAX_PRICE; w = true; }

        if (filter.getOrderBy() != null) {
            query += " " + CATALOG_ITEM_ORDER_BY.replace("${orderBy}", filter.getOrderBy()); }
        if (filter.getOffset() >= 0) {
            query += " " + CATALOG_PAGINATION_OFFSET; }
        if (filter.getFetch() >= 0) {
            query += " " + CATALOG_PAGINATION_FETCH_LIMIT; }

        return query;
    }

    /**
     * Populates and returns the given prepared statement for querying for a
     * list of Items from the Item relation in the Catalog database, given a set
     * of filtering rules.
     *
     * @param filter    object defining rules to filtering and specifies
     *                  the format of the result data.
     * @param ps        the PreparedStatement to populate
     * @return          the given PreparedStatement `ps`
     * @throws          SQLException
     */
    private static PreparedStatement prepareGetItemsQuery(ItemFilter filter,
            PreparedStatement ps) throws SQLException {
        int i = 1;

        if (filter == null) { return ps; }

        if (filter.getSearchTerm() != null) {
            ps.setString(i, "%" + filter.getSearchTerm() + "%"); i += 1; }
        if (filter.getCategory() >= 0) {
            ps.setInt(i, filter.getCategory()); i += 1; }
        if (filter.getMinPrice() >= 0) {
            ps.setDouble(i, filter.getMinPrice()); i += 1; }
        if (filter.getMaxPrice() >= 0) {
            ps.setDouble(i, filter.getMaxPrice()); i += 1; }

        if (filter.getOffset() >= 0) {
            ps.setInt(i, filter.getOffset()); i += 1; }
        if (filter.getFetch() >= 0) {
            ps.setInt(i, filter.getFetch()); i += 1; }

        return ps;
    }

    /**
     * Generates and returns a SQL statement for querying for a list of Item
     * Categories from the Category relation in the Catalog database, given a
     * set of filtering rules.
     *
     * @param filter    object defining rules to filtering and
     *                  specifies the format of the result data.
     * @return          the SQL statement generated
     */
    private static String generateGetCategoriesQuery(CategoryFilter filter) {
        boolean w = false;
        String query = CATALOG_GET_CATEGORIES;

        if (filter == null) { return query; }

        if (filter.getSearchTerm() != null) {
            query += whereConj(w) + CATALOG_FILTER_BY_SEARCH_CAT; w = true; }

        if (filter.getOrderBy() != null) {
            query += " " + CATALOG_CAT_ORDER_BY.replace("${orderBy}", filter.getOrderBy()); }
        if (filter.getOffset() >= 0) {
            query += " " + CATALOG_PAGINATION_OFFSET; }
        if (filter.getFetch() >= 0) {
            query += " " + CATALOG_PAGINATION_FETCH_LIMIT; }

        return query;
    }

    /**
     * Populates and returns the given prepared statement for querying for a
     * list of Categories from the Category relation in the Catalog
     * database, given a set of filtering rules.
     *
     * @param filter    object defining rules to filtering and
     *                  specifies the format of the result data.
     * @param ps        the PreparedStatement to populate
     * @return          the given PreparedStatement `ps`
     * @throws          SQLException
     */
    private static PreparedStatement prepareGetCategoriesQuery(
            CategoryFilter filter, PreparedStatement ps)
            throws SQLException {
        int i = 1;

        if (filter == null) { return ps; }

        if (filter.getSearchTerm() != null) {
            ps.setString(i, "%" + filter.getSearchTerm() + "%"); i += 1; }
        if (filter.getOffset() >= 0) {
            ps.setInt(i, filter.getOffset()); i += 1; }
        if (filter.getFetch() >= 0) {
            ps.setInt(i, filter.getFetch()); i += 1;}

        return ps;
    }

    // Execute SQL

    /**
     * Generates and prepares a query for a list of Items from the Item relation
     * in the Catalog database, given a set of filtering rules. Returns the
     * prepared statement.
     *
     * @param filter    object defining rules to filtering and
     *                  specifies the format of the result data.
     * @param conn      the database connection
     * @return          the Prepared Statement
     * @throws          SQLException
     */
    public static PreparedStatement
            getItems(ItemFilter filter, Connection conn) throws SQLException {
        String sql = generateGetItemsQuery(filter);
        PreparedStatement ps = conn.prepareStatement(sql);
        return prepareGetItemsQuery(filter, ps);
    }

    /**
     * Generates and prepares a query for a list of Categories from the
     * Category relation in the Catalog database, given a set of filtering
     * rules. Returns the prepared statement.
     *
     * @param filter    object defining rules to filtering and
     *                  specifies the format of the result data.
     * @param conn      the database connection
     * @return          the Prepared Statement
     * @throws          SQLException
     */
    public static PreparedStatement getCategories(CategoryFilter filter,
            Connection conn) throws SQLException {
        String sql = generateGetCategoriesQuery(filter);
        PreparedStatement ps = conn.prepareStatement(sql);
        return prepareGetCategoriesQuery(filter, ps);
    }

    /**
     * Generates and prepares a query for retrieving a specific Category from
     * the Category relation in the Catalog database, given the category's
     * unique identifier. Returns the Prepare Statement.
     *
     * @param id    the category's unique identifier
     * @param conn  the database connection
     * @return      the Prepared Statement
     * @throws      SQLException
     */
    public static PreparedStatement getCategory(int id, Connection conn)
            throws SQLException {
        String sql = CATALOG_GET_CATEGORIES + whereConj(false) + CATALOG_CATEGORY_BY_ID;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }

    /**
     * Generates and prepares a query for retrieving a specific Item from the
     * Item relation in the Catalog database, given the item's unique
     * identifier. Returns the Prepared Statement.
     *
     * @param number    the item's unique identifier
     * @param conn      the database connection
     * @return          the Prepared Statement
     * @throws          SQLException
     */
    public static PreparedStatement getItem(String number, Connection conn)
            throws SQLException {
        String sql = CATALOG_GET_ITEMS + whereConj(false) + CATALOG_ITEM_BY_NUMBER;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, number);
        return ps;
    }

    /**
     * Generates and prepares a query for retrieving the maximum price
     * of all items in the Item relation in the Catalog database.
     * Returns the Prepared Statement.
     *
     * @return          the Prepared Statement
     * @throws          SQLException
     */
    public static PreparedStatement getItemMaxPrice(Connection conn)
            throws SQLException {
        return conn.prepareStatement(CATALOG_ITEM_MAX_PRICE);
    }

} // CatalogDBQuery

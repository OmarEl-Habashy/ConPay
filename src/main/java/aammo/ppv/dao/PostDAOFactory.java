package aammo.ppv.dao;

public class PostDAOFactory {
    private static PostDAO postDAO;

    public static PostDAO getPostDAO() {
        if (postDAO == null) {
            postDAO = new JdbcPostDAO();
        }
        return postDAO;
    }
}
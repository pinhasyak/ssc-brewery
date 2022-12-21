package guru.sfg.brewery.domain.security;

public interface Authorities {
    public static final String BEER_CREATE = "beer.create";
    public static final String BEER_UPDATE = "beer.update";
    public static final String BEER_DELETE = "beer.delete";
    public static final String BEER_READ = "beer.read";

    public static final String BREWERY_CREATE = "brewery.create";
    public static final String BREWERY_UPDATE= "brewery.update";
    public static final String BREWERY_DELETE = "brewery.delete";
    public static final String BREWERY_READ = "brewery.read";

    public static final String CUSTOMER_CREATE = "customer.create";
    public static final String CUSTOMER_UPDATE= "customer.update";
    public static final String CUSTOMER_DELETE = "customer.delete";
    public static final String CUSTOMER_READ = "customer.read";

    public static final String ORDER_CREATE = "order.create";
    public static final String ORDER_UPDATE= "order.update";
    public static final String ORDER_DELETE = "order.delete";
    public static final String ORDER_READ = "order.read";

    public static final String ORDER_CREATE_CUSTOMER = "customer.order.create";
    public static final String ORDER_UPDATE_CUSTOMER= "customer.order.update";
    public static final String ORDER_DELETE_CUSTOMER = "customer.order.delete";
    public static final String ORDER_READ_CUSTOMER = "customer.order.read";
}

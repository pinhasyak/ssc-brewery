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
}

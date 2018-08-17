package helpers;

public class ProductBatch {
    public String product_id;
    public String product_title;

    @Override
    public String toString() {
        return product_title + product_id;
    }
}

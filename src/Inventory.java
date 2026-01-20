import java.util.ArrayList;
import java.util.List;

public class Inventory {
    List<Product> products = new ArrayList<>();
     public void addProduct(Product product) {
        products.add(product);
     }

     public void updateProduct(){} //needs implementation

    public Product  searchProduct(String name){
        for(Product product : products){
            if(product.name.equals(name)){
                return product;
            }
        }
        return null;
    }
    public void deleteProduct(String name){
        products.remove(searchProduct(name));
    }

    public List<Product> getLowStockProducts() {
        List<Product> lowStockProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.stock < 5) {
                lowStockProducts.add(product);
            }
        }
        return lowStockProducts;
    }

    public List<Product> getAllProducts() {
        return products;
    }
}

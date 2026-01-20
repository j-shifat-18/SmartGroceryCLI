public class Product {
    String name;
    String category;
    double price;
    int stock;

    public Product(String name, String category, double price, int stock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public void updateStock(int amount) {
        this.stock += amount;
    }

    public void updatePrice(double newPrice) {
        this.price = newPrice;
    }
}

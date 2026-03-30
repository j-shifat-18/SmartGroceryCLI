# SmartGrocery CLI

A Java-based command-line grocery management system with role-based access, inventory control, shopping cart, and sales analytics.

---

## Features

**Authentication & Security**
- SHA-256 password hashing with cryptographic salt
- Role-based access control (Admin / Customer)
- Automatic migration of legacy plain-text passwords
- Activity logging and failed login tracking

**Inventory Management** *(Admin)*
- Hierarchical product structure: Category → Product → Company variant
- Full CRUD for products, categories, and companies
- Stock and price management
- Smart low-stock detection based on 7-day sales velocity

**Shopping** *(Customer)*
- Category-based product browsing with variant comparison
- Product search with direct add-to-cart
- Cart management and checkout
- Itemized receipts saved to file
- Full purchase history with receipt lookup

**Analytics & Reports** *(Admin)*
- Most/least sold items
- Sales by category and top revenue products
- Daily and weekly sales reports
- Out-of-stock and low-stock alerts with urgency levels

**Recommendations** *(Customer)*
- Best sellers, recently popular, value for money
- History-based and budget-friendly suggestions
- Limited stock / hurry deals

---

## Project Structure

```
SmartGroceryCLI/
├── code/
│   ├── src/
│   │   ├── App.java
│   │   └── com/smartgrocery/
│   │       ├── auth/          # Authentication & roles
│   │       ├── engine/        # Analytics & recommendation engine
│   │       ├── inventory/     # Inventory management
│   │       ├── models/        # Domain models (Product, User, Purchase, ...)
│   │       ├── shopping/      # Cart & checkout
│   │       ├── storage/       # File-based persistence
│   │       ├── ui/            # CLI interface (Admin, Customer, Auth views)
│   │       └── utils/         # Password hashing, logging, reporting
│   ├── bin/                   # Compiled .class files
│   └── data/
│       ├── inventory/         # products.txt, categories.txt, companies.txt
│       ├── users/             # users.txt
│       ├── transactions/      # purchases.txt
│       ├── receipts/          # Per-transaction receipt files
│       └── logs/              # activity.log
└── doc/                       # Project reports and presentations
```

---

## Requirements

- Java 11 or higher (tested on OpenJDK 25)

---

## Getting Started

**1. Clone the repository**
```bash
git clone https://github.com/j-shifat-18/SmartGroceryCLI.git
cd SmartGroceryCLI
```

**2. Compile**
```bash
find code/src -name "*.java" | xargs javac -d code/bin -cp code/src
```

**3. Run** (must run from the `code/` directory so data paths resolve correctly)
```bash
cd code
java -cp bin com.smartgrocery.ui.CLI
```

---

## Default Credentials

| Username | Password   | Role     |
|----------|------------|----------|
| admin    | admin123   | Admin    |
| customer | pass123    | Customer |

> Passwords are stored as salted SHA-256 hashes. Plain-text passwords are automatically migrated on first login.

---

## Usage

**Admin menu**
```
1. Manage Inventory   → Add/update/remove products, categories, companies
2. Manage Users       → View users, change roles
3. View Reports       → Sales analytics, low stock alerts, revenue reports
4. Logout
```

**Customer menu**
```
1. Browse Products    → Navigate by category, compare variants, add to cart
2. Search Products    → Find by name, add to cart directly
3. View Cart          → Review items, checkout, clear cart
4. View Recommendations → Best sellers, budget picks, history-based suggestions
5. View History       → Past purchases with detailed receipt viewer
6. Logout
```

---

## Data Storage

All data is stored as plain-text files under `code/data/`:

| File | Contents |
|------|----------|
| `users/users.txt` | `username,hashedPassword,ROLE` |
| `inventory/products.txt` | `id,name,categoryId,companyId,price,stock` |
| `inventory/categories.txt` | `id,name,unitType` |
| `inventory/companies.txt` | `id,name` |
| `transactions/purchases.txt` | `receiptId;username;timestamp;total;items` |
| `receipts/RCP-*.txt` | Formatted receipt per transaction |
| `logs/activity.log` | Timestamped audit trail |

---

## Password Policy

- Minimum 8 characters
- At least one uppercase letter

---

## License

This project is for educational purposes.

---

*Built by the SmartGrocery team*

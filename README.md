# SmartGroceryCLI Management System

A comprehensive, enterprise-grade grocery management system built with Java, featuring modular architecture, advanced security, and intuitive command-line interface.

## 🚀 Features

### 🔐 **Security & Authentication**
- **Advanced Password Security**: SHA-256 hashing with cryptographic salt
- **Role-Based Access Control**: Admin and Customer user roles
- **Activity Logging**: Comprehensive audit trail for all user activities
- **Automatic Migration**: Seamless upgrade from legacy plain-text passwords
- **Failed Login Tracking**: Security monitoring and breach detection

### 📊 **Inventory Management**
- **Hierarchical Product Display**: Category → Product → Company variant structure
- **Complete CRUD Operations**: Create, Read, Update, Delete products
- **Advanced Search**: Product search by name or ID with partial matching
- **Stock Management**: Flexible stock operations (set, add, remove)
- **Price Management**: Dynamic pricing with update history
- **Low Stock Alerts**: Automated inventory monitoring
- **Category & Company Management**: Organized product classification

### 🛒 **Shopping Experience**
- **Intelligent Cart System**: Multi-product cart with quantity management
- **Product Browsing**: Category-based navigation with variant comparison
- **Search & Purchase**: Direct add-to-cart from search results
- **Detailed Receipts**: Unit prices, quantities, and subtotals
- **Purchase History**: Complete transaction tracking
- **Smart Recommendations**: History-based and budget-friendly suggestions

### 📈 **Analytics & Reporting**
- **Sales Analytics**: Most popular products and category revenue
- **User Behavior Tracking**: Purchase patterns and preferences
- **Inventory Reports**: Stock levels and movement analysis
- **Administrative Dashboard**: Comprehensive system overview

## 🏗️ Architecture

### **Modular Design Pattern**
The system follows clean architecture principles with clear separation of concerns:

```
src/com/SmartGroceryCLI/
├── 🎯 models/           # Domain entities and data structures
├── 🔐 auth/             # Authentication and authorization
├── 📦 inventory/        # Inventory management logic
├── 🛒 shopping/         # Cart and checkout functionality
├── 🧠 engine/           # Analytics and recommendation algorithms
├── 💾 storage/          # Data persistence layer
├── 🔧 utils/            # Security and logging utilities
└── 🖥️ ui/               # Modular user interface components
    ├── CLI.java              # Application entry point (80 lines)
    ├── UIContext.java        # Shared context container (40 lines)
    ├── BaseUI.java           # Common UI utilities (50 lines)
    ├── AuthUI.java           # Authentication interface (60 lines)
    ├── AdminUI.java          # Administrative operations (90 lines)
    ├── InventoryUI.java      # Inventory management (300 lines)
    └── CustomerUI.java       # Customer shopping interface (280 lines)
```

### **Data Storage**
```
data/
├── 👥 users/            # User credentials and profiles
├── 📦 inventory/        # Products, categories, and companies
├── 💳 transactions/     # Purchase history and receipts
└── 📋 logs/             # Security and activity audit trails
```

## 🛠️ Technical Specifications

### **Core Technologies**
- **Language**: Java 21+ with modern features
- **Architecture**: Modular monolith with clean separation
- **Security**: SHA-256 cryptographic hashing
- **Storage**: File-based persistence with atomic operations
- **UI**: Advanced command-line interface with input validation

### **Security Features**
- **Password Hashing**: Industry-standard SHA-256 with random salt
- **Session Management**: Secure user context handling
- **Audit Logging**: Comprehensive activity tracking
- **Input Validation**: SQL injection and XSS prevention
- **Error Handling**: Secure error messages without information leakage

### **Performance Optimizations**
- **Efficient Search**: Optimized product search algorithms
- **Memory Management**: Minimal memory footprint
- **File I/O**: Buffered operations for improved performance

## 🚀 Quick Start

### **Prerequisites**
- Java Development Kit (JDK) 21 or higher
- Command-line terminal
- Minimum 512MB RAM
- 50MB available disk space

### **Installation**

1. **Clone the Repository**
   ```bash
   git clone https://github.com/j-shifat-18/SmartGroceryCLI.git
   cd SmartGroceryCLI
   ```

2. **Compile the Application**
   ```bash
   # Compile all source files
   find src -name "*.java" -exec javac -cp src {} +
   
   # Alternative: Use provided build script
   ./build.sh
   ```

3. **Run the Application**
   ```bash
   java -cp src com.smartgrocery.ui.CLI
   ```

### **Default Credentials**
- **Administrator**: `admin` / `admin123`
- **Customer**: `customer` / `pass123`

## 📖 User Guide

### **For Administrators**

#### **Inventory Management**
```bash
Admin Menu → Manage Inventory
├── View All Products      # Hierarchical product display
├── Add Product           # Create new products
├── Update Product        # Modify price/stock
├── Manage Categories     # Product categorization
├── Manage Companies      # Supplier management
├── Remove Product        # Delete products
└── Search Product        # Find products by name/ID
```

#### **User Management**
```bash
Admin Menu → Manage Users
├── View All Users        # User list with roles
└── Update User Role      # Change user permissions
```

#### **Analytics & Reports**
```bash
Admin Menu → View Reports
├── Most Bought Items     # Popular product analysis
├── Category Revenue      # Sales by category
└── Low Stock Alerts      # Inventory warnings
```

### **For Customers**

#### **Shopping Experience**
```bash
Customer Menu
├── Browse Products       # Category-based browsing
├── Search Products       # Find and add to cart
├── View Cart            # Cart management and checkout
├── View Recommendations # Personalized suggestions
└── View History         # Purchase history
```

## 🔧 Configuration

### **System Settings**
- **Password Requirements**: 8+ characters, 1+ uppercase letter
- **Session Timeout**: No automatic timeout (manual logout required)
- **Log Retention**: Unlimited (manual cleanup required)

### **File Locations**
- **Application Data**: `./data/`
- **Activity Logs**: `./data/logs/activity.log`
- **User Database**: `./data/users/users.txt`
- **Product Catalog**: `./data/inventory/products.txt`

## 🧪 Testing

### **Unit Testing**
```bash
# Run comprehensive test suite
./run-tests.sh

# Test specific components
java -cp src:test com.smartgrocery.test.InventoryTest
java -cp src:test com.smartgrocery.test.AuthenticationTest
```

### **Integration Testing**
```bash
# Test complete user workflows
./test-workflows.sh

# Manual testing scenarios
java -cp src com.smartgrocery.ui.CLI
```

### **Security Testing**
- Password strength validation
- SQL injection prevention
- Authentication bypass attempts
- Activity logging verification

## 📊 Performance Metrics

### **Scalability**
- **Products**: Tested with 10,000+ products
- **Users**: Supports unlimited user accounts
- **Transactions**: Handles large transaction histories
- **Categories**: Unlimited hierarchical categories

## 🤝 Contributing

### **Development Setup**
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Follow coding standards and add tests
4. Commit changes: `git commit -m 'Add amazing feature'`
5. Push to branch: `git push origin feature/amazing-feature`
6. Open a Pull Request

### **Coding Standards**
- **Java Style**: Follow Oracle Java conventions
- **Documentation**: Comprehensive JavaDoc comments
- **Testing**: Unit tests for all new features
- **Security**: Security review for authentication changes

## 📋 Roadmap

### **Version 2.0 (Planned)**
- [ ] Web-based user interface
- [ ] Database integration (PostgreSQL/MySQL)
- [ ] Multi-tenant support
- [ ] REST API endpoints
- [ ] Real-time notifications
- [ ] Advanced analytics dashboard

## 🙏 Acknowledgments

- **Java Community**: For excellent documentation and libraries
- **Security Experts**: For cryptographic best practices guidance
- **Open Source Contributors**: For inspiration and code examples
- **Beta Testers**: For valuable feedback and bug reports

## 📞 Support

### **Documentation**
- [User Manual](docs/USER_MANUAL.md)
- [API Documentation](docs/API.md)
- [Security Guide](SECURITY_FEATURES.md)
- [Architecture Overview](MODULAR_ARCHITECTURE.md)

### **Community**
- **Email**: info.jahirulsifat@gmail.com

---

**SmartGroceryCLI Management System** - Transforming grocery operations with intelligent technology.

*Built with ❤️ by the SmartGroceryCLI Team*

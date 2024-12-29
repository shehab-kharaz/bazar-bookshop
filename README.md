## Bazar-Bookshop: A Minimalistic Multi-Tier Online Bookstore

**Bazar-Bookshop** is a lightweight, multi-tier online bookstore designed with a two-tier web architecture consisting of:  

1. **Frontend Tier**: A microservice responsible for handling user requests and initial processing.  
2. **Backend Tier**: Two distinct microservices:  
   - **Catalog Server**: Manages the book inventory, including stock levels, pricing, and categorization.  
   - **Order Server**: Handles purchase orders, ensuring availability and updating inventory.  

The system is implemented using the **Java Spark Framework** for developing RESTful APIs. All components communicate seamlessly using **HTTP RESTful APIs**, and the entire architecture is fully containerized using **Docker**, with each service running independently in its own container, ensuring portability and distributed deployment.

---

### System Operations

#### Frontend Tier:
1. **`search(topic)`**: Retrieves a list of books under the specified category.  
2. **`info(item_number)`**: Provides detailed information about a specific book, including availability and price.  
3. **`purchase(item_number)`**: Facilitates the purchase of a book by interacting with the backend services.  

#### Catalog Server:
1. **`query-by-subject(topic)`**: Returns all books within a specified category.  
2. **`query-by-item(item_number)`**: Fetches detailed information about a specific book.  
3. **`update(item_number)`**: Allows adjustments to book pricing and stock levels.  

#### Order Server:
1. **`purchase(item_number)`**: Processes a book purchase by verifying availability through the catalog server, decrementing stock levels, and logging the transaction.  

---


The **Bazar-Bookshop** project exemplifies a well-structured, multi-tier application leveraging the **Java Spark Framework** and containerized architecture. Its modular design ensures scalability, reliability, and ease of deployment.



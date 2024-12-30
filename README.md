
# Employee Metrics API


## Technologies Used

- **Spring Boot**: Framework to create REST APIs.
- **Elasticsearch**: Search engine and data aggregation tool.
- **Java**: Programming language for application development.

---

## Installation

### **Prerequisites**
1. **Java** version 11 or later.
2. **Elasticsearch** version 6.x (as per the implementation).
3. **Maven** for dependency management.

### **Steps**
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd <repository-folder>
   ```
2. Build the application using Maven:
   ```bash
   mvn clean install
   ```
3. Run Elasticsearch on localhost at port 9200.
4. Start the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

The application will run at [http://localhost:8080](http://localhost:8080).

---

## Elasticsearch Indexing Instructions and Data Loading

Before running the application, ensure you have created an Elasticsearch index and loaded employee data using the following steps:

### **1. Create Elasticsearch Index**

Use the following command to create the `companydatabase` index in Elasticsearch:

```bash
curl -XPUT 'http://127.0.0.1:9200/companydatabase?pretty' -H 'Content-Type: application/json' -d '{
    "settings": {
        "analysis": {
            "analyzer": {
                "comma_analyzer": {
                    "type": "custom",
                    "tokenizer": "comma_pattern",
                    "filter": ["lowercase"]
                }
            },
            "tokenizer": {
                "comma_pattern": {
                    "type": "pattern",
                    "pattern": ",\s*"
                }
            }
        }
    },
    "mappings": {
        "employees": {
            "properties": {
                "FirstName": { "type": "text" },
                "LastName": { "type": "text" },
                "Designation": {
                    "type": "text",
                    "fields": {
                        "keyword": { "type": "keyword" }
                    }
                },
                "Salary": { "type": "integer" },
                "DateOfJoining": {
                    "type": "date",
                    "format": "yyyy-MM-dd"
                },
                "Address": { "type": "text" },
                "Gender": {
                    "type": "text",
                    "fields": {
                        "keyword": { "type": "keyword" }
                    }
                },
                "Age": { "type": "integer" },
                "MaritalStatus": {
                    "type": "text",
                    "fields": {
                        "keyword": { "type": "keyword" }
                    }
                },
                "Interests": {
                    "type": "text",
                    "analyzer": "comma_analyzer",
                    "fields": {
                        "keyword": {
                            "type": "keyword"
                        }
                    }
                }
            }
        }
    }
}'
```

### **2. Load Data into Elasticsearch**

Ensure you have a file named **`Employees50K.json`** containing employee data in Elasticsearch bulk format. Use the following command to load the data into Elasticsearch:

```bash
curl -XPOST "http://127.0.0.1:9200/companydatabase/_bulk" --header "Content-Type: application/json" --data-binary "@/Users/Employees50K.json"
```

### **3. Verify Data**

After loading the data, you can verify that the data has been loaded into Elasticsearch using the following command:

```bash
curl -XGET 'http://127.0.0.1:9200/companydatabase/_search?pretty'
```

---

## API Endpoints

Below is the list of available endpoints:

### **1. Employee Count**
- **Endpoint**: `/api/employees/count`
- **Method**: `GET`
- **Description**: Returns the total number of documents in the index.
- **Sample Response**:
  ```json
  1250
  ```

### **2. Average Salary**
- **Endpoint**: `/api/employees/average-salary`
- **Method**: `GET`
- **Description**: Returns the average salary of employees.
- **Sample Response**:
  ```json
  {
    "average_salary": 55000.75
  }
  ```

### **3. Minimum and Maximum Salary**
- **Endpoint**: `/api/employees/min-max-salary`
- **Method**: `GET`
- **Description**: Returns the minimum and maximum salary of employees.
- **Sample Response**:
  ```json
  {
    "min_salary": 20000,
    "max_salary": 120000
  }
  ```

### **4. Age Distribution**
- **Endpoint**: `/api/employees/age-distribution`
- **Method**: `GET`
- **Description**: Returns the distribution of employees by age.
- **Sample Response**:
  ```json
  {
    "20-25": 50,
    "25-30": 120,
    "30-35": 200
  }
  ```

### **5. Gender Distribution**
- **Endpoint**: `/api/employees/gender-distribution`
- **Method**: `GET`
- **Description**: Returns the distribution of employees by gender.
- **Sample Response**:
  ```json
  {
    "Male": 700,
    "Female": 550
  }
  ```

### **6. Marital Status Distribution**
- **Endpoint**: `/api/employees/marital-status-distribution`
- **Method**: `GET`
- **Description**: Returns the distribution of employees by marital status.
- **Sample Response**:
  ```json
  {
    "Married": 800,
    "Single": 450
  }
  ```

### **7. Designation Distribution**
- **Endpoint**: `/api/employees/designation-distribution`
- **Method**: `GET`
- **Description**: Returns the distribution of employees by designation.
- **Sample Response**:
  ```json
  {
    "Manager": 300,
    "Engineer": 500,
    "HR": 150
  }
  ```

### **8. Date of Joining Histogram**
- **Endpoint**: `/api/employees/date-of-joining-histogram`
- **Method**: `GET`
- **Description**: Returns the histogram distribution of employees by their year of joining.
- **Sample Response**:
  ```json
  {
    "2010": 50,
    "2015": 200,
    "2020": 300
  }
  ```

### **9. Top 10 Interests**
- **Endpoint**: `/api/employees/top-interests`
- **Method**: `GET`
- **Description**: Returns the top 10 most common interests among employees.
- **Sample Response**:
  ```json
  {
    "Reading": 200,
    "Traveling": 150,
    "Coding": 100
  }
  ```


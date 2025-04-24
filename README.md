
# 📦 Repsy - Package Storage System

**Repsy** is a modular package storage system built with **Spring Boot**, supporting multiple storage strategies: local file system and object storage (via **MinIO**). This system allows uploading and downloading of `.rep` packages along with their `.json` metadata, organized by package name and version.

## 🚀 Features

-   Upload packages via REST API (`.rep` + `.json`)
    
-   Download specific files of a package
    
-   Choose between **object-storage (MinIO)** and **file-system** storage strategies dynamically via configuration
    
-   Ensures:
    
    -   File validation
        
    -   Package uniqueness by name-version-storageType
        
    -   Metadata consistency

## 🧠 Storage Strategy

### Interface

```java
public  interface  StorageStrategy { void  saveFile(String packageName, String version, MultipartFile file, String fileName);
    Resource loadFile(String packageName, String version, String fileName);
}
```

### Implementations

-   `ObjectStorageStrategy` (uses **MinIO**)
    
-   `FileSystemStorageStrategy` (saves files to disk)
    

The strategy is injected at runtime based on the `STORAGE_STRATEGY` environment variable.

## 📁 API Endpoints

### Deploy a Package

```http
POST /api/{packageName}/{version}
```

**Form-Data Body:**

-   `package` → `.rep` file (Max size: 50MB)
    
-   `meta` → `.json` metadata file
    

### Download a File


```http
GET /api/{packageName}/{version}/{fileName}
```

Returns the specified file if it exists.

## 🔒 Validation & Error Handling

-   Rejects empty or invalid file formats
    
-   Validates file size and structure
    
-   Prevents overwriting existing packages with the same name/version/storage
    
-   Fails gracefully with meaningful error messages

## 🐳 Docker Setup

This project uses `docker-compose` to orchestrate the following services:

### Services

-   **PostgreSQL**: Stores metadata and package information.
    
-   **MinIO**: Provides S3-compatible object storage.
    
-   **Repsy**: Spring Boot application for handling package upload/download operations.

### Example `docker-compose.yml`

```yaml
services:
  postgres:
    image: postgres:15
    container_name: postgres
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres-data:/var/lib/postgresql/data
    networks:
      - repsy-net

  minio:
    image: minio/minio
    container_name: minio
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: ${MINIO_ACCESS_KEY}
      MINIO_ROOT_PASSWORD: ${MINIO_SECRET_KEY}
    command: server /data --console-address ":9001"
    volumes:
      - minio-data:/data
    networks:
      - repsy-net

  repsy:
    image: repsy:latest
    container_name: repsy
    ports:
      - "8081:8081"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${POSTGRES_DB}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
      MINIO_ACCESS_KEY: ${MINIO_ACCESS_KEY}
      MINIO_SECRET_KEY: ${MINIO_SECRET_KEY}
      REPSY_USERNAME: ${REPSY_USERNAME}
      REPSY_PASSWORD: ${REPSY_PASSWORD}
      STORAGE_STRATEGY: object-storage
    volumes:
      - ./repsy_storage:/storage/repsy_storage
    networks:
      - repsy-net
    depends_on:
      - postgres
      - minio

networks:
  repsy-net:

volumes:
  postgres-data:
  minio-data:
```




## ⚙️ Environment Configuration

### Repsy Service Environment Variables

You need to configure the following environment variables for the Repsy service. These variables are essential for connecting to the PostgreSQL database and MinIO object storage, as well as setting up the basic authentication for the API.


| **Variable**                     | **Description**                                                |
|-----------------------------------|----------------------------------------------------------------|
| `SPRING_DATASOURCE_URL`           | PostgreSQL database connection URL                            |
| `SPRING_DATASOURCE_USERNAME`      | Database username                                              |
| `SPRING_DATASOURCE_PASSWORD`      | Database password                                              |
| `MINIO_ACCESS_KEY`                | MinIO root user                                                |
| `MINIO_SECRET_KEY`                | MinIO root password                                            |
| `REPSY_USERNAME`                  | Basic auth username for the Repsy API                          |
| `REPSY_PASSWORD`                  | Basic auth password for the Repsy API                          |
| `STORAGE_STRATEGY`                | Choose between `object-storage` (MinIO) or `file-system`       |





## 🛠 Installation

To get started with Repsy, follow these steps:

### Prerequisites

Ensure you have the following tools installed:

-   Docker
    
-   Docker Compose
    
-   Git
    

### Steps

1.  Clone the repository:
    



```bash
git clone https://github.com/furkanates07/repsy-assignment.git
```  

2.  Navigate to the project directory:
    
```bash
cd repsy-assignment

```  
3.  Open the `docker-compose.yml` file and replace the following environment variable placeholders with your actual values:
    


```yaml
environment:
  POSTGRES_DB: your_database_name
  POSTGRES_USER: your_postgres_user
  POSTGRES_PASSWORD: your_postgres_password
  MINIO_ACCESS_KEY: your_minio_access_key
  MINIO_SECRET_KEY: your_minio_secret_key
  REPSY_USERNAME: your_repsy_username
  REPSY_PASSWORD: your_repsy_password
  STORAGE_STRATEGY: object-storage # or file-system

```  

4.  Run the Docker containers:
    


```bash
docker-compose up --build
```


This will build and start all the services: **PostgreSQL**, **MinIO**, and **Repsy** (Spring Boot application).
## 🧪 Testing the API

You can test the endpoints using [Postman](https://www.postman.com/) or `curl`:


```bash
curl -X POST http://localhost:8081/api/my-package/1.0.0 \
  -F "package=@./my-package.rep" \
  -F "meta=@./meta.json"
```

### Example meta.json

```bash
{
  "name": "mypackage",
  "version": "1.0.0",
  "author": "John Doe",
  "dependencies": [
    {
      "package": "even",
      "version": "3.4.7"
    },
    {
      "package": "math",
      "version": "4.2.8"
    },
    {
      "package": "std",
      "version": "1.2.0"
    },
    {
      "package": "repsy",
      "version": "1.0.3"
    },
    {
      "package": "client",
      "version": "3.3.0"
    }
  ]
}
```

## 📦 Volumes & Persistence

-   `postgres-data`: Persists PostgreSQL data
    
-   `minio-data`: Persists MinIO object storage
    
-   `./repsy_storage`: Mount path for file system storage (if enabled)
    

----------

## 🛠 Technologies

-   **Java 17**, **Spring Boot**
    
-   **PostgreSQL**
    
-   **MinIO**
    
-   **Docker**, **Docker Compose**

## Contributing

Feel free to submit issues or pull requests if you find any bugs or want to add new features.

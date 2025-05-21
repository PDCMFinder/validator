# PDCMFinder Validator

A Java-based microservice for validating data templates submitted to the PDX Finder platform. This service ensures that incoming data conforms to the required formats and standards, facilitating accurate integration into the PDX Finder database.

## 🔍 Overview

The validator microservice is a component of the PDX Finder project, which aggregates clinical, genomic, and functional data from patient-derived xenograft (PDX) models to support cancer research. By validating data templates, this service helps maintain data quality and consistency across the platform.

## 📁 Project Structure

- `src/`: Contains the main Java source code for the validator service.
- `k8-deploy/`: Kubernetes deployment configurations.
- `local-deploy/`: Scripts and configurations for local deployment.
- `.gitlab-ci.yml` and `.travis.yml`: CI/CD pipeline configurations.
- `pom.xml`: Maven project configuration file.

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Maven 3.6 or higher

### Building the Project

```bash
git clone https://github.com/PDCMFinder/validator.git
cd validator
mvn clean install
```

### Running the validator

```
--local - to run the validator locally
--dir="/path/to/pdxfinder-data/data/UPDOG/{provider_dir}"
```

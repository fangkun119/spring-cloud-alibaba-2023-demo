# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Cloud Alibaba 2023 microservices demo project that demonstrates e-commerce order processing with distributed transaction capabilities. The architecture follows a typical microservices pattern with the following core components:

- **Order Service** (`tlmall-order`): Main orchestrator service that handles order creation and coordinates with other services
- **Account Service** (`tlmall-account`): Manages user account balances
- **Storage Service** (`tlmall-storage`): Handles inventory management
- **Gateway Service** (`tlmall-gateway`): API gateway with Sentinel integration for rate limiting and circuit breaking
- **Frontend Service** (`tlmall-frontend`): Web interface for placing orders
- **Common Module** (`tlmall-common`): Shared utilities and common data structures

## Technology Stack

- **Java 21** with Spring Boot 3.2.4
- **Spring Cloud 2023.0.1** with Spring Cloud Alibaba 2023.0.1.0
- **Nacos**: Service discovery and configuration management
- **OpenFeign**: Service-to-service communication with load balancing
- **Seata**: Distributed transaction management (AT mode)
- **Sentinel**: Rate limiting and circuit breaking
- **Spring Cloud Gateway**: API gateway
- **MyBatis**: Database ORM
- **MySQL**: Database with Druid connection pooling

## Development Commands

### Build and Run
```bash
# Build entire project
mvn clean compile

# Package all services
mvn clean package -DskipTests

# Run individual services (from service directory)
cd microservices/tlmall-order
mvn spring-boot:run

# Run tests
mvn test
```

### Service Ports
- Gateway: 8070
- Order Service: 8080
- Account Service: 8020
- Storage Service: 8010
- Frontend: 8090

### Configuration
- Local configuration files: `midwares/dev/local/`
- Nacos remote configuration: `midwares/dev/remote/nacos/`
- Each service uses Nacos for both service discovery and configuration management

## Key Architecture Patterns

### Distributed Transaction Flow
The order creation process (`OrderServiceImpl.createOrder:61`) demonstrates the classic distributed transaction pattern:
1. Reduce inventory via OpenFeign call to storage service
2. Reduce account balance via OpenFeign call to account service
3. Create local order record
4. All operations wrapped in Seata `@GlobalTransactional` for consistency

### Service Communication
- Services use OpenFeign clients for inter-service communication (see `AccountServiceFeignClient`, `StorageServiceFeignClient`)
- Load balancing is handled by Spring Cloud LoadBalancer
- Service discovery via Nacos allows using service names instead of hardcoded URLs

### Configuration Management
- Each service integrates with Nacos config center
- Bootstrap configuration in `application.yml` connects to Nacos
- Remote configurations stored in `midwares/dev/remote/nacos/` directory

### Error Handling
- Common error handling through `Result` wrapper class and `BusinessException`
- Standardized response format across all services
- Proper exception handling in distributed transaction scenarios

## Database Schema
Each service manages its own database:
- Order database stores order information
- Account database stores user balances
- Storage database manages product inventory

## Testing Notes
- Unit tests use Spring Boot Test framework
- Integration tests should account for Nacos and Seata dependencies
- Distributed transaction scenarios require careful test data setup
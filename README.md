# SUDS, Sample Java REST application with DynamoDB And Spring Boot

Just a silly dog grooming appointment application created to play with Amazon's DynamoDB.

## Tables

| Name     | Contents |
|----------|----------|
| Customer | Customer names and pet information |
| Groomer  | Groomers and work schedules (versioned) |

## Endpoints

GET `/customer/` Retrieves all customers with their pets
GET `/customer/pets` Retrieves just pets
GET `/customer/{phone number}` Retrieves single customer
POST `/customer/` Saves customer with pets


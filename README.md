# SUDS, Sample Java REST application with DynamoDB And Spring Boot

Just a silly dog grooming appointment application I created to play with Amazon's DynamoDB.

## Tables

| Name     | Contents |
|----------|----------|
| Customer | Customer names and pet information |
| Groomer  | Groomers and work schedules (versioned) |
| Schedule | Simple schedule entries |

## Endpoints

| Method | Path | Description |
|------|-------------------------------|----------------------------------------|
| GET  | `/customer/`                | Retrieves all customers with their pets |
| GET  | `/customer/pets`             | Retrieves just pets |
| GET  | `/customer/{phone number}` | Retrieves single customer |
| POST | `/customer/`                 | Saves customer with pets |
| GET  | `/groomer/`                    | Retrieves all groomers |
| GET  | `/groomer/{employee number}` | Retrieves single groomer |
| POST | `/groomer/`                    | Saves groomer |
| GET  | `/schedule/{start}/{end}`    | Retrieves schedule entries between yyyy-MM-dd dates |
| GET  | `/schedule/groomer/{employee number}/{start}/{end}` | Retrieves schedule entries for single groomer |
| GET  | `/schedule/customer/{phone number}/{start}/{end}`    | Retrieves schedule entries for single customer |
| POST | `/schedule/`                    | Saves schedule entry |

## Notes

* Clearly not the best solution for this type of application, a relational database would have done just fine.
* I ended up not using JPA because I wanted to be a little more hands on with the NoSQL stuff.
* It's a little light on error handling and logging ("light" meaning none).

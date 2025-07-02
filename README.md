# Rewards API

Simple Spring Boot API for calculating customer reward points.

## Run

```bash
./mvnw spring-boot:run
```

## Endpoints

- `GET /rewards` - Get all customer rewards
- `POST /transaction` - Add transaction

## Example

```bash
# Get rewards
curl http://localhost:8080/rewards

# Add transaction
curl -X POST http://localhost:8080/transaction \
  -H "Content-Type: application/json" \
  -d '{"customerId": 1, "amount": 120.00, "date": "2025-07-01"}'
```

## Reward Rules

- 2 points per dollar over $100
- 1 point per dollar between $50-$100
- 0 points under $50 
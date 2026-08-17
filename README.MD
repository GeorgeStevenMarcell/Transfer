# How to run the program
- Have docker installed on the system
- docker compose up --build

# How to do the POST
```bash
curl -X POST http://localhost:8080/v1/transaction/transfer \
  -H "Content-Type: application/json" \
  -d '{"merchantId":1,"amount":"1000","destinationAcct":"123"}'
```

# To query / see the data
```bash
docker compose exec -T postgres psql -U doitpay -d doitpay \
  -c "SELECT transaction_id, amount, status, failure_reason FROM transactions ORDER BY created_at DESC LIMIT 5;"
```

# To simulate the concurrent requests
```bash
./scripts/run.sh
```

# To verify the correct transaction with no miscalculation and wrong update data
```bash
docker compose exec -T postgres psql -U doitpay -d doitpay   -c "SELECT status, count(*) FROM transactions GROUP BY status;" \
   -c "SELECT count(*) AS leaked FROM transactions WHERE status='SUCCESS' AND failure_reason IS NOT NULL;" \
   -c "SELECT (SELECT balance FROM merchants WHERE id=1) - (10000000.00 - COALESCE(SUM(amount) FILTER (WHERE status IN ('PENDING','SUCCESS')),0)) AS drift FROM transactions;"
```

# Assumptions I made during making this project
- I only use 1 merchant for this project as stated in the PDF file
- The destination account is there but there's no logic to verify if it's the correct destination account
- There's no currency attached in the transaction
- The output is a simple DTO with generic http status

# Corners I cut
- No retry on failed transaction. It will instantly give a failure or success respond
- The timestamp does not have any timezone on it. So it will use what settings the system has.
- No DLQ.
- No transactional outbox.
- No client-side idempotency key. The idempotency key only generated in the backend side of things, so if its the same request because the client has a RTT but the server already process it, the same transaction will be processed again
- No message signing. The request can be tampered with in transit.


# Tests I did
I only did the basic test such as:
- hitting the api multiple times to see whether it will break or not
- see if it can handle multiple hits at once
- whether the transaction deduct correctly or not
- if there are multiple transaction at once, it deducts the correct UUID or not
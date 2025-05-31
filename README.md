# 💰 Wallets Service (Technical Exercise)

This is a simplified wallet service built as part of a backend technical exercise. It allows players to top up their wallets using a credit card and query their wallet balance.

---

## 🧾 Problem Statement

In the original company, this service is used to manage player wallets. Players can:
- Query balance.
- Top-up wallet. In this case, we charge the amount using a third-party payments platform (stripe, paypal, redsys).
- Spend balance on purchases in company.
- Return these purchases, and money is refunded.
- Check their history of transactions.


### ✅ Required Features (Implemented in this POC):
1. **Get a wallet** using its identifier.
2. **Top up money** in a wallet using a credit card. This action charges the amount via a third-party platform (Stripe simulator).
3. Taken into consideration this service must work in a microservices environment in high availability. You should care about concurrency too.
4. Didn't need to write tests for everything, but displayed different types of tests.

### 🔜 Not Implemented (but discussed in interviews):
- Spending money from the wallet.
- Refunding a payment.
- Transaction history.

---

## 🛠️ Project Notes

- Reorganized package structure and renamed references to anonymize the company name.
- Replaced hardcoded simulator URLs with an environment variable.

### Environment Variable

Set the Stripe simulator base URL via environment (change the value in `.env`):

```bash
cp .env.sample .env
export $(cat .env | xargs)
```
To **unset** this later (cleanup):
```bash
unset STRIPE_SIMULATOR_BASE_URI
```

**Alternatively**, just replace the `${}` reference in the `.yml` with the original hardcoded URL if needed.

---
## 🔧 Running the Application

You’ll need Java 17+ and Maven installed.

To start the app with the development profile (uses H2 in-memory DB with schema + data):

```bash
SPRING_PROFILES_ACTIVE=develop mvn spring-boot:run
```

> The `application.yml` is customized for easier testing. If needed, revert to the original and disable some tests.


---

## 🧪 Testing

- `StripeService` is covered with unit tests (success + error cases using `MockRestServiceServer`).
- Optimistic locking via `@Version` is implemented to prevent race conditions during concurrent wallet updates.
- Skipped controller tests for brevity — focus was on service logic and integration with the external Stripe simulator.
- **Credit card number is never stored** for security reasons.


---

## ⚙️ Concurrency Strategy

- **Optimistic locking** using JPA’s `@Version` field on the `Wallet` entity. This allows multiple parallel updates, but fails safely if conflicts occur. This approach balances consistency and availability in a microservices setup.
- **Alternative**: Pessimistic locking or external coordination tools like **ShedLock** — considered overkill for this scope.
- Avoided eventual consistency for wallet balance updates due to the risk of users unknowingly accruing debt.

---


## 🌐 Sample API Calls
This application uses a in-memory H2 database with a couple wallets already inside. Check the `data.sql` file for details.

### ➕ Top Up Wallet

```bash
curl -X POST http://localhost:8090/wallets/{walletId}/top-up \
  -H "Content-Type: application/json" \
  -d '{
        "amount": 15,
        "creditCard": "4242 4242 4242 4242"
      }'
```

### 📄 Get Wallet

```bash
curl http://localhost:8090/wallets/{walletId}
```

---

## Time Spent

- 1h: Environment setup, renaming company references for privacy, configuring `.env`, work on StripeService.
- 1h 30min: Packaging rearrange, implementing core wallet logic and unit tests.
- 15min: Testing the real application.
- 15min: Write this README.

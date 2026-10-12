# aciap-test: inventory-service

A small Spring Boot service in Java and Kotlin. It is the target repository
for the [Coding Agent Platform](https://github.com/kganesh/aciap): tickets in
the Jira simulator ask the agent to change this code, and the agent opens
pull requests here.

## Layout

| Path | Language | What |
| --- | --- | --- |
| `src/main/java/.../model` | Java | `Product` record (prices in cents) |
| `src/main/java/.../service` | Java | `InventoryService`, in-memory repository |
| `src/main/kotlin/.../pricing` | Kotlin | `PriceCalculator` |
| `src/main/kotlin/.../web` | Kotlin | REST controller and error mapping |
| `src/test/...` | Java + Kotlin | JUnit 5 unit tests and MockMvc tests |

No test needs Docker or external services.

## API

| Method | Path | Notes |
| --- | --- | --- |
| GET | `/products` | sorted by SKU |
| GET | `/products/{sku}` | 404 if unknown |
| POST | `/products` | `{"sku","name","priceCents","stock"}`; 409 on duplicate SKU |
| POST | `/products/{sku}/stock` | `{"delta": n}`; 409 if stock would go negative |
| GET | `/products/{sku}/quote?quantity=&discountPercent=` | order total in cents |

## Build

JDK 21 and Maven 3.9.

```bash
mvn -B verify          # compile and run all tests
mvn spring-boot:run    # serve on :8080
```

To add the Maven wrapper so builds use a pinned Maven version:

```bash
mvn -N wrapper:wrapper -Dmaven=3.9.9
```

CI uses `./mvnw` when the wrapper is present and `mvn` otherwise.

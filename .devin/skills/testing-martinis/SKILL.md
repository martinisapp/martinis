---
name: testing-martinis
description: Test the Martinis screenplay management app end-to-end. Use when verifying UI, security, or API changes against a running local instance.
---

# Testing the Martinis App

## Prerequisites

- Docker running (for MySQL)
- Java 17+ JDK (not JRE - JSPs need compilation)
- Maven

## Environment Setup

### 1. Start MySQL via Docker Compose

```bash
cd /home/ubuntu/repos/martinis
docker compose up -d mysql
```

Wait for MySQL to be healthy before starting the app.

### 2. Start the App with Maven (NOT Docker)

The Docker image uses JRE-only (`eclipse-temurin:17-jre-alpine`) which cannot compile JSPs at runtime. Always use `mvn spring-boot:run` with the JDK for local testing:

```bash
cd /home/ubuntu/repos/martinis
DATABASE_URL="jdbc:mysql://localhost:3306/martinis?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" \
SPRING_DATASOURCE_USERNAME=martinis_user \
SPRING_DATASOURCE_PASSWORD=dbpass123 \
ADMIN_USERNAME=admin \
ADMIN_PASSWORD=adminpass123 \
SQL_INIT_MODE=never \
nohup mvn spring-boot:run > /tmp/app.log 2>&1 &
```

Wait ~15 seconds for startup. Verify with `curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/login` (expect 200).

### 3. Create Test Data via MySQL

The app's "Create New Project/Scene" forms may have pre-existing JSP compilation issues. Create test data directly in MySQL:

```bash
docker exec martinis-mysql mysql -u martinis_user -pdbpass123 martinis -e "
INSERT INTO project (title) VALUES ('Test Project');
INSERT INTO scene (name, \`order\`, project_id) VALUES ('Scene One', 1, 1);
INSERT INTO scene (name, \`order\`, project_id) VALUES ('Scene Two', 2, 1);
INSERT INTO block (\`order\`, content, scene_id) VALUES (1, 'Block content', 1);
"
```

### 4. Create Non-Admin Test User

```bash
docker exec martinis-mysql mysql -u martinis_user -pdbpass123 martinis -e "
INSERT INTO user (username, password, enabled) VALUES ('testuser', '\$2b\$10\$ymtYC4FP/eEWfine/g/5HeYaBP5uX1XknfoxFAB1hcO2Aig2UEoKe', 1);
INSERT INTO authority (username, authority) VALUES ('testuser', 'ROLE_USER');
"
```

Password: `testpass`

## Known Issues That May Block Testing

These are pre-existing issues in the codebase, not caused by recent PRs:

1. **Login redirect loop**: `SecurityConfig` might not permit FORWARD dispatches. Fix by adding `.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()` before requestMatchers.

2. **Missing `spring-security-taglibs`**: JSPs using `<sec:...>` tags (including login.jsp) will fail to compile without this dependency in pom.xml.

3. **Missing `cleanView` property**: `ProjectProfileViewModel` might lack the `cleanView` boolean property referenced by `project/show.jsp`.

4. **`/project/toggleCleanView` not implemented**: The Clean View button calls a non-existent endpoint. The button click will return 404 but should NOT return 403 (which would indicate CSRF issues).

5. **JSP `<sf:input>` errors**: `project/create.jsp` may have unterminated Spring Form tags. Work around by creating data via MySQL instead of the UI.

## Testing Security Changes

### POST-Only Endpoints

Test that state-changing endpoints reject GET requests:

```bash
# Should return 405 Method Not Allowed
curl -s -w "%{http_code}" -b /tmp/cookies.txt "http://localhost:8080/project/delete?id=1"
curl -s -w "%{http_code}" -b /tmp/cookies.txt "http://localhost:8080/scene/moveDown?id=1"
curl -s -w "%{http_code}" -b /tmp/cookies.txt "http://localhost:8080/block/delete?id=1"
```

Verify the UI buttons still work by clicking them in the browser.

### CSRF Token Verification

Check that CSRF meta tags are present in the page:
```javascript
document.querySelector('meta[name="_csrf"]').getAttribute('content')
document.querySelector('meta[name="_csrf_header"]').getAttribute('content')
```

A CSRF issue would show as a 403 Forbidden response. A 404 response means the CSRF token was accepted but the endpoint doesn't exist.

### Role-Based Access

Test by logging in as different users and navigating to restricted URLs. Non-admin should get 403 on admin-only endpoints.

## Devin Secrets Needed

None required for local testing. All credentials are set via environment variables at app startup.

## Tips

- The `logout` endpoint requires POST (Spring Security default). To switch users, navigate to `/login` directly or clear cookies.
- After login, Spring Security may redirect to `/WEB-INF/jsp/login.jsp?continue` (a pre-existing saved-request-cache issue). Navigate to the desired page directly instead.
- The `<form>` inside `<p>` HTML issue causes edit/delete buttons to appear on separate lines. This is a known layout bug.
- Column named `order` in the database is a reserved word - use backticks: `` `order` ``.

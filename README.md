# staff-be

management-tool back-end

## Getting started 

If You run for the first time the Backend locally: 

- in application-local.yml change line 10 to mode: always

- Run commands in bash terminal :

```
export SPRING_PROFILES_ACTIVE=local

./mvnw clean package -DskipTests

java -jar target/staff-1.0.0-SNAPSHOT.jar 
```

- After first run, please edit again : 

application-local.yml change line 10 to mode: never
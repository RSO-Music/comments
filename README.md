# RSO: Image metadata microservice

## Prerequisites

```bash
docker run -d --name pg-image-metadata -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=image-metadata -p 5432:5432 postgres:12
```


kubectl create secret generic pg-pass --from-literal=KUMULUZEE_DATASOURCES0_PASSWORD=postgres1
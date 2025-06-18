# CDC Spring Boot Web Hook

## Creating the Cert

```shell
 keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore src/main/resources/keystore.p12 -validity 3650 -dname "CN=cockroachlabs.com, OU=IT, O=Education, L=NY, S=NY, C=US"
```

## Add the `keystore` to the `application.properties`

```properties
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-type=PKCS12
server.ssl.key-store-password=cockroachdb
server.ssl.key-alias=tomcat
```

## Test

```shell
https :8443/api/cdc/employees name=Felipe role=engineer --verify=no
```

## CDC

```sql
CREATE CHANGEFEED FOR TABLE cdc_demo.employees
INTO 'webhook-https://localhost:8443/api/cdc/employees?insecure_tls_skip_verify=true'
WITH format='json', envelope='wrapped', resolved='10s';
```
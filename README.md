# market
중고장터 및 배송 사이트


###application.yml
```yaml
spring:
  profiles:
    active: dev

  datasource:
    url: # url
    username: postgres
    password: pass
  jpa:
    hibernate:
      ddl-auto: update
    
  mail:
    host: # hostAddress
    port: # portNumber
    username: # mail
    password: # password
    properties:
      mail:
        smtp:
          auto: true
          timeout: 5000
          starttls.enable: true

```

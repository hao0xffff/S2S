server:
port: 8080

spring:
datasource:
url: jdbc:mysql://localhost:3306/your_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8
username: root
password: your_password
driver-class-name: com.mysql.cj.jdbc.Driver

mybatis:
mapper-locations: classpath:mapper/*.xml
configuration:
map-underscore-to-camel-case: true
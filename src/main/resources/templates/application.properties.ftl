# Server Settings
server.port=8080

# DataSource Settings
spring.datasource.url=jdbc:mysql://localhost:3306/your_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# MyBatis Settings
mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.configuration.map-underscore-to-camel-case=true

# Lombok Log Support (Optional)
logging.level.root=info
logging.level.${packageName}.mapper=debug
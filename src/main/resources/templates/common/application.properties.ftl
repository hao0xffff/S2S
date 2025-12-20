# Server Settings
server.port=8080

# DataSource Settings
<#if techStack.databaseType == "postgresql" || techStack.databaseType == "postgres">
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
<#else>
spring.datasource.url=jdbc:mysql://localhost:3306/your_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
</#if>

<#if techStack.ormFramework == "mybatis" || techStack.ormFramework == "mybatis-plus">
# MyBatis Settings
<#if techStack.ormFramework == "mybatis">
mybatis.mapper-locations=classpath:mapper/*.xml
</#if>
mybatis.configuration.map-underscore-to-camel-case=true
</#if>

<#if techStack.ormFramework == "mybatis-plus">
# MyBatis-Plus Settings
mybatis-plus.mapper-locations=classpath:mapper/*.xml
mybatis-plus.configuration.map-underscore-to-camel-case=true
</#if>

# Lombok Log Support (Optional)
logging.level.root=info
logging.level.${packageName}.mapper=debug


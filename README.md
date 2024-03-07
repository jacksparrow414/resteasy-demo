# RestEasy-OAuth-demo

## 各组件升级日志

- 源码的JDK已经升级到JDK11
- Servlet4升级到Servlet6，包名从javax全部变更为jakarta，web.xml的描述符也已经修改
- JSTL升级至2.0
- Tomcat9升级到Tomcat10
- Hibernate、Jackson均已升级
- Weld CDI已经升级至最新，并更新了webapp/META-INF/context.xml下的相关配置

## 参考实现
[参考实现的文章](https://www.baeldung.com/java-ee-oauth2-implementation)

## 实现
完全使用JakartaEE标准及其实现

## 使用框架
- [RestEasy](https://resteasy.dev/)
- [Hibernate](https://hibernate.org/orm/)
- [nimbus-jose-jwt](https://connect2id.com/products/nimbus-jose-jwt)
- [Weld](https://weld.cdi-spec.org/)
- [Jackson](https://github.com/FasterXML)
- [Lombok](https://projectlombok.org/setup/maven)
## 数据库
[H2](https://www.h2database.com/html/main.html)
## Server
Tomcat10,因为Tomcat10支持Servlet6

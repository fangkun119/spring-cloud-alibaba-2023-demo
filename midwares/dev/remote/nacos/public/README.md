# Nacos远程配置

## 用途：

为各个微服务托管在 Nacos 的配置，它们都在命名空间public下

| 文件                                     | 用途                         |
|----------------------------------------|----------------------------|
| ./.metadata.yml                        | 在Nacos UI界面创建配置项时填入的元数据    |
| ./DEFAULT_GROUP/nacos-discovery.yml    | 公用配置：服务发现、各微服务用来自己注册到Nacos |
| ./DEFAULT_GROUP/sentinel-dashboard.yml | 公用配置：各微服务进行流量控制            |
| ./DEFAULT_GROUP/seata-client.yml       | 公用配置：各微服务访问Seata以进行分布式事务   |
| ./DEFAULT_GROUP/db-common.yml          | 公用配置：各微服务链接数据库             |
| ./DEFAULT_GROUP/tlmall-gateway.yml     | 网关服务配置                     |
| ./DEFAULT_GROUP/tlmall-order.yml       | 订单服务配置                     |
| ./DEFAULT_GROUP/tlmall-storage.yml     | 库存服务配置                     |
| ./DEFAULT_GROUP/tlmall-account.yml     | 账户服务配置                     |

注意事项：需要在seata-client.yml中的namespace一项，填入自己在Nacos创建名为seata的namespace时得到的ID


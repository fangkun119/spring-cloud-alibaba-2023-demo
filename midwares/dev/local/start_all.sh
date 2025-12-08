#!/bin/bash

  echo "=== 启动微服务中间件环境 ==="

  # 定义变量
  MID_WARES_DIR="$HOME/Code/mid-wares"
  MYSQL_PASSWORD="your_mysql_password"  # 请替换为实际的MySQL密码

  # 1. 启动MySQL
  echo "1. 启动MySQL..."
  brew services start mysql

  # 2. 启动Nacos
  echo "2. 启动Nacos..."
  echo "waiting for mysql start"
  sleep 15
  cd "$MID_WARES_DIR/nacos"
  if [ -f "bin/startup.sh" ]; then
      bash bin/startup.sh
  else
      echo "Nacos未找到，请检查路径: $MID_WARES_DIR/nacos"
  fi

  # 3. 启动Seata
  echo "3. 启动Seata..."
  echo "waiting for nacos start"
  sleep 30
  cd "$MID_WARES_DIR/seata/bin"
  if [ -f "seata-server.sh" ]; then
      mkdir -p ~/logs/seata/
      ./seata-server.sh &
  else
      echo "Seata未找到，请检查路径: $MID_WARES_DIR/seata"
  fi

  # 4. 启动Sentinel
  echo "4. 启动Sentinel..."
  cd "$MID_WARES_DIR/sentinel"
  if [ -f "start.sh" ]; then
      bash start.sh
  else
      echo "Sentinel未找到，请检查路径: $MID_WARES_DIR/sentinel"
  fi

  # 5. 启动SkyWalking
  echo "5. 启动SkyWalking..."
  cd "$MID_WARES_DIR/apache-skywalking-apm-bin"
  if [ -f "bin/startup.sh" ]; then
      # 文档中提到需要在startup.sh中添加sleep 10，但我们在这里处理
      bash bin/startup.sh &
  else
      echo "SkyWalking未找到，请检查路径: $MID_WARES_DIR/apache-skywalking-apm-bin"
  fi

  echo "=== 所有中间件启动中，请等待... ==="

  # 等待一下让服务启动
  sleep 15

  echo "=== 检查服务状态 ==="

  # 检查各个服务端口是否启动
  echo "MySQL服务状态:"
  brew services list | grep mysql

  echo ""
  echo "Nacos端口状态 (8848):"
  lsof -i :8848 || echo "Nacos端口8848未启动"

  echo ""
  echo "Seata端口状态 (7091):"
  lsof -i :7091 || echo "Seata端口7091未启动"

  echo ""
  echo "Sentinel端口状态 (8888):"
  lsof -i :8888 || echo "Sentinel端口8888未启动"

  echo ""
  echo "SkyWalking端口状态 (18080):"
  lsof -i :18080 || echo "SkyWalking端口18080未启动"

  echo ""
  echo "=== 启动完成! ==="
  echo "访问地址:"
  echo "- Nacos:
  http://tlmall-nacos-server:8848/nacos/"
  echo "- Seata:
  http://tlmall-seata-server:7091/"
  echo "- Sentinel:
  http://tlmall-sentinel-dashboard:8888/"
  echo "- SkyWalking:
  http://tlmall-skywalking-server:18080/"


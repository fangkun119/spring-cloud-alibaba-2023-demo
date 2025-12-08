  #!/bin/bash

  echo "=== 关闭微服务中间件环境 ==="

  # 定义变量
  MID_WARES_DIR="$HOME/Code/mid-wares"

  # 1. 关闭SkyWalking
  echo "1. 关闭SkyWalking..."
  cd "$MID_WARES_DIR/apache-skywalking-apm-bin"
  if [ -f "bin/shutdown.sh" ]; then
      bash bin/shutdown.sh
  else
      echo "SkyWalking shutdown脚本未找到，尝试kill进程..."
      pkill -f "skywalking"
  fi

  # 2. 关闭Sentinel
  echo "2. 关闭Sentinel..."
  cd "$MID_WARES_DIR/sentinel"
  if [ -f "stop.sh" ]; then
      bash stop.sh
  else
      echo "Sentinel stop脚本未找到，尝试kill进程..."
      pkill -f "sentinel-dashboard"
  fi

  # 3. 关闭Seata
  echo "3. 关闭Seata..."
  # 使用jps找到seata-server进程并kill
  # jps | grep "SeataStartup" | awk '{print $1}' | xargs -r kill
  # 或者直接kill端口7091的进程
  # lsof -ti:7091 | xargs -r kill
  cd "$MID_WARES_DIR/seata/bin"
  if [ -f "seata-server.sh" ]; then
      ./seata-server.sh stop
  else
      echo "Seata未找到，请检查路径: $MID_WARES_DIR/seata"
  fi

  # 4. 关闭Nacos
  echo "4. 关闭Nacos..."
  cd "$MID_WARES_DIR/nacos"
  if [ -f "bin/shutdown.sh" ]; then
      bash bin/shutdown.sh
  else
      echo "Nacos shutdown脚本未找到，尝试kill进程..."
      lsof -ti:8848 | xargs -r kill
  fi

  # 5. 关闭MySQL
  echo "5. 关闭MySQL..."
  brew services stop mysql

  # 等待进程完全关闭
  sleep 5

  echo "=== 检查服务是否已关闭 ==="

  # 检查各个服务端口是否还在使用
  echo "检查端口状态:"
  echo "Nacos端口 (8848):"
  lsof -i :8848 || echo "✓ 已关闭"

  echo ""
  echo "Seata端口 (7091):"
  lsof -i :7091 || echo "✓ 已关闭"

  echo ""
  echo "Sentinel端口 (8888):"
  lsof -i :8888 || echo "✓ 已关闭"

  echo ""
  echo "SkyWalking端口 (18080):"
  lsof -i :18080 || echo "✓ 已关闭"

  echo ""
  echo "MySQL服务状态:"
  brew services list | grep mysql

  echo ""
  echo "=== 所有中间件已关闭! ==="

  # 清理临时进程（可选）
  echo "清理残留进程..."
  pkill -f "nacos" 2>/dev/null
  pkill -f "seata" 2>/dev/null
  pkill -f "sentinel" 2>/dev/null
  pkill -f "skywalking" 2>/dev/null

  echo "清理完成!"

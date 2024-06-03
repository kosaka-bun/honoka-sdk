#!/bin/bash

set -e

cd $(dirname "$0")/..
PROJECT_PATH="$(pwd)"

chmod +x gradlew

# 读取当前gradle项目根模块的版本信息，检查版本号是否符合要求
check_version_of_projects_out=$(./gradlew checkVersionOfProjects)
echo -e "check_version_of_projects_out:\n\n$check_version_of_projects_out"
# 若版本号检测不通过，则添加开发版标记
if [ "$(echo "$check_version_of_projects_out" | grep -i "results.passed=true")" == '' ]; then
  touch dev_flag.txt
fi

# 将kosaka-bun/maven-repo的git仓库clone到项目根目录下
git clone $1

# 打包，并发布到远程maven仓库在本地的一个拷贝当中
if [ -f dev_flag.txt ]; then
  ./gradlew -PremoteMavenRepositoryUrl=$PROJECT_PATH/maven-repo/repository/development \
            -PisDevelopmentRepository=true \
            publish
else
  ./gradlew -PremoteMavenRepositoryUrl=$PROJECT_PATH/maven-repo/repository/release publish
fi
# 将maven-repo/repository目录打包，然后将tar移动到另一个单独的目录中
tar -zcf maven-repo.tar.gz maven-repo/repository
mkdir remote-maven-repo-copy
mv maven-repo.tar.gz remote-maven-repo-copy/
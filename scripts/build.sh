#!/bin/bash

set -e

cd $(dirname "$0")/..
PROJECT_PATH="$(pwd)"

chmod +x gradlew

# 读取当前gradle项目根模块的版本信息，检查版本号是否符合要求
./gradlew checkVersionOfProjects
check_version_of_projects_out=$(./gradlew checkVersionOfProjects)
# 检查版本号
# 当grep命令未找到匹配的字符串时，将返回非0的返回值（返回值为Exit Code，不是程序的输出内容，可通过“$?”得到上一行命令的返回值）
# 文件设置了set -e，任何一行命令返回值不为0时，均会中止脚本的执行，在命令后加上“|| true”可忽略单行命令的异常
# true是一个shell命令，它的返回值始终为0，false命令的返回值始终为1。
projects_passed=$(echo "$check_version_of_projects_out" | grep -i "results.projectsPassed=true") || true
dependencies_passed=$(echo "$check_version_of_projects_out" | grep -i "results.dependenciesPassed=true") || true
# -z表示字符串为空，-n表示字符串不为空
if [ -n $projects_passed ] && [ -z $dependencies_passed ]; then
  echo 'Projects with release version contain dependencies with development version!'
  exit 10
fi

# 将kosaka-bun/maven-repo的git仓库clone到项目根目录下
git clone $1

# 打包，并发布到远程maven仓库在本地的一个拷贝当中
./gradlew build
if [ -n $projects_passed ]; then
  echo -e '\n\nUsing development repository to publish artifacts.\n'
  echo "IS_DEVELOPMENT_VERSION=true" >> "$GITHUB_OUTPUT"
  ./gradlew -PremoteMavenRepositoryUrl=$PROJECT_PATH/maven-repo/repository/development \
            -PisDevelopmentRepository=true \
            publish
else
  echo "IS_DEVELOPMENT_VERSION=false" >> "$GITHUB_OUTPUT"
  ./gradlew -PremoteMavenRepositoryUrl=$PROJECT_PATH/maven-repo/repository/release publish
fi
# 将maven-repo/repository目录打包，然后将tar移动到另一个单独的目录中
tar -zcf maven-repo.tar.gz maven-repo/repository
mkdir remote-maven-repo-copy
mv maven-repo.tar.gz remote-maven-repo-copy/
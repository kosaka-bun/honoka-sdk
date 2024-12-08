#!/bin/bash

set -e

cd $(dirname "$0")/..
PROJECT_PATH="$(pwd)"

chmod +x gradlew

# 读取当前gradle项目根模块的版本信息，检查版本号是否符合要求
./gradlew checkVersionOfProjects
check_version_of_projects_out=$(./gradlew checkVersionOfProjects)

#
# 检查版本号
#
# 当grep命令未找到匹配的字符串时，将返回非0的返回值（返回值为Exit Code，不是程序的输出内容，
# 可通过“$?”得到上一行命令的返回值）。
# 文件设置了set -e，任何一行命令返回值不为0时，均会中止脚本的执行，在命令后加上“|| true”可
# 忽略单行命令的异常。
# true是一个shell命令，它的返回值始终为0，false命令的返回值始终为1。
#
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
repository_name=development
is_development_version=true

if [ -z $projects_passed ]; then
  echo -e '\n\nUsing development repository to publish artifacts.\n'
else
  repository_name=release
  is_development_version=false
fi

echo "IS_DEVELOPMENT_VERSION=$is_development_version" >> "$GITHUB_OUTPUT"

gradle-publish() {
  task_name=publish
  if [ -n "$1" ]; then
    task_name=":$1:$task_name"
  fi
  ./gradlew -PremoteMavenRepositoryUrl=$PROJECT_PATH/maven-repo/repository/$repository_name \
            -PisDevelopmentRepository=$is_development_version $task_name
}

#
# 若仅在gradle中指定task的依赖关系，无法保证在B模块依赖A模块时，在A模块的publish任务执行完成之后，
# 就能在同一次构建的后续任务当中，使B模块能够从本地仓库中找到其所依赖的A模块。
#
# 需要根据模块间依赖关系，按顺序多次执行不同的构建。
#
gradle-publish honoka-utils
gradle-publish honoka-kotlin-utils
gradle-publish

# 将maven-repo/repository目录打包，然后将tar移动到另一个单独的目录中
tar -zcf maven-repo.tar.gz maven-repo/repository
mkdir remote-maven-repo-copy
mv maven-repo.tar.gz remote-maven-repo-copy/
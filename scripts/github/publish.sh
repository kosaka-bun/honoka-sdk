#!/bin/bash

set -e

cd $(dirname "$0")/../..
PROJECT_PATH="$(pwd)"

# 将kosaka-bun/maven-repo的git仓库clone到项目根目录下
git clone $1

# 解压maven-repo.tar.gz
cd remote-maven-repo-copy
tar -zxf maven-repo.tar.gz
cd ..

#
# 将[项目根目录]/remote-maven-repo-copy/maven-repo/repository下所有内容，复制到[项目根目录]/maven-repo/repository下，
# 并替换已存在的内容。
#
cp -rf remote-maven-repo-copy/maven-repo/repository/* maven-repo/repository/

# 进入名为maven-repo的git仓库，设置提交者信息，然后提交并推送
cd maven-repo/repository

commit_message='Update honoka-sdk'
if [ "$IS_DEVELOPMENT_VERSION" == 'true' ]; then
  commit_message="$commit_message (dev)"
fi

git config --global user.name 'Kosaka Bun'
git config --global user.email 'kosaka-bun@qq.com'
git add .
git commit -m "$commit_message"
git push
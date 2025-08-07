#!/bin/bash

set -e

if [ -z "$GITHUB_WORKSPACE" ]; then
  echo 'Must specify a project root path!'
  exit 10
fi

cd "$GITHUB_WORKSPACE"

if [ "$BUILD_SCRIPTS_VERSION" == 'master' ]; then
  FILE_PATH="heads/$BUILD_SCRIPTS_VERSION"
else
  FILE_PATH="tags/$BUILD_SCRIPTS_VERSION"
fi

SCRIPTS_URL="https://github.com/honoka-studio/$BUILD_SCRIPTS_REPO/archive/refs/$FILE_PATH.tar.gz"

echo "Downloading scripts from $SCRIPTS_URL"
curl -L -o scripts.tar.gz $SCRIPTS_URL

tar -zxf scripts.tar.gz
rm -f scripts.tar.gz
mv $BUILD_SCRIPTS_REPO-$BUILD_SCRIPTS_VERSION build-scripts

find build-scripts -type f -name '*.sh' | xargs chmod +x

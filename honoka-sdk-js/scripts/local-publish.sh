#!/bin/sh

set -e

cd $(dirname "$0")/..
project_path="$(pwd)"

publish() {
  rm -rf "$APPDATA/verdaccio/storage/@honoka/$1"
  cd "$project_path/$1"
  npm publish --registry http://localhost:4873/
}

publish js-utils

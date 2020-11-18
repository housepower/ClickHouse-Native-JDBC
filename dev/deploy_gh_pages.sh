#!/usr/bin/env bash

set -e

npm run docs:build

cd docs/.vuepress/dist

git init
git add -A
git commit -m 'Deploy GitHub Pages'
git push -f git@github.com:powerhouse/ClickHouse-Native-JDBC.git master:gh-pages

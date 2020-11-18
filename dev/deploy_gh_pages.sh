#!/usr/bin/env bash

if [[ -z $(git show HEAD -- docs) ]]; then
  echo 'no changes in docs, skip'
  exit 0
fi

cd docs/.vuepress/dist

git config --global user.name  'GitHub Workflow'
git config --global user.email 'dummy@dummy.dummy'

git init
git add -A
git commit -m 'Deploy GitHub Pages'
git push -f https://pan3793:${PAGES_DEPLOY_TOKEN}@github.com/housepower/ClickHouse-Native-JDBC.git master:gh-pages

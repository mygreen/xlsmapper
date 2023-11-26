#!/bin/sh -e

echo "Building sphinx documentation for version $1"

SCRIPT_DIR=$(cd $(dirname $0); pwd)
cd $SCRIPT_DIR

if [ -e ./src/site/sphinx/build ]; then
  echo "step - remove old build directory."
  sudo /bin/rm -rf ./src/site/sphinx/source/_build
fi

SOURCE_DIR=$SCRIPT_DIR/src/site/sphinx/source

echo "step - make html by sphinx."
docker run --rm -v $SOURCE_DIR:/docs xlsmapper/sphinx sphinx-build -M html . _build

echo "step - change owner for build directry with jenkins."
sudo /usr/bin/chown -R jenkins:jenkins $SOURCE_DIR/_build

## copy html dir
cd $SCRIPT_DIR

echo "step - remove target sphinx directory."
sudo /bin/rm -rf ./target/site/sphinx
/bin/mkdir -p ./target/site/sphinx

echo "step - copy build html to target directory."
/bin/cp -vr $SOURCE_DIR/_build/html/* ./target/site/sphinx/

## github-pagesのsphinx対応
echo "step - create file or .nojekyll."
touch ./target/site/.nojekyll


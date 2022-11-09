#!/bin/bash

set -e

download_folder=$(mktemp -d)
pushd ${download_folder}
curl -o sdk-${SDK_VERSION}.zip https://s3-eu-west-1.amazonaws.com/urplus-developer-site/sdk/sdk-${SDK_VERSION}.zip
mkdir sdk
unzip -q sdk-${SDK_VERSION}.zip -d sdk
cd sdk
sed -e 's/\<sudo\>//g' ./install.sh > ./install_no_sudo.sh
chmod +x ./install_no_sudo.sh
yes | ./install_no_sudo.sh
popd

cd com.ur.urcap.examples.mytoolbarjog/
mvn install

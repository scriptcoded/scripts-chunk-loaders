#!/usr/bin/env bash

version=$1

if [[ -z "$version" ]]; then
  echo "Usage: $0 <version>"
  exit 1
fi

# Replace version in gradle.properties
sed -i "s/^mod_version=.*/mod_version=$version/" gradle.properties

# Build the project
./gradlew build

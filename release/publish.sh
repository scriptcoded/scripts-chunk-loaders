#!/usr/bin/env bash

export MODRINTH_CHANGELOG="$1"

# Publish to modrinth
./gradlew modrinth

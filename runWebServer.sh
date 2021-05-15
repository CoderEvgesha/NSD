#!/bin/sh
./gradlew runPartyAServer &
./gradlew runPartyBServer &
./gradlew runPartyCServer &
./gradlew runPartyDServer &

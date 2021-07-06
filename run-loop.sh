#!/bin/bash

let TOTAL_RUNS=10;
let FAILED_COUNTER=0;

mvn clean;

for (( i=0; i<$TOTAL_RUNS; i++ ))
do
  if ! mvn test; then
    let FAILED_COUNTER++;
  fi
done

printf "\n\nFailures: $FAILED_COUNTER of $TOTAL_RUNS\n"


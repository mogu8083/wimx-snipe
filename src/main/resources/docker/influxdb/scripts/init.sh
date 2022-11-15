#!/bin/bash
set -e

influx -execute "CREATE USER ulalalab WITH PASSWORD 'ulalalab12!@' WITH ALL PRIVILEGES;"
influx -execute "CREATE DATABASE wimx;"
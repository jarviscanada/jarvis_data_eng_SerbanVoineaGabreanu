#!/bin/bash

#This script is responsible for collecting the server's suage data, and then said data is inserted into a psql database.
#Crontab should be configured to run this script ideally every minute.

#Setup/Validation arguments
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

#Checks the number of arguments. (E.g. if psql_host... to psql_password is added then its good, otherwise no.
if [ "$#" -ne 5 ]; then
	echo "Iliegal number of parameters"
	exit 1
fi

#Saves the statistics of the machine in MegaBytes (MB) and the hostname to variables.
vmstat_mb=$(vmstat --unit M)
hostname=$(hostname -f)

#Gets the hardware specification variables.
memory_free=$(echo "$vmstat_mb" | awk '{print $4}'| tail -n1 | xargs)
cpu_idle=$(echo "$vmstat_mb" | tail -n1 | awk '{print $15}' | xargs)
cpu_kernel=$(echo "$vmstat_mb" | tail -n1 | awk '{print $14}' | xargs)
disk_io=$(vmstat -d | tail -n1 | awk '{print $10}' | xargs)
disk_available=$(df -BM / | tail -n1 | awk '{print $4}' | sed 's/M//' | xargs)
timestamp=$(vmstat -t | tail -n1 | awk '{print $(NF-1) " " $NF}' | xargs) 

#Query for finding matching id in the host_info table.
host_id="(SELECT id FROM host_info WHERE hostname='$hostname')"

#PSQL command for inserting sever usage data in the host_usage table.
insert_stmt="INSERT INTO host_usage(timestamp, host_id, memory_free, cpu_idle, cpu_kernel, disk_io, disk_available)
VALUES('$timestamp', $host_id, $memory_free, $cpu_idle, $cpu_kernel, $disk_io, $disk_available);"

#Sets up env var for pql command.
export PGPASSWORD=$psql_password

#Inserts date into the database.
psql -h "$psql_host" -p "$psql_port" -d "$db_name" -U "$psql_user" -c "$insert_stmt"
exit $?


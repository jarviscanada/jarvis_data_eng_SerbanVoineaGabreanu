#!/bin/bash

#This script is responsible for collecting hardware specification data and then inserting it into the host_info table.
#This script only needs to be run once for a machine.

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

lscpu_out=$(lscpu)
meminfo_out=$(cat /proc/meminfo)

cpu_number=$(echo "$lscpu_out" | egrep "^CPU\(s\):" | awk '{print $2}' | xargs)
cpu_architecture=$(echo "$lscpu_out" | egrep "^Architecture:" | awk '{print $2}' | xargs)
cpu_model=$(echo "$lscpu_out" | egrep "^Model name:" | cut -d':' -f2 | xargs)
cpu_mhz=$(grep -m 1 "^cpu MHz" /proc/cpuinfo | awk -F: '{print $2}' | xargs | cut -d'.' -f1)
l2_cache=$(echo "$lscpu_out" | egrep "^L2 cache:" | awk '{print $3}' | sed 's/K//' | xargs)
total_mem=$(echo "$meminfo_out" | egrep "^MemTotal:" | awk '{print $2}' | xargs)
timestamp=$(vmstat -t | tail -n1 | awk '{print $(NF-1) " " $NF}' | xargs) 
hostname=$(hostname -f)

#PSQL command for inserting sever usage data in the host_info table.
insert_stmt="INSERT INTO host_info(hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, total_mem, timestamp)
VALUES('$hostname', $cpu_number, '$cpu_architecture', '$cpu_model', $cpu_mhz, $l2_cache, $total_mem, '$timestamp');"

#Sets up env var for pql command.
export PGPASSWORD=$psql_password

#Inserts date into the database.
psql -h "$psql_host" -p "$psql_port" -d "$db_name" -U "$psql_user" -c "$insert_stmt"
exit $?


